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
  // 🆕 Change: Added state for Name
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
        setEstimatedFare(response.data); 
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
            // 🆕 Change: Set the Name from the database object
            setUserName(myUser.name); 
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
        setLocations(response.data);
    } catch (error) {
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

        const finalFare = response.data.data.fare;
        alert(`Ride Confirmed!\nAmount: ₹${finalFare}\nDriver: ${response.data.data.driverName}`);
        navigate('/'); 

    } catch (error) {
        console.error("Booking Error:", error);
        alert(error.response?.data?.message || "Booking Failed.");
    } finally {
        setLoading(false);
    }
  };
  // Check if both are selected AND they are the same
    const isInvalidRoute = source && destination && source === destination;
  return (
    <div className="booking-page-wrapper">
      
      {/* 1. TOP BAR */}
      <div className="top-bar">
        <div className="logo-section"><Logo /></div>
        <div className="contact-info">
          <div className="contact-item">
            <span className="icon">📞</span>
            <div><p className="contact-label">Call Us</p><p className="contact-value">0413-225356</p></div>
          </div>
          <div className="contact-item">
            <span className="icon">✉️</span>
            <div><p className="contact-label">Email</p><p className="contact-value">info@cabify.com</p></div>
          </div>
          <div className="contact-item">
            <span className="icon">📍</span>
            <div><p className="contact-label">Location</p><p className="contact-value">Chennai</p></div>
          </div>
        </div>
      </div>

      {/* 2. MAIN BOOKING AREA */}
      <div className="booking-container">
        <div className="booking-box">
            <div className="booking-header">
                <h2>Request a Ride</h2>
                {/* 🆕 Change: Display Name instead of Email */}
                <p>Welcome, {userName || 'Traveler'}</p>
            </div>

            <div className="ride-input-group">
                <label>Pickup Location</label>
                <select className="ride-select" value={source} onChange={(e) => setSource(e.target.value)}>
                    <option value="">Select Pickup...</option>
                    {locations.map((loc, i) => <option key={i} value={loc}>{loc}</option>)}
                </select>
            </div>

            <div className="ride-input-group">
                <label>Drop Location</label>
                <select className="ride-select" value={destination} onChange={(e) => setDestination(e.target.value)}>
                    <option value="">Select Drop...</option>
                    {locations.map((loc, i) => <option key={i} value={loc}>{loc}</option>)}
                </select>
            </div>
            {isInvalidRoute && (
            <p style={{ color: '#ff4444', fontSize: '0.85rem', marginTop: '-10px', marginBottom: '10px' }}>
            ⚠️ Pickup and Drop location cannot be the same.
            </p>
            )}
            <div className="fare-display">
                <span>ESTIMATED FARE</span>
                <h3>₹ {estimatedFare > 0 ? estimatedFare.toFixed(2) : '--'}</h3>
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