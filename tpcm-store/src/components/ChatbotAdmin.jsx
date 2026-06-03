import React, { useState } from 'react';
import ChatBot from 'react-chatbotify';
import { motion } from 'framer-motion';
import chatbotOrchestrator from '../services/chatbotOrchestrator';

function ChatbotAdmin() {
    const [conversationHistory, setConversationHistory] = useState([]);
    const [isProcessing, setIsProcessing] = useState(false);

    const handleUserMessage = async (params) => {
        const userMessage = params.userInput;
        setIsProcessing(true);

        try {
            const result = await chatbotOrchestrator.processMessage(conversationHistory, userMessage);
            setConversationHistory(result.conversationHistory);
            return result.message;
        } catch (error) {
            console.error('Error handling message:', error);
            return 'Error processing request. Please try again.';
        } finally {
            setIsProcessing(false);
        }
    };

    const flow = {
        start: {
            message: `Hello! I'm the Customer Support Assistant, a helpful chatbot designed to help customer support operators with their inquiries. How can I help you today?`,
            path: 'loop',
        },
        loop: {
            message: async (params) => {
                return await handleUserMessage(params);
            },
            path: 'loop',
        },
    };


    const settings = {
        general: {
            primaryColor: '#ff7a00',
            secondaryColor: '#6c757d',
            fontFamily: '-apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif',
            embedded: true,
        },
        chatHistory: {
            disabled: true,
        },
        header: {
            title: 'TPCM Chatbot Assistant',
            showAvatar: true,
        },
        notification: {
            disabled: true,
        },
        audio: {
            disabled: true,
        },
        chatButton: {
            icon: <i className="bi bi-chat-dots-fill"></i>,
        },
        footer: {
            text: null,
        },
        userBubble: {
            showAvatar: false,
            simStream: false,
        },
        botBubble: {
            showAvatar: true,
            simStream: false,
        },
    };

    const styles = {
        headerStyle: {
            background: 'linear-gradient(135deg, #ff7a00 0%, #ff9933 100%)',
            color: '#fff',
            padding: '16px 20px',
            fontSize: '1.1rem',
            fontWeight: '500',
        },
        chatWindowStyle: {
            height: '700px',
            width: '100%',
            maxWidth: '100%',
            borderRadius: '8px',
        },
        userBubbleStyle: {
            backgroundColor: '#ff7a00',
            color: '#fff',
            maxWidth: '70%',
        },
        botBubbleStyle: {
            backgroundColor: '#f8f9fa',
            color: '#212529',
            border: '1px solid #e0e0e0',
            maxWidth: '70%',
        },
        chatInputContainerStyle: {
            padding: '16px',
            borderTop: '1px solid #dee2e6',
            backgroundColor: '#fff',
        },
        chatInputAreaStyle: {
            borderRadius: '20px',
            border: '1px solid #ced4da',
            padding: '10px 16px',
        },
        sendButtonStyle: {
            backgroundColor: '#ff7a00',
            borderRadius: '50%',
            width: '42px',
            height: '42px',
        },
        bodyStyle: {
            width: '100%',
            maxWidth: '100%',
        },
    };

    return (
        <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.3 }}
            className="container-fluid p-4"
        >
            <div className="row" style={{ width: '100%', margin: 0 }}>
                <div className="col-12">
                    <div className="card shadow-sm border-0" style={{ width: '100%', maxWidth: '1000px', margin: '0 auto', borderRadius: '8px' }}>
                        <div className="card-body p-0" style={{ backgroundColor: '#ffffff' }}>
                            <div style={{ 
                                height: '700px', 
                                width: '100%',
                                display: 'flex',
                                justifyContent: 'center',
                                alignItems: 'stretch',
                                position: 'relative'
                            }}>
                                {isProcessing && (
                                    <div style={{
                                        position: 'absolute',
                                        top: '10px',
                                        right: '10px',
                                        zIndex: 1000,
                                        backgroundColor: 'rgba(255, 122, 0, 0.9)',
                                        color: '#fff',
                                        padding: '8px 16px',
                                        borderRadius: '20px',
                                        fontSize: '0.85rem',
                                        display: 'flex',
                                        alignItems: 'center',
                                        boxShadow: '0 2px 8px rgba(0,0,0,0.15)'
                                    }}>
                                        <span className="spinner-border spinner-border-sm me-2" style={{ width: '14px', height: '14px' }}></span>
                                        <span>Processing...</span>
                                    </div>
                                )}
                                <div style={{ width: '100%', height: '100%' }}>
                                    <ChatBot flow={flow} settings={settings} styles={styles} />
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </motion.div>
    );
}

export default ChatbotAdmin;