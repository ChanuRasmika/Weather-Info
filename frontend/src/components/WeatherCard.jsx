function WeatherCard({ city, weather }) {
  if (!weather) {
    return (
      <div className="weather-card">
        <h3>{city.CityName}</h3>
        <div>Loading...</div>
      </div>
    )
  }

  return (
    <div className="weather-card">
      <h3>{weather.name}</h3>
      <div className="temperature">{Math.round(weather.temp)}°C</div>
      <div className="description">{weather.description}</div>
    </div>
  )
}

export default WeatherCard