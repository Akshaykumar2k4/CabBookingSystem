import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom'; // Import useNavigate to redirect
import axios from 'axios'; // Import Axios
import Logo from '../components/Logo';
import './Login.css';
import { ToastContainer, toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
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
    const { name, value } = e.target;
    let finalValue = value;

    if (name === 'phone') {
        // 🚀 THE FIX: Filter non-numbers AND limit to 10 digits in one go
        finalValue = value.replace(/[^0-9]/g, '').slice(0, 10);
    }

    // Call setFormData exactly ONCE regardless of the field name
    setFormData((prev) => ({ 
        ...prev, 
        [name]: finalValue 
    }));
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
      toast.success("Registration Successful! Redirecting to login...", {
        position: "top-right",
        autoClose: 2000,
      });

      // Wait 2 seconds so they can read the toast before redirecting
      setTimeout(() => navigate('/login'), 2000);

    } catch (error) {
      console.error("Registration Error:", error);
      
      if (error.response) {
        const errorMsg = typeof error.response.data === 'string' 
                         ? error.response.data 
                         : (error.response.data.message || "Registration Failed.");
        
        toast.error(errorMsg);
      } else {
        toast.error("Server is not responding. Please try again later.");
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="user-login-page-wrapper">
      <ToastContainer />
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
                    placeholder="name@gmail.com"
                    pattern="^[a-zA-Z0-9._%+\-]+@[a-zA-Z0-9.\-]+\.[a-zA-Z]{2,}$"
                    /* 🚀 THIS TEXT will help the user understand why the popup appeared */
                    title="Email must include a dot and a domain extension (e.g., .com)"
                    required 
                  />
                </div>
                <div className="form-group">
                    <label>Phone</label>
                    <input 
                      type="tel"              
                      name="phone" 
                      value={formData.phone}
                      onChange={handleChange}
                      placeholder="9876543210"
                      pattern="[0-9]{10}"        
                      title="Please enter a valid 10-digit phone number"
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
                    /* 🚀 THE FIX: Requires 1 uppercase, 1 lowercase, 1 number, and min 8 chars */
                    pattern="^(?=.*[a-z])(?=.*[A-Z])(?=.*\d).{8,}$"
                    title="Password must be at least 8 characters long and include one uppercase letter, one lowercase letter, and one number."
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