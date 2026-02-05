import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import axios from 'axios'; // Import Axios
import Logo from '../components/Logo';
import './Login.css';

const Login = () => {
  const navigate = useNavigate(); // Hook to move user
  
  // Changed 'username' to 'email' to match your Java Backend
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);

  const handleLogin = async (e) => {
    e.preventDefault();
    setLoading(true);

    try {
      // 1. Send Login Request
      const response = await axios.post('http://localhost:8081/api/users/login', {
        email: email,       // Sending email
        password: password  // Sending password
      });

      // 2. Handle Success
      console.log("Login Success:", response.data);
      
      // 3. Save the JWT Token (The "Key Card")
      // Your backend returns { "jwt": "eyJhbGciOi..." }
      const token = response.data.jwt; 
      localStorage.setItem('token', token);
      
      alert("Login Successful! Redirecting...");
      
      // 4. Redirect to Home Page
      navigate('/');

    } catch (error) {
      console.error("Login Error:", error);
      // Handle "Bad Credentials" or server errors
      if (error.response && error.response.status === 403) {
        alert("Invalid Email or Password. Please try again.");
      } else {
        alert("Login failed. Is the backend running?");
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="login-page-wrapper">
      
      {/* Top Bar */}
      <div className="top-bar">
        <div className="logo-section">
          <Logo />
        </div>

        <div className="contact-info">
          <div className="contact-item">
            <span className="icon">📞</span>
            <div>
              <p className="contact-label">Call Us Now</p>
              <p className="contact-value">0413-225356</p>
            </div>
          </div>

          <div className="contact-item">
            <span className="icon">✉️</span>
            <div>
              <p className="contact-label">Email Now</p>
              <p className="contact-value">info.cabify@gmail.com</p>
            </div>
          </div>

          <div className="contact-item">
            <span className="icon">📍</span>
            <div>
              <p className="contact-label">Location</p>
              <p className="contact-value">Chennai, TamilNadu</p>
            </div>
          </div>
        </div>
      </div>
      {/* Main Login Area */}
      <div className="login-container">
        <div className="login-box">
            <h2>Welcome Back</h2>
            <p>Enter your details to login</p>
            
            <form onSubmit={handleLogin}>
                <div className="form-group">
                    <label>Email Address</label>
                    {/* Updated Input to type="email" */}
                    <input 
                      type="email" 
                      placeholder="john@mail.com" 
                      value={email}
                      onChange={(e) => setEmail(e.target.value)}
                      required
                    />
                </div>

                <div className="form-group">
                    <label>Password</label>
                    <input 
                      type="password" 
                      placeholder="Password" 
                      value={password}
                      onChange={(e) => setPassword(e.target.value)}
                      required
                    />
                </div>

                <button type="submit" className="login-btn" disabled={loading}>
                  {loading ? "LOGGING IN..." : "LOGIN"}
                </button>
            </form>

            <p className="redirect-text">
                New User? <Link to="/register">Create Account</Link>
            </p>
        </div>
      </div>
    </div>
  )
}

export default Login;