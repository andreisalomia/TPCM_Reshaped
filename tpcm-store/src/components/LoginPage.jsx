import React from "react";

function LoginPage() {
    const handleGoogleLogin = () => {
        window.location.href =
            "http://localhost:8080/oauth2/authorization/google";
    };

    const handleComingSoon = (provider) => {
        alert(`${provider} login coming soon!`);
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
                                    Sign in to continue
                                </p>
                            </div>

                            <button
                                onClick={handleGoogleLogin}
                                className="btn btn-lg w-100 bg-white text-dark fw-bold mb-3"
                                style={{ border: "none" }}
                            >
                                <i className="bi bi-google me-2"></i>
                                Continue with Google
                            </button>

                            <button
                                onClick={() => handleComingSoon("Apple")}
                                className="btn btn-lg w-100 text-white fw-bold mb-3 position-relative"
                                style={{
                                    backgroundColor: "#555",
                                    border: "1px solid #333",
                                }}
                                onMouseEnter={(e) =>
                                    (e.currentTarget.style.backgroundColor =
                                        "#666")
                                }
                                onMouseLeave={(e) =>
                                    (e.currentTarget.style.backgroundColor =
                                        "#555")
                                }
                            >
                                <i className="bi bi-apple me-2"></i>
                                Continue with Apple
                                <span
                                    className="badge bg-warning text-dark position-absolute"
                                    style={{
                                        top: "10px",
                                        right: "10px",
                                        fontSize: "0.65rem",
                                    }}
                                >
                                    Soon
                                </span>
                            </button>

                            <button
                                onClick={() => handleComingSoon("Facebook")}
                                className="btn btn-lg w-100 text-white fw-bold mb-3 position-relative"
                                style={{
                                    backgroundColor: "#1877F2",
                                    border: "none",
                                }}
                                onMouseEnter={(e) =>
                                    (e.currentTarget.style.backgroundColor =
                                        "#5b9ee8")
                                }
                                onMouseLeave={(e) =>
                                    (e.currentTarget.style.backgroundColor =
                                        "#4a90e2")
                                }
                            >
                                <i className="bi bi-facebook me-2"></i>
                                Continue with Facebook
                                <span
                                    className="badge bg-warning text-dark position-absolute"
                                    style={{
                                        top: "10px",
                                        right: "10px",
                                        fontSize: "0.65rem",
                                    }}
                                >
                                    Soon
                                </span>
                            </button>

                            <small className="text-white d-block text-center mt-3">
                                Secure authentication powered by OAuth 2.0
                            </small>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}

export default LoginPage;
