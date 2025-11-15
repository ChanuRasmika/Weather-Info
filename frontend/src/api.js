const API_BASE = 'http://localhost:8080/api';

export const api = {
  async login(email, password) {
    const response = await fetch(`${API_BASE}/auth/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email, password })
    });
    return response.json();
  },

  async verifyMfa(email, code) {
    const response = await fetch(`${API_BASE}/mfa/verify`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email, code })
    });
    return response.json();
  },

  async getCities() {
    const response = await fetch(`${API_BASE}/weather/cities`);
    return response.json();
  },

  async getWeather(cityCode) {
    const response = await fetch(`${API_BASE}/weather/${cityCode}`);
    return response.json();
  }
};