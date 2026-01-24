import axios from 'axios';

const VOICE_SIMULATOR_API_URL = import.meta.env.VITE_VOICE_SIMULATOR_API_URL || 'http://localhost:4242/api';

const voiceSimulatorApi = axios.create({
  baseURL: VOICE_SIMULATOR_API_URL,
  headers: {
    'Content-Type': 'application/json',
  }
});

export const voiceCallsApi = {
  startCall: async (callerNumber, premiumNumber, durationSeconds) => {
    try {
      const response = await voiceSimulatorApi.post('/voice-simulator/call/start', {
        callerNumber,
        premiumNumber,
        durationSeconds
      });
      return response.data;
    } catch (error) {
      console.error('Error starting call:', error);
      throw error;
    }
  },

  getAllCalls: async () => {
    try {
      const response = await voiceSimulatorApi.get('/voice-calls');
      return response.data;
    } catch (error) {
      console.error('Error fetching calls:', error);
      throw error;
    }
  },

  getCallById: async (callId) => {
    try {
      const response = await voiceSimulatorApi.get(`/voice-calls/${callId}`);
      return response.data;
    } catch (error) {
      console.error('Error fetching call:', error);
      throw error;
    }
  },

  getAllPremiumNumbers: async () => {
    try {
      const response = await voiceSimulatorApi.get('/premium-numbers');
      return response.data;
    } catch (error) {
      console.error('Error fetching premium numbers:', error);
      throw error;
    }
  }
};

export default voiceSimulatorApi;