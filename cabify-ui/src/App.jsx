import { Routes, Route } from 'react-router-dom'
import Home from './pages/Home'
import Login from './pages/Login'
import Register from './pages/Register';
import Booking from './pages/Booking';
import MyRides from './pages/MyRides';
import Profile from './pages/Profile';
import DriverRegister from './pages/DriverRegister';
import DriverLogin from './pages/DriverLogin';
import DriverDashboard from './pages/DriverDashboard';
import DriverRides from './pages/DriverRides';
import Feedback from './pages/Feedback';
import DriverProfile from './pages/DriverProfile';
function App() {
  return (
    <Routes>
      <Route path="/" element={<Home />} />
      <Route path="/login" element={<Login />} />
      <Route path="/register" element={<Register />} />
      <Route path="/booking" element={<Booking />} />
      <Route path="/my-rides" element={<MyRides />} />
      <Route path="/profile" element={<Profile />} />
      <Route path="/driver-register" element={<DriverRegister />} />
      <Route path="/driver-login" element={<DriverLogin />} />
      <Route path="/driver-dashboard" element={<DriverDashboard />} />
      <Route path="/driver-rides" element={<DriverRides />} />
      <Route path="/driver-profile" element={<DriverProfile />} />
      <Route path="/feedback" element={<Feedback />} />
    </Routes>
  )
}

export default App