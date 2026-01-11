const Images = {
    cyber_assault_2077: "/images/games/cyber_assault_2077.jpg",
    shadow_warrior_elite: "/images/games/shadow_warrior_elite.jpg",
    apocalypse_survivors: "/images/games/apocalypse_survivors.jpg",
    street_racer_x: "/images/games/street_racer_x.jpg",
    dragons_quest_legacy: "/images/games/dragons_quest_legacy.jpg",
    mystic_realms_online: "/images/games/mystic_realms_online.jpg",
    dark_dungeon_chronicles: "/images/games/dark_dungeon_chronicles.jpg",
    wizards_academy: "/images/games/wizards_academy.jpg",
    empire_builder_pro: "/images/games/empire_builder_pro.jpg",
    space_command_elite: "/images/games/space_command_elite.jpg",
    medieval_tactics: "/images/games/medieval_tactics.jpg",
    city_architect_2025: "/images/games/city_architect_2025.jpg",
    lost_island_explorer: "/images/games/lost_island_explorer.jpg",
    time_travelers_paradox: "/images/games/time_travelers_paradox.jpg",
    oceans_mystery: "/images/games/oceans_mystery.jpg",
    mountain_peak_challenge: "/images/games/mountain_peak_challenge.jpg",
    mind_bender_pro: "/images/games/mind_bender_pro.jpg",
    block_master_deluxe: "/images/games/block_master_deluxe.jpg",
    logic_gates_academy: "/images/games/logic_gates_academy.jpg",
    maze_runner_3d: "/images/games/maze_runner_3d.jpg",
    soccer_champions_2025: "/images/games/soccer_champions_2025.jpg",
    basketball_dynasty: "/images/games/basketball_dynasty.jpg",
    extreme_snowboarding: "/images/games/extreme_snowboarding.jpg",
    tennis_grand_slam: "/images/games/tennis_grand_slam.jpg",
    nightmare_asylum: "/images/games/nightmare_asylum.jpg",
    zombie_outbreak: "/images/games/zombie_outbreak.jpg",
    ghost_hunter_chronicles: "/images/games/ghost_hunter_chronicles.jpg",
    farm_life_simulator: "/images/games/farm_life_simulator.jpg",
    flight_simulator_pro: "/images/games/flight_simulator_pro.jpg",
    restaurant_tycoon: "/images/games/restaurant_tycoon.jpg",
    pixel_art_adventure: "/images/games/pixel_art_adventure.jpg",
    starlight_dreams: "/images/games/starlight_dreams.jpg",
    roguelike_dungeon: "/images/games/roguelike_dungeon.jpg",
};


const getName = (title) => {
    const key = title
        .toLowerCase()
        .replace(/'s\b/g, 's')
        .replace(/[^a-z0-9]/g, '_')
        .replace(/__+/g, '_')
        .replace(/^_|_$/g, '');

    return Images[key] || "images/games/fallback.jpg";
};

export const games = [
    {
        id: 1,
        title: "Cyber Assault 2077",
        description: "Futuristic action RPG set in a dystopian megacity. Make choices that matter.",
        price: 7.99,
        image: getName("Cyber Assault 2077"),
        category: "action",
        features: ["Open World", "Story-Driven", "4K Support"]
    },
    {
        id: 2,
        title: "Shadow Warrior Elite",
        description: "Fast-paced ninja combat with stunning visuals and fluid gameplay.",
        price: 59.99,
        image: getName("Shadow Warrior Elite"),
        category: "action",
        features: ["Multiplayer", "60 FPS", "Controller Support"]
    },
    {
        id: 3,
        title: "Apocalypse Survivors",
        description: "Survive in a post-apocalyptic world. Build, craft, and fight to stay alive.",
        price: 2.99,
        image: getName("Apocalypse Survivors"),
        category: "action",
        features: ["Co-op Mode", "Crafting System", "Survival"]
    },
    {
        id: 4,
        title: "Street Racer X",
        description: "High-octane street racing with customizable cars and intense competition.",
        price: 3.99,
        image: getName("Street Racer X"),
        category: "racing",
        features: ["Online Racing", "Car Customization", "HDR"]
    },
    {
        id: 5,
        title: "Dragon's Quest Legacy",
        description: "Epic fantasy RPG with dragons, magic, and legendary quests.",
        price: 14.99,
        image: getName("Dragon's Quest Legacy"),
        category: "rpg",
        features: ["100+ Hours", "Character Customization", "Epic Story"]
    },
    {
        id: 6,
        title: "Mystic Realms Online",
        description: "MMORPG with thousands of players. Build your legend online.",
        price: 1.99,
        image: getName("Mystic Realms Online"),
        category: "rpg",
        features: ["MMO", "Guilds", "PvP Arena"]
    },
    {
        id: 7,
        title: "Dark Dungeon Chronicles",
        description: "Tactical RPG with turn-based combat and deep character progression.",
        price: 17.99,
        image: getName("Dark Dungeon Chronicles"),
        category: "rpg",
        features: ["Turn-Based", "Strategy", "Permadeath"]
    },
    {
        id: 8,
        title: "Wizard's Academy",
        description: "Learn magic, attend classes, and uncover dark secrets in this school RPG.",
        price: 3.99,
        image: getName("Wizard's Academy"),
        category: "rpg",
        features: ["Choice-Driven", "Magic System", "Romance Options"]
    },
    {
        id: 9,
        title: "Empire Builder Pro",
        description: "Build your empire from scratch. Manage resources, armies, and diplomacy.",
        price: 6.99,
        image: getName("Empire Builder Pro"),
        category: "strategy",
        features: ["Real-Time Strategy", "Multiplayer", "Mod Support"]
    },
    {
        id: 10,
        title: "Space Command Elite",
        description: "Command fleets in epic space battles. Strategy meets sci-fi.",
        price: 29.99,
        image: getName("Space Command Elite"),
        category: "strategy",
        features: ["Fleet Management", "Space Combat", "Campaign Mode"]
    },
    {
        id: 11,
        title: "Medieval Tactics",
        description: "Turn-based medieval warfare. Position your troops wisely.",
        price: 2.99,
        image: getName("Medieval Tactics"),
        category: "strategy",
        features: ["Turn-Based", "Historical", "Challenging AI"]
    },
    {
        id: 12,
        title: "City Architect 2025",
        description: "Design and manage your dream city. Balance growth with sustainability.",
        price: 8.99,
        image: getName("City Architect 2025"),
        category: "strategy",
        features: ["City Building", "Economics", "Sandbox Mode"]
    },
    {
        id: 13,
        title: "Lost Island Explorer",
        description: "Explore a mysterious island full of secrets and ancient ruins.",
        price: 39.99,
        image: getName("Lost Island Explorer"),
        category: "adventure",
        features: ["Exploration", "Puzzle Solving", "Beautiful Graphics"]
    },
    {
        id: 14,
        title: "Time Traveler's Paradox",
        description: "Travel through time to prevent a catastrophic future.",
        price: 3.99,
        image: getName("Time Traveler's Paradox"),
        category: "adventure",
        features: ["Time Travel", "Multiple Endings", "Narrative-Driven"]
    },
    {
        id: 15,
        title: "Ocean's Mystery",
        description: "Dive deep into the ocean and discover what lies beneath.",
        price: 42.99,
        image: getName("Ocean's Mystery"),
        category: "adventure",
        features: ["Underwater Exploration", "Marine Life", "Relaxing"]
    },
    {
        id: 16,
        title: "Mountain Peak Challenge",
        description: "Climb the world's highest peaks in this breathtaking adventure.",
        price: 1.99,
        image: getName("Mountain Peak Challenge"),
        category: "adventure",
        features: ["Mountain Climbing", "Weather System", "Realistic Physics"]
    },
    {
        id: 17,
        title: "Mind Bender Pro",
        description: "Challenge your brain with increasingly complex puzzles.",
        price: 4.99,
        image: getName("Mind Bender Pro"),
        category: "puzzle",
        features: ["500+ Puzzles", "Daily Challenges", "Leaderboards"]
    },
    {
        id: 18,
        title: "Block Master Deluxe",
        description: "Classic block-matching gameplay with modern twists.",
        price: 9.99,
        image: getName("Block Master Deluxe"),
        category: "puzzle",
        features: ["Casual Gaming", "Quick Sessions", "Addictive"]
    },
    {
        id: 19,
        title: "Logic Gates Academy",
        description: "Learn programming logic through engaging puzzle gameplay.",
        price: 26.99,
        image: getName("Logic Gates Academy"),
        category: "puzzle",
        features: ["Educational", "Progressive Difficulty", "Code Learning"]
    },
    {
        id: 20,
        title: "Maze Runner 3D",
        description: "Navigate complex 3D mazes with increasing difficulty.",
        price: 12.99,
        image: getName("Maze Runner 3D"),
        category: "puzzle",
        features: ["3D Mazes", "Time Trials", "Level Editor"]
    },
    {
        id: 21,
        title: "Soccer Champions 2025",
        description: "The most realistic soccer simulation ever made.",
        price: 4.99,
        image: getName("Soccer Champions 2025"),
        category: "sports",
        features: ["Online Leagues", "Career Mode", "Real Teams"]
    },
    {
        id: 22,
        title: "Basketball Dynasty",
        description: "Build your basketball legacy from rookie to hall of fame.",
        price: 3.99,
        image: getName("Basketball Dynasty"),
        category: "sports",
        features: ["Career Mode", "Online Tournaments", "Customization"]
    },
    {
        id: 23,
        title: "Extreme Snowboarding",
        description: "Perform insane tricks on the world's best slopes.",
        price: 2.99,
        image: getName("Extreme Snowboarding"),
        category: "sports",
        features: ["Trick System", "Open Mountains", "Multiplayer"]
    },
    {
        id: 24,
        title: "Tennis Grand Slam",
        description: "Compete in all four Grand Slam tournaments.",
        price: 3.99,
        image: getName("Tennis Grand Slam"),
        category: "sports",
        features: ["Official Tournaments", "Online Ranked", "Motion Capture"]
    },
    {
        id: 25,
        title: "Nightmare Asylum",
        description: "Survive the night in this terrifying abandoned asylum.",
        price: 1.99,
        image: getName("Nightmare Asylum"),
        category: "horror",
        features: ["Psychological Horror", "Atmospheric", "VR Support"]
    },
    {
        id: 26,
        title: "Zombie Outbreak",
        description: "Fight for survival in a zombie-infested city.",
        price: 3.99,
        image: getName("Zombie Outbreak"),
        category: "horror",
        features: ["Co-op Survival", "Weapon Crafting", "Intense Action"]
    },
    {
        id: 27,
        title: "Ghost Hunter Chronicles",
        description: "Investigate paranormal activities with high-tech ghost hunting equipment.",
        price: 1.99,
        image: getName("Ghost Hunter Chronicles"),
        category: "horror",
        features: ["Investigation", "Equipment Upgrade", "Multiplayer"]
    },
    {
        id: 28,
        title: "Farm Life Simulator",
        description: "Build and manage your dream farm from the ground up.",
        price: 0.99,
        image: getName("Farm Life Simulator"),
        category: "simulation",
        features: ["Farming", "Animal Care", "Seasonal Events"]
    },
    {
        id: 29,
        title: "Flight Simulator Pro",
        description: "Experience realistic flight simulation with detailed aircraft.",
        price: 0.99,
        image: getName("Flight Simulator Pro"),
        category: "simulation",
        features: ["Realistic Physics", "World Scenery", "Weather System"]
    },
    {
        id: 30,
        title: "Restaurant Tycoon",
        description: "Build a restaurant empire from a small diner to a global franchise.",
        price: 2.99,
        image: getName("Restaurant Tycoon"),
        category: "simulation",
        features: ["Business Management", "Recipe Creation", "Staff Management"]
    },
    {
        id: 31,
        title: "Pixel Art Adventure",
        description: "Charming indie platformer with retro pixel art graphics.",
        price: 15.99,
        image: getName("Pixel Art Adventure"),
        category: "indie",
        features: ["Retro Graphics", "Challenging Platforming", "Great Soundtrack"]
    },
    {
        id: 32,
        title: "Starlight Dreams",
        description: "Emotional narrative experience about hope and perseverance.",
        price: 18.99,
        image: getName("Starlight Dreams"),
        category: "indie",
        features: ["Story-Rich", "Beautiful Art", "Touching Narrative"]
    },
    {
        id: 33,
        title: "Roguelike Dungeon",
        description: "Procedurally generated dungeons with permadeath. Every run is unique.",
        price: 19.99,
        image: getName("Roguelike Dungeon"),
        category: "indie",
        features: ["Roguelike", "Procedural Generation", "High Replayability"]
    }
];

export const getGamesByCategory = (category) => {
    if (!category || category === 'all') return games;
    return games.filter(game => game.category === category);
};

export const getPaginatedGames = (page = 1, itemsPerPage = 9, category = 'all') => {
    const filtered = getGamesByCategory(category);
    const startIndex = (page - 1) * itemsPerPage;
    const endIndex = startIndex + itemsPerPage;

    return {
        games: filtered.slice(startIndex, endIndex),
        totalPages: Math.ceil(filtered.length / itemsPerPage),
        currentPage: page,
        totalItems: filtered.length
    };
};

export const getCategories = () => {
    const categories = [...new Set(games.map(game => game.category))];
    return ['all', ...categories];
};

const shuffleArray = (array) => {
    const shuffled = [...array];
    for (let i = shuffled.length - 1; i > 0; i--) {
        const j = Math.floor(Math.random() * (i + 1));
        [shuffled[i], shuffled[j]] = [shuffled[j], shuffled[i]];
    }
    return shuffled;
};

export const getRandomGames = (count = 9) => {
    return shuffleArray(games).slice(0, count);
};

export const getPaginatedGamesShuffled = (page = 1, itemsPerPage = 9, category = 'all', shuffle = false) => {
    let filtered = getGamesByCategory(category);

    if (shuffle) {
        filtered = shuffleArray(filtered);
    }

    const startIndex = (page - 1) * itemsPerPage;
    const endIndex = startIndex + itemsPerPage;

    return {
        games: filtered.slice(startIndex, endIndex),
        totalPages: Math.ceil(filtered.length / itemsPerPage),
        currentPage: page,
        totalItems: filtered.length
    };
};