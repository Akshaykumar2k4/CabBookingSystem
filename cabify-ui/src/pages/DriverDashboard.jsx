import React, { useState, useEffect, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import Logo from '../components/Logo';
import './DriverDashboard.css';

const DriverDashboard = () => {
    const navigate = useNavigate();
    const [driver, setDriver] = useState(null);
    const [status, setStatus] = useState('OFFLINE');
    const [activeRide, setActiveRide] = useState(null); 
    const [loadingRide, setLoadingRide] = useState(false);
    
    // Modal States
    const [showPaymentModal, setShowPaymentModal] = useState(false);
    const [completedRideData, setCompletedRideData] = useState(null);

    useEffect(() => {
        const token = localStorage.getItem('driverToken');
        const info = JSON.parse(localStorage.getItem('driverInfo'));
        if (!token || !info) {
            navigate('/driver-login');
        } else {
            setDriver(info);
            setStatus(info.status || 'OFFLINE');
        }
    }, [navigate]);

    const fetchActiveRide = useCallback(async () => {
        if (!driver?.driverId) return;
        try {
            const token = localStorage.getItem('driverToken');
            const response = await axios.get(
                `http://localhost:8081/api/rides/active-request/${driver.driverId}`,
                { headers: { Authorization: `Bearer ${token}` } }
            );
            if (response.data && response.data.data) {
                setActiveRide(response.data.data);
                setStatus('BUSY'); 
            } else if (status === 'BUSY') {
                setActiveRide(null); 
                setStatus('AVAILABLE');
            }
        } catch (err) {
            console.warn("Polling for active ride...");
        }
    }, [driver, status]);

    useEffect(() => {
        if (!driver) return;
        fetchActiveRide();
        const interval = setInterval(fetchActiveRide, 5000);
        return () => clearInterval(interval);
    }, [driver, fetchActiveRide]);

    const handleToggleStatus = async () => {
        const newStatus = status === 'AVAILABLE' ? 'OFFLINE' : 'AVAILABLE';
        const token = localStorage.getItem('driverToken');
        try {
            await axios.put(
                `http://localhost:8081/api/drivers/status/${driver.driverId}?status=${newStatus}`,
                {}, { headers: { Authorization: `Bearer ${token}` } }
            );
            setStatus(newStatus);
            const updatedInfo = { ...driver, status: newStatus };
            localStorage.setItem('driverInfo', JSON.stringify(updatedInfo));
            if (newStatus === 'AVAILABLE') fetchActiveRide();
        } catch (error) {
            console.error("Toggle failed:", error);
        }
    };

    const handleEndRide = async () => {
        if (!window.confirm("Confirm trip completion and generate receipt?")) return;
        setLoadingRide(true);
        const token = localStorage.getItem('driverToken');
        try {
            const response = await axios.put(
                `http://localhost:8081/api/rides/${activeRide.rideId}/end`,
                {}, { headers: { Authorization: `Bearer ${token}` } }
            );
            if (response.status === 200) {
                setCompletedRideData(response.data.data);
                setShowPaymentModal(true);
                setStatus('AVAILABLE');
                const updatedInfo = { ...driver, status: 'AVAILABLE' };
                localStorage.setItem('driverInfo', JSON.stringify(updatedInfo));
                setDriver(updatedInfo);
                setActiveRide(null);
            }
        } catch (error) {
            alert("Error ending ride.");
        } finally {
            setLoadingRide(false);
        }
    };

    if (!driver) return null;

    return (
        <div className="driver-dashboard-wrapper">
            {/* 🚀 MODAL OVERLAY: STICKY TOP LAYER WITH BLUR */}
            {showPaymentModal && completedRideData && (
                <div className="modal-overlay">
                    <div className="payment-modal">
                        <div className="receipt-header">
                            <h3>Ride Receipt</h3>
                            <p>Trip Completed for {completedRideData.userName}</p>
                        </div>
                        <div className="receipt-body">
                            <div className="receipt-row"><span>Base Fare</span><span>₹{(completedRideData.fare * 0.8).toFixed(2)}</span></div>
                            <div className="receipt-row"><span>Taxes (GST 18%)</span><span>₹{(completedRideData.fare * 0.18).toFixed(2)}</span></div>
                            <div className="receipt-row"><span>Service Fee</span><span>₹{(completedRideData.fare * 0.02).toFixed(2)}</span></div>
                            <hr className="receipt-divider" />
                            <div className="receipt-row total"><span>Total Amount</span><span>₹{completedRideData.fare.toFixed(2)}</span></div>
                        </div>
                        <div className="payment-actions">
                            <button className="pay-now-btn" onClick={() => setShowPaymentModal(false)}>Confirm & Close</button>
                        </div>
                    </div>
                </div>
            )}

            <div className="top-bar">
                <div className="logo-section" onClick={() => navigate('/driver-dashboard')} style={{ cursor: 'pointer' }}>
                    <Logo theme="driver" />
                </div>
                <div className="contact-info">
                    <div className="nav-links">
                        <button className="nav-btn" onClick={() => navigate('/driver-rides')}>My Rides</button>
                         <button className="logout-btn" onClick={() => { localStorage.clear(); navigate('/driver-login'); }}>Logout</button>
                        <div className="profile-icon-circle" onClick={() => navigate('/driver-profile')}>
                            {driver.name ? driver.name.charAt(0).toUpperCase() : 'D'}
                        </div>
                    </div>
                </div>
            </div>

            <div className="dashboard-content">
                <div className="dashboard-grid">
                    <div className="card welcome-card">
                        <div className="booking-header">
                            <h1>Welcome Back, <span className="highlight">{driver.name}</span></h1>
                            <div className={`status-badge ${status.toLowerCase()}`}>{status}</div>
                        </div>
                        <div className="info-section">
                            <p className="ride-info-line">
                                <strong>Vehicle:</strong> {driver.vehicleModel} ({driver.vehiclePlate})
                            </p>
                            <p className="ride-info-line"><strong>License:</strong> {driver.licenseNumber}</p>
                        </div>
                        <div className="toggle-container">
                            <button onClick={handleToggleStatus} className={`main-toggle-btn ${status === 'AVAILABLE' || status === 'BUSY' ? 'is-online' : 'is-offline'}`} disabled={status === 'BUSY'}>
                                <span>{status === 'OFFLINE' ? 'Go online' : 'Go offline'}</span>
                            </button>
                        </div>
                    </div>

                    {activeRide ? (
                        <div className="card current-ride-card">
                            <div className="ride-header">
                                <span className="live-dot"></span>
                                <h3>Active Ride Assigned</h3>
                            </div>
                            <div className="ride-details-body">
                                <p className="ride-info-line"><strong>Passenger:</strong> {activeRide.userName}</p>
                                <p className="ride-info-line"><strong>Pickup:</strong> {activeRide.source}</p>
                                <p className="ride-info-line"><strong>Destination:</strong> {activeRide.destination}</p>
                                <p className="ride-info-line"><strong>Fare:</strong> <span className="fare-text">₹ {activeRide.fare}</span></p>
                                <button className="end-ride-btn" onClick={handleEndRide} disabled={loadingRide}>
                                    {loadingRide ? "COMPLETING..." : "END RIDE"}
                                </button>
                            </div>
                        </div>
                    ) : (
                        <div className="card no-ride-placeholder">
                            <h3>No Active Ride</h3>
                            <p>{status === 'AVAILABLE' ? "Waiting for requests..." : "Go Online to see rides"}</p>
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
};

export default DriverDashboard;