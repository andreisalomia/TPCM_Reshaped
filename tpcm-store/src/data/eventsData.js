const Images = {
    summer_music_festival_2025: "/images/events/summer_music_festival_2025.jpg",
    rock_legends_live: "/images/events/rock_legends_live.jpg",
    electronic_nights: "/images/events/electronic_nights.jpg",
    jazz_under_the_stars: "/images/events/jazz_under_the_stars.jpg",
    hip_hop_festival: "/images/events/hip_hop_festival.jpg",
    champions_league_final: "/images/events/champions_league_final.jpg",
    marathon_challenge: "/images/events/marathon_challenge.jpg",
    basketball_championship: "/images/events/basketball_championship.jpg",
    extreme_sports_fest: "/images/events/extreme_sports_fest.jpg",
    shakespeare_festival: "/images/events/shakespeare_festival.jpg",
    comedy_night_live: "/images/events/comedy_night_live.jpg",
    opera_gala_night: "/images/events/opera_gala_night.jpg",
    contemporary_dance_show: "/images/events/contemporary_dance_show.jpg",
    gamecon_2025: "/images/events/gamecon_2025.jpg",
    tech_summit_romania: "/images/events/tech_summit_romania.jpg",
    esports_championship: "/images/events/esports_championship.jpg",
    street_food_festival: "/images/events/street_food_festival.jpg",
    wine_tasting_evening: "/images/events/wine_tasting_evening.jpg",
};

const getName = (title) => {
    const key = title
        .toLowerCase()
        .replace(/'s\b/g, "s")
        .replace(/[^a-z0-9]/g, "_")
        .replace(/__+/g, "_")
        .replace(/^_|_$/g, "");

    return Images[key] || "images/games/fallback.jpg";
};

export const events = [
    // Music Festivals
    {
        id: 1,
        title: "Summer Music Festival 2025",
        description:
            "Three days of non-stop music with international and local artists.",
        price: 89.99,
        image: getName("Summer Music Festival 2025"),
        date: "2025-07-15",
        location: "Bucharest, Arena Națională",
        category: "music",
        features: ["3-Day Pass", "Multiple Stages", "Camping Available"],
    },
    {
        id: 2,
        title: "Rock Legends Live",
        description: "Classic rock bands reunion concert. One night only!",
        price: 125.0,
        image: getName("Rock Legends Live"),
        date: "2025-08-20",
        location: "Cluj-Napoca, BT Arena",
        category: "music",
        features: ["VIP Packages", "Meet & Greet", "Limited Seats"],
    },
    {
        id: 3,
        title: "Electronic Nights",
        description:
            "Best DJs from around the world in one epic electronic music event.",
        price: 65.0,
        image: getName("Electronic Nights"),
        date: "2025-09-10",
        location: "Constanța, Beach Arena",
        category: "music",
        features: ["Beach Party", "All Night Event", "Light Show"],
    },
    {
        id: 4,
        title: "Jazz Under The Stars",
        description:
            "Sophisticated evening of jazz music in an intimate outdoor setting.",
        price: 45.0,
        image: getName("Jazz Under The Stars"),
        date: "2025-06-25",
        location: "Sibiu, Piața Mare",
        category: "music",
        features: ["Outdoor Venue", "Premium Seating", "Wine & Dining"],
    },
    {
        id: 5,
        title: "Hip Hop Festival",
        description:
            "Urban culture celebration with top hip hop artists and street dancers.",
        price: 55.0,
        image: getName("Hip Hop Festival"),
        date: "2025-10-05",
        location: "Timișoara, Stadion Dan Păltinișanu",
        category: "music",
        features: ["Dance Battles", "Graffiti Zone", "Food Trucks"],
    },

    // Sports Events
    {
        id: 6,
        title: "Champions League Final",
        description:
            "Watch the biggest football match of the year on giant screens.",
        price: 30.0,
        image: getName("Champions League Final"),
        date: "2025-05-31",
        location: "Bucharest, Piața Constituției",
        category: "sports",
        features: ["Giant Screens", "Fan Zone", "Food & Drinks"],
    },
    {
        id: 7,
        title: "Marathon Challenge",
        description:
            "Annual city marathon for runners of all levels. 42km of pure adrenaline.",
        price: 25.0,
        image: getName("Marathon Challenge"),
        date: "2025-04-15",
        location: "Bucharest, City Center",
        category: "sports",
        features: ["Official Timing", "Medal & T-Shirt", "Multiple Categories"],
    },
    {
        id: 8,
        title: "Basketball Championship",
        description:
            "National basketball championship finals. Best teams compete!",
        price: 20.0,
        image: getName("Basketball Championship"),
        date: "2025-11-22",
        location: "Cluj-Napoca, Sala Polivalentă",
        category: "sports",
        features: [
            "Championship Finals",
            "Family Friendly",
            "Premium Seats Available",
        ],
    },
    {
        id: 9,
        title: "Extreme Sports Fest",
        description:
            "Skateboarding, BMX, and parkour competitions with pro athletes.",
        price: 35.0,
        image: getName("Extreme Sports Fest"),
        date: "2025-07-01",
        location: "Brașov, Skate Park",
        category: "sports",
        features: ["Pro Competitions", "Workshops", "Live Music"],
    },

    // Theater & Arts
    {
        id: 10,
        title: "Shakespeare Festival",
        description:
            "Classic Shakespeare plays performed by renowned theater companies.",
        price: 40.0,
        image: getName("Shakespeare Festival"),
        date: "2025-06-10",
        location: "Sibiu, Teatrul Național",
        category: "theater",
        features: [
            "Multiple Performances",
            "English Subtitles",
            "Matinee & Evening Shows",
        ],
    },
    {
        id: 11,
        title: "Comedy Night Live",
        description:
            "Stand-up comedy show with the funniest comedians in Romania.",
        price: 35.0,
        image: getName("Comedy Night Live"),
        date: "2025-08-15",
        location: "Bucharest, Sala Palatului",
        category: "theater",
        features: ["Stand-Up Comedy", "18+ Event", "Bar Available"],
    },
    {
        id: 12,
        title: "Opera Gala Night",
        description: "Elegant opera performance featuring renowned soloists.",
        price: 75.0,
        image: getName("Opera Gala Night"),
        date: "2025-09-20",
        location: "Iași, Opera Națională",
        category: "theater",
        features: [
            "Black Tie Event",
            "Orchestra Performance",
            "Premium Experience",
        ],
    },
    {
        id: 13,
        title: "Contemporary Dance Show",
        description:
            "Modern dance performance exploring themes of identity and connection.",
        price: 30.0,
        image: getName("Contemporary Dance Show"),
        date: "2025-10-18",
        location: "Cluj-Napoca, Teatrul Maghiar",
        category: "theater",
        features: ["Modern Dance", "Visual Effects", "Limited Run"],
    },

    // Technology & Gaming
    {
        id: 14,
        title: "GameCon 2025",
        description:
            "Largest gaming convention. Try new games, meet developers, compete!",
        price: 50.0,
        image: getName("GameCon 2025"),
        date: "2025-11-15",
        location: "Bucharest, Romexpo",
        category: "gaming",
        features: ["Game Demos", "Tournaments", "Cosplay Contest"],
    },
    {
        id: 15,
        title: "Tech Summit Romania",
        description:
            "Conference for tech professionals. Networking, talks, and workshops.",
        price: 150.0,
        image: getName("Tech Summit Romania"),
        date: "2025-05-20",
        location: "Bucharest, JW Marriott",
        category: "technology",
        features: ["Industry Leaders", "Workshops", "Networking Event"],
    },
    {
        id: 16,
        title: "Esports Championship",
        description:
            "National esports tournament. Watch pro gamers compete for the title.",
        price: 25.0,
        image: getName("Esports Championship"),
        date: "2025-12-05",
        location: "Cluj-Napoca, BT Arena",
        category: "gaming",
        features: ["Live Matches", "Prize Pool", "Audience Participation"],
    },

    // Food & Culture
    {
        id: 17,
        title: "Street Food Festival",
        description: "International cuisine from food trucks. Taste the world!",
        price: 15.0,
        image: getName("Street Food Festival"),
        date: "2025-06-01",
        location: "Bucharest, Herăstrău Park",
        category: "food",
        features: ["50+ Food Trucks", "Live Music", "Family Event"],
    },
    {
        id: 18,
        title: "Wine Tasting Evening",
        description: "Sample premium Romanian wines with expert sommeliers.",
        price: 60.0,
        image: getName("Wine Tasting Evening"),
        date: "2025-09-30",
        location: "Sinaia, Castel Peleș",
        category: "food",
        features: ["Premium Wines", "Cheese Pairing", "Educational"],
    },
];

export const getEventsByCategory = (category) => {
    if (!category || category === "all") return events;
    return events.filter((event) => event.category === category);
};

export const getPaginatedEvents = (
    page = 1,
    itemsPerPage = 6,
    category = "all"
) => {
    const filtered = getEventsByCategory(category);
    const startIndex = (page - 1) * itemsPerPage;
    const endIndex = startIndex + itemsPerPage;

    return {
        events: filtered.slice(startIndex, endIndex),
        totalPages: Math.ceil(filtered.length / itemsPerPage),
        currentPage: page,
        totalItems: filtered.length,
    };
};

export const getCategories = () => {
    const categories = [...new Set(events.map((event) => event.category))];
    return ["all", ...categories];
};

export const getUpcomingEvents = (limit = 6) => {
    const now = new Date();
    return events
        .filter((event) => new Date(event.date) >= now)
        .sort((a, b) => new Date(a.date) - new Date(b.date))
        .slice(0, limit);
};

const shuffleArray = (array) => {
    const shuffled = [...array];
    for (let i = shuffled.length - 1; i > 0; i--) {
        const j = Math.floor(Math.random() * (i + 1));
        [shuffled[i], shuffled[j]] = [shuffled[j], shuffled[i]];
    }
    return shuffled;
};

export const getRandomEvents = (count = 6) => {
    return shuffleArray(events).slice(0, count);
};

export const getPaginatedEventsShuffled = (
    page = 1,
    itemsPerPage = 6,
    category = "all",
    shuffle = false
) => {
    let filtered = getEventsByCategory(category);

    if (shuffle) {
        filtered = shuffleArray(filtered);
    }

    const startIndex = (page - 1) * itemsPerPage;
    const endIndex = startIndex + itemsPerPage;

    return {
        events: filtered.slice(startIndex, endIndex),
        totalPages: Math.ceil(filtered.length / itemsPerPage),
        currentPage: page,
        totalItems: filtered.length,
    };
};
