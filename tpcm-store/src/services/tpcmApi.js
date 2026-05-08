import axios from 'axios';

const API_BASE_URL = import.meta.env.VITE_TPCM_API_URL || 'http://localhost:8080/api/app';

const tpcmApi = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
  auth: {
    username: 'admin',
    password: 'admin'
  }
});

export const transactionApi = {
  requestTransaction: async (msisdn, amount, thirdPartyId = 21, channel = 'APP') => {
    try {
      const response = await tpcmApi.post('/transactions/flow/request', {
        msisdn,
        amount,
        thirdPartyId,
        partialReservation: 'N',
        channel
      });
      return response.data;
    } catch (error) {
      console.error('Error requesting transaction:', error);
      throw error;
    }
  },

  commitTransaction: async (transactionId, amount) => {
    try {
      const response = await tpcmApi.post('/transactions/flow/commit', {
        transactionId,
        amount
      });
      return response.data;
    } catch (error) {
      console.error('Error committing transaction:', error);
      throw error;
    }
  },

  getBalance: async (msisdn) => {
    try {
      const response = await tpcmApi.get(`/transactions/flow/balance/${msisdn}`);
      return response.data;
    } catch (error) {
      console.error('Error fetching balance:', error);
      throw error;
    }
  },

  cancelTransaction: async (transactionId) => {
    try {
      const response = await tpcmApi.post('/transactions/flow/cancel', {
        transactionId
      });
      return response.data;
    } catch (error) {
      console.error('Error cancelling transaction:', error);
      throw error;
    }
  }
};

export default tpcmApi;