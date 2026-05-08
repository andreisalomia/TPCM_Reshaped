export const smsServices = [
    {
        id: 1,
        shortNumber: '7458',
        title: 'STB Journey',
        description: 'Activate a 90-minute journey on all STB public transport means.',
        price: 3.50,
        category: 'transport',
        icon: 'bi-bus-front-fill',
        getReply: () => {
            const expiry = new Date(Date.now() + 90 * 60 * 1000);
            return `Your 90-minute journey has been activated, valid until ${expiry.toLocaleTimeString('en-GB', { hour: '2-digit', minute: '2-digit' })}.`;
        }
    },
    {
        id: 2,
        shortNumber: '7444',
        title: 'Parking Zone 1',
        description: 'Activate one hour of parking in Zone 1 Bucharest.',
        price: 2.00,
        category: 'parking',
        icon: 'bi-p-square-fill',
        getReply: () => {
            const expiry = new Date(Date.now() + 60 * 60 * 1000);
            return `One hour of parking in Zone 1 activated. Valid until ${expiry.toLocaleTimeString('en-GB', { hour: '2-digit', minute: '2-digit' })}.`;
        }
    },
    {
        id: 3,
        shortNumber: '7444',
        title: 'Parking Zone 2',
        description: 'Activate one hour of parking in Zone 2 Bucharest.',
        price: 1.50,
        category: 'parking',
        icon: 'bi-p-square-fill',
        getReply: () => {
            const expiry = new Date(Date.now() + 60 * 60 * 1000);
            return `One hour of parking in Zone 2 activated. Valid until ${expiry.toLocaleTimeString('en-GB', { hour: '2-digit', minute: '2-digit' })}.`;
        }
    },
    {
        id: 4,
        shortNumber: '7444',
        title: 'Parking Zone 3',
        description: 'Activate one hour of parking in Zone 3 Bucharest.',
        price: 1.00,
        category: 'parking',
        icon: 'bi-p-square-fill',
        getReply: () => {
            const expiry = new Date(Date.now() + 60 * 60 * 1000);
            return `One hour of parking in Zone 3 activated. Valid until ${expiry.toLocaleTimeString('en-GB', { hour: '2-digit', minute: '2-digit' })}.`;
        }
    },
    {
        id: 5,
        shortNumber: '7477',
        title: 'Daily News Pass',
        description: 'Access to today\'s headlines from all major publications.',
        price: 0.50,
        category: 'news',
        icon: 'bi-newspaper',
        getReply: () => `Your daily news pass has been activated! Access valid until midnight.`
    },
    {
        id: 6,
        shortNumber: '7488',
        title: 'Premium Weather',
        description: 'Detailed weather forecast for the next 7 days.',
        price: 51,
        category: 'news',
        icon: 'bi-cloud-sun-fill',
        getReply: () => `Premium weather forecast activated! You will receive updates for the next 7 days.`
    },
    {
        id: 7,
        shortNumber: '7499',
        title: 'Sports Flash',
        description: 'Live scores and breaking sports news for 24 hours.',
        price: 0.75,
        category: 'news',
        icon: 'bi-trophy-fill',
        getReply: () => `Sports Flash activated! Live scores and breaking news available for the next 24 hours.`
    },
    {
        id: 8,
        shortNumber: '7458',
        title: 'STB Day Pass',
        description: 'Unlimited travel on all STB means for the entire day.',
        price: 8.00,
        category: 'transport',
        icon: 'bi-bus-front-fill',
        getReply: () => {
            const expiry = new Date();
            expiry.setHours(23, 59, 59);
            return `STB Day Pass activated! Unlimited travel valid until ${expiry.toLocaleTimeString('en-GB', { hour: '2-digit', minute: '2-digit' })}.`;
        }
    },
    {
        id: 9,
        shortNumber: '7422',
        title: 'Airport Express',
        description: 'Single journey ticket on the Bucharest Airport Express train.',
        price: 4.50,
        category: 'transport',
        icon: 'bi-train-front-fill',
        getReply: () => {
            const expiry = new Date(Date.now() + 60 * 60 * 1000);
            return `Airport Express ticket activated! Valid for boarding until ${expiry.toLocaleTimeString('en-GB', { hour: '2-digit', minute: '2-digit' })}.`;
        }
    },
    {
        id: 10,
        shortNumber: '7433',
        title: 'Bike Share - 1h',
        description: 'Unlock one hour of access to the city bike sharing network.',
        price: 1.00,
        category: 'transport',
        icon: 'bi-bicycle',
        getReply: () => {
            const expiry = new Date(Date.now() + 60 * 60 * 1000);
            return `Bike Share access activated! Valid until ${expiry.toLocaleTimeString('en-GB', { hour: '2-digit', minute: '2-digit' })}.`;
        }
    },
];

export const getCategories = () => {
    const cats = ['all', ...new Set(smsServices.map(s => s.category))];
    return cats;
};