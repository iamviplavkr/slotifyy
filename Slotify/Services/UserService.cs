using Microsoft.Data.SqlClient;
using Microsoft.Extensions.Configuration;
using Slotify.Models;
using System.Data;

namespace Slotify.Services
{
    public class UserService
    {
        private readonly IConfiguration _config;

        public UserService(IConfiguration config)
        {
            _config = config;
        }

        public UserDto UpsertUser(string firebaseUid, string phoneNumber)
        {
            using SqlConnection conn = new SqlConnection(
                _config.GetConnectionString("DefaultConnection")
            );

            using SqlCommand cmd = new SqlCommand("sp_User_Upsert", conn);
            cmd.CommandType = CommandType.StoredProcedure;

            cmd.Parameters.AddWithValue("@FirebaseUid", firebaseUid);
            cmd.Parameters.AddWithValue("@PhoneNumber", phoneNumber);

            conn.Open();

            using SqlDataReader reader = cmd.ExecuteReader();
            reader.Read();

            return new UserDto
            {
                UserId = Convert.ToInt32(reader["UserId"]),
                FirebaseUid = reader["FirebaseUid"].ToString(),
                PhoneNumber = reader["PhoneNumber"].ToString(),
                Role = reader["Role"].ToString()
            };
        }
    }
}
