import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import Logo from '../components/Logo';
import './DriverRides.css';
import { ToastContainer, toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';

const DriverRides = () => {
    const navigate = useNavigate();
    const [rides, setRides] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [driver, setDriver] = useState(null);
    const [isLoggingOut, setIsLoggingOut] = useState(false);
    useEffect(() => {
        const storedInfo = localStorage.getItem('driverInfo');
        const token = localStorage.getItem('driverToken');

        if (!token || !storedInfo) {
            navigate('/driver-login');
            return;
        }

        const info = JSON.parse(storedInfo);
        setDriver(info);
        
        // 🚀 Ensure we use the correct ID key (check if it's driverId or id)
        const idToFetch = info.driverId || info.id; 
        fetchDriverHistory(idToFetch, token);
    }, [navigate]);
   const handleLogout = () => {
    setLoading(true); // 👈 This keeps the component from returning null
    
    toast.info("Logged out successfully. Drive safe!", {
        position: "top-center",
        autoClose: 1500,
    });

    setTimeout(() => {
        localStorage.clear();
        navigate('/driver-login');
    }, 1500);
};
    const fetchDriverHistory = async (driverId, token) => {
        try {
            setIsLoggingOut(true);
            const response = await axios.get(`http://localhost:8081/api/rides/driver/history/${driverId}`, {
                headers: { Authorization: `Bearer ${token}` }
            });
            
            // 🚀 FIX: More aggressive check for array data
            let rawData = [];
            if (Array.isArray(response.data)) {
                rawData = response.data;
            } else if (response.data && Array.isArray(response.data.data)) {
                rawData = response.data.data;
            } else if (response.data && typeof response.data === 'object') {
                // If backend returns an object with a list inside
                rawData = Object.values(response.data).find(val => Array.isArray(val)) || [];
            }

            // Sort: Newest trips first (handle null bookingTime safely)
            const sortedRides = rawData.sort((a, b) => {
                const dateB = b.bookingTime ? new Date(b.bookingTime) : 0;
                const dateA = a.bookingTime ? new Date(a.bookingTime) : 0;
                return dateB - dateA;
            });

            setRides(sortedRides);
        } catch (err) {
            console.error("Error fetching history:", err);
            setError("You have no rides so far");
        } finally {
            setLoading(false);
        }
    };

    // Replace your current guard with this:
if (!driver && !isLoggingOut) return null;
    return (
        <div className="driver-rides-wrapper">
        <ToastContainer />  
            <div className="top-bar">
                <div className="logo-section" onClick={() => navigate('/driver-dashboard')} style={{cursor: 'pointer'}}>
                    <Logo theme="driver" />
                </div>
                
                <div className="nav-links">
                    <button className="nav-btn-blue" onClick={() => navigate('/driver-dashboard')}>Dashboard</button>
                    <button className="logout-btn-red" onClick={handleLogout}>Logout</button>
                    <div className="profile-icon-circle-blue" onClick={() => navigate('/driver-profile')} style={{cursor: 'pointer'}}>
                        {driver.name ? driver.name.charAt(0).toUpperCase() : 'D'}
                    </div>
                </div>
            </div>

            <div className="rides-bg-container">
                <div className="glass-history-box-blue">
                    <div className="history-header">
                        <h2>My Trip Logs</h2>
                        <p>Total Completed: <span className="highlight-green">{rides.length}</span></p>
                    </div>
                    
                    {loading && <div className="loading-spinner">Accessing ride data...</div>}
                    {error && <div className="error-box">{error}</div>}
                    
                    {!loading && !error && rides.length === 0 && (
                        <div className="empty-state">
                            <p>No trip records found for your account.</p>
                            <button className="blue-action-btn" onClick={() => navigate('/driver-dashboard')}>Return to Dashboard</button>
                        </div>
                    )}

                    <div className="rides-scroll-list">
                        {rides.map((ride) => (
                            <div key={ride.rideId || Math.random()} className="glass-ride-card-blue">
                                <div className="card-header">
                                    <span className="date">
                                        📅 {ride.bookingTime ? new Date(ride.bookingTime).toLocaleDateString() : 'N/A'}
                                    </span>
                                    <span className={`status-badge-blue ${(ride.status || 'pending').toLowerCase()}`}>
                                        {ride.status || 'UNKNOWN'}
                                    </span>
                                </div>

                                <div className="route-row">
                                    <div className="route-point">
                                        <span className="dot source-blue">🟢</span>
                                        <span>{ride.source}</span>
                                    </div>
                                    <div className="route-arrow-blue">➝</div>
                                    <div className="route-point">
                                        <span className="dot dest-red">🔴</span>
                                        <span>{ride.destination}</span>
                                    </div>
                                </div>

                                <div className="info-row">
                                    <div className="info-item">
                                        <small>Earnings</small>
                                        <strong className="earning-text">₹{ride.fare || '0'}</strong>
                                    </div>
                                    <div className="info-item">
                                        <small>Passenger</small>
                                        <strong>{ride.userName || ride.user?.name || "Customer"}</strong>
                                    </div>
                                    <div className="info-item">
                                        <small>Trip ID</small>
                                        <strong>#{ride.rideId || '---'}</strong>
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

export default DriverRides;