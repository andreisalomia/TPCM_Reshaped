import React, { useState, useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import 'bootstrap/dist/css/bootstrap.min.css';

import LoginPage from './components/LoginPage';
import AuthCallback from './components/AuthCallback';
import Dashboard from './components/Dashboard';
import CardDetailPage from './components/CardDetailPage';
import LinkMsisdnPage from './components/LinkMsisdnPage';

function App() {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const token = localStorage.getItem('tpcm_token');
    if (token) {
      try {
        const payload = JSON.parse(atob(token.split('.')[1]));
        setUser({
          msisdn: payload.msisdn,
          email: payload.email,
          name: payload.name,
          role: payload.role,
          authenticated: true
        });
      } catch (error) {
        console.error('Invalid token:', error);
        localStorage.removeItem('tpcm_token');
      }
    }
    setLoading(false);
  }, []);

  const handleLogout = () => {
    setUser(null);
    localStorage.removeItem('tpcm_token');
  };

  if (loading) {
    return (
      <div className="container-fluid vh-100 d-flex align-items-center justify-content-center bg-dark">
        <div className="spinner-border text-warning" role="status">
          <span className="visually-hidden">Loading...</span>
        </div>
      </div>
    );
  }

  return (
    <Router>
      <Routes>
        <Route path="/login" element={user ? <Navigate to="/" /> : <LoginPage />} />
        <Route path="/auth/callback" element={<AuthCallback />} />

        <Route path="/" element={user ? <Dashboard user={user} onLogout={handleLogout} /> : <Navigate to="/login" />} />
        <Route path="/app/:id" element={user ? <CardDetailPage user={user} type="app" /> : <Navigate to="/login" />} />
        <Route path="/event/:id" element={user ? <CardDetailPage user={user} type="event" /> : <Navigate to="/login" />} />
        <Route path="/link-msisdn" element={<LinkMsisdnPage />} />
        
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </Router>
  );
}

export default App;