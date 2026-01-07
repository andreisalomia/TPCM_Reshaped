import React from 'react';

function Navbar({ user, onLogout }) {
  return (
    <nav className="navbar navbar-expand-lg" style={{ backgroundColor: '#212529' }}>
      <div className="container d-flex justify-content-between align-items-center">
        <div className="d-flex align-items-center">
          <img
            src="/orange_logo.jpg"
            alt="Orange Logo"
            style={{ width: '40px', height: '40px', objectFit: 'contain', marginRight: '15px' }}
          />
          <span className="navbar-brand mb-0 h1" style={{ color: '#ff7a00', fontWeight: '600' }}>
            <i className="bi bi-phone me-2"></i>
            TPCM Store
          </span>
        </div>

        <div className="d-flex align-items-center">
          <span className="text-light me-3">
            <i className="bi bi-person-circle me-2"></i>
            {user.msisdn}
          </span>
          <button className="btn btn-outline-light btn-sm" onClick={onLogout}>
            <i className="bi bi-box-arrow-right me-1"></i>
            Logout
          </button>
        </div>
      </div>
    </nav>
  );
}

export default Navbar;