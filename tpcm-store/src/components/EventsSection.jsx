import React from 'react';
import { Link } from 'react-router-dom';
import { events } from '../data/storeItems';

function EventsSection() {
  return (
    <div>
      <div className="text-center mb-5">
        <h2 className="fw-bold" style={{ color: '#212529' }}>Events & Tickets</h2>
        <p className="text-muted">Buy tickets to upcoming events.</p>
      </div>
      <div className="row g-4">
        {events.map(event => (
          <div key={event.id} className="col-lg-6 col-md-12">
            <div className="card h-100 border-0 shadow-sm" style={{ backgroundColor: '#212529', borderRadius: '15px' }}>
              <div className="card-body p-4 text-light">
                <div className="d-flex justify-content-between align-items-start mb-3">
                  <div>
                    <h5 className="card-title">{event.title}</h5>
                    <p className="card-text">{event.description}</p>
                  </div>
                </div>
                
                {/* detaliile eventului */}
                <div className="mb-3">
                  <div className="d-flex align-items-center mb-2">
                    <i className="bi bi-calendar3 me-2 text-warning"></i>
                    <small className="text-light">{new Date(event.date).toLocaleDateString('en-US', {
                      weekday: 'long',
                      year: 'numeric',
                      month: 'long',
                      day: 'numeric'
                    })}</small>
                  </div>
                  <div className="d-flex align-items-center">
                    <i className="bi bi-geo-alt me-2 text-warning"></i>
                    <small className="text-light">{event.location}</small>
                  </div>
                </div>

                {/* zona de features */}
                <div className="mb-3">
                  <div className="row g-2">
                    {event.features?.slice(0, 2).map((feature, index) => (
                      <div key={index} className="col-6">
                        <small className="text-muted">
                          <i className="bi bi-check-circle me-1"></i>
                          {feature}
                        </small>
                      </div>
                    ))}
                  </div>
                </div>

                <div className="d-flex justify-content-between align-items-center">
                  <span className="text-warning fw-bold">${event.price}</span>
                  <Link 
                    to={`/event/${event.id}`}
                    className="btn btn-sm" 
                    style={{ backgroundColor: '#ff7a00', color: '#000', textDecoration: 'none' }}
                  >
                    <i className="bi bi-ticket-perforated me-1"></i>
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

export default EventsSection;