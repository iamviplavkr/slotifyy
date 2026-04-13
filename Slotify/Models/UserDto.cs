namespace Slotify.Models
{
    public class UserDto
    {
        public int UserId { get; set; }
        public string FirebaseUid { get; set; }
        public string PhoneNumber { get; set; }
        public string Role { get; set; }
        public bool IsActive { get; set; }
        public DateTime CreatedAt { get; set; }
        public DateTime UpdatedAt { get; set; }
        public DateTime? LastLoginAt { get; set; }
    }
}
