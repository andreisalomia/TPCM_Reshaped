import React, { useState } from 'react';
import { motion } from 'framer-motion';
import { transactionApi } from '../services/tpcmApi';

function PurchaseModal({ item, user, onClose, onSuccess }) {
  const [step, setStep] = useState(1);
  const [transaction, setTransaction] = useState(null);
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(false);

  const handleConfirmPurchase = async () => {
    setLoading(true);
    setStep(2);

    try {
      const requestResponse = await transactionApi.requestTransaction(
        user.msisdn,
        item.price,
        21
      );

      setTransaction(requestResponse);

      const commitResponse = await transactionApi.commitTransaction(
        requestResponse.transactionId,
        item.price
      );

      setTransaction(commitResponse);
      setStep(3);

      setTimeout(() => {
        onSuccess();
      }, 3000);

    } catch (error) {
      console.error('Purchase error:', error);
      setError(error.response?.data?.message || 'Purchase failed. Please try again.');
      setStep(3);
    } finally {
      setLoading(false);
    }
  };

  const handleCancel = async () => {
    if (transaction && transaction.transactionId) {
      try {
        await transactionApi.cancelTransaction(transaction.transactionId);
      } catch (error) {
        console.error('Error cancelling transaction:', error);
      }
    }
    onClose();
  };

  return (
    <div className="modal d-block" style={{ backgroundColor: 'rgba(0,0,0,0.5)' }}>
      <div className="modal-dialog modal-dialog-centered">
        <motion.div
          className="modal-content"
          initial={{ scale: 0.8, opacity: 0 }}
          animate={{ scale: 1, opacity: 1 }}
          transition={{ duration: 0.3 }}
        >
          <div className="modal-header">
            <h5 className="modal-title">
              {step === 1 && 'Confirm Purchase'}
              {step === 2 && 'Processing Payment'}
              {step === 3 && (error ? 'Payment Failed' : 'Payment Successful')}
            </h5>
            {step !== 2 && (
              <button
                type="button"
                className="btn-close"
                onClick={onClose}
                aria-label="Close"
              ></button>
            )}
          </div>

          <div className="modal-body">
            {step === 1 && (
              <div>
                <div className="text-center mb-4">
                  <h4>{item.title}</h4>
                  <h2 className="text-warning">${item.price}</h2>
                </div>
                <div className="alert alert-info">
                  <i className="bi bi-info-circle me-2"></i>
                  You will be charged ${item.price} from your account balance.
                </div>
                <p className="text-muted">
                  By confirming this purchase, you agree to the terms and conditions.
                </p>
              </div>
            )}

            {step === 2 && (
              <div className="text-center">
                <div className="spinner-border text-warning mb-3" role="status">
                  <span className="visually-hidden">Processing...</span>
                </div>
                <p>Processing your payment...</p>
                {transaction && (
                  <small className="text-muted">
                    Transaction ID: {transaction.transactionId}
                  </small>
                )}
              </div>
            )}

            {step === 3 && !error && (
              <div className="text-center">
                <i className="bi bi-check-circle-fill text-success" style={{ fontSize: '3rem' }}></i>
                <h4 className="mt-3">Payment Successful!</h4>
                <p>Your purchase has been completed successfully.</p>
                {transaction && (
                  <small className="text-muted">
                    Transaction ID: {transaction.transactionId}
                  </small>
                )}
              </div>
            )}

            {step === 3 && error && (
              <div className="text-center">
                <i className="bi bi-x-circle-fill text-danger" style={{ fontSize: '3rem' }}></i>
                <h4 className="mt-3">Payment Failed</h4>
                <p className="text-danger">{error}</p>
              </div>
            )}
          </div>

          <div className="modal-footer">
            {step === 1 && (
              <>
                <button
                  type="button"
                  className="btn btn-secondary"
                  onClick={onClose}
                >
                  Cancel
                </button>
                <button
                  type="button"
                  className="btn btn-primary"
                  style={{ backgroundColor: '#ff7a00', borderColor: '#ff7a00' }}
                  onClick={handleConfirmPurchase}
                  disabled={loading}
                >
                  Confirm Purchase
                </button>
              </>
            )}

            {step === 2 && (
              <button
                type="button"
                className="btn btn-outline-danger"
                onClick={handleCancel}
              >
                Cancel Transaction
              </button>
            )}

            {step === 3 && (
              <button
                type="button"
                className="btn btn-primary"
                style={{ backgroundColor: '#ff7a00', borderColor: '#ff7a00' }}
                onClick={onClose}
              >
                Close
              </button>
            )}
          </div>
        </motion.div>
      </div>
    </div>
  );
}

export default PurchaseModal;