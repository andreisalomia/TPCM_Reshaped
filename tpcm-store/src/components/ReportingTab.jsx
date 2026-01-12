import React, { useState, useEffect } from "react";
import axios from "axios";
import "bootstrap/dist/css/bootstrap.min.css";

const ReportingTab = ({ user }) => {
    const [activeSubTab, setActiveSubTab] = useState("app-transactions");
    const [appTransactions, setAppTransactions] = useState([]);
    const [voiceCalls, setVoiceCalls] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");

    const [searchMsisdn, setSearchMsisdn] = useState("");
    const [activeMsisdn, setActiveMsisdn] = useState(user?.msisdn || "");
    const [searchError, setSearchError] = useState("");

    const [dateFilter, setDateFilter] = useState({
        startDate: "",
        endDate: "",
    });
    const [statusFilter, setStatusFilter] = useState("");

    const createAuthConfig = () => ({
        auth: {
            username: "admin",
            password: "admin",
        },
    });

    const handleSearchMsisdn = async () => {
        setSearchError("");

        if (!searchMsisdn.trim()) {
            setSearchError("Please enter a phone number");
            return;
        }

        const phoneRegex = /^\+?[1-9]\d{1,14}$/;
        if (!phoneRegex.test(searchMsisdn.trim())) {
            setSearchError("Invalid phone number format");
            return;
        }

        const newMsisdn = searchMsisdn.trim();

        if (newMsisdn === activeMsisdn) {
            setAppTransactions([]);
            setVoiceCalls([]);
            if (activeSubTab === "app-transactions") {
                await loadAppTransactions();
            } else {
                await loadVoiceCalls();
            }
        } else {
            setActiveMsisdn(newMsisdn);
            setAppTransactions([]);
            setVoiceCalls([]);
        }
    };

const handleResetSearch = async () => {
    setSearchMsisdn('');
    const originalMsisdn = user?.msisdn || '';
    setSearchError('');
    setAppTransactions([]);
    setVoiceCalls([]);
    
    if (originalMsisdn === activeMsisdn) {
        if (activeSubTab === 'app-transactions') {
            await loadAppTransactions();
        } else {
            await loadVoiceCalls();
        }
    } else {
        setActiveMsisdn(originalMsisdn);
    }
};

    const TPCM_APP_API_URL = "http://localhost:8080/api/app";
    const TPCM_VOICE_API_URL = "http://localhost:4242/api";

    useEffect(() => {
        if (!activeMsisdn) return;

        const loadCurrentTabData = async () => {
            if (activeSubTab === "app-transactions") {
                await loadAppTransactions();
            } else {
                await loadVoiceCalls();
            }
        };

        loadCurrentTabData();
    }, [activeSubTab, activeMsisdn]);

    const loadAppTransactions = async () => {
        setLoading(true);
        setError("");
        try {
            const url = `${TPCM_APP_API_URL}/transactions/search/by-msisdn`;
            const params = { msisdn: activeMsisdn };

            const response = await axios.get(url, {
                params,
                ...createAuthConfig(),
            });

            let transactions = response.data || [];

            transactions = transactions.filter(
                (transaction) => transaction.channel !== "CALL"
            );

            if (dateFilter.startDate || dateFilter.endDate || statusFilter) {
                transactions = transactions.filter((transaction) => {
                    let matchesDate = true;
                    let matchesStatus = true;

                    if (dateFilter.startDate) {
                        const transactionDate = new Date(
                            transaction.createdDate
                        );
                        const startDate = new Date(dateFilter.startDate);
                        matchesDate =
                            matchesDate && transactionDate >= startDate;
                    }

                    if (dateFilter.endDate) {
                        const transactionDate = new Date(
                            transaction.createdDate
                        );
                        const endDate = new Date(dateFilter.endDate);
                        endDate.setHours(23, 59, 59, 999);
                        matchesDate = matchesDate && transactionDate <= endDate;
                    }

                    if (statusFilter) {
                        matchesStatus = transaction.status === statusFilter;
                    }

                    return matchesDate && matchesStatus;
                });
            }

            setAppTransactions(transactions);
        } catch (err) {
            console.error("Error loading transactions:", err);
            setAppTransactions([]);
        } finally {
            setLoading(false);
        }
    };

    const loadVoiceCalls = async () => {
        setLoading(true);
        setError("");
        try {
            const url = `${TPCM_VOICE_API_URL}/voice-calls`;
            const response = await axios.get(url, createAuthConfig());

            let calls = response.data || [];

            calls = calls.filter((call) => call.callerNumber === activeMsisdn);

            if (dateFilter.startDate || dateFilter.endDate) {
                calls = calls.filter((call) => {
                    let matchesDate = true;

                    if (dateFilter.startDate) {
                        const callDate = new Date(call.callTimestamp);
                        const startDate = new Date(dateFilter.startDate);
                        matchesDate = matchesDate && callDate >= startDate;
                    }

                    if (dateFilter.endDate) {
                        const callDate = new Date(call.callTimestamp);
                        const endDate = new Date(dateFilter.endDate);
                        endDate.setHours(23, 59, 59, 999);
                        matchesDate = matchesDate && callDate <= endDate;
                    }

                    return matchesDate;
                });
            }

            setVoiceCalls(calls);
        } catch (err) {
            console.error("Error loading voice calls:", err);
        } finally {
            setLoading(false);
        }
    };

    const handleSubTabChange = async (subTab) => {
        setActiveSubTab(subTab);
        setError("");
    };

    const handleApplyFilters = async () => {
        if (activeSubTab === "app-transactions") {
            await loadAppTransactions();
        } else {
            await loadVoiceCalls();
        }
    };

    const handleResetFilters = async () => {
        setDateFilter({ startDate: "", endDate: "" });
        setStatusFilter("");
        if (activeSubTab === "app-transactions") {
            await loadAppTransactions();
        } else {
            await loadVoiceCalls();
        }
    };

    const formatDuration = (seconds) => {
        const mins = Math.floor(seconds / 60);
        const secs = seconds % 60;
        return `${mins}:${secs.toString().padStart(2, "0")}`;
    };

    const formatDate = (dateString) => {
        return new Date(dateString).toLocaleString("ro-RO");
    };

    return (
        <div className="container-fluid mt-4">
            <div className="row">
                <div className="col-12">
                    <h2>
                        <i className="bi bi-graph-up me-2"></i>
                        Transactions for {activeMsisdn}
                    </h2>
                    <p className="text-muted">
                        TPCM Store & SMS Transactions and Premium Voice Calls
                    </p>
                </div>
            </div>

            {user.role === "ADMIN" && (
                <div className="row mb-4">
                    <div className="col-12">
                        <div className="card border-dark">
                            <div className="card-header bg-dark text-white">
                                <h5 className="mb-0">
                                    <i className="bi bi-search me-2"></i>
                                    Admin Search - Query Any Phone Number
                                </h5>
                            </div>
                            <div className="card-body">
                                <div className="row align-items-end">
                                    <div className="col-md-6">
                                        <label className="form-label fw-bold">
                                            Phone Number (MSISDN)
                                        </label>
                                        <input
                                            type="text"
                                            className={`form-control ${
                                                searchError ? "is-invalid" : ""
                                            }`}
                                            placeholder="Enter phone number (e.g., +40712345678)"
                                            value={searchMsisdn}
                                            onChange={(e) =>
                                                setSearchMsisdn(e.target.value)
                                            }
                                            onKeyPress={(e) => {
                                                if (e.key === "Enter") {
                                                    handleSearchMsisdn();
                                                }
                                            }}
                                        />
                                        {searchError && (
                                            <div className="invalid-feedback">
                                                {searchError}
                                            </div>
                                        )}
                                    </div>
                                    <div className="col-md-3">
                                        <button
                                            className="btn w-100"
                                            style={{
                                                backgroundColor: "#ff7a00",
                                                color: "#000",
                                                border: "none",
                                            }}
                                            onClick={handleSearchMsisdn}
                                            disabled={loading}
                                        >
                                            <i className="bi bi-search me-2"></i>
                                            Search
                                        </button>
                                    </div>
                                    <div className="col-md-3">
                                        <button
                                            className="btn btn-outline-secondary w-100"
                                            onClick={handleResetSearch}
                                            disabled={loading}
                                        >
                                            <i className="bi bi-arrow-counterclockwise me-2"></i>
                                            Reset
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            )}

            <div className="row mb-4">
                <div className="col-12">
                    <div className="card">
                        <div className="card-header">
                            <h5>
                                <i className="bi bi-funnel me-2"></i>Time Filter
                            </h5>
                        </div>
                        <div className="card-body">
                            <div className="row">
                                <div className="col-md-3">
                                    <label className="form-label">
                                        Start Date
                                    </label>
                                    <input
                                        type="date"
                                        className="form-control"
                                        value={dateFilter.startDate}
                                        onChange={(e) =>
                                            setDateFilter({
                                                ...dateFilter,
                                                startDate: e.target.value,
                                            })
                                        }
                                    />
                                </div>
                                <div className="col-md-3">
                                    <label className="form-label">
                                        End Date
                                    </label>
                                    <input
                                        type="date"
                                        className="form-control"
                                        value={dateFilter.endDate}
                                        onChange={(e) =>
                                            setDateFilter({
                                                ...dateFilter,
                                                endDate: e.target.value,
                                            })
                                        }
                                    />
                                </div>
                                {activeSubTab === "app-transactions" && (
                                    <div className="col-md-3">
                                        <label className="form-label">
                                            Transaction Status
                                        </label>
                                        <select
                                            className="form-select"
                                            value={statusFilter}
                                            onChange={(e) =>
                                                setStatusFilter(e.target.value)
                                            }
                                        >
                                            <option value="">All</option>
                                            <option value="PENDING">
                                                PENDING
                                            </option>
                                            <option value="COMMITTED">
                                                COMMITTED
                                            </option>
                                            <option value="COMPLETED">
                                                FAILED
                                            </option>
                                        </select>
                                    </div>
                                )}
                                <div className="col-md-3 d-flex align-items-end">
                                    <button
                                        className="btn btn-primary me-2"
                                        onClick={handleApplyFilters}
                                        disabled={loading}
                                        style={{
                                            backgroundColor: "#ff7a00",
                                            borderColor: "#ff7a00",
                                            color: "#000",
                                        }}
                                    >
                                        <i className="bi bi-search me-1"></i>
                                        Search
                                    </button>
                                    <button
                                        className="btn btn-outline-secondary"
                                        onClick={handleResetFilters}
                                    >
                                        <i className="bi bi-arrow-clockwise me-1"></i>
                                        Reset
                                    </button>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <div className="row">
                <div className="col-12">
                    <div className="card">
                        <div className="card-header">
                            <ul className="nav nav-tabs card-header-tabs">
                                <li className="nav-item">
                                    <button
                                        className={`nav-link ${
                                            activeSubTab === "app-transactions"
                                                ? "active"
                                                : ""
                                        }`}
                                        style={{
                                            backgroundColor:
                                                activeSubTab ===
                                                "app-transactions"
                                                    ? "#ff7a00"
                                                    : "transparent",
                                            color:
                                                activeSubTab ===
                                                "app-transactions"
                                                    ? "#000"
                                                    : "#000",
                                            borderColor:
                                                activeSubTab ===
                                                "app-transactions"
                                                    ? "#ff7a00"
                                                    : "transparent",
                                        }}
                                        onClick={() =>
                                            handleSubTabChange(
                                                "app-transactions"
                                            )
                                        }
                                    >
                                        <i className="bi bi-phone me-2"></i>
                                        TPCM Store & SMS Transactions
                                    </button>
                                </li>
                                <li className="nav-item">
                                    <button
                                        className={`nav-link ${
                                            activeSubTab === "voice-calls"
                                                ? "active"
                                                : ""
                                        }`}
                                        style={{
                                            backgroundColor:
                                                activeSubTab === "voice-calls"
                                                    ? "#ff7a00"
                                                    : "transparent",
                                            color:
                                                activeSubTab === "voice-calls"
                                                    ? "#000"
                                                    : "#000",
                                            borderColor:
                                                activeSubTab === "voice-calls"
                                                    ? "#ff7a00"
                                                    : "transparent",
                                        }}
                                        onClick={() =>
                                            handleSubTabChange("voice-calls")
                                        }
                                    >
                                        <i className="bi bi-telephone me-2"></i>
                                        Premium Voice Calls
                                    </button>
                                </li>
                            </ul>
                        </div>

                        <div className="card-body">
                            {error && (
                                <div
                                    className="alert alert-danger"
                                    role="alert"
                                >
                                    <i className="bi bi-exclamation-triangle me-2"></i>
                                    {error}
                                </div>
                            )}

                            {activeSubTab === "app-transactions" &&
                                !loading && (
                                    <div>
                                        {appTransactions.length === 0 ? (
                                            <div className="alert alert-info">
                                                <i className="bi bi-info-circle me-2"></i>
                                                You have not made any
                                                transactions in the selected
                                                period.
                                            </div>
                                        ) : (
                                            <div className="table-responsive">
                                                <table className="table table-striped table-hover">
                                                    <thead className="table-dark">
                                                        <tr>
                                                            <th>
                                                                Transaction ID
                                                            </th>
                                                            <th>
                                                                Amount (EUR)
                                                            </th>
                                                            <th>Channel</th>
                                                            <th>Status</th>
                                                            <th>Date</th>
                                                        </tr>
                                                    </thead>
                                                    <tbody>
                                                        {appTransactions.map(
                                                            (transaction) => (
                                                                <tr
                                                                    key={
                                                                        transaction.transactionId
                                                                    }
                                                                >
                                                                    <td>
                                                                        <span
                                                                            className="badge"
                                                                            style={{
                                                                                backgroundColor:
                                                                                    "#ff7a00",
                                                                                color: "#000",
                                                                            }}
                                                                        >
                                                                            #
                                                                            {
                                                                                transaction.transactionId
                                                                            }
                                                                        </span>
                                                                    </td>
                                                                    <td>
                                                                        <span className="fw-bold">
                                                                            {transaction.amount?.toFixed(
                                                                                2
                                                                            )}{" "}
                                                                            EUR
                                                                        </span>
                                                                    </td>
                                                                    <td>
                                                                        <span
                                                                            className={`badge ${
                                                                                transaction.channel ===
                                                                                "APP"
                                                                                    ? "bg-info"
                                                                                    : "bg-secondary"
                                                                            }`}
                                                                        >
                                                                            {
                                                                                transaction.channel
                                                                            }
                                                                        </span>
                                                                    </td>
                                                                    <td>
                                                                        <span
                                                                            className={`badge ${
                                                                                transaction.status ===
                                                                                "COMMITTED"
                                                                                    ? "bg-success"
                                                                                    : transaction.status ===
                                                                                      "PENDING"
                                                                                    ? "bg-warning text-dark"
                                                                                    : transaction.status ===
                                                                                      "FAILED"
                                                                                    ? "bg-danger"
                                                                                    : "bg-secondary"
                                                                            }`}
                                                                        >
                                                                            {
                                                                                transaction.status
                                                                            }
                                                                        </span>
                                                                    </td>
                                                                    <td>
                                                                        <small>
                                                                            {formatDate(
                                                                                transaction.createdDate
                                                                            )}
                                                                        </small>
                                                                    </td>
                                                                </tr>
                                                            )
                                                        )}
                                                    </tbody>
                                                </table>
                                            </div>
                                        )}
                                    </div>
                                )}

                            {activeSubTab === "voice-calls" && !loading && (
                                <div>
                                    {voiceCalls.length === 0 ? (
                                        <div className="alert alert-info">
                                            <i className="bi bi-info-circle me-2"></i>
                                            No calls made during the selected
                                            period.
                                        </div>
                                    ) : (
                                        <div className="table-responsive">
                                            <table className="table table-striped table-hover">
                                                <thead className="table-dark">
                                                    <tr>
                                                        <th>Call ID</th>
                                                        <th>Premium Number</th>
                                                        <th>Duration</th>
                                                        <th>Cost (EUR)</th>
                                                        <th>Date/Time</th>
                                                    </tr>
                                                </thead>
                                                <tbody>
                                                    {voiceCalls.map((call) => (
                                                        <tr key={call.id}>
                                                            <td>
                                                                <span
                                                                    className="badge"
                                                                    style={{
                                                                        backgroundColor:
                                                                            "#ff7a00",
                                                                        color: "#000",
                                                                    }}
                                                                >
                                                                    #{call.id}
                                                                </span>
                                                            </td>
                                                            <td>
                                                                <span className="badge bg-danger">
                                                                    {call
                                                                        .premiumNumber
                                                                        ?.phoneNumber ||
                                                                        "N/A"}
                                                                </span>
                                                            </td>
                                                            <td>
                                                                <span className="fw-bold">
                                                                    {formatDuration(
                                                                        call.durationSeconds ||
                                                                            0
                                                                    )}
                                                                </span>
                                                            </td>
                                                            <td>
                                                                <span className="fw-bold text-success">
                                                                    {call.chargedAmount?.toFixed(
                                                                        2
                                                                    ) ||
                                                                        "0.00"}{" "}
                                                                    EUR
                                                                </span>
                                                            </td>
                                                            <td>
                                                                <small>
                                                                    {formatDate(
                                                                        call.callTimestamp
                                                                    )}
                                                                </small>
                                                            </td>
                                                        </tr>
                                                    ))}
                                                </tbody>
                                            </table>
                                        </div>
                                    )}
                                </div>
                            )}
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default ReportingTab;
