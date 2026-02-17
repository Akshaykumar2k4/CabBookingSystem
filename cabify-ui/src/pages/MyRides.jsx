import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import Logo from '../components/Logo';
import './MyRides.css';
import { ToastContainer, toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
 
const MyRides = () => {
  const navigate = useNavigate();
  const [rides, setRides] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [userName, setUserName] = useState(localStorage.getItem('userName') || '');
 
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
     
      const rawData = response.data.data || response.data || [];
     
      const sortedRides = [...rawData].sort((a, b) =>
        new Date(b.bookingTime) - new Date(a.bookingTime)
      );
 
      setRides(sortedRides);
    } catch (err) {
      console.error("Error fetching history:", err);
      if (err.response && (err.response.status === 401)) {
          toast.error("Session expired. Please login.");
          navigate('/login');
      } else {
          toast.error("Network error: Could not load ride history.");
          setError("Failed to load ride history.");
      }
    } finally {
      setLoading(false);
    }
  };
  useEffect(() => {
    // Check if there's an active ride that isn't finished yet
    const hasActiveRide = rides.some(r => r.status === 'BOOKED' || r.status === 'IN_PROGRESS');

    if (!hasActiveRide) return;

    // Start a timer to check the database every 3 seconds
    const pollInterval = setInterval(async () => {
        try {
            const token = localStorage.getItem('token');
            const response = await axios.get('http://localhost:8081/api/rides/history', {
                headers: { Authorization: `Bearer ${token}` }
            });
            
            const currentRides = response.data.data || [];
            // Find the most recent ride in the list
            const latest = currentRides.sort((a, b) => new Date(b.bookingTime) - new Date(a.bookingTime))[0];

            // 🚀 THE REDIRECT TRIGGER
            // When the driver ends the ride, the status updates to PAID on the backend
            if (latest && latest.status === 'PAID') {
    clearInterval(pollInterval);
    
    // ✅ Tell them the ride is done before moving them
    toast.success("Ride Completed! Hope you had a great trip.", {
        position: "top-right",
        autoClose: 2000
    });

    // Small delay so they see the toast
    setTimeout(() => {
        navigate('/feedback', { state: { rideId: latest.rideId } });
    }, 2000);
}
        } catch (err) {
            console.error("Simultaneous check failed:", err);
        }
    }, 3000); 

    // Clean up the interval if the user leaves the page
    return () => clearInterval(pollInterval);
}, [rides, navigate]);
  const handleLogout = () => {
    localStorage.clear();
    toast.info("Logged out successfully!", {
      position: "top-center",
      autoClose: 1500,
    });
    setTimeout(() => navigate('/login'), 1500);
  };
 
  return (
    <div className="my-rides-wrapper">
      <ToastContainer />
      <div className="top-bar">
        <div
          className="logo-section"
          onClick={() => navigate('/booking')}
          style={{cursor: 'pointer'}}
        >
          <Logo />
        </div>
       
        <div className="nav-links">
            <button className="nav-btn" onClick={() => navigate('/booking')}>New Ride</button>
            <button className="logout-btn" onClick={handleLogout}>Logout</button>
            <div
              className="profile-icon-circle"
              onClick={() => navigate('/profile')}
              title="View Profile"
              style={{
                width: '35px',
                height: '35px',
                backgroundColor: '#ffc107',
                color: 'black',
                borderRadius: '50%',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                fontWeight: 'bold',
                cursor: 'pointer',
              }}
            >
              {userName ? userName.charAt(0).toUpperCase() : 'U'}
            </div>
        </div>
      </div>
 
      <div className="rides-bg-container">
        <div className="glass-history-box">
          <h2>My Ride History</h2>
         
          {loading && <p className="loading-text">Loading your journeys...</p>}
          {error && <p className="error-text">{error}</p>}
         
          {!loading && !error && rides.length === 0 && (
            <div className="empty-state">
              <p>You haven't booked any rides yet.</p>
              <button className="confirm-btn" onClick={() => navigate('/booking')}>Book a Ride</button>
            </div>
          )}
 
          <div className="rides-scroll-list">
            {rides.map((ride) => (
              <div key={ride.rideId} className="glass-ride-card">
                <div className="card-header">
                  <span className="date">
                    📅 {new Date(ride.bookingTime).toLocaleDateString()} at {new Date(ride.bookingTime).toLocaleTimeString([], {hour: '2-digit', minute:'2-digit'})}
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
                      <strong>
                        {ride.vehicleModel && ride.vehiclePlate 
                          ? `${ride.vehicleModel} - ${ride.vehiclePlate}` 
                          : (ride.vehicleModel || ride.vehiclePlate || "N/A")}
                      </strong>
                    </div>
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
};
 
export default MyRides;
 