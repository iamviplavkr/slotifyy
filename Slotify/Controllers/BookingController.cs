using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Slotify.Models;
using Slotify.Services;

namespace Slotify.Controllers
{
    [ApiController]
    [Route("api/booking")]
    public class BookingController : ControllerBase
    {
        private readonly BookingService _bookingService;

        public BookingController(BookingService bookingService)
        {
            _bookingService = bookingService;
        }

        [Authorize]
        [HttpPost("create")]
        public IActionResult CreateBooking([FromBody] BookingRequest request)
        {
            var userIdClaim = User.FindFirst("userId")?.Value;

            if (userIdClaim == null)
                return Unauthorized();

            int userId = int.Parse(userIdClaim);

            var bookingId = _bookingService.CreateBooking(userId, request);

            return Ok(new { bookingId });
        }
    }
}
