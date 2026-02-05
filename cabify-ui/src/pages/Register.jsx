import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom'; // Import useNavigate to redirect
import axios from 'axios'; // Import Axios
import Logo from '../components/Logo';
import './Login.css';

const Register = () => {
  const navigate = useNavigate(); // Hook to move user to Login page after success

  const [formData, setFormData] = useState({
    name: '',
    email: '',
    phone: '',
    password: ''
  });

  const [loading, setLoading] = useState(false); // To show spinner/disable button

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true); // Disable button

    try {
      // 1. Send Data to Spring Boot (Port 8081)
      const response = await axios.post('http://localhost:8081/api/users/register', {
        name: formData.name,
        email: formData.email,
        phone: Number(formData.phone), // Ensure phone is a number (Long in Java)
        password: formData.password
      });

      // 2. Handle Success
      console.log("Registration Success:", response.data);
      alert("Registration Successful! Please Login.");
      
      // 3. Redirect to Login Page
      navigate('/login');

    } catch (error) {
      // 4. Handle Error
      console.error("Registration Error:", error);
      if (error.response) {
        // Server responded with a status code (like 400 or 500)
        alert(`Error: ${error.response.data.message || "Registration Failed"}`);
      } else {
        // Network error (Server is down)
        alert("Server is not responding. Is Spring Boot running on 8081?");
      }
    } finally {
      setLoading(false); // Re-enable button
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
      {/* Main Form */}
      <div className="login-container">
        <div className="login-box">
            <h2>Create Account</h2>
            <p>Join our community today</p>

            <form onSubmit={handleSubmit}>
                <div className="form-group">
                    <label>Full Name</label>
                    <input 
                      type="text" 
                      name="name" 
                      value={formData.name} 
                      onChange={handleChange} 
                      placeholder="John Doe" 
                      required 
                    />
                </div>
                <div className="form-group">
                    <label>Email</label>
                    <input 
                      type="email" 
                      name="email" 
                      value={formData.email} 
                      onChange={handleChange} 
                      placeholder="john@mail.com" 
                      required 
                    />
                </div>
                <div className="form-group">
                    <label>Phone</label>
                    <input 
                      type="number" 
                      name="phone" 
                      value={formData.phone} 
                      onChange={handleChange} 
                      placeholder="9876543210" 
                      required 
                    />
                </div>
                <div className="form-group">
                    <label>Password</label>
                    <input 
                      type="password" 
                      name="password" 
                      value={formData.password} 
                      onChange={handleChange} 
                      placeholder="•••••••" 
                      required 
                    />
                </div>

                <button type="submit" className="login-btn" disabled={loading}>
                  {loading ? "REGISTERING..." : "SIGN UP"}
                </button>
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