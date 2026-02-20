import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import axios from 'axios';
import Logo from '../components/Logo';
import './DriverRegister.css';
import { ToastContainer, toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';

const DriverRegister = () => {
  const navigate = useNavigate();
  
  const [formData, setFormData] = useState({
    name: '',
    phone: '',
    email: '',
    password: '',
    licenseNumber: '',
    vehicleModel: '',
    vehiclePlate: ''
  });

  const handleChange = (e) => {
    const { name, value } = e.target;
    let finalValue = value;

    if (name === 'phone') {
        finalValue = value.replace(/[^0-9]/g, '').slice(0, 10);
    } else if (name === 'licenseNumber' || name === 'vehicleModel' || name === 'vehiclePlate') {
        finalValue = value.toUpperCase();
    }
    setFormData((prev) => ({ 
        ...prev, 
        [name]: finalValue 
    }));
};

  const handleRegister = async (e) => {
    e.preventDefault();

    const payload = {
        name: formData.name,
        phone: formData.phone,
        email: formData.email,
        password: formData.password,
        licenseNumber: formData.licenseNumber,
        vehicleModel: formData.vehicleModel,
        vehiclePlate: formData.vehiclePlate  
    };

    try {
        await axios.post("http://localhost:8081/api/drivers/register", payload);
        
        toast.success("Success! Welcome to the Cabify Fleet. 🚕", {
            position: "top-right",
            autoClose: 2000,
        });

        // Redirect after the user has time to read the success toast
        setTimeout(() => navigate("/driver-login"), 2000);

    } catch (error) {
        console.error("🔥 Registration Error:", error.response);

        // Logic to extract specific error reasons (Duplicate Plate, Email, etc.)
        let errorMessage = error.response?.data?.message;

        if (!errorMessage && error.response?.data) {
            // If backend sends validation object { license: "Already exists" }
            if (typeof error.response.data === 'object') {
                errorMessage = Object.values(error.response.data).join(", ");
            } else {
                errorMessage = error.response.data;
            }
        }

        errorMessage = errorMessage || "Registration Failed. Please check your details.";

        toast.error(`❌ ${errorMessage}`, {
            position: "top-right",
            autoClose: 5000,
        });
    }
};

  return (
    <div className="driver-login-page-wrapper">
      <ToastContainer />
      
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
                      <input 
                          type="text" 
                          name="name" 
                          value={formData.name} /* Added value binding */
                          placeholder="John Doe" 
                          onChange={handleChange} 
                          required 
                      />
                  </div>

                  <div className="driver-form-grid">
                      <div className="form-group">
                          <label>Phone Number</label>
                          <input 
                              type="tel" /* Changed to tel */
                              name="phone" 
                              value={formData.phone} /* Added value binding */
                              placeholder="9876543210" 
                              onChange={handleChange} 
                              pattern="[0-9]{10}" 
                              title="Please enter exactly 10 digits"
                              required 
                          />
                      </div>
                      <div className="form-group">
                          <label>License Number</label>
                          <input 
                              type="text" 
                              name="licenseNumber" 
                              value={formData.licenseNumber} 
                              placeholder="DL-123456" 
                              onChange={handleChange} 
                              pattern="^[A-Z]{2}-\d{6,}$" 
                              title="Expected format: DL-123456 (2 letters, hyphen, 6+ digits)"
                              required 
                          />
                      </div>
                  </div>

                  <div className="driver-form-grid">
                      <div className="form-group">
                          <label>Vehicle Model</label>
                          <input 
                              type="text" 
                              name="vehicleModel" 
                              value={formData.vehicleModel} 
                              placeholder="Toyota Prius" 
                              onChange={handleChange} 
                              required 
                          />
                      </div>
                      <div className="form-group">
                          <label>Vehicle Plate</label>
                          <input 
                              type="text" 
                              name="vehiclePlate" 
                              value={formData.vehiclePlate} 
                              placeholder="KA-05-MX-1234" 
                              onChange={handleChange} 
                              pattern="^[A-Z]{2}-\d{2}-[A-Z]{1,2}-\d{4}$" 
                              title="Expected format: KA-05-MX-1234"
                              required 
                          />
                      </div>
                  </div>

                  <div className="form-group">
                      <label>Email </label>
                      <input 
                          type="email" 
                          name="email" 
                          value={formData.email} /* Added value binding */
                          placeholder="driver@cabify.com" 
                          onChange={handleChange} 
                          pattern="^[a-zA-Z0-9._%+\-]+@[a-zA-Z0-9.\-]+\.[a-zA-Z]{2,}$" 
                          title="Please include a '.' and a valid domain extension (e.g., .com)"
                          required 
                      />
                  </div>

                  <div className="form-group">
                      <label>Password</label>
                      <input 
                          type="password" 
                          name="password" 
                          value={formData.password} /* Added value binding */
                          placeholder="••••••••" 
                          onChange={handleChange} 
                          pattern="^(?=.*[a-z])(?=.*[A-Z])(?=.*\d).{8,}$" 
                          title="Must be at least 8 characters long with 1 uppercase letter, 1 lowercase letter, and 1 number"
                          required 
                      />
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