import { useState, useEffect } from 'react'
import WeatherCard from './WeatherCard'
import { api } from '../api'

function WeatherDashboard({ cities, onLogout }) {
  const [weatherData, setWeatherData] = useState(new Map())

  useEffect(() => {
    loadAllWeatherData()
  }, [cities])

  const loadAllWeatherData = async () => {
    for (const city of cities) {
      try {
        const result = await api.getWeather(city.CityCode)
        if (result.success) {
          setWeatherData(prev => new Map(prev.set(city.CityCode, result.data)))
        }
      } catch (error) {
        console.error(`Failed to load weather for ${city.CityName}:`, error)
      }
    }
  }

  return (
    <div className="weather-container">
      <button className="logout-btn" onClick={onLogout}>
        Logout
      </button>
      <h1>Weather Dashboard</h1>
      <div className="weather-grid">
        {cities.map(city => (
          <WeatherCard
            key={city.CityCode}
            city={city}
            weather={weatherData.get(city.CityCode)}
          />
        ))}
      </div>
    </div>
  )
}

export default WeatherDashboard