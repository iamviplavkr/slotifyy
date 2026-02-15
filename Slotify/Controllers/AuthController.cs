using FirebaseAdmin.Auth;
using Microsoft.AspNetCore.Mvc;
using Slotify.Services;
using Slotify.Models;

namespace Slotify.Controllers
{
    [ApiController]
    [Route("api/auth")]
    public class AuthController : ControllerBase
    {
        private readonly UserService _userService;
        private readonly JwtService _jwtService;

        public AuthController(UserService userService, JwtService jwtService)
        {
            _userService = userService;
            _jwtService = jwtService;
        }

        [HttpPost("login")]
        public async Task<IActionResult> Login([FromBody] FirebaseLoginRequest request)
        {
            if (string.IsNullOrWhiteSpace(request.IdToken))
                return BadRequest("Firebase ID token is required");

            FirebaseToken decodedToken;

            try
            {
                decodedToken = await FirebaseAuth.DefaultInstance
                    .VerifyIdTokenAsync(request.IdToken);
            }
            catch
            {
                return Unauthorized("Invalid Firebase token");
            }

            var firebaseUid = decodedToken.Uid;

            var phoneNumber = decodedToken.Claims.ContainsKey("phone_number")
                ? decodedToken.Claims["phone_number"]?.ToString()
                : null;

            if (string.IsNullOrEmpty(phoneNumber))
                return Unauthorized("Phone number not found in token");

            // ✅ UPSERT USER IN SQL
            UserDto user = _userService.UpsertUser(firebaseUid, phoneNumber);

            // ✅ GENERATE JWT
            string jwt = _jwtService.GenerateToken(
                user.FirebaseUid,
                user.PhoneNumber,
                user.Role
            );

            return Ok(new
            {
                token = jwt,
                userId = user.UserId,
                firebaseUid = user.FirebaseUid,
                phoneNumber = user.PhoneNumber,
                role = user.Role,
                message = "Login successful"
            });
        }
    }

    public class FirebaseLoginRequest
    {
        public string IdToken { get; set; }
    }
}
