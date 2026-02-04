import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import Logo from '../components/Logo'; // Import Logo
import './Login.css'; // Re-use the new styles!

const Register = () => {
  const [formData, setFormData] = useState({
    name: '', email: '', phone: '', password: ''
  });

  const handleChange = (e) => setFormData({ ...formData, [e.target.name]: e.target.value });

  return (
    <div className="login-page-wrapper">
      
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

      {/* 2. THE MAIN REGISTER AREA */}
      <div className="login-container">
        <div className="login-box">
            <h2>Create Account</h2>
            <p>Join our community today</p>

            <form>
                <div className="form-group">
                    <label>Full Name</label>
                    <input type="text" name="name" onChange={handleChange} placeholder="John Doe" />
                </div>
                <div className="form-group">
                    <label>Email</label>
                    <input type="email" name="email" onChange={handleChange} placeholder="john@mail.com" />
                </div>
                <div className="form-group">
                    <label>Phone</label>
                    <input type="tel" name="phone" onChange={handleChange} placeholder="1234567890" />
                </div>
                <div className="form-group">
                    <label>Password</label>
                    <input type="password" name="password" onChange={handleChange} placeholder="•••••••" />
                </div>

                <button type="submit" className="login-btn">SIGN UP</button>
            </form>

            <p className="redirect-text">
                Already member? <Link to="/login">Login Here</Link>
            </p>
        </div>
      </div>
    </div>
  );
};

export default Register;