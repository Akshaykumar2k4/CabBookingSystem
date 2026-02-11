import React from 'react';
import { Link } from 'react-router-dom';
import './Home.css';
import Logo from '../components/Logo';

const Home = () => {
  return (
    <div className="main-wrapper">
      
      {/* 1. TOP INFO BAR */}
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

      {/* 2. SPLIT HERO SECTION */}
      <div className="split-hero">
        
        {/* RIDER SIDE (Left) */}
        <div className="hero-half rider-side">
          <div className="hero-content">
            <p className="subtitle">Need a ride?</p>
            <h1 className="main-title">Premium Travel <br /> At Your Doorstep.</h1>
            <p className="description">
              Secure, fast, and comfortable rides with top-rated drivers.
            </p>
            <Link to="/login" className="book-btn rider-btn">
              Ride with Cabify
            </Link>
          </div>
          
        </div>

        {/* DRIVER SIDE (Right) */}
        <div className="hero-half driver-side">
          <div className="hero-content">
            <p className="subtitle secondary">Want to earn?</p>
            <h1 className="main-title">Drive with Us & Be Your Own Boss.</h1>
            <p className="description">
              Flexible hours, instant payouts, and 24/7 partner support.
            </p>
            <Link to="/driver-login" className="book-btn driver-btn">
              Become a Partner
            </Link>
          </div>
    
        </div>

      </div>
    </div>
  );
};

export default Home;