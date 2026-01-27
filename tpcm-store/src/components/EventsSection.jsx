import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { getPaginatedEvents, getCategories, getRandomEvents } from '../data/eventsData';

function EventsSection() {
    const [currentPage, setCurrentPage] = useState(1);
    const [selectedCategory, setSelectedCategory] = useState('all');
    const [shuffledEvents, setShuffledEvents] = useState([]);
    const itemsPerPage = 6;

    const categories = getCategories();

    useEffect(() => {
        if (selectedCategory === 'all') {
            const random = getRandomEvents(25);
            setShuffledEvents(random);
        }
    }, []);

    const getCurrentEvents = () => {
        let events;
        let totalItems;

        if (selectedCategory === 'all' && shuffledEvents.length > 0) {
            events = shuffledEvents;
            totalItems = shuffledEvents.length;
        } else {
            const result = getPaginatedEvents(currentPage, itemsPerPage, selectedCategory);
            events = result.events;
            totalItems = result.totalItems;
        }

        const startIndex = (currentPage - 1) * itemsPerPage;
        const endIndex = startIndex + itemsPerPage;

        return {
            events: events.slice(startIndex, endIndex),
            totalPages: Math.ceil(totalItems / itemsPerPage),
            totalItems: totalItems,
        };
    };

    const { events, totalPages, totalItems } = getCurrentEvents();

    const handlePageChange = (page) => {
        setCurrentPage(page);
        window.scrollTo({ top: 0, behavior: 'smooth' });
    };

    const handleCategoryChange = (category) => {
        setSelectedCategory(category);
        setCurrentPage(1);

        if (category === 'all') {
            const random = getRandomEvents(25);
            setShuffledEvents(random);
        }
    };

    const renderPagination = () => {
        if (totalPages <= 1) return null;

        const pages = [];
        for (let i = 1; i <= totalPages; i++) {
            pages.push(
                <button
                    key={i}
                    className={`btn btn-sm mx-1 ${i === currentPage ? 'btn-warning' : 'btn-outline-secondary'}`}
                    onClick={() => handlePageChange(i)}
                    style={i === currentPage ? { backgroundColor: '#ff7a00', border: 'none', color: '#000' } : {}}
                >
                    {i}
                </button>
            );
        }

        return (
            <div className="d-flex justify-content-center align-items-center mt-4">
                <button className="btn btn-outline-secondary btn-sm me-2" onClick={() => handlePageChange(currentPage - 1)} disabled={currentPage === 1}>
                    <i className="bi bi-chevron-left"></i> Previous
                </button>

                {pages}

                <button className="btn btn-outline-secondary btn-sm ms-2" onClick={() => handlePageChange(currentPage + 1)} disabled={currentPage === totalPages}>
                    Next <i className="bi bi-chevron-right"></i>
                </button>
            </div>
        );
    };

    return (
        <div>
            <div className="text-center mb-4">
                <h2 className="fw-bold" style={{ color: '#212529' }}>
                    Events & Tickets
                </h2>
                <p className="text-muted">Buy tickets to upcoming events - {totalItems} available</p>
            </div>

            <div className="mb-4 d-flex justify-content-between align-items-center flex-wrap">
                <div className="btn-group mb-2" role="group">
                    {categories.map((category) => (
                        <button
                            key={category}
                            type="button"
                            className={`btn ${selectedCategory === category ? 'btn-warning' : 'btn-outline-secondary'}`}
                            onClick={() => handleCategoryChange(category)}
                            style={selectedCategory === category ? { backgroundColor: '#ff7a00', border: 'none', color: '#000' } : {}}
                        >
                            {category.charAt(0).toUpperCase() + category.slice(1)}
                        </button>
                    ))}
                </div>

                {selectedCategory === 'all' && (
                    <button
                        className="btn btn-outline-warning mb-2"
                        onClick={() => {
                            const random = getRandomEvents(25);
                            setShuffledEvents(random);
                            setCurrentPage(1);
                        }}
                        style={{ borderColor: '#ff7a00', color: '#ff7a00' }}
                    >
                        <i className="bi bi-shuffle me-2"></i>
                        Shuffle
                    </button>
                )}
            </div>

            <div className="row g-4">
                {events.map((event) => (
                    <div key={event.id} className="col-lg-6 col-md-12">
                        <div className="card h-100 border-0 shadow-sm" style={{ backgroundColor: '#212529', borderRadius: '15px' }}>
                            <div className="row g-0">
                                <div className="col-md-4">
                                    <img
                                        src={event.image}
                                        alt={event.title}
                                        className="img-fluid"
                                        style={{
                                            height: '100%',
                                            minHeight: '250px',
                                            objectFit: 'cover',
                                            borderRadius: '15px 0 0 15px',
                                        }}
                                    />
                                </div>
                                <div className="col-md-8">
                                    <div className="card-body p-4 text-light d-flex flex-column h-100">
                                        <h5 className="card-title text-white">{event.title}</h5>
                                        <p className="card-text text-light flex-grow-1" style={{ fontSize: '0.9rem', opacity: 0.9 }}>
                                            {event.description.length > 100 ? event.description.substring(0, 100) + '...' : event.description}
                                        </p>

                                        <div className="mb-3">
                                            <div className="d-flex align-items-center mb-2">
                                                <i className="bi bi-calendar3 me-2 text-warning"></i>
                                                <small className="text-light" style={{ opacity: 0.9 }}>
                                                    {new Date(event.date).toLocaleDateString('en-US', {
                                                        weekday: 'long',
                                                        year: 'numeric',
                                                        month: 'long',
                                                        day: 'numeric',
                                                    })}
                                                </small>
                                            </div>
                                            <div className="d-flex align-items-center">
                                                <i className="bi bi-geo-alt me-2 text-warning"></i>
                                                <small className="text-light" style={{ opacity: 0.9 }}>
                                                    {event.location}
                                                </small>
                                            </div>
                                        </div>

                                        {event.features && event.features.length > 0 && (
                                            <div className="mb-3">
                                                <div className="row g-2">
                                                    {event.features.slice(0, 2).map((feature, index) => (
                                                        <div key={index} className="col-6">
                                                            <small className="text-light" style={{ opacity: 0.8 }}>
                                                                <i className="bi bi-check-circle me-1 text-success"></i>
                                                                {feature}
                                                            </small>
                                                        </div>
                                                    ))}
                                                </div>
                                            </div>
                                        )}

                                        <div className="d-flex justify-content-between align-items-center mt-auto">
                                            <span className="text-warning fw-bold">{event.price === 0 ? 'FREE' : `$${event.price}`}</span>
                                            <Link to={`/event/${event.id}`} className="btn btn-sm" style={{ backgroundColor: '#ff7a00', color: '#000', textDecoration: 'none' }}>
                                                <i className="bi bi-ticket-perforated me-1"></i>
                                                View Details
                                            </Link>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                ))}
            </div>

            {renderPagination()}
        </div>
    );
}

export default EventsSection;
