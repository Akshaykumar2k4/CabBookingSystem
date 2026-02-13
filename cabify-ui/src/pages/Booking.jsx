import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import Logo from '../components/Logo';
import './Booking.css';

const Booking = () => {
  const navigate = useNavigate();
  
  const [source, setSource] = useState('');
  const [destination, setDestination] = useState('');
  const [estimatedFare, setEstimatedFare] = useState(0);
  const [loading, setLoading] = useState(false);
  const [locations, setLocations] = useState([]);
  
  const [currentUserId, setCurrentUserId] = useState(null); 
  const [userName, setUserName] = useState(''); 

  useEffect(() => {
    fetchLocations();
    identifyUser(); 
  }, []);

  useEffect(() => {
    if (source && destination && source !== destination) {
        getFareEstimate();
    } else {
        setEstimatedFare(0);
    }
  }, [source, destination]);

  const getFareEstimate = async () => {
    try {
        const token = localStorage.getItem('token');
        const response = await axios.get(`http://localhost:8081/api/rides/estimate`, {
            params: { source, destination },
            headers: { Authorization: `Bearer ${token}` }
        });
        setEstimatedFare(response.data.data || response.data); 
    } catch (error) {
        setEstimatedFare(0);
    }
  };

  const identifyUser = async () => {
    const token = localStorage.getItem('token');
    const email = localStorage.getItem('email'); 

    if (!email || !token) {
        alert("Session expired. Please login again.");
        navigate('/login');
        return;
    }

    try {
        const response = await axios.get('http://localhost:8081/api/users/profile', {
            headers: { Authorization: `Bearer ${token}` }
        });

        const myUser = response.data.find(u => u.email === email);

        if (myUser) {
            const realId = myUser.id || myUser.userId; 
            setCurrentUserId(realId);
            setUserName(myUser.name); 
            localStorage.setItem('userName', myUser.name);
            localStorage.setItem('userId', realId);
        }
    } catch (error) {
        console.error("Error fetching user identity:", error);
    }
  };

  const fetchLocations = async () => {
    try {
        const token = localStorage.getItem('token');
        const response = await axios.get('http://localhost:8081/api/rides/locations', {
            headers: { Authorization: `Bearer ${token}` }
        });
        setLocations(response.data.data || response.data || []);
    } catch (error) {
        console.error("Error fetching locations", error);
        setLocations(["Adyar", "AnnaNagar", "Guindy", "Marina", "Sholinganallur", "Tambaram", "TNagar", "Velachery"]);
    }
  };

  const handleBookRide = async () => {
    if (!source || !destination) { alert("Please select locations"); return; }
    
    if (!currentUserId) {
        alert("Still loading your profile... Please wait a moment.");
        return;
    }

    setLoading(true);
    const token = localStorage.getItem('token');

    try {
        const response = await axios.post('http://localhost:8081/api/rides/book', {
            source: source,
            destination: destination,
            userId: currentUserId 
        }, {
            headers: { Authorization: `Bearer ${token}` }
        });

        const responseData = response.data.data || response.data;
        const finalFare = responseData.fare;
        alert(`Ride Confirmed!\nAmount: ₹${finalFare}\nDriver: ${responseData.driverName}`);
        navigate('/my-rides'); 

    } catch (error) {
        console.error("Booking Error:", error);
        alert(error.response?.data?.message || "Booking Failed.");
    } finally {
        setLoading(false);
    }
  };

  const isInvalidRoute = source && destination && source === destination;

  const handleLogout = () => {
    if (window.confirm("Are you sure you want to logout?")) {
      localStorage.clear();
      navigate('/login');
    }
  };

  return (
    <div className="booking-page-wrapper">
      
      {/* 1. TOP BAR */}
      <div className="top-bar">
        {/* UPDATED: Added onClick and pointer cursor to logo-section */}
        <div 
            className="logo-section" 
            onClick={() => navigate('/booking')} 
            style={{ cursor: 'pointer' }}
        >
            <Logo />
        </div>

        <div className="contact-info">
          <div className="nav-links">
            <button className="nav-btn" onClick={() => navigate('/my-rides')}>My Rides</button>
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
                marginLeft: '10px'
              }}
            >
              {userName ? userName.charAt(0).toUpperCase() : 'U'}
            </div>
          </div>
        </div>
      </div>

      {/* 2. MAIN BOOKING AREA */}
      <div className="booking-container">
        <div className="booking-box">
            <div className="booking-header">
                <h2>Request a Ride</h2>
                <p>Welcome, {userName || 'Traveler'}</p>
            </div>

            <div className="ride-input-group">
                <label>Pickup Location</label>
                <select className="ride-select" value={source} onChange={(e) => setSource(e.target.value)}>
                    <option value="">Select Pickup...</option>
                    {(locations || []).map((loc, i) => <option key={i} value={loc}>{loc}</option>)}
                </select>
            </div>

            <div className="ride-input-group">
                <label>Drop Location</label>
                <select className="ride-select" value={destination} onChange={(e) => setDestination(e.target.value)}>
                    <option value="">Select Drop...</option>
                    {(locations || []).map((loc, i) => <option key={i} value={loc}>{loc}</option>)}
                </select>
            </div>

            {isInvalidRoute && (
            <p style={{ color: '#ff4444', fontSize: '0.85rem', marginTop: '-10px', marginBottom: '10px' }}>
            ⚠️ Pickup and Drop location cannot be the same.
            </p>
            )}

            <div className="fare-display">
                <span>ESTIMATED FARE</span>
                <h3>₹ {Number(estimatedFare) > 0 ? Number(estimatedFare).toFixed(2) : '--'}</h3>
            </div>

            <button className="confirm-btn" onClick={handleBookRide} disabled={loading || estimatedFare === 0 || isInvalidRoute}>
                {loading ? "BOOKING..." : "CONFIRM RIDE"}
            </button>
            
            <button className="cancel-btn" onClick={() => navigate('/')}>
                Cancel & Go Home
            </button>
        </div>
      </div>
    </div>
  );
};

export default Booking;