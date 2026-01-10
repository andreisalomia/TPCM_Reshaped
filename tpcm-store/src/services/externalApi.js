const TICKETMASTER_API_KEY = import.meta.env.VITE_TICKETMASTER_API_KEY;

const CHEAPSHARK_BASE_URL = 'https://www.cheapshark.com/api/1.0';
const TICKETMASTER_BASE_URL = 'https://app.ticketmaster.com/discovery/v2';

const transformGameToApp = (game) => {
  const price = parseFloat(game.cheapest);
  
  const cleanTitle = game.external.replace(/\s*\(.*?\)\s*/g, '').trim();

  const features = [];
  
  if (price < 5) {
    features.push('Great Value Deal');
  } else if (price < 10) {
    features.push('Special Offer');
  }
  
  const gamingFeatures = [
    'Steam Achievements',
    'Cloud Save Support',
    'Full Controller Support',
    'Online Multiplayer',
    'Single Player Campaign',
    'Steam Workshop',
    'Trading Cards',
    'VR Support'
  ];

  const shuffled = gamingFeatures.sort(() => 0.5 - Math.random());
  features.push(...shuffled.slice(0, 3 - features.length));

  return {
    id: game.gameID,
    title: cleanTitle,
    description: `${cleanTitle} - Currently on sale! Amazing deals from multiple stores.`,
    price: price,
    image: game.thumb || '/placeholder-game.jpg',
    category: 'games',
    features: features.slice(0, 3),
    steamAppID: game.steamAppID
  };
};

const transformTicketmasterToEvent = (event) => {
  let price = 19.99;
  
  if (event.priceRanges && event.priceRanges.length > 0) {
    price = event.priceRanges[0].min || event.priceRanges[0].max || 19.99;
  }

  const features = [];
  
  if (event.classifications && event.classifications.length > 0) {
    const classification = event.classifications[0];
    if (classification.segment) {
      features.push(classification.segment.name);
    }
    if (classification.genre) {
      features.push(classification.genre.name);
    }
  }
  
  if (event.info) {
    features.push(event.info);
  }

  const defaultFeatures = [
    'Full Event Access',
    'Reserved Seating',
    'Digital Ticket',
    'Early Entry Available',
    'VIP Options',
    'Exclusive Merchandise'
  ];
  
  while (features.length < 3 && defaultFeatures.length > 0) {
    features.push(defaultFeatures.shift());
  }

  let location = 'Venue TBA';
  if (event._embedded?.venues && event._embedded.venues.length > 0) {
    const venue = event._embedded.venues[0];
    location = venue.city?.name 
      ? `${venue.name}, ${venue.city.name}` 
      : venue.name || location;
  }

  return {
    id: event.id,
    title: event.name,
    description: event.info || event.description || `${event.name} - Don't miss this amazing event!`,
    price: parseFloat(price.toFixed(2)),
    image: event.images?.[0]?.url || '/placeholder-event.jpg',
    date: event.dates?.start?.localDate || new Date().toISOString().split('T')[0],
    location: location,
    features: features.slice(0, 3)
  };
};

export const externalApi = {
  /**
   * Fetch games from CheapShark API
   * @param {number} limit - Number of games to fetch (default: 10)
   * @returns {Promise<Array>} Array of game objects in our format
   */
  getGames: async (limit = 10) => {
    try {
      const popularSearches = [
        'batman', 'call of duty', 'fallout', 'grand theft auto', 
        'dark souls', 'witcher', 'assassin', 'borderlands',
        'resident evil', 'final fantasy', 'tomb raider', 'bioshock'
      ];
      
      const randomSearch = popularSearches[Math.floor(Math.random() * popularSearches.length)];
      
      const response = await fetch(
        `${CHEAPSHARK_BASE_URL}/games?title=${encodeURIComponent(randomSearch)}&limit=${limit}`
      );
      
      if (!response.ok) {
        throw new Error(`CheapShark API error: ${response.status}`);
      }

      const data = await response.json();
      
      return data.map(transformGameToApp).slice(0, limit);
      
    } catch (error) {
      console.error('Error fetching games from CheapShark:', error);
      return [];
    }
  },

  /**
   * Fetch events from Ticketmaster API
   * @param {number} limit - Number of events to fetch (default: 10)
   * @param {string} countryCode - Country code (default: 'RO' for Romania)
   * @returns {Promise<Array>} Array of event objects in our format
   */
  getEvents: async (limit = 10, countryCode = 'RO') => {
    try {
      const response = await fetch(
        `${TICKETMASTER_BASE_URL}/events.json?apikey=${TICKETMASTER_API_KEY}&countryCode=${countryCode}&size=${limit}&sort=date,asc`
      );
      
      if (!response.ok) {
        throw new Error(`Ticketmaster API error: ${response.status}`);
      }

      const data = await response.json();
      
      if (!data._embedded || !data._embedded.events) {
        console.warn('No events found from Ticketmaster');
        return [];
      }

      return data._embedded.events.map(transformTicketmasterToEvent);
      
    } catch (error) {
      console.error('Error fetching events from Ticketmaster:', error);
      return [];
    }
  },

  getGameById: async (id) => {
    try {
      const response = await fetch(
        `${CHEAPSHARK_BASE_URL}/games?id=${id}`
      );
      
      if (!response.ok) {
        throw new Error(`CheapShark API error: ${response.status}`);
      }

      const data = await response.json();
      
      const gameInfo = {
        id: id,
        title: data.info.title,
        description: `${data.info.title} - Check out these amazing deals! Lowest price ever: $${data.cheapestPriceEver.price}`,
        price: parseFloat(data.deals[0]?.price || '9.99'),
        image: data.info.thumb || '/placeholder-game.jpg',
        category: 'games',
        features: [
          `Cheapest Deal: $${data.deals[0]?.price}`,
          `Best Ever: $${data.cheapestPriceEver.price}`,
          `Available on ${data.deals.length} stores`
        ],
        deals: data.deals.slice(0, 5)
      };
      
      return gameInfo;
      
    } catch (error) {
      console.error('Error fetching game details:', error);
      return null;
    }
  },

  getEventById: async (id) => {
    try {
      const response = await fetch(
        `${TICKETMASTER_BASE_URL}/events/${id}.json?apikey=${TICKETMASTER_API_KEY}`
      );
      
      if (!response.ok) {
        throw new Error(`Ticketmaster API error: ${response.status}`);
      }

      const event = await response.json();
      return transformTicketmasterToEvent(event);
      
    } catch (error) {
      console.error('Error fetching event details:', error);
      return null;
    }
  }
};

export default externalApi;