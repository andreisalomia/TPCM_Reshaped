import React, { useState, useEffect } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import axios from 'axios';

function LinkMsisdnPage() {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  
  const providerUserId = searchParams.get('providerUserId');
  const email = searchParams.get('email');
  const displayName = searchParams.get('displayName');

    const validateMsisdn = (value) => {
        const cleaned = value.replace(/\s+/g, "");
        if (!cleaned) return "MSISDN is required";
        if (!/^\+?\d+$/.test(cleaned))
            return "MSISDN must contain only numbers";
        if (cleaned.length < 8 || cleaned.length > 16)
            return "MSISDN must be between 8 and 16 digits";
        return null;
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError("");

        const validationError = validateMsisdn(msisdn);
        if (validationError) {
            setError(validationError);
            return;
        }

        setLoading(true);
        try {
            const cleanedMsisdn = msisdn.replace(/\s+/g, "");

            const response = await axios.post(
                "http://localhost:8080/api/auth/link-msisdn",
                {
                    msisdn: cleanedMsisdn,
                    providerUserId,
                    email,
                    displayName,
                }
            );

            localStorage.setItem("tpcm_token", response.data.token);

            navigate("/");
        } catch (err) {
            setError(err.response?.data?.error || "Failed to link MSISDN");
        } finally {
            setLoading(false);
        }
    };

    if (!providerUserId) {
        navigate("/login");
        return null;
    }

    return (
        <div className="container-fluid vh-100 d-flex align-items-center justify-content-center bg-dark">
            <div className="row w-100">
                <div className="col-md-4 offset-md-4">
                    <div className="card shadow-lg bg-black text-light border-0 position-relative">
                        <img
                            src="/orange_logo.jpg"
                            alt="Orange Logo"
                            className="position-absolute"
                            style={{
                                top: "15px",
                                left: "15px",
                                width: "70px",
                                height: "70px",
                                objectFit: "contain",
                                zIndex: 10,
                            }}
                        />

                        <div className="card-body p-5">
                            <div
                                className="text-center mb-4"
                                style={{ paddingTop: "40px" }}
                            >
                                <h2
                                    className="card-title"
                                    style={{ color: "#ff7a00" }}
                                >
                                    <i className="bi bi-phone me-2"></i>
                                    Link Your MSISDN
                                </h2>
                                <p className="text-secondary">
                                    Welcome, {displayName}!
                                </p>
                                <p className="text-muted small">{email}</p>
                            </div>

                            {error && (
                                <div
                                    className="alert alert-danger"
                                    role="alert"
                                >
                                    <i className="bi bi-exclamation-triangle me-2"></i>
                                    {error}
                                </div>
                            )}

                            <form onSubmit={handleSubmit}>
                                <div className="mb-4">
                                    <label className="form-label text-light">
                                        <i className="bi bi-phone me-2"></i>
                                        Phone Number (MSISDN)
                                    </label>
                                    <input
                                        type="tel"
                                        className="form-control form-control-lg bg-dark text-light border-secondary"
                                        placeholder="+40744340284"
                                        value={msisdn}
                                        onChange={(e) => {
                                            setMsisdn(e.target.value);
                                            if (error) setError("");
                                        }}
                                        disabled={loading}
                                        autoFocus
                                    />
                                    <small className="text-secondary">
                                        Enter your registered mobile number
                                    </small>
                                </div>

                                <button
                                    type="submit"
                                    className="btn btn-lg w-100 text-white fw-bold"
                                    style={{
                                        backgroundColor: "#ff7a00",
                                        border: "none",
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
