using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using System.Data;
using System.Data.SqlClient;
using Microsoft.Extensions.Configuration;

namespace Slotify.Controllers
{
    [ApiController]
    [Route("api/payment")]
    [Authorize] // 🔐 JWT Protected
    public class PaymentController : ControllerBase
    {
        private readonly IConfiguration _config;

        public PaymentController(IConfiguration config)
        {
            _config = config;
        }

        [HttpPost("create")]
        public IActionResult CreatePayment([FromBody] PaymentRequest request)
        {
            using SqlConnection conn = new SqlConnection(
                _config.GetConnectionString("DefaultConnection")
            );

            using SqlCommand cmd = new SqlCommand("sp_CreatePayment", conn);
            cmd.CommandType = CommandType.StoredProcedure;

            cmd.Parameters.AddWithValue("@BookingId", request.BookingId);
            cmd.Parameters.AddWithValue("@Amount", request.Amount);
            cmd.Parameters.AddWithValue("@PaymentMode", request.PaymentMode);

            conn.Open();
            var result = cmd.ExecuteScalar()?.ToString();

            return Ok(new
            {
                Message = result
            });
        }
    }

    public class PaymentRequest
    {
        public int BookingId { get; set; }
        public decimal Amount { get; set; }
        public string PaymentMode { get; set; }
    }
}
