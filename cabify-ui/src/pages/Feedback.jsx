import React, { useState } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import axios from 'axios';
import Logo from '../components/Logo';
import './Feedback.css';
import { ToastContainer, toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';

const Feedback = () => {
    const navigate = useNavigate();
    const location = useLocation();
    
    // Get the rideId passed from the MyRides payment modal
    const rideId = location.state?.rideId; 

    const [rating, setRating] = useState(0);
    const [comment, setComment] = useState('');
    const [hover, setHover] = useState(0);
    const [isSubmitting, setIsSubmitting] = useState(false);

    const handleSubmit = async (e) => {
        e.preventDefault();
        
        // 1. Validation: Ensure we have a ride to rate
        if (!rideId) {
            alert("Ride information missing. Returning to booking.");
            navigate('/booking');
            return;
        }

        if (rating === 0) {
            toast.warning("Please select a star rating before submitting.");
            return;
        }

        setIsSubmitting(true);
        try {
            const token = localStorage.getItem('token');
            const userId = localStorage.getItem('userId'); 

            // 2. PAYLOAD: MUST match RatingRequestDto.java exactly
            const feedbackData = {
                rideId: Number(rideId),       // Ensure it's a Long/Number
                passengerId: Number(userId),  // Ensure it's a Long/Number
                score: Number(rating),        // Matches 'private int score'
                comments: comment             // Matches 'private String comments'
            };

            console.log("Sending Feedback:", feedbackData);

            const response = await axios.post('http://localhost:8081/api/ratings/submit', feedbackData, {
                headers: { Authorization: `Bearer ${token}` }
            });

            toast.success("Thank you! Your feedback helps us improve.", {
            position: "top-center",
            autoClose: 2000,
            });
            setTimeout(() => navigate('/booking'), 2000);
        } catch (error) {
            // 3. LOGGING: This helps debug the 500 error
            console.error("Backend Error Details:", error.response?.data);
            
            const errorMsg = error.response?.data?.message || "Server Error: Could not save rating.";
            toast.error(error.response?.data?.message || "Could not save rating. Please try again.");
            
            // If the error is a duplicate, we should exit the page
            if (error.response?.status === 400 || error.response?.status === 409) {
                navigate('/booking');
            }
        } finally {
            setIsSubmitting(false);
        }
    };

    return (
        <div className="feedback-page-wrapper">
            <ToastContainer />
            <div className="top-bar">
                <div className="logo-section" onClick={() => navigate('/booking')} style={{cursor: 'pointer'}}>
                    <Logo />
                </div>
            </div>

            <div className="feedback-container">
                <div className="feedback-box">
                    <h2>How was your ride?</h2>
                    <p>Your feedback helps us improve the Cabify experience.</p>

                    <form onSubmit={handleSubmit}>
                        <div className="star-rating">
                            {[...Array(5)].map((_, index) => {
                                const starValue = index + 1;
                                return (
                                    <button
                                        type="button"
                                        key={starValue}
                                        className={starValue <= (hover || rating) ? "on" : "off"}
                                        onClick={() => setRating(starValue)}
                                        onMouseEnter={() => setHover(starValue)}
                                        onMouseLeave={() => setHover(0)}
                                    >
                                        <span className="star">&#9733;</span>
                                    </button>
                                );
                            })}
                        </div>

                        <textarea
                            placeholder="Share your experience (optional)..."
                            value={comment}
                            onChange={(e) => setComment(e.target.value)}
                            className="feedback-text"
                        />

                        <button type="submit" className="submit-feedback-btn" disabled={isSubmitting}>
                            {isSubmitting ? "SUBMITTING..." : "SUBMIT FEEDBACK"}
                        </button>
                        
                        <button type="button" className="skip-btn" onClick={() => navigate('/booking')}>
                            Skip for now
                        </button>
                    </form>
                </div>
            </div>
        </div>
    );
};

export default Feedback;