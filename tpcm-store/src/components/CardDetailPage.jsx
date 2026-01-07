import React, { useState, useEffect, useCallback } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { motion } from 'framer-motion';
import { transactionApi } from '../services/tpcmApi';
import { apps, events } from '../data/storeItems';
import Navbar from './Navbar';
import PurchaseModal from './PurchaseModal';

function CardDetailPage({ user, type }) {
  const { id } = useParams();
  const navigate = useNavigate();
  const [item, setItem] = useState(null);
  const [showPurchaseModal, setShowPurchaseModal] = useState(false);
  const [balance, setBalance] = useState(null);
  const [loading, setLoading] = useState(true);
  const [balanceError, setBalanceError] = useState(null);

  const fetchBalance = useCallback(async () => {
    setLoading(true);
    setBalanceError(null);

    try {
      const balanceData = await transactionApi.getBalance(user.msisdn);
      setBalance(balanceData.availableBalance);
    } catch (error) {
      console.error('Error fetching balance:', error);
      setBalanceError(error.message || 'Failed to fetch balance');
      setBalance(0);
    } finally {
      setLoading(false);
    }
  }, [user.msisdn]);

  useEffect(() => {
    const items = type === 'app' ? apps : events;
    const foundItem = items.find(item => item.id === parseInt(id));

    if (!foundItem) {
      navigate('/');
      return;
    }

    setItem(foundItem);
    fetchBalance();
  }, [id, type, navigate, fetchBalance]);
  const handlePurchase = () => {
    setShowPurchaseModal(true);
  };

  if (loading && item === null) {
    return (
      <div className="d-flex justify-content-center align-items-center min-vh-100">
        <div className="spinner-border text-warning" role="status">
          <span className="visually-hidden">Loading...</span>
        </div>
      </div>
    );
  }

  if (!item) return null;

  return (
    <div className="min-vh-100" style={{ backgroundColor: '#f8f9fa' }}>
      <Navbar user={user} onLogout={() => navigate('/')} />

      <div className="container mt-4">
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.3 }}
        >
          {/* butonul de back */}
          <button
            className="btn btn-outline-secondary mb-4"
            onClick={() => navigate('/')}
          >
            <i className="bi bi-arrow-left me-2"></i>
            Back to Store
          </button>

          <div className="row">
            <div className="col-md-6">
              <div
                className="card border-0 shadow-sm"
                style={{ backgroundColor: '#212529', borderRadius: '15px' }}
              >
                <div className="card-body p-0">
                  <img
                    src={item.image}
                    alt={item.title}
                    className="card-img-top"
                    // shrunk the  image to fit better
                    style={{ borderRadius: '15px 15px 0 0', objectFit: 'cover', transform : 'scale(0.8)' }}
                  />
                </div>
              </div>
            </div>

            <div className="col-md-6">
              <div className="card border-0 shadow-sm" style={{ borderRadius: '15px' }}>
                <div className="card-body p-4">
                  <h1 className="card-title fw-bold">{item.title}</h1>
                  <p className="text-muted mb-4">{item.description}</p>

                  {/* zona de features */}
                  <h5 className="fw-bold mb-3">Features:</h5>
                  <ul className="list-unstyled">
                    {item.features?.map((feature, index) => (
                      <li key={index} className="mb-2">
                        <i className="bi bi-check-circle-fill text-success me-2"></i>
                        {feature}
                      </li>
                    ))}
                  </ul>

                  {/* detalii pt eveniment */}
                  {type === 'event' && (
                    <div className="mb-4">
                      <h5 className="fw-bold mb-3">Event Details:</h5>
                      <p>
                        <i className="bi bi-calendar3 me-2"></i>
                        {new Date(item.date).toLocaleDateString('en-US', {
                          weekday: 'long',
                          year: 'numeric',
                          month: 'long',
                          day: 'numeric'
                        })}
                      </p>
                      <p><i className="bi bi-geo-alt me-2"></i>{item.location}</p>
                    </div>
                  )}

                  {/* balanta */}
                  <div className="mb-4">
                    {loading ? (
                      <div className="d-flex align-items-center">
                        <div className="spinner-border spinner-border-sm text-warning me-2" role="status">
                          <span className="visually-hidden">Loading balance...</span>
                        </div>
                        <small className="text-muted">Loading balance...</small>
                      </div>
                    ) : balanceError ? (
                      <div className="alert alert-warning py-2 px-3 mb-2">
                        <small>
                          <i className="bi bi-exclamation-triangle me-2"></i>
                          Unable to fetch balance: {balanceError}
                        </small>
                        <button
                          className="btn btn-sm btn-outline-warning ms-2"
                          onClick={fetchBalance}
                        >
                          Retry
                        </button>
                      </div>
                    ) : (
                      <small className="text-muted">
                        Available Balance: <span className="fw-bold">${balance?.toFixed(2) || '0.00'}</span>
                      </small>
                    )}
                  </div>

                  {/* pret si cumparare */}
                  <div className="d-flex justify-content-between align-items-center">
                    <h3 className="text-warning fw-bold mb-0">${item.price}</h3>
                    <button
                      className="btn btn-lg px-4"
                      style={{ backgroundColor: '#ff7a00', color: '#000', fontWeight: '600' }}
                      onClick={handlePurchase}
                      disabled={balance !== null && balance < item.price}
                    >
                      <i className={`bi ${type === 'event' ? 'bi-ticket-perforated' : 'bi-cart-plus'} me-2`}></i>
                      {balance !== null && balance < item.price ? 'Insufficient Balance' :
                        type === 'event' ? 'Buy Ticket' : 'Purchase'}
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </motion.div>
      </div>

      {/* modal de purchase */}
      {showPurchaseModal && (
        <PurchaseModal
          item={item}
          user={user}
          onClose={() => setShowPurchaseModal(false)}
          onSuccess={() => {
            setShowPurchaseModal(false);
            fetchBalance();
          }}
        />
      )}
    </div>
  );
}

export default CardDetailPage;