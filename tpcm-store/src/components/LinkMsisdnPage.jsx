import React, { useState, useEffect } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import axios from 'axios';
import { jwtUtils } from '../utils/jwtUtils';

function LinkMsisdnPage() {
    const navigate = useNavigate();
    const [searchParams] = useSearchParams();

    const [msisdn, setMsisdn] = useState('');
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);

    const providerUserId = searchParams.get('providerUserId');
    const email = searchParams.get('email');
    const displayName = searchParams.get('displayName');

    const validateMsisdn = (value) => {
        const cleaned = value.replace(/\s+/g, '');
        if (!cleaned) return 'MSISDN is required';
        if (!/^\+?\d+$/.test(cleaned)) return 'MSISDN must contain only numbers';
        if (cleaned.length < 8 || cleaned.length > 16) return 'MSISDN must be between 8 and 16 digits';
        return null;
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');

        const validationError = validateMsisdn(msisdn);
        if (validationError) {
            setError(validationError);
            return;
        }

        setLoading(true);
        try {
            const cleanedMsisdn = msisdn.replace(/\s+/g, '');

            const response = await axios.post('http://localhost:8080/api/auth/link-msisdn', {
                msisdn: cleanedMsisdn,
                providerUserId,
                email,
                displayName,
            });

            jwtUtils.saveToken(response.data.token);

            window.location.href = '/';
        } catch (err) {
            setError(err.response?.data?.error || 'Failed to link MSISDN');
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        if (!providerUserId) {
            navigate('/login', { replace: true });
        }
    }, [providerUserId, navigate]);

    if (!providerUserId) {
        return null;
    }

    return (
        <div className="container-fluid vh-100 d-flex align-items-center justify-content-center bg-dark">
            <div className="row w-100">
                <div className="col-md-4 offset-md-4">
                    <div className="card shadow-lg bg-black text-light border-0 position-relative">
                        <img
                            src="/store_icon.png"
                            alt="Orange Logo"
                            className="position-absolute"
                            style={{
                                top: '15px',
                                left: '15px',
                                width: '70px',
                                height: '70px',
                                objectFit: 'contain',
                                zIndex: 10,
                            }}
                        />

                        <div className="card-body p-5">
                            <div className="text-center mb-4" style={{ paddingTop: '40px' }}>
                                <h2 className="card-title" style={{ color: '#ff7a00' }}>
                                    <i className="bi bi-phone me-2"></i>
                                    Link Your Phone Number
                                </h2>
                                <p className="text-secondary">Welcome, {displayName}!</p>
                                {/* make text gray */}
                                <p className="text-secondary">Please enter your mobile number to complete registration</p>
                            </div>

                            <form onSubmit={handleSubmit}>
                                <div className="mb-4">
                                    <label htmlFor="msisdn" className="form-label" style={{ color: '#ff7a00' }}>
                                        Mobile Number (MSISDN)
                                    </label>
                                    <input
                                        type="text"
                                        className="form-control form-control-lg bg-dark text-light border-secondary"
                                        id="msisdn"
                                        placeholder="Enter your phone number"
                                        value={msisdn}
                                        onChange={(e) => setMsisdn(e.target.value)}
                                        disabled={loading}
                                        style={{
                                            borderColor: error ? '#dc3545' : '',
                                        }}
                                    />
                                    {error && (
                                        <div className="text-danger mt-2 small">
                                            <i className="bi bi-exclamation-circle me-1"></i>
                                            {error}
                                        </div>
                                    )}
                                </div>

                                <button
                                    type="submit"
                                    className="btn btn-lg w-100 fw-bold"
                                    style={{
                                        backgroundColor: '#ff7a00',
                                        border: 'none',
                                        color: '#000',
                                    }}
                                    disabled={loading}
                                >
                                    {loading ? (
                                        <>
                                            <span className="spinner-border spinner-border-sm me-2"></span>
                                            Linking...
                                        </>
                                    ) : (
                                        <>
                                            <i className="bi bi-link-45deg me-2"></i>
                                            Link Account
                                        </>
                                    )}
                                </button>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}

export default LinkMsisdnPage;
