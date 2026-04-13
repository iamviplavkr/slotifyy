using System.Data;
using System.Data.SqlClient;
using Slotify.Models;

namespace Slotify.Services
{
    public class BookingService
    {
        private readonly IConfiguration _config;

        public BookingService(IConfiguration config)
        {
            _config = config;
        }

        public int CreateBooking(int userId, BookingRequest request)
        {
            using SqlConnection conn = new SqlConnection(
                _config.GetConnectionString("DefaultConnection")
            );

            using SqlCommand cmd = new SqlCommand("sp_CreateBooking", conn);
            cmd.CommandType = CommandType.StoredProcedure;

            cmd.Parameters.AddWithValue("@UserId", userId);
            cmd.Parameters.AddWithValue("@SlotId", request.SlotId);
            cmd.Parameters.AddWithValue("@StartTime", request.StartTime);
            cmd.Parameters.AddWithValue("@EndTime", request.EndTime);
            cmd.Parameters.AddWithValue("@TotalAmount", request.TotalAmount);

            conn.Open();

            var bookingId = Convert.ToInt32(cmd.ExecuteScalar());

            return bookingId;
        }
    }
}
