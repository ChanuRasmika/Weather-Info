# Weather-Info

A full-stack weather application with user authentication and real-time weather data.

## Features

- User authentication with email/password
- Multi-factor authentication (MFA) via email
- Real-time weather data for 8 cities
- Responsive weather dashboard
- JWT-based security

## Tech Stack

**Backend:**
- Java Spring Boot
- Spring Security
- MySQL Database
- OpenWeather API
- Email service (Gmail SMTP)

**Frontend:**
- React 18
- Vite
- HTML5/CSS3
- Fetch API

## Setup

### Backend
1. Navigate to `backend` folder
2. Configure environment variables in `.env`:
   ```
   DATASOURCE_USER=your_db_user
   DATASOURCE_PASSWORD=your_db_password
   DATASOURCE_URL=jdbc:mysql://localhost:3306/weather_api
   EMAIL=your_gmail@gmail.com
   PASSWORD=your_app_password
   SECRET_KEY=your_jwt_secret
   EXPIRE_TIME=3600000
   REFRESH_TIME=604800000
   API_KEY=your_openweather_api_key
   ```
3. Run: `./mvnw spring-boot:run`

### Frontend
1. Navigate to `frontend` folder
2. Run: `npm install && npm run dev`

## API Endpoints

### Authentication
- `POST /api/auth/login` - User login
- `POST /api/mfa/verify` - MFA verification

### Weather
- `GET /api/weather/cities` - Get available cities
- `GET /api/weather/{cityCode}` - Get weather for specific city

## Cities Available
- Colombo (1248991)
- Tokyo (1850147)
- Liverpool (2644210)
- Paris (2988507)
- Sydney (2147714)
- Boston (4930956)
- Shanghai (1796236)
- Oslo (3143244)

## Usage
1. Register/Login with email and password
2. Enter MFA code sent to email
3. View weather dashboard with live data for all cities