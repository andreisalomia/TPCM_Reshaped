import React, { useState } from "react";

function LoginForm({ onLogin }) {
    const [msisdn, setMsisdn] = useState("");
    const [error, setError] = useState("");

    const validateMsisdn = (value) => {
        const cleaned = value.replace(/\s+/g, "");
        if (!cleaned) {
            return "MSISDN is required";
        }
        if (!/^\+?\d+$/.test(cleaned)) {
            return "MSISDN must contain only numbers";
        }
        if (cleaned.length < 8 || cleaned.length > 16) {
            return "MSISDN must be between 8 and 16 characters";
        }
        return null;
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        setError("");

        const validationError = validateMsisdn(msisdn);
        if (validationError) {
            setError(validationError);
            return;
        }

        const cleanedMsisdn = msisdn.replace(/\s+/g, "");
        const userData = {
            msisdn: cleanedMsisdn,
            loginTime: new Date().toISOString(),
            authenticated: true,
        };
        onLogin(userData);
    };

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
                                    TPCM Store
                                </h2>
                                <p className="text-secondary">
                                    Enter your MSISDN to continue
                                </p>
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
                                        placeholder="e.g., 40712345678"
                                        value={msisdn}
                                        onChange={(e) => {
                                            setMsisdn(e.target.value);
                                            if (error) setError("");
                                        }}
                                        autoFocus
                                    />
                                    <small className="text-secondary">
                                        Enter your mobile number without spaces
                                        or symbols
                                    </small>
                                </div>

                                <button
                                    type="submit"
                                    className="btn btn-lg w-100 text-white fw-bold"
                                    style={{
                                        backgroundColor: "#ff7a00",
                                        border: "none",
                                    }}
                                >
                                    <i className="bi bi-box-arrow-in-right me-2"></i>
                                    Continue
                                </button>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}

export default LoginForm;
