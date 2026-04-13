using FirebaseAdmin;
using Google.Apis.Auth.OAuth2;
using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.IdentityModel.Tokens;
using Slotify.Services;
using System.Text;

var builder = WebApplication.CreateBuilder(args);

// ===============================
// ?? Firebase Admin SDK
// ===============================
FirebaseApp.Create(new AppOptions
{
    Credential = GoogleCredential.FromFile(
        @"C:\secrets\slotify\firebase-admin.json"
    )
});

// ===============================
// ?? JWT Authentication
// ===============================
var jwtSettings = builder.Configuration.GetSection("JwtSettings");

builder.Services
    .AddAuthentication(JwtBearerDefaults.AuthenticationScheme)
    .AddJwtBearer(options =>
    {
        options.TokenValidationParameters = new TokenValidationParameters
        {
            ValidateIssuer = true,
            ValidateAudience = true,
            ValidateLifetime = true,
            ValidateIssuerSigningKey = true,

            ValidIssuer = jwtSettings["Issuer"],
            ValidAudience = jwtSettings["Audience"],

            IssuerSigningKey = new SymmetricSecurityKey(
                Encoding.UTF8.GetBytes(jwtSettings["SecretKey"])
            )
        };
    });

// ===============================
// ?? Dependency Injection
// ===============================
builder.Services.AddControllers();

builder.Services.AddScoped<UserService>();
builder.Services.AddScoped<JwtService>();
builder.Services.AddScoped<FirebaseAuthService>();
builder.Services.AddScoped<BookingService>();


// ===============================
// ?? Swagger
// ===============================
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();

// ===============================
// ?? Build App
// ===============================
var app = builder.Build();

// ===============================
// ?? Middleware Pipeline
// ===============================
if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI();
}

app.UseAuthentication(); // ?? MUST be before Authorization
app.UseAuthorization();

app.MapControllers();

app.Run();
