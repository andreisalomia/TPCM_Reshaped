import React, { useState, useEffect } from 'react';
import { motion } from 'framer-motion';
import AppsSection from './AppsSection';
import EventsSection from './EventsSection';
import ReportingTab from './ReportingTab';
import ChatbotAdmin from './ChatbotAdmin';
import VoiceCallsTab from './VoiceCallsTab';
import Navbar from './Navbar';

function Dashboard({ user, onLogout }) {
    const [activeTab, setActiveTab] = useState(() => {
        return localStorage.getItem('activeTab') || 'apps';
    });

    useEffect(() => {
        localStorage.setItem('activeTab', activeTab);
    }, [activeTab]);

    console.log(user);

    return (
        <div className="min-vh-100" style={{ backgroundColor: '#f8f9fa' }}>
            <Navbar user={user} onLogout={onLogout} />

            <div className="container mt-4">
                <div className="d-flex justify-content-center mb-4">
                    <div className="btn-group" role="group" style={{ borderRadius: '25px', overflow: 'hidden' }}>
                        <button
                            type="button"
                            className={`btn btn-lg px-4 py-3 ${activeTab === 'apps' ? 'active' : ''}`}
                            style={{
                                backgroundColor: activeTab === 'apps' ? '#ff7a00' : '#6c757d',
                                border: 'none',
                                color: activeTab === 'apps' ? '#000' : '#fff',
                                fontWeight: '600',
                                transition: 'all 0.3s ease',
                            }}
                            onClick={() => setActiveTab('apps')}
                        >
                            <i className="bi bi-controller me-2"></i>
                            Apps & Games
                        </button>
                        <button
                            type="button"
                            className={`btn btn-lg px-4 py-3 ${activeTab === 'events' ? 'active' : ''}`}
                            style={{
                                backgroundColor: activeTab === 'events' ? '#ff7a00' : '#6c757d',
                                border: 'none',
                                color: activeTab === 'events' ? '#000' : '#fff',
                                fontWeight: '600',
                                transition: 'all 0.3s ease',
                            }}
                            onClick={() => setActiveTab('events')}
                        >
                            <i className="bi bi-calendar-event me-2"></i>
                            Events & Tickets
                        </button>
                        <button
                            type="button"
                            className={`btn btn-lg px-4 py-3 ${activeTab === 'voice-calls' ? 'active' : ''}`}
                            style={{
                                backgroundColor: activeTab === 'voice-calls' ? '#ff7a00' : '#6c757d',
                                border: 'none',
                                color: activeTab === 'voice-calls' ? '#000' : '#fff',
                                fontWeight: '600',
                                transition: 'all 0.3s ease',
                            }}
                            onClick={() => setActiveTab('voice-calls')}
                        >
                            <i className="bi bi-telephone-fill me-2"></i>
                            Voice Calls
                        </button>
                        <button
                            type="button"
                            className={`btn btn-lg px-4 py-3 ${activeTab === 'reporting' ? 'active' : ''}`}
                            style={{
                                backgroundColor: activeTab === 'reporting' ? '#ff7a00' : '#6c757d',
                                border: 'none',
                                color: activeTab === 'reporting' ? '#000' : '#fff',
                                fontWeight: '600',
                                transition: 'all 0.3s ease',
                            }}
                            onClick={() => setActiveTab('reporting')}
                        >
                            <i className="bi bi-graph-up me-2"></i>
                            Your Transactions
                        </button>
                        {user.role === 'ADMIN' && user.authenticated === true && (
                            <button
                                type="button"
                                className={`btn btn-lg px-4 py-3 ${activeTab === 'chatbot' ? 'active' : ''}`}
                                style={{
                                    backgroundColor: activeTab === 'chatbot' ? '#ff7a00' : '#6c757d',
                                    border: 'none',
                                    color: activeTab === 'chatbot' ? '#000' : '#fff',
                                    fontWeight: '600',
                                    transition: 'all 0.3s ease',
                                }}
                                onClick={() => setActiveTab('chatbot')}
                            >
                                <i className="bi bi-robot me-2"></i>
                                Chatbot Admin
                            </button>
                        )}
                    </div>
                </div>

                <motion.div key={activeTab} initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }} transition={{ duration: 0.3 }}>
                    {activeTab === 'apps' && <AppsSection />}
                    {activeTab === 'events' && <EventsSection />}
                    {activeTab === 'voice-calls' && <VoiceCallsTab user={user} />}
                    {activeTab === 'reporting' && (
                        <>
                            {console.log('Passing user to ReportingTab:', user)}
                            <ReportingTab user={user} />
                        </>
                    )}
                    {activeTab === 'chatbot' && <ChatbotAdmin />}
                </motion.div>
            </div>
        </div>
    );
}

export default Dashboard;
