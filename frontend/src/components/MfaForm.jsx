import { useState } from 'react'

function MfaForm({ onVerify }) {
  const [code, setCode] = useState('')
  const [message, setMessage] = useState('')
  const [loading, setLoading] = useState(false)

  const handleSubmit = async (e) => {
    e.preventDefault()
    setLoading(true)
    setMessage('')
    
    try {
      const result = await onVerify(code)
      if (!result.success) {
        setMessage({ type: 'error', text: result.message })
      }
    } catch (error) {
      setMessage({ type: 'error', text: 'Verification failed' })
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="container">
      <h2>Enter Verification Code</h2>
      <p>Check your email for the verification code</p>
      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label>Verification Code:</label>
          <input
            type="text"
            value={code}
            onChange={(e) => setCode(e.target.value)}
            maxLength="6"
            required
          />
        </div>
        <button type="submit" disabled={loading}>
          {loading ? 'Verifying...' : 'Verify'}
        </button>
        {message && (
          <div className={message.type}>
            {message.text}
          </div>
        )}
      </form>
    </div>
  )
}

export default MfaForm