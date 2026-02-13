import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import Logo from '../components/Logo';
import './DriverRides.css';

const DriverRides = () => {
    const navigate = useNavigate();
    const [rides, setRides] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [driver, setDriver] = useState(null);

    useEffect(() => {
        const info = JSON.parse(localStorage.getItem('driverInfo'));
        const token = localStorage.getItem('driverToken');

        if (!token || !info) {
            navigate('/driver-login');
            return;
        }
        setDriver(info);
        fetchDriverHistory(info.driverId, token);
    }, [navigate]);

    const fetchDriverHistory = async (driverId, token) => {
        try {
            const response = await axios.get(`http://localhost:8081/api/rides/history/${driverId}`, {
                headers: { Authorization: `Bearer ${token}` }
            });
            
            const rawData = response.data.data || response.data || [];
            
            // Sort: Newest trips first
            const sortedRides = [...rawData].sort((a, b) => 
                new Date(b.bookingTime) - new Date(a.bookingTime)
            );

            setRides(sortedRides);
        } catch (err) {
            console.error("Error fetching history:", err);
            setError("Failed to load your trip history.");
        } finally {
            setLoading(false);
        }
    };

    if (!driver) return null;

    return (
        <div className="driver-rides-wrapper">
            {/* TOP BAR - FIXED BLUE THEME */}
            <div className="top-bar">
                <div className="logo-section" onClick={() => navigate('/driver-dashboard')} style={{cursor: 'pointer'}}>
                    <Logo theme="driver" />
                </div>
                
                <div className="nav-links">
                    <button className="nav-btn-blue" onClick={() => navigate('/driver-dashboard')}>Dashboard</button>
                    <button className="logout-btn-red" onClick={() => { localStorage.clear(); navigate('/driver-login'); }}>Logout</button>
                    <div className="profile-icon-circle-blue">
                        {driver.name.charAt(0).toUpperCase()}
                    </div>
                </div>
            </div>

            {/* MAIN CONTENT AREA */}
            <div className="rides-bg-container">
                <div className="glass-history-box-blue">
                    <div className="history-header">
                        <h2>My trip logs</h2>
                        <p>Total Completed: <span className="highlight-green">{rides.length}</span></p>
                    </div>
                    
                    {loading && <p className="loading-text">Accessing ride data...</p>}
                    {error && <p className="error-text">{error}</p>}
                    
                    {!loading && !error && rides.length === 0 && (
                        <div className="empty-state">
                            <p>You haven't completed any trips yet.</p>
                            <button className="blue-action-btn" onClick={() => navigate('/driver-dashboard')}>Go to Dashboard</button>
                        </div>
                    )}

                    <div className="rides-scroll-list">
                        {rides.map((ride) => (
                            <div key={ride.rideId} className="glass-ride-card-blue">
                                <div className="card-header">
                                    <span className="date">
                                        📅 {new Date(ride.bookingTime).toLocaleDateString()} at {new Date(ride.bookingTime).toLocaleTimeString([], {hour: '2-digit', minute:'2-digit'})}
                                    </span>
                                    <span className={`status-badge-blue ${ride.status.toLowerCase()}`}>
                                        {ride.status}
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
                                        <strong className="earning-text">₹{ride.fare}</strong>
                                    </div>
                                    <div className="info-item">
                                        <small>Passenger</small>
                                        <strong>{ride.userName || "Customer"}</strong>
                                    </div>
                                    <div className="info-item">
                                        <small>Trip ID</small>
                                        <strong>#{ride.rideId}</strong>
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