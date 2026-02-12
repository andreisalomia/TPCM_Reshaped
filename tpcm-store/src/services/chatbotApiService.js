import axios from 'axios';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';

const api = axios.create({
    baseURL: API_BASE_URL,
    headers: {
        'Content-Type': 'application/json',
    },
});

api.interceptors.request.use(
    (config) => {
        const username = import.meta.env.VITE_API_USERNAME || 'admin';
        const password = import.meta.env.VITE_API_PASSWORD || 'admin';
        const basicAuth = 'Basic ' + btoa(`${username}:${password}`);
        config.headers.Authorization = basicAuth;
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

export const chatbotApiService = {

    getSubscriberByMsisdn: async (msisdn) => {
        try {
            const response = await api.get('/api/app/subscribers/search/by-msisdn', {
                params: { msisdn: msisdn }
            });
            return response.data;
        } catch (error) {
            console.error('Error fetching subscriber:', error);
            throw error;
        }
    },

    getSubscriberBalance: async (msisdn) => {
        try {
            const response = await api.get(`/api/app/transactions/flow/balance/${msisdn}`);
            return response.data;
        } catch (error) {
            console.error('Error fetching balance:', error);
            throw error;
        }
    },

    getLimitBySubscriberId: async (subscriberId) => {
        try {
            const response = await api.get(`/api/app/limits/${subscriberId}`);
            return response.data;
        } catch (error) {
            console.error('Error fetching limit:', error);
            throw error;
        }
    },

    searchByName: async (name) => {
        try {
            const response = await api.get('/api/app/chatbot/search-by-name', {
                params: { name }
            });
            return response.data;
        } catch (error) {
            console.error('Error searching by name:', error);
            throw error;
        }
    },

    normalizeMsisdn: (msisdn) => {
        if (!msisdn) return null;

        msisdn = msisdn.trim().replaceAll(/\s/g, '');

        if (msisdn.startsWith('+40')) {
            return msisdn;
        }

        if (msisdn.startsWith('40') && msisdn.length >= 10) {
            return '+' + msisdn;
        }

        return msisdn;
    },

    getSubscriberFullInfo: async (msisdn) => {
        try {
            const normalizedMsisdn = chatbotApiService.normalizeMsisdn(msisdn);

            const subscribers = await chatbotApiService.getSubscriberByMsisdn(normalizedMsisdn);
            if (!subscribers || subscribers.length === 0) {
                throw new Error('Subscriber not found');
            }

            const subscriber = subscribers[0];

            const balanceInfo = await chatbotApiService.getSubscriberBalance(normalizedMsisdn);

            const limitInfo = await chatbotApiService.getLimitBySubscriberId(subscriber.subscriberID);

            return {
                subscriber: subscriber,
                balance: balanceInfo,
                limit: limitInfo,
                msisdn: subscriber.msisdn
            };
        } catch (error) {
            console.error('Error getting full subscriber info:', error);
            throw error;
        }
    }
};

export default chatbotApiService;