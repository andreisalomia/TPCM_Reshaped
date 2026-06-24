import React from 'react';

function Navbar({ user, onLogout }) {
    return (
        <nav className="navbar navbar-expand-lg" style={{ backgroundColor: '#212529' }}>
            <div className="container d-flex justify-content-between align-items-center">
                <div className="d-flex align-items-center">
                    <img
                        src="/store_icon.png"
                        alt="Orange Logo"
                        style={{
                            width: '40px',
                            height: '40px',
                            objectFit: 'contain',
                            marginRight: '15px',
                        }}
                    />
                    <span className="navbar-brand mb-0 h1" style={{ color: '#ff7a00', fontWeight: '600' }}>
                        <i className="bi bi-phone me-2"></i>
                        Store
                    </span>
                </div>

                <div className="d-flex align-items-center">
                    <span className="text-light me-3 d-flex align-items-center">
                        <i className="bi bi-person-circle me-2"></i>
                        {user.msisdn}
                        {user.role === 'ADMIN' && (
                            <span
                                className="badge ms-2"
                                style={{
                                    backgroundColor: '#ff7a00',
                                    color: '#000',
                                    fontSize: '0.7rem',
                                    fontWeight: '600',
                                    padding: '0.25rem 0.5rem',
                                    borderRadius: '4px',
                                }}
                            >
                                <i className="bi bi-shield-check me-1"></i>
                                ADMIN
                            </span>
                        )}
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
