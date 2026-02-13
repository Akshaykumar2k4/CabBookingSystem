import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import Logo from '../components/Logo';
import './Profile.css';

const Profile = () => {
    const navigate = useNavigate();
    const [user, setUser] = useState({ name: '', email: '',phone:'', id: '' });
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const fetchUserProfile = async () => {
            const token = localStorage.getItem('token');
            const email = localStorage.getItem('email');

            if (!token || !email) {
                navigate('/login');
                return;
            }

            try {
                const response = await axios.get('http://localhost:8081/api/users/profile', {
                    headers: { Authorization: `Bearer ${token}` }
                });

                // Find the specific user from the list
                const userData = response.data.find(u => u.email === email);
                if (userData) {
                    setUser({
                        name: userData.name,
                        email: userData.email,
                        phone:userData.phone,
                        id: userData.id || userData.userId
                    });
                }
            } catch (error) {
                console.error("Error fetching profile:", error);
            } finally {
                setLoading(false);
            }
        };

        fetchUserProfile();
    }, [navigate]);

    return (
        <div className="profile-page-wrapper">
            <div className="top-bar">
                <div className="logo-section" onClick={() => navigate('/booking')} style={{cursor: 'pointer'}}>
                    <Logo />
                </div>
                <div className="nav-links">
                    <button className="nav-btn" onClick={() => navigate('/booking')}>Back to Booking</button>
                    <button className="logout-btn" onClick={() => { localStorage.clear(); navigate('/login'); }}>Logout</button>
                </div>
            </div>

            <div className="profile-container">
                <div className="profile-card">
                    <div className="profile-avatar">
                        {user.name ? user.name.charAt(0).toUpperCase() : '?'}
                    </div>
                    <h2>User Profile</h2>
                    
                    <div className="profile-details">
                        <div className="detail-item">
                            <label>Full Name</label>
                            <p>{loading ? "Loading..." : user.name}</p>
                        </div>
                        <div className="detail-item">
                            <label>Email Address</label>
                            <p>{loading ? "Loading..." : user.email}</p>
                        </div>
                        <div className="detail-item">
                            <label>Phone Number</label> 
                             <p>{loading ? "Loading..." : user.phone}</p>
                        </div>
                        <div className="detail-item">
                            <label>Customer ID</label>
                            <p># {loading ? "..." : user.id}</p>
                        </div>
                    </div>

                </div>
            </div>
        </div>
    );
};

export default Profile;