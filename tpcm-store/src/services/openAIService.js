import axios from 'axios';
import chatbotApiService from './chatbotApiService';

const OPENAI_API_KEY = import.meta.env.VITE_OPENAI_API_KEY;
const OPENAI_API_URL = 'https://api.openai.com/v1/chat/completions';



export const openAIService = {
    sendMessage: async (messages) => {
        try {
            const response = await axios.post(
                OPENAI_API_URL,
                {
                    model: 'gpt-4o-mini',
                    messages: messages,
                    max_tokens: 1000,
                },
                {
                    headers: {
                        'Content-Type': 'application/json',
                        Authorization: `Bearer ${OPENAI_API_KEY}`,
                    },
                }
            );

            chatbotApiService.trackQuery();

            return response.data;
        } catch (error) {
            console.error('Error calling OpenAI API:', error);
            if (error.response) {
                console.error('OpenAI API Error Details:', {
                    status: error.response.status,
                    data: error.response.data
                });
            }
            throw error;
        }
    },
};

export default openAIService;