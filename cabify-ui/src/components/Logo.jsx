import React from 'react';

const Logo = ({ theme }) => {
  // Logic to determine colors based on the theme
  const isDriver = theme === 'driver';
  const primaryColor = isDriver ? '#00d2ff' : '#FFCC00'; // Blue for Driver, Yellow for Rider
  const textColor = isDriver ? '#f8feffff' : '#fffcfcff'; // Blue text for Driver, White for Rider

  return (
    <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
      {/* 1. The Aesthetic Icon */}
      <svg 
        width="45" 
        height="45" 
        viewBox="0 0 100 100" 
        fill="none" 
        xmlns="http://www.w3.org/2000/svg"
        style={{ transition: '0.3s' }} // Smooth color transition
      >
        {/* Dynamic Squircle Background */}
        <rect width="100" height="100" rx="25" fill={primaryColor} />
        
        {/* Minimalist 'C' / Route Path in Black */}
        <path 
          fillRule="evenodd" 
          clipRule="evenodd" 
          d="M65 35C65 29.4772 60.5228 25 55 25H45C33.9543 25 25 33.9543 25 45V55C25 66.0457 33.9543 75 45 75H55C60.5228 75 65 70.5228 65 65H55C49.4772 65 45 60.5228 45 55V45C45 39.4772 49.4772 35 55 35H65ZM75 35V65C75 76.0457 66.0457 85 55 85H45C28.4315 85 15 71.5685 15 55V45C15 28.4315 28.4315 15 45 15H55C66.0457 15 75 23.9543 75 35Z" 
          fill="#1A1A1A"
        />
        {/* A subtle 'Location Dot' accent */}
        <circle cx="68" cy="32" r="6" fill="#1A1A1A" />
      </svg>
      
      {/* 2. The Clean Typography */}
      <span style={{ 
        fontSize: '26px', 
        fontWeight: '700', 
        color: textColor, 
        letterSpacing: '-0.5px', 
        fontFamily: "'Inter', 'Segoe UI', sans-serif",
        transition: '0.3s'
      }}>
        cabify.
      </span>
    </div>
  );
};

export default Logo;