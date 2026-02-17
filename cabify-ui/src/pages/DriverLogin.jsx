import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import axios from 'axios';
import Logo from '../components/Logo';
import './DriverLogin.css';

const DriverLogin = () => {
  const navigate = useNavigate();
  
  const [formData, setFormData] = useState({
    email: '',
    password: ''
  });

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleLogin = async (e) => {
    e.preventDefault();
    try {
      const response = await axios.post('http://localhost:8081/api/drivers/login', formData);
      
      // 🚀 THE FIX: Extracting data from your SuccessResponse structure
      // response.data.data contains the DriverLoginResponseDto {token, driver}
      const loginData = response.data.data; 
      const token = loginData?.token;
      const driverInfo = loginData?.driver;

      if (token && driverInfo) {
          // Save keys specifically for the driver portal
          localStorage.setItem('driverToken', token);
          localStorage.setItem('driverInfo', JSON.stringify(driverInfo)); 
          localStorage.setItem('driverEmail', formData.email); 
          navigate('/driver-dashboard'); // 🚀 This will now trigger the redirect
      } else {
          alert("Login Failed: Backend did not return a valid token.");
      }

    } catch (error) {
      console.error("Driver Login Error:", error);
      alert("Login Failed: Incorrect Email or Password");
    }
  };

  return (
    <div className="driver-login-page-wrapper">
      {/* 1. THE TOP BAR - UNCHANGED AS PER YOUR REQUEST */}
      <div className="top-bar driver-theme">
        <div className="logo-section">
          <Logo theme="driver" />
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

      {/* 2. THE MAIN LOGIN AREA */}
      <div className="login-container">
        <div className="login-box">
            <h2>Welcome Back, Partner!</h2>
            <p>Enter your driver credentials to login</p>
            
            <form onSubmit={handleLogin}>
                <div className="form-group">
                    <label>Driver Email</label>
                    <input 
                      type="email" 
                      name="email"
                      placeholder="Enter your registered email" 
                      value={formData.email}
                      onChange={handleChange}
                      required
                    />
                </div>

                <div className="form-group">
                    <label>Password</label>
                    <input 
                      type="password" 
                      name="password"
                      placeholder="Enter your password" 
                      value={formData.password}
                      onChange={handleChange}
                      required
                    />
                </div>

                <button type="submit" className="login-btn">LOG IN TO DASHBOARD</button>
            </form>

            <p className="redirect-text">
                Not a partner yet? <Link to="/driver-register">Join the Fleet</Link>
            </p>
        </div>
      </div>
    </div>
  )
}

export default DriverLogin;