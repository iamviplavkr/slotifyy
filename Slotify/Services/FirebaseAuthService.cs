using Microsoft.AspNetCore.Mvc;

namespace Slotify.Services
{
    public class FirebaseAuthService : Controller
    {
        public IActionResult Index()
        {
            return View();
        }
    }
}
