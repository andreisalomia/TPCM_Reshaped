import axios from 'axios';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';

const api = axios.create({
    baseURL: API_BASE_URL,
    headers: { 'Content-Type': 'application/json' },
});

api.interceptors.request.use(
    (config) => {
        const username = import.meta.env.VITE_API_USERNAME || 'admin';
        const password = import.meta.env.VITE_API_PASSWORD || 'admin';
        config.headers.Authorization = 'Basic ' + btoa(`${username}:${password}`);
        return config;
    },
    (error) => Promise.reject(error)
);

export const logAnalysisService = {
    getLogLines: async (operation, identifier) => {
        try {
            const response = await api.get(`/api/app/chatbot/logs/${operation}/${identifier}`);
            return response.data;
        } catch (error) {
            console.error('Error fetching log lines:', error);
            throw error;
        }
    }
};

export default logAnalysisService;