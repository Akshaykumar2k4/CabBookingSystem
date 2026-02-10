import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import Logo from '../components/Logo';
import './MyRides.css';

const MyRides = () => {
  const navigate = useNavigate();
  const [rides, setRides] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetchHistory();
  }, []);

  const fetchHistory = async () => {
    const token = localStorage.getItem('token');
    if (!token) {
      navigate('/login');
      return;
    }

    try {
      const response = await axios.get('http://localhost:8081/api/rides/history', {
        headers: { Authorization: `Bearer ${token}` }
      });
      setRides(response.data.data || response.data || []);
    } catch (err) {
      console.error("Error fetching history:", err);
      if (err.response && (err.response.status === 401 || err.response.status === 403)) {
          navigate('/login');
      } else {
          setError("Failed to load ride history.");
      }
    } finally {
      setLoading(false);
    }
  };

  const handleEndRide = async (rideId) => {
    if (!window.confirm("Are you sure you want to end this ride?")) return;
    try {
      const token = localStorage.getItem('token');
      // Use PUT and the correct URL structure
      await axios.put(`http://localhost:8081/api/rides/${rideId}/end`, {}, {
        headers: { Authorization: `Bearer ${token}` }
      });
      alert("Ride Completed Successfully! ✅");
      fetchHistory(); 
    } catch (err) {
      console.error(err);
      alert(err.response?.data?.message || "Failed to end ride.");
    }
  };

  const handleLogout = () => {
    if (window.confirm("Are you sure you want to logout?")) {
      localStorage.removeItem('token');
      localStorage.removeItem('email');
      navigate('/login');
    }
  };

  return (
    <div className="my-rides-wrapper">
      
      {/* 1. TOP BAR (Matches Login Page) */}
      <div className="top-bar">
        <div className="logo-section" onClick={() => navigate('/')} style={{cursor: 'pointer'}}>
          <Logo />
        </div>
        
        <div className="nav-links">
            <button className="logout-btn" onClick={handleLogout}>Logout</button>
          </div>
        {/* Contact Info Section */}
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

      {/* 2. MAIN CONTENT AREA (Background Image) */}
      <div className="rides-bg-container">
        
        {/* 3. GLASS BOX (Holds the list) */}
        <div className="glass-history-box">
          <h2>My Ride History</h2>
          
          {loading && <p className="loading-text">Loading your journeys...</p>}
          {error && <p className="error-text">{error}</p>}
          
          {!loading && !error && rides.length === 0 && (
            <div className="empty-state">
              <p>You haven't booked any rides yet.</p>
              <button onClick={() => navigate('/booking')}>Book a Ride</button>
            </div>
          )}

          <div className="rides-scroll-list">
            {rides.map((ride) => (
              <div key={ride.rideId} className="glass-ride-card">
                <div className="card-header">
                  <span className="date">
                    📅 {new Date(ride.bookingTime).toLocaleDateString()}
                  </span>
                  <span className={`status-badge ${ride.status.toLowerCase()}`}>
                    {ride.status}
                  </span>
                </div>

                <div className="route-row">
                  <div className="route-point">
                    <span className="dot source">🟢</span>
                    <span>{ride.source}</span>
                  </div>
                  <div className="route-arrow">➝</div>
                  <div className="route-point">
                    <span className="dot dest">🔴</span>
                    <span>{ride.destination}</span>
                  </div>
                </div>

                <div className="info-row">
                  <div className="info-item">
                    <small>Fare</small>
                    <strong>₹{ride.fare}</strong>
                  </div>
                  <div className="info-item">
                    <small>Driver</small>
                    <strong>{ride.driverName || "Assigning..."}</strong>
                  </div>
                  <div className="info-item">
                    <small>Vehicle</small>
                    <strong>{ride.vehicleDetails || "N/A"}</strong>
                  </div>
                </div>

                {/* End Ride Button */}
                {(ride.status === 'BOOKED' || ride.status === 'IN_PROGRESS') && (
                  <button 
                    className="glass-end-btn"
                    onClick={() => handleEndRide(ride.rideId)}
                  >
                    End Ride
                  </button>
                )}
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
};

export default MyRides;