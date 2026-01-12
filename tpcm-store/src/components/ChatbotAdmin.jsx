import React, { useState, useRef, useEffect } from "react";
import { motion } from "framer-motion";

function ChatbotAdmin() {
    const [messages, setMessages] = useState([
        {
            id: 1,
            text: "Hello! I'm your AI assistant. How can I help you today?",
            sender: "bot",
            timestamp: new Date(),
        },
    ]);
    const [inputValue, setInputValue] = useState("");
    const [isTyping, setIsTyping] = useState(false);
    const messagesEndRef = useRef(null);

    const scrollToBottom = () => {
        messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
    };

    useEffect(() => {
        scrollToBottom();
    }, [messages, isTyping]);

    const handleSendMessage = (e) => {
        e.preventDefault();
        if (!inputValue.trim()) return;

        const userMessage = {
            id: messages.length + 1,
            text: inputValue,
            sender: "user",
            timestamp: new Date(),
        };

        setMessages([...messages, userMessage]);
        setInputValue("");
        setIsTyping(true);

        setTimeout(() => {
            const loremResponses = [
                "Lorem ipsum dolor sit, amet consectetur adipisicing elit. Repellendus harum officia totam earum a at maiores ducimus, dolores repudiandae cupiditate in nostrum itaque cum doloribus nisi non rem dolorum reprehenderit.",
                "Lorem ipsum dolor sit amet consectetur adipisicing elit. Laboriosam dolorem quod rerum inventore asperiores eum vel facere qui tempore cupiditate reprehenderit itaque distinctio nihil nam dolores aut, iste minus similique.",
                "Lorem ipsum dolor sit amet consectetur, adipisicing elit. Numquam aliquid voluptatibus ad vero voluptas quae hic, laborum dolorem consectetur vel possimus illum dignissimos esse delectus adipisci. Eius accusamus vel quibusdam.",
                "Lorem ipsum dolor sit amet consectetur, adipisicing elit. Officiis omnis suscipit, necessitatibus repellat, provident amet error magnam sapiente facilis iure, aut corrupti hic odio nemo! Nihil similique voluptatem qui voluptates.",
            ];

            const randomResponse =
                loremResponses[
                    Math.floor(Math.random() * loremResponses.length)
                ];

            const botMessage = {
                id: messages.length + 2,
                text: randomResponse,
                sender: "bot",
                timestamp: new Date(),
            };

            setMessages((prev) => [...prev, botMessage]);
            setIsTyping(false);
        }, 1000 + Math.random() * 1000);
    };

    const formatTime = (date) => {
        return date.toLocaleTimeString("en-US", {
            hour: "2-digit",
            minute: "2-digit",
        });
    };

    return (
        <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.3 }}
            className="container"
        >
            <div className="row justify-content-center">
                <div className="col-lg-10">
                    <div className="card shadow-lg border-0 rounded-4 overflow-hidden">
                        <div
                            className="card-header text-white py-3"
                            style={{ backgroundColor: "#ff7a00" }}
                        >
                            <div className="d-flex align-items-center">
                                <div
                                    className="bg-white rounded-circle d-flex align-items-center justify-content-center me-3"
                                    style={{ width: "50px", height: "50px" }}
                                >
                                    <i
                                        className="bi bi-robot fs-3"
                                        style={{ color: "#ff7a00" }}
                                    ></i>
                                </div>
                                <div>
                                    <h5 className="mb-0 fw-bold text-dark">
                                        AI Customer Support Assistant
                                    </h5>
                                    <small className="opacity-75 text-dark">
                                        <i
                                            className="bi bi-circle-fill text-success me-1"
                                            style={{ fontSize: "0.5rem" }}
                                        ></i>
                                        Online
                                    </small>
                                </div>
                            </div>
                        </div>

                        <div
                            className="card-body bg-light overflow-auto"
                            style={{ height: "500px" }}
                        >
                            {messages.map((message) => (
                                <motion.div
                                    key={message.id}
                                    initial={{ opacity: 0, y: 10 }}
                                    animate={{ opacity: 1, y: 0 }}
                                    transition={{ duration: 0.3 }}
                                    className={`d-flex mb-3 ${
                                        message.sender === "user"
                                            ? "justify-content-end"
                                            : "justify-content-start"
                                    }`}
                                >
                                    <div style={{ maxWidth: "70%" }}>
                                        {message.sender === "bot" && (
                                            <div className="d-flex align-items-start mb-1">
                                                <div
                                                    className="text-white rounded-circle d-flex align-items-center justify-content-center me-2 flex-shrink-0"
                                                    style={{
                                                        width: "32px",
                                                        height: "32px",
                                                        backgroundColor:
                                                            "#ff7a00",
                                                    }}
                                                >
                                                    <i className="bi bi-robot small"></i>
                                                </div>
                                                <div className="flex-grow-1">
                                                    <div className="p-3 rounded-3 shadow-sm bg-white">
                                                        <p className="mb-0 text-dark">
                                                            {message.text}
                                                        </p>
                                                    </div>
                                                    <small className="text-muted ms-2">
                                                        {formatTime(
                                                            message.timestamp
                                                        )}
                                                    </small>
                                                </div>
                                            </div>
                                        )}
                                        {message.sender === "user" && (
                                            <div>
                                                <div
                                                    className="p-3 rounded-3 shadow-sm"
                                                    style={{
                                                        backgroundColor:
                                                            "#6c757d",
                                                        color: "#fff",
                                                    }}
                                                >
                                                    <p className="mb-0 fw-semibold">
                                                        {message.text}
                                                    </p>
                                                </div>
                                                <div className="text-end">
                                                    <small className="text-muted me-2">
                                                        {formatTime(
                                                            message.timestamp
                                                        )}
                                                    </small>
                                                </div>
                                            </div>
                                        )}
                                    </div>
                                </motion.div>
                            ))}

                            {isTyping && (
                                <motion.div
                                    initial={{ opacity: 0, y: 10 }}
                                    animate={{ opacity: 1, y: 0 }}
                                    className="d-flex justify-content-start mb-3"
                                >
                                    <div className="d-flex align-items-start">
                                        <div
                                            className="text-white rounded-circle d-flex align-items-center justify-content-center me-2"
                                            style={{
                                                width: "32px",
                                                height: "32px",
                                                backgroundColor: "#ff7a00",
                                            }}
                                        >
                                            <i className="bi bi-robot small"></i>
                                        </div>
                                        <div className="p-3 rounded-3 shadow-sm bg-white">
                                            <div className="typing-indicator">
                                                <span></span>
                                                <span></span>
                                                <span></span>
                                            </div>
                                        </div>
                                    </div>
                                </motion.div>
                            )}

                            <div ref={messagesEndRef} />
                        </div>

                        <div className="card-footer bg-white border-0 pt-3">
                            <form onSubmit={handleSendMessage}>
                                <div className="input-group">
                                    <input
                                        type="text"
                                        className="form-control border-0 shadow-sm rounded-start-pill px-4 py-2"
                                        placeholder="Type your message..."
                                        value={inputValue}
                                        onChange={(e) =>
                                            setInputValue(e.target.value)
                                        }
                                    />
                                    <button
                                        className="btn shadow-sm rounded-end-pill px-4 py-2 fw-semibold border-0"
                                        type="submit"
                                        style={{
                                            backgroundColor: "#ff7a00",
                                            color: "#000",
                                        }}
                                    >
                                        <i className="bi bi-send-fill"></i>
                                    </button>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            </div>

            <style>{`
                .typing-indicator {
                    display: flex;
                    align-items: center;
                    gap: 4px;
                }

                .typing-indicator span {
                    width: 8px;
                    height: 8px;
                    border-radius: 50%;
                    background-color: #ff7a00;
                    animation: typing 1.4s infinite;
                }

                .typing-indicator span:nth-child(2) {
                    animation-delay: 0.2s;
                }

                .typing-indicator span:nth-child(3) {
                    animation-delay: 0.4s;
                }

                @keyframes typing {
                    0%, 60%, 100% {
                        transform: translateY(0);
                        opacity: 0.5;
                    }
                    30% {
                        transform: translateY(-10px);
                        opacity: 1;
                    }
                }

                .card-body::-webkit-scrollbar {
                    width: 8px;
                }

                .card-body::-webkit-scrollbar-track {
                    background: #f1f1f1;
                    border-radius: 10px;
                }

                .card-body::-webkit-scrollbar-thumb {
                    background: #ff7a00;
                    border-radius: 10px;
                }

                .card-body::-webkit-scrollbar-thumb:hover {
                    background: #e66d00;
                }
            `}</style>
        </motion.div>
    );
}

export default ChatbotAdmin;
