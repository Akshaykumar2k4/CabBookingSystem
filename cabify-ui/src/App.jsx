import { Routes, Route } from 'react-router-dom'
import Home from './pages/Home'
import Login from './pages/Login'
import Register from './pages/Register';
import Booking from './pages/Booking';
import MyRides from './pages/MyRides';
import DriverRegister from './pages/DriverRegister';
import DriverLogin from './pages/DriverLogin';
import DriverDashboard from './pages/DriverDashboard';
import DriverRides from './pages/DriverRides';
function App() {
  return (
    <Routes>
      <Route path="/" element={<Home />} />
      <Route path="/login" element={<Login />} />
      <Route path="/register" element={<Register />} />
      <Route path="/booking" element={<Booking />} />
      <Route path="/my-rides" element={<MyRides />} />
      <Route path="/driver-register" element={<DriverRegister />} />
      <Route path="/driver-login" element={<DriverLogin />} />
      <Route path="/driver-dashboard" element={<DriverDashboard />} />
      <Route path="/driver-rides" element={<DriverRides />} />
    </Routes>
  )
}

export default App