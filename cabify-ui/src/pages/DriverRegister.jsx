import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import axios from 'axios';
import Logo from '../components/Logo';
import './DriverRegister.css';

const DriverRegister = () => {
  const navigate = useNavigate();
  
  const [formData, setFormData] = useState({
    name: '',
    phone: '',
    email: '', // Add to Entity later
    password: '', // Add to Entity later
    licenseNumber: '',
    vehicleModel: '', // Temporary local state
    vehiclePlate: ''  // Temporary local state
  });

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleRegister = async (e) => {
    e.preventDefault();
    
    // 🚀 THE FIX: These keys MUST match the DriverDto exactly
    const payload = {
        name: formData.name,
        phone: formData.phone,
        licenseNumber: formData.licenseNumber,
        vehicleDetails: `${formData.vehicleModel} - ${formData.vehiclePlate}`,
        email: formData.email,      // Make sure this isn't 'emailAddress' or 'userEmail'
        password: formData.password  // Make sure this isn't 'pass' or 'pwd'
    };

    try {
        const response = await axios.post("http://localhost:8081/api/drivers/register", payload);
        alert("Success! You are now a Cabify Partner.");
        navigate("/driver-login");
    } catch (error) {
        // This is what you just showed me in the console
        console.error("🔥 Validation Error Details:", error.response?.data);
        alert("Registration Failed: " + JSON.stringify(error.response?.data));
    }
};

  return (
    <div className="driver-login-page-wrapper">
      <div className="top-bar driver-theme">
        <div className="logo-section"><Logo theme="driver" /></div>
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

      <div className="login-container">
        <div className="login-box registration-box">
            <h2>Join the Fleet</h2>
            <p>Register as a partner to start earning</p>
            
            <form onSubmit={handleRegister}>
                <div className="form-group">
                    <label>Full Name</label>
                    <input type="text" name="name" placeholder="John Doe" onChange={handleChange} required />
                </div>

                <div className="driver-form-grid">
                  <div className="form-group">
                      <label>Phone Number</label>
                      <input type="text" name="phone" placeholder="9876543210" onChange={handleChange} required />
                  </div>
                  <div className="form-group">
                      <label>License Number</label>
                      <input type="text" name="licenseNumber" placeholder="DL-123456" onChange={handleChange} required />
                  </div>
                </div>

                <div className="driver-form-grid">
                  <div className="form-group">
                      <label>Vehicle Model</label>
                      <input type="text" name="vehicleModel" placeholder="Toyota Prius" onChange={handleChange} required />
                  </div>
                  <div className="form-group">
                      <label>Vehicle Plate</label>
                      <input type="text" name="vehiclePlate" placeholder="KA-05-MX-1234" onChange={handleChange} required />
                  </div>
                </div>

                <div className="form-group">
                    <label>Email (For Login)</label>
                    <input type="email" name="email" placeholder="driver@cabify.com" onChange={handleChange} required />
                </div>

                <div className="form-group">
                    <label>Password</label>
                    <input type="password" name="password" placeholder="••••••••" onChange={handleChange} required />
                </div>

                <button type="submit" className="login-btn">REGISTER AS DRIVER</button>
            </form>

            <p className="redirect-text">
                Already a partner? <Link to="/driver-login">Login here</Link>
            </p>
        </div>
      </div>
    </div>
  );
};

export default DriverRegister;