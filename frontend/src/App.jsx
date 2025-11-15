import { useState, useEffect } from 'react'
import LoginForm from './components/LoginForm'
import MfaForm from './components/MfaForm'
import WeatherDashboard from './components/WeatherDashboard'
import { api } from './api'
import './style.css'

function App() {
  const [currentUser, setCurrentUser] = useState(null)
  const [showMfa, setShowMfa] = useState(false)
  const [cities, setCities] = useState([])

  useEffect(() => {
    const token = localStorage.getItem('token')
    if (token) {
      loadCities()
    }
  }, [])

  const handleLogin = async (email, password) => {
    const result = await api.login(email, password)
    
    if (result.success && result.data.mfaRequired) {
      setCurrentUser({ email })
      setShowMfa(true)
      return { success: true, message: 'Verification code sent to your email' }
    } else if (result.success && !result.data.mfaRequired) {
      setCurrentUser(result.data.loginResponse.user)
      localStorage.setItem('token', result.data.loginResponse.accessToken)
      await loadCities()
      return { success: true }
    }
    return { success: false, message: 'Login failed' }
  }

  const handleMfaVerify = async (code) => {
    const result = await api.verifyMfa(currentUser.email, code)
    
    if (result.success) {
      setCurrentUser(result.data.user)
      localStorage.setItem('token', result.data.accessToken)
      setShowMfa(false)
      await loadCities()
      return { success: true }
    }
    return { success: false, message: 'Invalid verification code' }
  }

  const loadCities = async () => {
    const result = await api.getCities()
    if (result.success) {
      setCities(result.data)
    }
  }

  const handleLogout = () => {
    localStorage.removeItem('token')
    setCurrentUser(null)
    setShowMfa(false)
    setCities([])
  }

  if (cities.length > 0) {
    return <WeatherDashboard cities={cities} onLogout={handleLogout} />
  }

  if (showMfa) {
    return <MfaForm onVerify={handleMfaVerify} />
  }

  return <LoginForm onLogin={handleLogin} />
}

export default App