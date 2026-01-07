import React from 'react';
import { Link } from 'react-router-dom';
import { apps } from '../data/storeItems';

function AppsSection() {
  return (
    <div>
      <div className="text-center mb-5">
        <h2 className="fw-bold" style={{ color: '#212529' }}>Apps & Games</h2>
        <p className="text-muted">Download your favourite apps and games.</p>
      </div>
      <div className="row g-4">
        {apps.map(app => (
          <div key={app.id} className="col-lg-4 col-md-6">
            <div className="card h-100 border-0 shadow-sm" style={{ backgroundColor: '#212529', borderRadius: '15px' }}>
              <div className="card-body p-4 text-light">
                <h5 className="card-title">{app.title}</h5>
                <p className="card-text">{app.description}</p>
                <div className="d-flex justify-content-between align-items-center">
                  <span className="text-warning fw-bold">${app.price}</span>
                  <Link
                    to={`/app/${app.id}`}
                    className="btn btn-sm"
                    style={{ backgroundColor: '#ff7a00', color: '#000', textDecoration: 'none' }}
                  >
                    <i className="bi bi-cart-plus me-1"></i>
                    View Details
                  </Link>
                </div>
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}

export default AppsSection;