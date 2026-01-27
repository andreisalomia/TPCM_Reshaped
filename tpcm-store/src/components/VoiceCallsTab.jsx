import React, { useState, useEffect, useRef } from 'react';
import { motion } from 'framer-motion';
import { voiceCallsApi } from '../services/voiceSimulatorApi';

function VoiceCallsTab({ user }) {
    const [premiumNumbers, setPremiumNumbers] = useState([]);
    const [selectedPremiumNumber, setSelectedPremiumNumber] = useState('');
    const [durationSeconds, setDurationSeconds] = useState(60);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    const [activeCall, setActiveCall] = useState(null);
    const [callHistory, setCallHistory] = useState([]);
    const [elapsedTime, setElapsedTime] = useState(0);
    const [currentPhase, setCurrentPhase] = useState('');
    const timerRef = useRef(null);

    useEffect(() => {
        loadPremiumNumbers();
        loadCallHistory();
    }, []);

    useEffect(() => {
        if (activeCall) {
            startTimer();
        } else {
            stopTimer();
        }

        return () => {
            stopTimer();
        };
    }, [activeCall]);

    useEffect(() => {
        if (activeCall && elapsedTime >= activeCall.requestedDuration) {
            stopTimer();

            setActiveCall(null);
            setElapsedTime(0);
            setCurrentPhase('');

            loadCallHistory();
        }
    }, [elapsedTime, activeCall]);

    const loadPremiumNumbers = async () => {
        try {
            const numbers = await voiceCallsApi.getAllPremiumNumbers();
            setPremiumNumbers(numbers);
            if (numbers.length > 0) {
                setSelectedPremiumNumber(numbers[0].phoneNumber);
            }
        } catch (err) {
            console.error('Error loading premium numbers:', err);
        }
    };

    const loadCallHistory = async () => {
        try {
            const calls = await voiceCallsApi.getAllCalls();
            const sortedCalls = calls
                .filter((call) => call.callerNumber === user.msisdn)
                .sort((a, b) => new Date(b.callTimestamp) - new Date(a.callTimestamp))
                .slice(0, 5);
            setCallHistory(sortedCalls);
        } catch (err) {
            console.error('Error loading call history:', err);
        }
    };

    const startTimer = () => {
        timerRef.current = setInterval(() => {
            setElapsedTime((prev) => prev + 1);
        }, 1000);
    };

    const stopTimer = () => {
        if (timerRef.current) {
            clearInterval(timerRef.current);
            timerRef.current = null;
        }
    };

    const handleStartCall = async (e) => {
        e.preventDefault();
        setError('');
        setLoading(true);

        try {
            const response = await voiceCallsApi.startCall(user.msisdn, selectedPremiumNumber, durationSeconds);

            if (response.status === 'ACCEPTED') {
                setActiveCall({
                    callId: response.callId,
                    premiumNumber: selectedPremiumNumber,
                    requestedDuration: durationSeconds,
                    startTime: new Date(),
                });
                setElapsedTime(0);

                const premiumConfig = premiumNumbers.find((num) => num.phoneNumber === selectedPremiumNumber);

                if (premiumConfig?.duration1 > 0) {
                    const isFree = !premiumConfig.cost1 || premiumConfig.cost1 === 0;
                    setCurrentPhase(isFree ? 'Free Phase' : 'Phase 1 (Paid)');
                } else if (premiumConfig?.duration2 > 0) {
                    setCurrentPhase('Phase 2 (Indivisible)');
                } else {
                    setCurrentPhase('Unknown Phase');
                }
            } else {
                setError(response.message || 'Failed to start call');
            }
        } catch (err) {
            setError(err.response?.data?.message || 'Error starting call');
        } finally {
            setLoading(false);
        }
    };

    const formatDuration = (seconds) => {
        const mins = Math.floor(seconds / 60);
        const secs = seconds % 60;
        return `${mins}:${secs.toString().padStart(2, '0')}`;
    };

    const formatTimestamp = (timestamp) => {
        return new Date(timestamp).toLocaleString('ro-RO');
    };

    const getSelectedPremiumConfig = () => {
        return premiumNumbers.find((num) => num.phoneNumber === selectedPremiumNumber);
    };

    const calculateEstimatedCost = () => {
        const config = getSelectedPremiumConfig();
        if (!config) return 0;

        let cost = 0;
        let remaining = durationSeconds;

        if (config.duration1 > 0) {
            const phase1Time = Math.min(remaining, config.duration1);
            if (config.cost1 && config.cost1 > 0) {
                cost += config.cost1;
            }
            remaining -= phase1Time;
        }

        if (remaining <= 0) {
            return cost.toFixed(2);
        }

        if (config.duration2 > 0) {
            cost += config.cost2;
            remaining -= config.duration2;
        }

        if (remaining <= 0) {
            return cost.toFixed(2);
        }

        if (config.cost3 && config.duration3) {
            const costPerSecond = config.cost3 / config.duration3;
            cost += costPerSecond * remaining;
        } else if (config.cost2 && config.duration2) {
            const segments = Math.ceil(remaining / config.duration2);
            cost += segments * config.cost2;
        }

        return cost.toFixed(2);
    };

    const getCurrentPhaseInfo = () => {
        const config = getSelectedPremiumConfig();
        if (!config || !activeCall) return null;

        if (elapsedTime < config.duration1) {
            const isFree = !config.cost1 || config.cost1 === 0;
            return {
                name: isFree ? 'Free Phase' : 'Phase 1',
                color: isFree ? 'success' : 'warning',
                description: isFree ? `${config.duration1}s free calling` : `${config.cost1} EUR for ${config.duration1}s`,
            };
        } else if (elapsedTime < config.duration1 + config.duration2 && config.duration2 > 0) {
            return {
                name: 'Phase 2 (Indivisible)',
                color: 'warning',
                description: `${config.cost2} EUR for ${config.duration2}s`,
            };
        } else {
            const hasPhase3 = config.cost3 && config.duration3;
            return {
                name: hasPhase3 ? 'Phase 3 (Divisible)' : 'Phase 2 (Repeating)',
                color: 'danger',
                description: hasPhase3 ? `${config.cost3} EUR per ${config.duration3}s segment` : `Repeating ${config.cost2} EUR per ${config.duration2}s`,
            };
        }
    };

    const phaseInfo = getCurrentPhaseInfo();

    return (
        <div className="container mt-4">
            <motion.div initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }} transition={{ duration: 0.5 }}>
                <div className="text-center mb-4">
                    <h2 className="fw-bold" style={{ color: '#212529' }}>
                        <i className="bi bi-telephone-fill me-2" style={{ color: '#ff7a00' }}></i>
                        Voice Call Simulator
                    </h2>
                    <p className="text-muted">Simulate premium rate calls in real-time</p>
                </div>

                <div className="row g-4">
                    <div className="col-lg-6">
                        <div className="card shadow-sm h-100">
                            <div className="card-header text-white" style={{ backgroundColor: '#ff7a00' }}>
                                <h5 className="mb-0">
                                    <i className="bi bi-play-circle-fill me-2"></i>
                                    Start New Call
                                </h5>
                            </div>
                            <div className="card-body">
                                {error && (
                                    <div className="alert alert-danger" role="alert">
                                        <i className="bi bi-exclamation-triangle-fill me-2"></i>
                                        {error}
                                    </div>
                                )}

                                <form onSubmit={handleStartCall}>
                                    <div className="mb-3">
                                        <label className="form-label fw-bold">
                                            <i className="bi bi-person-circle me-2"></i>
                                            Caller Number
                                        </label>
                                        <input type="text" className="form-control" value={user.msisdn} disabled />
                                    </div>

                                    <div className="mb-3">
                                        <label className="form-label fw-bold">
                                            <i className="bi bi-hash me-2"></i>
                                            Premium Number
                                        </label>
                                        <select className="form-select" value={selectedPremiumNumber} onChange={(e) => setSelectedPremiumNumber(e.target.value)} disabled={activeCall || loading}>
                                            {premiumNumbers.map((num) => (
                                                <option key={num.phoneNumber} value={num.phoneNumber}>
                                                    {num.phoneNumber}
                                                </option>
                                            ))}
                                        </select>
                                    </div>

                                    <div className="mb-3">
                                        <label className="form-label fw-bold">
                                            <i className="bi bi-clock-fill me-2"></i>
                                            Duration (seconds)
                                        </label>
                                        <input
                                            type="number"
                                            className="form-control"
                                            min="1"
                                            max="300"
                                            value={durationSeconds}
                                            onChange={(e) => setDurationSeconds(parseInt(e.target.value))}
                                            disabled={activeCall || loading}
                                        />
                                        <small className="text-muted">Duration: {formatDuration(durationSeconds)}</small>
                                    </div>

                                    {getSelectedPremiumConfig() && (
                                        <div className="mb-3">
                                            <div className="card bg-light">
                                                <div className="card-body py-2">
                                                    <h6 className="mb-2">
                                                        <i className="bi bi-info-circle-fill me-2 text-primary"></i>
                                                        Premium Number Configuration
                                                    </h6>
                                                    <div className="row g-2">
                                                        <div className="col-4">
                                                            <small className="text-muted d-block">
                                                                Phase 1 {!getSelectedPremiumConfig().cost1 || getSelectedPremiumConfig().cost1 === 0 ? '(Free)' : ''}
                                                            </small>
                                                            <strong>
                                                                {getSelectedPremiumConfig().cost1 && getSelectedPremiumConfig().cost1 > 0 ? `${getSelectedPremiumConfig().cost1}€ / ` : ''}
                                                                {getSelectedPremiumConfig().duration1}s
                                                            </strong>
                                                        </div>
                                                        <div className="col-4">
                                                            <small className="text-muted d-block">Phase 2</small>
                                                            <strong>
                                                                {getSelectedPremiumConfig().cost2}s{' / '}
                                                                {getSelectedPremiumConfig().duration2}€
                                                            </strong>
                                                        </div>
                                                        <div className="col-4">
                                                            <small className="text-muted d-block">Phase 3</small>
                                                            <strong>
                                                                {getSelectedPremiumConfig().cost3 || 'N/A'}
                                                                {getSelectedPremiumConfig().cost3 && '€'}
                                                                {getSelectedPremiumConfig().duration3 && ` / ${getSelectedPremiumConfig().duration3}s`}
                                                            </strong>
                                                        </div>
                                                    </div>
                                                    <div className="mt-2 pt-2 border-top">
                                                        <small className="text-muted">Estimated Cost:</small>
                                                        <strong className="ms-2 text-danger">{calculateEstimatedCost()} EUR</strong>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    )}

                                    <button
                                        type="submit"
                                        className="btn w-100"
                                        style={{
                                            backgroundColor: '#ff7a00',
                                            color: '#000',
                                            fontWeight: 'bold',
                                        }}
                                        disabled={activeCall || loading}
                                    >
                                        {loading ? (
                                            <>
                                                <span className="spinner-border spinner-border-sm me-2" role="status"></span>
                                                Starting Call...
                                            </>
                                        ) : (
                                            <>
                                                <i className="bi bi-telephone-outbound-fill me-2"></i>
                                                Start Call
                                            </>
                                        )}
                                    </button>
                                </form>
                            </div>
                        </div>
                    </div>

                    <div className="col-lg-6">
                        <div className="card shadow-sm h-100">
                            <div className={`card-header text-white ${activeCall ? 'bg-success' : 'bg-secondary'}`}>
                                <h5 className="mb-0">
                                    <i className="bi bi-activity me-2"></i>
                                    {activeCall ? 'Active Call' : 'No Active Call'}
                                </h5>
                            </div>
                            <div className="card-body d-flex flex-column">
                                {activeCall ? (
                                    <div
                                        className="text-center"
                                        style={{
                                            display: 'flex',
                                            flexDirection: 'column',
                                            flex: 1,
                                        }}
                                    >
                                        <div className="mb-4">
                                            <div
                                                className="rounded-circle bg-success mx-auto mb-3 d-flex align-items-center justify-content-center animate-pulse"
                                                style={{
                                                    width: '100px',
                                                    height: '100px',
                                                }}
                                            >
                                                <i className="bi bi-telephone-fill text-white" style={{ fontSize: '3rem' }}></i>
                                            </div>
                                            <h3 className="fw-bold mb-0">{formatDuration(elapsedTime)}</h3>
                                            <small className="text-muted">of {formatDuration(activeCall.requestedDuration)}</small>
                                        </div>

                                        <div className="mb-4">
                                            <div className="progress" style={{ height: '25px' }}>
                                                <div
                                                    className="progress-bar progress-bar-striped progress-bar-animated bg-success"
                                                    role="progressbar"
                                                    style={{
                                                        width: `${(elapsedTime / activeCall.requestedDuration) * 100}%`,
                                                    }}
                                                >
                                                    {Math.round((elapsedTime / activeCall.requestedDuration) * 100)}%
                                                </div>
                                            </div>
                                        </div>

                                        <div className="mb-0" style={{ flex: 1 }}>
                                            <div className="card bg-light h-100">
                                                <div className="card-body">
                                                    <div className="row g-3">
                                                        <div className="col-6">
                                                            <small className="text-muted d-block">Call ID</small>
                                                            <strong>#{activeCall.callId}</strong>
                                                        </div>
                                                        <div className="col-6">
                                                            <small className="text-muted d-block">Premium Number</small>
                                                            <strong>{activeCall.premiumNumber}</strong>
                                                        </div>
                                                        {phaseInfo && (
                                                            <div className="col-12">
                                                                <small className="text-muted d-block mb-2">Current Phase</small>
                                                                <div className={`alert alert-${phaseInfo.color === 'success' ? 'success' : phaseInfo.color === 'warning' ? 'warning' : 'danger'} mb-0`}>
                                                                    <div className="d-flex align-items-center">
                                                                        <i
                                                                            className="bi bi-info-circle-fill me-2"
                                                                            style={{
                                                                                fontSize: '1.2rem',
                                                                            }}
                                                                        ></i>
                                                                        <div>
                                                                            <strong className="d-block">{phaseInfo.name}</strong>
                                                                            <small>{phaseInfo.description}</small>
                                                                        </div>
                                                                    </div>
                                                                </div>
                                                            </div>
                                                        )}
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                ) : (
                                    <div className="text-center text-muted py-5">
                                        <i
                                            className="bi bi-telephone-x"
                                            style={{
                                                fontSize: '4rem',
                                                opacity: 0.3,
                                            }}
                                        ></i>
                                        <p className="mt-3">No active call</p>
                                        <small>Start a call to see real-time monitoring</small>
                                    </div>
                                )}
                            </div>
                        </div>
                    </div>
                </div>

                <div className="row mt-4">
                    <div className="col-12">
                        <div className="card shadow-sm">
                            <div className="card-header text-white" style={{ backgroundColor: '#ff7a00' }}>
                                <h5 className="mb-0">
                                    <i className="bi bi-clock-history me-2"></i>
                                    Recent Calls
                                </h5>
                            </div>
                            <div className="card-body">
                                {callHistory.length > 0 ? (
                                    <div className="table-responsive">
                                        <table className="table table-hover">
                                            <thead>
                                                <tr>
                                                    <th>
                                                        <i className="bi bi-hash me-1"></i>
                                                        Call ID
                                                    </th>
                                                    <th>
                                                        <i className="bi bi-telephone me-1"></i>
                                                        Premium Number
                                                    </th>
                                                    <th>
                                                        <i className="bi bi-clock me-1"></i>
                                                        Duration
                                                    </th>
                                                    <th>
                                                        <i className="bi bi-currency-euro me-1"></i>
                                                        Cost
                                                    </th>
                                                    <th>
                                                        <i className="bi bi-calendar me-1"></i>
                                                        Timestamp
                                                    </th>
                                                </tr>
                                            </thead>
                                            <tbody>
                                                {callHistory.map((call) => (
                                                    <tr key={call.id}>
                                                        <td>
                                                            <span
                                                                className="badge"
                                                                style={{
                                                                    backgroundColor: '#ff7a00',
                                                                    color: '#000',
                                                                }}
                                                            >
                                                                #{call.id}
                                                            </span>
                                                        </td>
                                                        <td>
                                                            <span className="badge bg-danger">{call.premiumNumber?.phoneNumber || 'N/A'}</span>
                                                        </td>
                                                        <td>
                                                            <strong>{formatDuration(call.durationSeconds || 0)}</strong>
                                                        </td>
                                                        <td>
                                                            <span className="text-danger fw-bold">{call.chargedAmount?.toFixed(2)} EUR</span>
                                                        </td>
                                                        <td>
                                                            <small className="text-muted">{formatTimestamp(call.callTimestamp)}</small>
                                                        </td>
                                                    </tr>
                                                ))}
                                            </tbody>
                                        </table>
                                    </div>
                                ) : (
                                    <div className="text-center text-muted py-4">
                                        <i
                                            className="bi bi-inbox"
                                            style={{
                                                fontSize: '3rem',
                                                opacity: 0.3,
                                            }}
                                        ></i>
                                        <p className="mt-2">No call history yet</p>
                                        <small>Your completed calls will appear here</small>
                                    </div>
                                )}
                            </div>
                        </div>
                    </div>
                </div>

                <style>{`
                    @keyframes pulse {
                        0%, 100% {
                            opacity: 1;
                        }
                        50% {
                            opacity: 0.7;
                        }
                    }
                    .animate-pulse {
                        animation: pulse 2s cubic-bezier(0.4, 0, 0.6, 1) infinite;
                    }
                `}</style>
            </motion.div>
        </div>
    );
}

export default VoiceCallsTab;
