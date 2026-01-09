import React from 'react';

function LoginPage() {
  const handleGoogleLogin = () => {
    window.location.href = 'http://localhost:8080/oauth2/authorization/google';
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
                top: '15px',
                left: '15px',
                width: '70px',
                height: '70px',
                objectFit: 'contain',
                zIndex: 10
              }}
            />

            <div className="card-body p-5">
              <div className="text-center mb-4" style={{ paddingTop: '40px' }}>
                <h2 className="card-title" style={{ color: '#ff7a00' }}>
                  <i className="bi bi-phone me-2"></i>
                  TPCM Store
                </h2>
                <p className="text-secondary">Sign in to continue</p>
              </div>

              <button
                onClick={handleGoogleLogin}
                className="btn btn-lg w-100 bg-white text-dark fw-bold mb-3"
                style={{ border: 'none' }}
              >
                <i className="bi bi-google me-2"></i>
                Continue with Google
              </button>
              
              <small className="text-white d-block text-center">
                Secure authentication powered by Google
              </small>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default LoginPage;