import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import axios from 'axios';
import Logo from '../components/Logo';
import './DriverLogin.css';
import { ToastContainer, toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';

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
      
      const loginData = response.data.data; 
      const token = loginData?.token;
      const driverInfo = loginData?.driver;

      if (token && driverInfo) {
          localStorage.setItem('driverToken', token);
          localStorage.setItem('driverInfo', JSON.stringify(driverInfo)); 
          localStorage.setItem('driverEmail', formData.email); 

          toast.success(`Welcome back, ${driverInfo.name}! 🚕`, {
            position: "top-right",
            autoClose: 1500,
          });

          setTimeout(() => navigate('/driver-dashboard'), 1500);
      } else {
          toast.error("Login Error: Server returned an invalid response.");
      }

    } catch (error) {
      console.error("Driver Login Error:", error);
      // ✅ Removed alert and added toast for consistency with User side
      const errorMessage = error.response?.data?.message || "Invalid Email or Password";
      toast.error(`❌ ${errorMessage}`, {
        position: "top-right",
        autoClose: 3000,
      });
    }
  };

  return (
    <div className="driver-login-page-wrapper">
      {/* ✅ Necessary Change: ToastContainer added to handle notifications */}
      <ToastContainer />

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