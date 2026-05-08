import React, { useState } from 'react';
import { motion } from 'framer-motion';
import { transactionApi } from '../services/tpcmApi';
import { smsServices, getCategories } from '../data/smsData';

function SmsTab({ user }) {
    const [selectedCategory, setSelectedCategory] = useState('all');
    const [selectedService, setSelectedService] = useState(null);
    const [step, setStep] = useState(1);
    const [transaction, setTransaction] = useState(null);
    const [replyMessage, setReplyMessage] = useState('');
    const [error, setError] = useState('');

    const categories = getCategories();
    const filteredServices = selectedCategory === 'all'
        ? smsServices
        : smsServices.filter(s => s.category === selectedCategory);

    const handleSelectService = (service) => {
        setSelectedService(service);
        setStep(1);
        setTransaction(null);
        setReplyMessage('');
        setError('');
    };

    const handleClose = () => {
        setSelectedService(null);
    };

    const handleConfirm = async () => {
        setStep(2);
        try {
            const requestResponse = await transactionApi.requestTransaction(
                user.msisdn, selectedService.price, 21, 'SMS'
            );
            setTransaction(requestResponse);
            await transactionApi.commitTransaction(requestResponse.transactionId, selectedService.price);
            setReplyMessage(selectedService.getReply());
            setStep(3);
        } catch (err) {
            setError(err.response?.data?.message || 'Maximum limit for one transaction exceeded or monthly limit reached.');
            if (transaction?.transactionId) {
                try {
                    await transactionApi.cancelTransaction(transaction.transactionId);
                } catch (_) {}
            }
            setStep(3);
        }
    };

    return (
        <div className="container-fluid px-0">
            <motion.div initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }} transition={{ duration: 0.3 }}>

                <div className="d-flex flex-wrap gap-2 mb-4">
                    {categories.map(cat => (
                        <button
                            key={cat}
                            className="btn btn-sm"
                            style={{
                                backgroundColor: selectedCategory === cat ? '#ff7a00' : '#6c757d',
                                color: selectedCategory === cat ? '#000' : '#fff',
                                border: 'none',
                                borderRadius: '20px',
                            }}
                            onClick={() => setSelectedCategory(cat)}
                        >
                            {cat.charAt(0).toUpperCase() + cat.slice(1)}
                        </button>
                    ))}
                </div>

                <div className="row g-3">
                    {filteredServices.map(service => (
                        <div key={service.id} className="col-md-4 col-sm-6">
                            <motion.div whileHover={{ scale: 1.02 }} transition={{ duration: 0.15 }}>
                                <div className="card shadow-sm h-100">
                                    <div className="card-body d-flex flex-column">
                                        <div className="d-flex align-items-center mb-3">
                                            <div
                                                className="rounded-circle d-flex align-items-center justify-content-center me-3"
                                                style={{ width: '48px', height: '48px', backgroundColor: '#fff3e0', flexShrink: 0 }}
                                            >
                                                <i className={`bi ${service.icon}`} style={{ color: '#ff7a00', fontSize: '1.4rem' }}></i>
                                            </div>
                                            <div>
                                                <h6 className="mb-0 fw-bold">{service.title}</h6>
                                                <small className="text-muted">Short number: <strong>{service.shortNumber}</strong></small>
                                            </div>
                                        </div>
                                        <p className="text-muted small flex-grow-1">{service.description}</p>
                                        <div className="d-flex justify-content-between align-items-center mt-2">
                                            <span className="fw-bold" style={{ color: '#ff7a00', fontSize: '1.1rem' }}>
                                                {service.price.toFixed(2)} EUR
                                            </span>
                                            <button
                                                className="btn btn-sm text-white"
                                                style={{ backgroundColor: '#ff7a00', border: 'none', borderRadius: '20px' }}
                                                onClick={() => handleSelectService(service)}
                                            >
                                                <i className="bi bi-send-fill me-1"></i>
                                                Send SMS
                                            </button>
                                        </div>
                                    </div>
                                </div>
                            </motion.div>
                        </div>
                    ))}
                </div>
            </motion.div>

            {selectedService && (
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
                                    <button type="button" className="btn-close" onClick={handleClose}></button>
                                )}
                            </div>

                            <div className="modal-body">
                                {step === 1 && (
                                    <div>
                                        <div className="text-center mb-4">
                                            <div
                                                className="rounded-circle d-flex align-items-center justify-content-center mx-auto mb-3"
                                                style={{ width: '64px', height: '64px', backgroundColor: '#fff3e0' }}
                                            >
                                                <i className={`bi ${selectedService.icon}`} style={{ color: '#ff7a00', fontSize: '2rem' }}></i>
                                            </div>
                                            <h5>{selectedService.title}</h5>
                                            <h3 style={{ color: '#ff7a00' }}>{selectedService.price.toFixed(2)} RON</h3>
                                        </div>
                                        <div className="alert alert-info">
                                            <i className="bi bi-info-circle me-2"></i>
                                            You are about to send an SMS to short number <strong>{selectedService.shortNumber}</strong>.
                                            <strong> {selectedService.price.toFixed(2)} RON</strong> will be deducted from your limit.
                                        </div>
                                    </div>
                                )}

                                {step === 2 && (
                                    <div className="text-center py-3">
                                        <div className="spinner-border mb-3" style={{ color: '#ff7a00' }} role="status">
                                            <span className="visually-hidden">Processing...</span>
                                        </div>
                                        <p>Processing your transaction...</p>
                                        {transaction && (
                                            <small className="text-muted">Transaction ID: {transaction.transactionId}</small>
                                        )}
                                    </div>
                                )}

                                {step === 3 && !error && (
                                    <div className="text-center py-2">
                                        <i className="bi bi-check-circle-fill text-success" style={{ fontSize: '3rem' }}></i>
                                        <h5 className="mt-3">Service activated successfully!</h5>
                                        <div className="alert alert-success mt-3 text-start">
                                            <i className="bi bi-phone me-2"></i>
                                            {replyMessage}
                                        </div>
                                        {transaction && (
                                            <small className="text-muted">Transaction ID: {transaction.transactionId}</small>
                                        )}
                                    </div>
                                )}

                                {step === 3 && error && (
                                    <div className="text-center py-2">
                                        <i className="bi bi-x-circle-fill text-danger" style={{ fontSize: '3rem' }}></i>
                                        <h5 className="mt-3">Activation failed</h5>
                                        <div className="alert alert-danger mt-3 text-start">
                                            <i className="bi bi-exclamation-triangle me-2"></i>
                                            {error}
                                        </div>
                                    </div>
                                )}
                            </div>

                            <div className="modal-footer">
                                {step === 1 && (
                                    <>
                                        <button className="btn btn-secondary" onClick={handleClose}>
                                            Cancel
                                        </button>
                                        <button
                                            className="btn text-white"
                                            style={{ backgroundColor: '#ff7a00', border: 'none' }}
                                            onClick={handleConfirm}
                                        >
                                            <i className="bi bi-send-fill me-1"></i>
                                            Confirm
                                        </button>
                                    </>
                                )}
                                {step === 3 && (
                                    <button
                                        className="btn text-white"
                                        style={{ backgroundColor: '#ff7a00', border: 'none' }}
                                        onClick={handleClose}
                                    >
                                        Close
                                    </button>
                                )}
                            </div>
                        </motion.div>
                    </div>
                </div>
            )}
        </div>
    );
}

export default SmsTab;