import React from 'react';
import { Link } from 'react-router-dom';
import './Home.css';
import Logo from '../components/Logo';

const Home = () => {
  return (
    <div className="main-wrapper">
      
      {/* 1. THE TOP INFO BAR (White Section) */}
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

      {/* 2. THE HERO SECTION (Dark Background + Car) */}
      <div className="hero-section">
        
        {/* Left Side: Text */}
        <div className="hero-text">
          <p className="subtitle">Travel securely with us!</p>
          <h1 className="main-title">Premium Rides at <br /> Pocket-Friendly Prices.</h1>
          <p className="description">
            Experience the ultimate comfort with our verified drivers. 
            Zero delays, 100% safety, and instant booking confirmation.
          </p>
          <Link to="/login" className="book-btn">
            Book Now & Save 20%
          </Link>
        </div>

        {/* Right Side: Car Image */}
        <div className="hero-image-container">
            <img 
              src="https://upload.wikimedia.org/wikipedia/commons/thumb/e/e9/Yellow_taxi_cab.svg/1200px-Yellow_taxi_cab.svg.png" 
              alt="Yellow Taxi" 
              className="taxi-img" 
            />
        </div>

      </div>
    </div>
  );
};

export default Home;