import openAIService from './openAIService';
import chatbotApiService from './chatbotApiService';
import logAnalysisService from './logAnalysisService';

export class ChatbotOrchestrator {

    async processMessage(conversationHistory, userMessage) {
        try {
            const analysis = await this.analyzeUserIntent(userMessage, conversationHistory);

            if (!analysis.needsData) {
                return {
                    message: analysis.response,
                    conversationHistory: [
                        ...conversationHistory,
                        { role: 'user', content: userMessage },
                        { role: 'assistant', content: analysis.response },
                    ],
                };
            }

            let data;
            if (analysis.dataType === 'msisdn_history' || analysis.dataType === 'address_history') {
                data = await this.fetchLogData(analysis);
            } else {
                data = await this.fetchDataFromExistingEndpoints(analysis);
            }

            const formattedResponse = await this.formatResponse(userMessage, analysis, data);

            return {
                message: formattedResponse,
                conversationHistory: [
                    ...conversationHistory,
                    { role: 'user', content: userMessage },
                    { role: 'assistant', content: formattedResponse },
                ],
            };

        } catch (error) {
            console.error('Error processing message:', error);
            return this.handleError(error, conversationHistory, userMessage);
        }
    }

    async analyzeUserIntent(userMessage, conversationHistory) {
        const analysisPrompt = `Analyze this user message and determine what they want:

USER MESSAGE: "${userMessage}"

You need to determine:
1. Wether data is needed from existing endpoints? Boolean: true or false
2. What type of data? 
   - limit_reset = asking about when limit was reset
   - status = asking about subscriber status  
   - balance = asking about balance
   - msisdn_history = asking about phone number CHANGES/HISTORY for a subscriber
   - address_history = asking about address CHANGES/HISTORY for a customer
   - none = greeting or other
3. What identifier is mentioned?
   - MSISDN (phone number): 10-12 digits, may include +40 prefix
   - Name: person's name like "Ion Popescu", "Maria Ionescu", etc.
   - Subscriber ID: number after "subscriber"
   - Customer ID: number after "customer"

Respond in this JSON format:
{
  "needsData": boolean,
  "dataType": "limit_reset|status|balance|msisdn_history|address_history|none", 
  "msisdn": "extracted phone number or null",
  "name": "extracted person name or null",
  "subscriberId": "extracted subscriber ID or null",
  "customerId": "extracted customer ID or null",
  "response": "direct response if needsData is false"
}

Examples:
- "When was limit reset for +40721234567?" → {"needsData": true, "dataType": "limit_reset", "msisdn": "+40721234567", "name": null, "subscriberId": null, "customerId": null}
- "When was the phone number changed for subscriber 12?" → {"needsData": true, "dataType": "msisdn_history", "msisdn": null, "name": null, "subscriberId": "12", "customerId": null}
- "Show address history for customer 21" → {"needsData": true, "dataType": "address_history", "msisdn": null, "name": null, "subscriberId": null, "customerId": "21"}
- "What was Ion Popescu's old address?" → {"needsData": true, "dataType": "address_history", "msisdn": null, "name": "Ion Popescu", "subscriberId": null, "customerId": null}
- "Hello" → {"needsData": false, "dataType": "none", "msisdn": null, "name": null, "subscriberId": null, "customerId": null, "response": "Hello! How can I assist you today?"}

Currency: Always use EUR in responses.
Try to respond in Romanian ONLY IF the user asked in Romanian.`;

        try {
            const response = await openAIService.sendMessage([
                { role: 'system', content: analysisPrompt },
                { role: 'user', content: userMessage }
            ]);

            const analysisText = response.choices[0].message.content;
            const cleanJson = analysisText.replace(/```json|```/g, '').trim();
            return JSON.parse(cleanJson);

        } catch (error) {
            console.error('Error analyzing intent:', error);
            return this.fallbackAnalysis(userMessage);
        }
    }

    fallbackAnalysis(message) {
        const msisdnPattern = /\b(\+40\d{9,10}|40\d{9,10}|\d{10,12})\b/;
        const msisdnMatch = message.match(msisdnPattern);
        let msisdn = msisdnMatch ? msisdnMatch[1] : null;

        if (msisdn && !msisdn.startsWith('+')) {
            if (msisdn.startsWith('40')) {
                msisdn = '+' + msisdn;
            }
        }

        // console.log('Original message:', message);
        // console.log('Regex match:', msisdnMatch);
        // console.log('Extracted MSISDN:', msisdn);

        const namePattern = /\b([A-Z][a-z]+\s+[A-Z][a-z]+)\b/;
        const nameMatch = message.match(namePattern);
        const name = nameMatch ? nameMatch[1] : null;

        const lowerMessage = message.toLowerCase();

        if (lowerMessage.includes('reset') || lowerMessage.includes('resetat')) {
            return {
                needsData: !!(msisdn || name),
                dataType: (msisdn || name) ? 'limit_reset' : 'none',
                msisdn: msisdn,
                name: name,
                response: !(msisdn || name) ? 'Please provide an MSISDN or customer name.' : null
            };
        }

        if (lowerMessage.includes('status') || lowerMessage.includes('active') || lowerMessage.includes('activ')) {
            return {
                needsData: !!(msisdn || name),
                dataType: (msisdn || name) ? 'status' : 'none',
                msisdn: msisdn,
                name: name,
                response: !(msisdn || name) ? 'Please provide an MSISDN or customer name.' : null
            };
        }

        if (lowerMessage.includes('balance') || lowerMessage.includes('sold') || lowerMessage.includes('credit')) {
            return {
                needsData: !!(msisdn || name),
                dataType: (msisdn || name) ? 'balance' : 'none',
                msisdn: msisdn,
                name: name,
                response: !(msisdn || name) ? 'Please provide an MSISDN or customer name.' : null
            };
        }

        return {
            needsData: false,
            dataType: 'none',
            msisdn: null,
            name: null,
            response: 'Hello! I can help you check subscribers information. Please provide an MSISDN or customer name.'
        };
    }

    async fetchLogData(analysis) {
        try {
            let operation, identifier;

            if (analysis.dataType === 'msisdn_history') {
                operation = 'updateSubscriberMsisdn';

                if (analysis.subscriberId || analysis.msisdn) {
                    identifier = analysis.subscriberId || analysis.msisdn;
                } else if (analysis.name) {
                    const nameSearchResult = await chatbotApiService.searchByName(analysis.name);

                    if (!nameSearchResult.found || nameSearchResult.subscribers.length === 0) {
                        throw new Error(`No subscriber found for customer name: ${analysis.name}`);
                    }

                    const firstSubscriber = nameSearchResult.subscribers[0];
                    identifier = firstSubscriber.msisdn;

                    analysis._searchInfo = {
                        searchedByName: true,
                        customerName: firstSubscriber.customerName,
                        foundCount: nameSearchResult.count,
                        msisdn: firstSubscriber.msisdn
                    };
                } else {
                    throw new Error('Subscriber ID, MSISDN, or customer name required');
                }

            } else if (analysis.dataType === 'address_history') {
                operation = 'updateCustomerAddress';

                if (analysis.customerId) {
                    identifier = analysis.customerId;
                } else if (analysis.name) {
                    identifier = analysis.name;
                } else {
                    throw new Error('Customer ID or name required');
                }

            } else {
                throw new Error('Unknown log data type');
            }

            const logLines = await logAnalysisService.getLogLines(operation, identifier);

            if (!logLines || logLines.length === 0) {
                return {
                    found: false,
                    dataType: analysis.dataType,
                    message: 'No log entries found',
                    operation,
                    identifier
                };
            }

            return {
                found: true,
                dataType: analysis.dataType,
                operation,
                identifier,
                logLines: logLines,
                totalLines: logLines.length,
                searchInfo: analysis._searchInfo
            };

        } catch (error) {
            console.error('Error fetching log data:', error);
            throw error;
        }
    }

    async fetchDataFromExistingEndpoints(analysis) {
        let msisdn = analysis.msisdn;

        if (!msisdn && analysis.name) {
            try {
                const nameSearchResult = await chatbotApiService.searchByName(analysis.name);

                if (!nameSearchResult.found || nameSearchResult.subscribers.length === 0) {
                    throw new Error(`No subscriber found for customer name: ${analysis.name}`);
                }

                const firstSubscriber = nameSearchResult.subscribers[0];
                msisdn = firstSubscriber.msisdn;

                analysis._searchInfo = {
                    searchedByName: true,
                    customerName: firstSubscriber.customerName,
                    foundCount: nameSearchResult.count
                };
            } catch (error) {
                throw new Error(`Could not find subscriber for ${analysis.name}: ${error.message}`);
            }
        }

        if (!msisdn) {
            throw new Error('MSISDN required for data fetch');
        }

        switch (analysis.dataType) {
            case 'limit_reset':
                const fullInfo = await chatbotApiService.getSubscriberFullInfo(msisdn);
                return {
                    msisdn: fullInfo.msisdn,
                    lastReset: fullInfo.limit.lastReset,
                    consumedAmount: fullInfo.limit.consumedAmount,
                    maxAmountCycle: fullInfo.limit.maxAmountCycle,
                    daysSinceReset: fullInfo.limit.lastReset ?
                        Math.floor((Date.now() - new Date(fullInfo.limit.lastReset).getTime()) / (1000 * 60 * 60 * 24)) : 0
                };

            case 'status':
                const subscribers = await chatbotApiService.getSubscriberByMsisdn(msisdn);
                if (!subscribers || subscribers.length === 0) {
                    throw new Error('Subscriber not found');
                }
                const subscriber = subscribers[0];
                return {
                    msisdn: subscriber.msisdn,
                    status: subscriber.status,
                    subscriptionType: subscriber.subscriptionType,
                    isActive: subscriber.status === 'ACTIVE'
                };

            case 'balance':
                const balanceData = await chatbotApiService.getSubscriberBalance(msisdn);
                const subscriberInfo = await chatbotApiService.getSubscriberByMsisdn(msisdn);
                const limitData = await chatbotApiService.getLimitBySubscriberId(subscriberInfo[0].subscriberID);

                return {
                    msisdn: subscriberInfo[0].msisdn,
                    availableBalance: balanceData.availableBalance,
                    consumedAmount: limitData.consumedAmount,
                    maxAmountCycle: limitData.maxAmountCycle,
                    lastReset: limitData.lastReset
                };

            default:
                throw new Error('Unknown data type: ' + analysis.dataType);
        }
    }

    async formatResponse(userMessage, analysis, data) {
        let contextInfo = '';

        if (analysis._searchInfo?.searchedByName) {
            contextInfo = `\nNOTE: User searched by name "${analysis.name}" and we found customer "${analysis._searchInfo.customerName}" with MSISDN ${data.msisdn}.`;
            if (analysis._searchInfo.foundCount > 1) {
                contextInfo += ` Multiple subscribers found (${analysis._searchInfo.foundCount}), showing first one.`;
            }
        }

        let logContext = '';
        if (data.dataType === 'msisdn_history' || data.dataType === 'address_history') {
            if (data.found && data.logLines) {
                logContext = `\n\nCDR LOG ENTRIES (${data.totalLines} lines):\n${data.logLines.join('\n')}`;
                logContext += `\n\nCDR Format: timestamp|operation_name|request_id|http_code|internal_result|operation_specific_details`;

                if (data.dataType === 'msisdn_history') {
                    logContext += `\nFor updateSubscriberMsisdn: subscriberId|oldMsisdn|newMsisdn`;
                } else {
                    logContext += `\nFor updateCustomerAddress: customerId|oldAddress|newAddress`;
                }
            }
        }

        const formatPrompt = `Format this data into a natural, helpful response for a customer support operator.

USER ASKED: "${userMessage}"
DATA TYPE: ${analysis.dataType}
SEARCH BY: ${analysis.msisdn ? 'MSISDN' : analysis.subscriberId ? 'Subscriber ID' : analysis.customerId ? 'Customer ID' : 'Customer Name'}
RAW DATA: ${JSON.stringify(data)}${contextInfo}${logContext}

Create a natural, conversational response that:
1. Answers the user's question directly
2. For log queries (msisdn_history, address_history):
   - Parse the CDR log lines yourself
   - Show the COMPLETE history of changes (from -> to, with dates)
   - Mention how many changes occurred
   - Show the most recent change prominently
   THE FORMAT OF THE CDR LOGS IS: timestamp|operation_name|request_id|http_code|internal_result|operation_specific_details
   Operation specific details are:
   updateSubscriberMsisdn <-> subscriberId|oldMsisdn|newMsisdn
   updateCustomerEmail <-> customerId|oldEmail|newEmail
3. If searched by name, mention the customer name and MSISDN found
4. Uses EUR for currency (not RON)
5. Uses Romanian ONLY AND ONLY if the user asked in Romanian
6. Be concise but informative

No excessive formatting, do not mention you're an AI, don't apologize, don't use emojis.`;

        try {
            const response = await openAIService.sendMessage([
                { role: 'system', content: formatPrompt }
            ]);

            return response.choices[0].message.content;

        } catch (error) {
            console.error('Error formatting response:', error);
            return this.fallbackFormat(analysis, data);
        }
    }

    fallbackFormat(analysis, data) {
        switch (analysis.dataType) {
            case 'msisdn_history':
                if (!data.found) return 'No phone number changes found.';
                return `Found ${data.totalLines} phone number change entries for ${data.operation}/${data.identifier}. Please check the logs for details.`;

            case 'address_history':
                if (!data.found) return 'No address changes found.';
                return `Found ${data.totalLines} address change entries for ${data.operation}/${data.identifier}. Please check the logs for details.`;

            case 'limit_reset':
                return `Limit reset info for ${data.msisdn}: Last reset: ${new Date(data.lastReset).toLocaleString()} Days since reset: ${data.daysSinceReset || 0} Consumed: ${data.consumedAmount} EUR`;

            case 'status':
                return `Status for ${data.msisdn}: Status: ${data.status} Subscription: ${data.subscriptionType}`;

            case 'balance':
                return `Balance for ${data.msisdn}: Available: ${data.availableBalance} EUR Consumed: ${data.consumedAmount} EUR`;

            default:
                return 'Information retrieved successfully.';
        }
    }

    handleError(error, conversationHistory, userMessage) {
        let errorMessage = 'Sorry, I encountered an error while processing your request.';

        if (error.response?.status === 404) {
            errorMessage = 'Subscriber not found. Please check the MSISDN and try again.';
        } else if (error.response?.status === 401) {
            errorMessage = 'Authentication error. Please contact your administrator.';
        }

        return {
            message: errorMessage,
            conversationHistory: [
                ...conversationHistory,
                { role: 'user', content: userMessage },
                { role: 'assistant', content: errorMessage },
            ],
        };
    }
}

const chatbotOrchestrator = new ChatbotOrchestrator();

export default chatbotOrchestrator;