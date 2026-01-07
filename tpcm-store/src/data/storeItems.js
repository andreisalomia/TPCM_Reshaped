import jetpack from  '../images/jetpack.jpg';
import photoeditor from '../images/photoeditor.png';
import calories from '../images/calories.jpg';
import summer from '../images/summer.png';
import tech from '../images/tech.png';

export const apps = [
  {
    id: 1,
    title: "JetPack Joyride",
    description: "Runner game where you control a charachter with a jetpack",
    price: 3.99,
    image: jetpack,
    category: "games",
    features: ["No Ads", "Offline Play", "Cloud Save"]
  },
  {
    id: 2,
    title: "Photo Editor Pro",
    description: "Professional photo editing app with advanced features",
    price: 9.99,
    image: photoeditor,
    category: "utility",
    features: ["Filters", "Crop & Resize", "AI Enhancements"]
  },
  {
    id: 3,
    title: "Calories Tracker",
    description: "Track your daily calorie intake and exercise",
    price: 2.99,
    image: calories,
    category: "utility",
    features: ["Calorie Tracking", "Exercise Logging", "Progress Reports"]
  }
];

export const events = [
  {
    id: 1,
    title: "Summer Well",
    description: "Annual music festival with top artists",
    price: 89.99,
    image: summer,
    date: "2025-08-15",
    location: "Buftea",
    features: ["Full Festival Access", "VIP Lounge", "Exclusive Merchandise"]
  },
  {
    id: 2,
    title: "Web Conference 2025",
    description: "Annual technology conference with industry leaders",
    price: 19.99,
    image: tech,
    date: "2025-09-29",
    location: "Romexpo, Bucharest",
    features: ["Speeches", "Workshops", "Networking Opportunities"]
  }
];