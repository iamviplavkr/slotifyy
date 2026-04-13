namespace Slotify.Models
{
    public class BookingRequest
    {
        public int SlotId { get; set; }
        public DateTime StartTime { get; set; }
        public DateTime EndTime { get; set; }
        public decimal TotalAmount { get; set; }
    }
}
