package com.viplavkr.slotify.user.activities

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import com.viplavkr.slotify.R
import java.util.concurrent.Executors

@androidx.camera.core.ExperimentalGetImage
class ScannerActivity : AppCompatActivity() {

    private lateinit var previewView: PreviewView
    private val cameraExecutor = Executors.newSingleThreadExecutor()

    // 🔥 Prevent multiple scans
    private var isScanned = false

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) startCamera()
            else Toast.makeText(this, "Camera permission required", Toast.LENGTH_SHORT).show()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanner)

        previewView = findViewById(R.id.previewView)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED
        ) {
            startCamera()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun startCamera() {

        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({

            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            val barcodeScanner = BarcodeScanning.getClient()

            val imageAnalyzer = ImageAnalysis.Builder().build().also {

                it.setAnalyzer(cameraExecutor) { imageProxy ->

                    if (isScanned) {
                        imageProxy.close()
                        return@setAnalyzer
                    }

                    val mediaImage = imageProxy.image

                    if (mediaImage != null) {

                        val image = InputImage.fromMediaImage(
                            mediaImage,
                            imageProxy.imageInfo.rotationDegrees
                        )

                        barcodeScanner.process(image)
                            .addOnSuccessListener { barcodes ->

                                for (barcode in barcodes) {
                                    val value = barcode.rawValue

                                    if (!value.isNullOrEmpty() && !isScanned) {

                                        isScanned = true   // 🔥 lock scan

                                        runOnUiThread {
                                            handleScannedData(value)
                                        }

                                        break
                                    }
                                }
                            }
                            .addOnCompleteListener {
                                imageProxy.close()
                            }

                    } else {
                        imageProxy.close()
                    }
                }
            }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                this,
                cameraSelector,
                preview,
                imageAnalyzer
            )

        }, ContextCompat.getMainExecutor(this))
    }

    private fun handleScannedData(data: String) {

        if (!data.startsWith("SLOTIFY")) {
            Toast.makeText(this, "Invalid QR Code", Toast.LENGTH_SHORT).show()
            isScanned = false
            return
        }

        val parts = data.split("|")

        if (parts.size < 2) {
            Toast.makeText(this, "Invalid QR Format", Toast.LENGTH_SHORT).show()
            isScanned = false
            return
        }

        val bookingId = parts[1]

        Toast.makeText(this, "Booking ID: $bookingId", Toast.LENGTH_LONG).show()

        // 👉 NEXT STEP: connect validation
        // MockParkingRepository.validateEntry(bookingId)

        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}