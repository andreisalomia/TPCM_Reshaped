import React, { useEffect } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';

function AuthCallback() {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();

  useEffect(() => {
    const token = searchParams.get('token');
    
    if (token) {
      localStorage.setItem('tpcm_token', token);
      
      window.location.href = '/';
    } else {
      navigate('/login');
    }
  }, [searchParams, navigate]);

  return (
    <div className="container-fluid vh-100 d-flex align-items-center justify-content-center bg-dark">
      <div className="text-center">
        <div className="spinner-border text-warning mb-3" role="status">
          <span className="visually-hidden">Loading...</span>
        </div>
        <p className="text-light">Completing sign in...</p>
      </div>
    </div>
  );
}

export default AuthCallback;