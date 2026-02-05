import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import Logo from '../components/Logo';
import './Booking.css';

const Booking = () => {
  const navigate = useNavigate();
  
  // --- STATE MANAGEMENT ---
  const [source, setSource] = useState('');
  const [destination, setDestination] = useState('');
  const [estimatedFare, setEstimatedFare] = useState(0);
  const [loading, setLoading] = useState(false);
  const [locations, setLocations] = useState([]);
  
  // 🔐 IDENTITY STATE
  const [currentUserId, setCurrentUserId] = useState(null); 
  const [userEmail, setUserEmail] = useState('');

  // --- INITIALIZATION ---
  useEffect(() => {
    fetchLocations();
    identifyUser(); // Auto-detect who is logged in
  }, []);

  // --- 1. LIVE FARE ESTIMATE (Calls Backend) ---
  useEffect(() => {
    // Only ask backend if we have both points and they are different
    if (source && destination && source !== destination) {
        getFareEstimate();
    } else {
        setEstimatedFare(0);
    }
  }, [source, destination]);

  const getFareEstimate = async () => {
    try {
        const token = localStorage.getItem('token');
        // 🚀 THE NEW CLEAN API CALL
        const response = await axios.get(`http://localhost:8081/api/rides/estimate`, {
            params: { source, destination },
            headers: { Authorization: `Bearer ${token}` }
        });
        setEstimatedFare(response.data); // Backend does the math!
    } catch (error) {
        console.error("Could not fetch fare estimate:", error);
        setEstimatedFare(0);
    }
  };

  // --- 2. AUTOMATIC USER ID FETCHER ---
  const identifyUser = async () => {
    const token = localStorage.getItem('token');
    const email = localStorage.getItem('email'); 

    if (!email || !token) {
        alert("Session expired. Please login again.");
        navigate('/login');
        return;
    }

    setUserEmail(email);

    try {
        const response = await axios.get('http://localhost:8081/api/users/profile', {
            headers: { Authorization: `Bearer ${token}` }
        });

        const myUser = response.data.find(u => u.email === email);

        if (myUser) {
            console.log("✅ Identity Verified! My UserID is:", myUser.userId);
            setCurrentUserId(myUser.userId);
        } else {
            console.error("❌ User not found in database list");
        }
    } catch (error) {
        console.error("Error fetching user identity:", error);
    }
  };

  // --- 3. FETCH LOCATIONS ---
  const fetchLocations = async () => {
    try {
        const token = localStorage.getItem('token');
        const response = await axios.get('http://localhost:8081/api/rides/locations', {
            headers: { Authorization: `Bearer ${token}` }
        });
        setLocations(response.data);
    } catch (error) {
        // Fallback if backend location API fails
        setLocations(["Adyar", "AnnaNagar", "Guindy", "Marina", "Sholinganallur", "Tambaram", "TNagar", "Velachery"]);
    }
  };

  // --- 4. BOOKING FUNCTION ---
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
            userId: currentUserId, // 👈 Uses the auto-detected ID
            driverId: 2            
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

  return (
    <div className="booking-page-wrapper">
      <div className="top-bar">
        <div className="logo-section"><Logo /></div>
        <div className="contact-info">
            <button className="login-btn" style={{width:'auto'}} onClick={() => navigate('/')}>Home</button>
        </div>
      </div>
      <div className="booking-container">
        <div className="booking-box">
            <div className="booking-header">
                <h2>Request a Ride</h2>
                <p>Welcome, {userEmail}</p>
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

            <div className="fare-display">
                <span>ESTIMATED FARE</span>
                <h3>₹ {estimatedFare > 0 ? estimatedFare.toFixed(2) : '--'}</h3>
            </div>

            <button className="confirm-btn" onClick={handleBookRide} disabled={loading || estimatedFare === 0}>
                {loading ? "BOOKING..." : "CONFIRM RIDE"}
            </button>
        </div>
      </div>
    </div>
  );
};

export default Booking;