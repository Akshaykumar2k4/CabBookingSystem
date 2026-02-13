import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import Logo from '../components/Logo';
import './DriverProfile.css';

const DriverProfile = () => {
    const navigate = useNavigate();
    const [driver, setDriver] = useState({ 
        name: '', email: '', phone: '', 
        licenseNumber: '', vehicleDetails: '', driverId: '' 
    });
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const fetchDriverProfile = async () => {
            const token = localStorage.getItem('driverToken');
            const driverInfo = JSON.parse(localStorage.getItem('driverInfo'));

            if (!token || !driverInfo) {
                navigate('/driver-login');
                return;
            }

            try {
                const response = await axios.get('http://localhost:8081/api/drivers/profile', {
                    headers: { Authorization: `Bearer ${token}` }
                });
                const data = response.data.find(d => d.email === driverInfo.email);
                if (data) setDriver(data);
                else setDriver(driverInfo);
            } catch (error) {
                setDriver(driverInfo);
            } finally {
                setLoading(false);
            }
        };
        fetchDriverProfile();
    }, [navigate]);

    return (
        <div className="profile-page-background">
            <div className="profile-top-navbar">
                <div className="logo-section" onClick={() => navigate('/driver-dashboard')}>
                    <Logo theme="driver" />
                </div>
                <div className="nav-actions">
                    <button className="back-btn" onClick={() => navigate('/driver-dashboard')}>Back to Dashboard</button>
                    <button className="logout-btn-red" onClick={() => { localStorage.clear(); navigate('/driver-login'); }}>Logout</button>
                </div>
            </div>

            <div className="profile-card-container">
                <div className="glass-card">
                    <div className="profile-avatar-circle">
                        {driver.name ? driver.name.charAt(0).toUpperCase() : 'D'}
                    </div>
                    
                    <h2 className="profile-title">Driver Profile</h2>
                    
                    <div className="profile-info-list">
                        <div className="info-group">
                            <label>FULL NAME</label>
                            <p>{loading ? "..." : driver.name}</p>
                        </div>
                        
                        <div className="info-group">
                            <label>EMAIL ADDRESS</label>
                            <p>{loading ? "..." : driver.email}</p>
                        </div>

                        <div className="info-group">
                            <label>PHONE NUMBER</label>
                            <p>{loading ? "..." : driver.phone}</p>
                        </div>

                        <div className="info-group">
                            <label>VEHICLE & LICENSE</label>
                            <p>{loading ? "..." : `${driver.vehicleDetails} | ${driver.licenseNumber}`}</p>
                        </div>

                        <div className="info-group">
                            <label>DRIVER ID</label>
                            <p># {loading ? "..." : driver.driverId}</p>
                        </div>
                    </div>

                    
                </div>
            </div>
        </div>
    );
};

export default DriverProfile;