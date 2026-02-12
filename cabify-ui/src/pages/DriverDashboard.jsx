import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import Logo from '../components/Logo';
import './DriverDashboard.css';

const DriverDashboard = () => {
    const navigate = useNavigate();
    const [driver, setDriver] = useState(null);
    const [status, setStatus] = useState('OFFLINE');

    useEffect(() => {
        const token = localStorage.getItem('driverToken');
        // 🚀 Fixed the "im is not defined" error here
        const info = JSON.parse(localStorage.getItem('driverInfo'));

        if (!token || !info) {
            navigate('/driver-login');
        } else {
            setDriver(info);
            setStatus(info.status || 'OFFLINE');
        }
    }, [navigate]);

    const handleToggleStatus = async () => {
        const newStatus = status === 'AVAILABLE' ? 'OFFLINE' : 'AVAILABLE';
        
        try {
            const token = localStorage.getItem('driverToken');
            
            // 🚀 The URL must include the ID: /status/{id}?status={newStatus}
            // If driver.driverId is missing, this triggers the 500 error
            await axios.put(
                `http://localhost:8081/api/drivers/status/${driver.driverId}?status=${newStatus}`,
                {}, 
                { headers: { Authorization: `Bearer ${token}` } }
            );
            
            setStatus(newStatus);
            
            // Update storage so the status persists on page refresh
            const updatedInfo = { ...driver, status: newStatus };
            localStorage.setItem('driverInfo', JSON.stringify(updatedInfo));
            
        } catch (error) {
            console.error("Update failed:", error);
            alert("Status Update Failed: Check backend logs.");
        }
    };

    if (!driver) return null;

    return (
        <div className="driver-dashboard-wrapper">
            <nav className="dashboard-nav">
                <Logo theme="driver" />
                <button onClick={() => { localStorage.clear(); navigate('/driver-login'); }} className="logout-btn">
                    Logout
                </button>
            </nav>

            <div className="dashboard-content">
                <div className="welcome-card">
                    <h1>Welcome Back, <span className="highlight">{driver.name}</span></h1>
                    <div className={`status-badge ${status.toLowerCase()}`}>{status}</div>
                    
                    <div className="info-section">
                        <p><strong>Vehicle:</strong> {driver.vehicleDetails}</p>
                        <p><strong>License:</strong> {driver.licenseNumber}</p>
                    </div>

                    <button 
                        onClick={handleToggleStatus} 
                        className={`toggle-btn ${status === 'AVAILABLE' ? 'btn-red' : 'btn-green'}`}
                    >
                        {status === 'AVAILABLE' ? 'GO OFFLINE' : 'GO ONLINE'}
                    </button>
                </div>
            </div>
        </div>
    );
};

export default DriverDashboard;