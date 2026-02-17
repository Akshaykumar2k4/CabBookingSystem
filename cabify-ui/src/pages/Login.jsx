import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import axios from 'axios';
import Logo from '../components/Logo';
import './Login.css';
import { ToastContainer, toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';

const Login = () => {
  const navigate = useNavigate();
  
  // State handles Email & Password
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
      // 1. Call Backend
      const response = await axios.post('http://localhost:8081/api/users/login', formData);
      
      console.log("🔥 Backend Response:", response.data);

      // 2. Smart Token Finder (Handles different backend structures)
      const token = response.data.token || response.data.jwt || (response.data.data && response.data.data.token);

      if (token) {
          console.log("Login Success! Token:", token);
          
          // 3. CRITICAL: Save Token AND Email
          localStorage.setItem('token', token);
          localStorage.setItem('email', formData.email); // Booking page needs this!
          
          toast.success("Login Successful! Redirecting...", {
            position: "top-right",
            autoClose: 1500, 
          });
          setTimeout(() => {
          navigate('/booking');
          }, 1500); 
      } else {
          console.error("Token missing in response:", response.data);
          toast.error("Login Failed: Token not found.");
      }

    } catch (error) {
      console.error("Login Error:", error);
      toast.error("Invalid Email or Password. Please try again.");
    }
  };

  return (
    <div className="user-login-page-wrapper">
      <ToastContainer />
      {/* 1. THE TOP BAR */}
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

      {/* 2. THE MAIN LOGIN AREA */}
      <div className="login-container">
        <div className="login-box">
            <h2>Welcome Back!</h2>
            <p>Enter your details to login</p>
            
            <form onSubmit={handleLogin}>
                <div className="form-group">
                    <label>Email Address</label>
                    <input 
                      type="email" 
                      name="email"
                      placeholder="Enter your email" 
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

                <button type="submit" className="login-btn">LOGIN</button>
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