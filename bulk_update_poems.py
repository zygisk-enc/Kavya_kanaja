import json

file_path = 'app/src/main/assets/poems.json'

with open(file_path, 'r', encoding='utf-8') as f:
    data = json.load(f)

updates = {
    47: {
        "title": "Taravalla Tagi Ninna Tamburi",
        "poet": "Shishunala Sharif",
        "content": "ತರವಲ್ಲ ತಗಿ ನಿನ್ನ ತಂಬೂರಿ - ಸ್ವರ ಬರದೆ ಬಾರಿಸದಿರು ತಂಬೂರಿ\nಸರಸ ಸಂಗೀತದ ಕುರುಹುಗಳ ಅರಿಯದೆ ಬರಿದೆ ಬಾರಿಸದಿರು ತಂಬೂರಿ ||\n\nಮದ್ದಲಿ ದನಿಯೊಳು ತಂಬೂರಿ - ಅದ ತಿದ್ದಿ ನುಡಿಸಬೇಕು ತಂಬೂರಿ\nಸಿದ್ಧ ಸಾಧಕರ ಸುವಿದ್ಯೆಗೆ ಒದಗುವ ಬುದ್ಧಿವಂತಗೆ ತಕ್ಕ ತಂಬೂರಿ ||\n\nಬಾಲ ಬಲ್ಲವರಿಗೆ ತಂಬೂರಿ - ದೇವ ಬಾಲಾಕ್ಷ ರಚಿಸಿದ ತಂಬೂರಿ\nಹೇಳಲಿ ಏನಿದರ ಹಂಚಿಕೆ ತಿಳಿಯದ ತಾಳಗೆಡಿಗೆ ಸಲ್ಲ ತಂಬೂರಿ ||",
        "meanings": {
            "ತರವಲ್ಲ": "Not proper",
            "ತಗಿ": "Touch/Play",
            "ತಂಬೂರಿ": "Body/Instrument",
            "ಸ್ವರ": "Note/Melody",
            "ಬಾರಿಸದಿರು": "Do not play",
            "ಕುರುಹು": "Sign/Knowledge",
            "ಮದ್ದಲಿ": "Drum (Material noise)",
            "ತಿದ್ದಿ": "Tuning/Correcting",
            "ಸಿದ್ಧ": "Enlightened",
            "ಬುದ್ಧಿವಂತ": "Wise",
            "ತಾಳಗೆಡಿ": "One who lost rhythm"
        }
    },
    48: {
        "title": "Naanu Naanalla",
        "poet": "Chi. Udayashankar",
        "content": "ನಾನು ನಾನಲ್ಲ, ನೀನೇ ನಾನು\nನನ್ನ ಈ ಬದುಕಿನ, ನೀನೇ ನಾನು\nನನ್ನ ಈ ಉಸಿರಿನ, ನೀನೇ ನಾನು\nನನ್ನ ಈ ಕನಸಿನ, ನೀನೇ ನಾನು ||\n\nಕಣ್ಣಿನ ಪಾಪೆಯು ನೀನಾದೆ\nಮನದ ಆಸೆಯು ನೀನಾದೆ\nನನ್ನಯ ನಡಿಗೆಯು ನೀನಾದೆ\nನನ್ನಯ ನುಡಿಯು ನೀನಾದೆ ||\n\nಬಾಳಿನ ದಾರಿಯು ನೀನಾದೆ\nಬೆಳಕಿನ ಕಿರಣವು ನೀನಾದೆ\nನನ್ನಯ ಪ್ರೇಮವು ನೀನಾದೆ\nನನ್ನಯ ದೈವವು ನೀನಾದೆ ||",
        "meanings": {
            "ನಾನು ನಾನಲ್ಲ": "I am not me",
            "ನೀನೇ ನಾನು": "You are me",
            "ಬದುಕು": "Life",
            "ಉಸಿರು": "Breath",
            "ಕನಸು": "Dream",
            "ಪಾಪೆ": "Pupil of eye",
            "ನಡಿಗೆ": "Walk/Path",
            "ನುಡಿ": "Speech",
            "ಕಿರಣ": "Ray",
            "ದೈವ": "God"
        }
    },
    49: {
        "title": "Yetta Tirugali Kannu",
        "poet": "Kuvempu",
        "content": "ಎತ್ತ ತಿರುಗಲಿ ಕಣ್ಣು ನಿನ್ನ ಕಾಣುವಾಸೆ\nಏನ ನೆನೆಯಲಿ ಚಿತ್ತ ನಿನ್ನ ಚಿಂತಿಪಾಸೆ\nನುಡಿವ ನಾಲಿಗೆಗೊಂದೆ ನಿನ್ನ ಹೆಸರಿನಾಸೆ\nಮಣಿವ ಹಣೆಗೆ ಇಹುದೊಂದೆ ನಿನ್ನ ಚರಣದಾಸೆ ||\n\nಮುತ್ತಿಡುವ ತುಟಿಗೊಂದೆ ನಿನ್ನ ಕೆನ್ನೆಯಾಸೆ\nಆಲಂಗಿಸೊ ತೋಳಗೇ ನಿನ್ನ ವಕ್ಷದಾಸೆ\nಮೊಲ್ಲೆ ಮಲ್ಲಿಗೆ ರಾಶಿ ಮೈಯನ್ನಪ್ಪುವಾಸೆ\nಜೀವ ದುಂಬಿಗೆ ಮೂಸಿ ತಣಿವನೊಪ್ಪದಾಸೆ ||\n\nಉಸಿರು ಉಸಿರೊಳು ನಿನ್ನ ಒಳಗೆ ಕೊಳ್ಳುವಾಸೆ\nಉಸಿರು ಉಸಿರು ನಿನ್ನೊಳು ನಿತ್ಯವಾಗುವಾಸೆ\nಹೀರಿದನಿತು ಹೀರಿ ಸೇರಿ ಹೋಗುವಾಸೆ\nಸೇರಿ ಹೋಗುವಾಸೆ... ಸೇರಿ ಹೋಗುವಾಸೆ... ||",
        "meanings": {
            "ಎತ್ತ": "Where/Whichever side",
            "ತಿರುಗಲಿ": "Turn",
            "ಕಾಣುವಾಸೆ": "Desire to see",
            "ಚಿತ್ತ": "Mind/Heart",
            "ಹಣೆ": "Forehead",
            "ಚರಣ": "Feet",
            "ತೋಳು": "Arms",
            "ದುಂಬಿ": "Bee/Soul",
            "ಉಸಿರು": "Breath",
            "ಸೇರಿ ಹೋಗು": "Merge"
        }
    },
    50: {
        "title": "Ellaru Maduvudu Hottegagi",
        "poet": "Kanakadasa",
        "content": "ಎಲ್ಲಾರು ಮಾಡುವುದು ಹೊಟ್ಟೆಗಾಗಿ ಗೇಣು ಬಟ್ಟೆಗಾಗಿ ||\n\nವೇದ ಶಾಸ್ತ್ರ ಪಂಚಾಂಗ ಓದಿಕೊಂಡು ಅನ್ಯರಿಗೆ\nಬೋಧನೆಯ ಮಾಡುವುದು ಹೊಟ್ಟೆಗಾಗಿ ಗೇಣು ಬಟ್ಟೆಗಾಗಿ ||\n\nಚಂಡ ಭಟರಾಗಿ ನಡೆದು ಕತ್ತಿ ಢಾಲು ಕೈಲಿ ಹಿಡಿದು\nಖಂಡ ತುಂಡ ಮಾಡುವುದು ಹೊಟ್ಟೆಗಾಗಿ ಗೇಣು ಬಟ್ಟೆಗಾಗಿ ||\n\nಸನ್ಯಾಸಿ ಜಂಗಮ ಜೋಗಿ ಜಟ್ಟಿ ಮೊಂಡ ಭೈರಾಗಿ\nನಾನಾ ವೇಷ ಹಾಕುವುದು ಹೊಟ್ಟೆಗಾಗಿ ಗೇಣು ಬಟ್ಟೆಗಾಗಿ ||",
        "meanings": {
            "ಹೊಟ್ಟೆಗಾಗಿ": "For the stomach/hunger",
            "ಗೇಣು ಬಟ್ಟೆ": "Span of cloth/clothing",
            "ಅನ್ಯರಿಗೆ": "To others",
            "ಬೋಧನೆ": "Preaching",
            "ಭಟ": "Soldier",
            "ಕತ್ತಿ": "Sword",
            "ವೇಷ": "Guise/Role"
        }
    },
    51: {
        "title": "Hogutiddu Nodu Ee Jagava",
        "poet": "Folk",
        "content": "ಹೋಗುತಿದ್ದು ನೋಡು ಈ ಜಗವ | ಹೋಗುತಿದ್ದು ನೋಡು ಈ ಜಗವ ||\n\nಬಂದವರು ಹೋದರು | ನಿಂದವರು ಹೋದರು |\nಕಂದಮ್ಮಗಳ ಬಿಟ್ಟು ತಂದೆ ತಾಯಿ ಹೋದರು ||\n\nರಾಜರು ಹೋದರು | ರಾಜ್ಯವ ಬಿಟ್ಟು |\nತೇಜವಂತರು ಹೋದರು ದೇಶವ ಬಿಟ್ಟು ||\n\nಯಾರೂ ಬರುವುದಿಲ್ಲ | ಯಾರೂ ಇರುವುದಿಲ್ಲ |\nಸತ್ಯವೊಂದೇ ಉಳಿಯುವುದು ಈ ಜಗದೊಳಗೆ ||",
        "meanings": {
            "ಜಗವ": "World",
            "ಬಂದವರು": "Those who came",
            "ಕಂದಮ್ಮ": "Children/Infants",
            "ರಾಜ": "King",
            "ತೇಜವಂತ": "Illustrious/Glorious",
            "ಸತ್ಯ": "Truth",
            "ಉಳಿಯುವುದು": "Will remain"
        }
    },
    52: {
        "title": "Maleyali Jotheyali",
        "poet": "Jayant Kaikini",
        "content": "ಮಳೆಯಲಿ ಜೊತೆಯಲಿ ದಿನವಿಡೀ ನೆನೆಯಲು\nನನಗೆ ಕುತೂಹಲ.. ಹೋ ಓ ಓ ತುಂಬಾ ಕುತೂಹಲ ||\nಹನೀ ಹನಿಯ ಸವಿ ದುನಿಯಾ ನಾ ವಿವರಿಸಿ ಹೇಳಲಾ ||\n\nಅದೇ ಅದೆ ಮೊಡವೀಗ ವಿನೂತನ ರೂಪ ತಾಳಿ ನಿನ್ನಾ ಸೋಕಿದೆ\nಪದೇ ಪದೇ ಗಂಧ ಗಾಳಿ ವಿಚಾರಿಸಿ ನೂರು ಬಾರಿ ಸುಮ್ಮನಾಗಿದೆ\nಕನಸಿನಾ ಕುಡಿಯನು ಮನಸಲೇ ಬಿಡಿಸಲು ತುಂಬಾ ಕುತೂಹಲ ||",
        "meanings": {
            "ಮಳೆ": "Rain",
            "ಜೊತೆ": "Together",
            "ನೆನೆಯಲು": "To get drenched",
            "ಕುತೂಹಲ": "Curiosity/Longing",
            "ಹನಿ": "Drop",
            "ಮೊಡ": "Cloud",
            "ಸೋಕಿದೆ": "Touched",
            "ಗಂಧ ಗಾಳಿ": "Scented wind"
        }
    },
    53: {
        "title": "Tanu Karagadavaralli",
        "poet": "Akka Mahadevi",
        "content": "ತನು ಕರಗದವರಲ್ಲಿ ಮಜ್ಜನವನೊಲ್ಲೆಯಯ್ಯಾ ನೀನು |\nಮನ ಕರಗದವರಲ್ಲಿ ಪುಷ್ಪವನೊಲ್ಲೆಯಯ್ಯಾ ನೀನು |\nಅರಿವು ಕಣ್ದೆರೆಯದವರಲ್ಲಿ ಆರತಿಯನೊಲ್ಲೆಯಯ್ಯಾ ನೀನು |\nಭಾವಶುದ್ಭವಿಲ್ಲದವರಲ್ಲಿ ಧೂಪವನೊಲ್ಲೆಯಯ್ಯಾ ನೀನು |\nಹೃದಯಕಮಲ ಅರಳದವರಲ್ಲಿ ಇರಲೊಲ್ಲೆಯಯ್ಯಾ ನೀನು |",
        "meanings": {
            "ತನು": "Body",
            "ಕರಗದ": "Not melting/surrendering",
            "ಮಜ್ಜನ": "Ritual bath",
            "ಮನ": "Mind",
            "ಪುಷ್ಪ": "Flower",
            "ಅರಿವು": "Knowledge/Awareness",
            "ಆರತಿ": "Lamp ritual",
            "ಭಾವಶುದ್ಧಿ": "Purity of intent",
            "ಅರಳದ": "Unblossomed"
        }
    },
    54: {
        "title": "Ullavaru Shivalaya Maduvaru",
        "poet": "Basavanna",
        "content": "ಉಳ್ಳವರು ಶಿವಾಲಯವ ಮಾಡುವರು\nನಾನೇನು ಮಾಡಲಿ ಬಡವನಯ್ಯಾ?\nಎನ್ನ ಕಾಲೇ ಕಂಬ, ದೇಹವೇ ದೇಗುಲ,\nಶಿರವೇ ಹೊನ್ನ ಕಳಶವಯ್ಯಾ.\nಕೂಡಲಸಂಗಮದೇವ ಕೇಳಯ್ಯಾ,\nಸ್ಥಾವರಕ್ಕಳಿವುಂಟು, ಜಂಗಮಕ್ಕಳಿವಿಲ್ಲ.",
        "meanings": {
            "ಉಳ್ಳವರು": "Rich/Wealthy",
            "ಬಡವ": "Poor man",
            "ಕಾಲೇ ಕಂಬ": "Legs are pillars",
            "ದೇಹ": "Body",
            "ದೇಗುಲ": "Shrine",
            "ಶಿರ": "Head",
            "ಕಳಶ": "Pinnacle/Golden dome",
            "ಸ್ಥಾವರ": "Static (Buildings)",
            "ಜಂಗಮ": "Moving/Living soul"
        }
    },
    55: {
        "title": "Sakkare Chakori",
        "poet": "Kiran Kaverappa",
        "content": "ಮೂಟೆ ಮೂಟೆ ಸಕ್ಕರೆ ಚೋರಿ\nಹಾರಿ ಪರಾರಿ ಸಕ್ಕರೆ ಚಕೋರಿ\nನಕ್ಕಳು ಪೋರಿ ಅಕ್ಕರೆ ತೋರಿ\nಹಾರಿ ಪರಾರಿ ಸಕ್ಕರೆ ಚಕೋರಿ\n\nಮುದ್ದು ಚಿಗರಿ ನಿಲ್ಲದೆ ಬೆದರಿ\nಹಾರಿದ ಲೆಗರಿ ಮெல்லನೆ ಜಾರಿ\nಅವಳ ಹಿಂದೆ ಓಡಿದೆ ನಗರಿ\nಹೀಯಲಾರದೆ ಸೋತಿದೆ ಭಾರಿ",
        "meanings": {
            "ಸಕ್ಕರೆ": "Sugar",
            "ಚಕೋರಿ": "Moon-bird (Beloved)",
            "ಚೋರಿ": "Theft",
            "ಪರಾರಿ": "Escaped",
            "ಪೋರಿ": "Girl",
            "ಅಕ್ಕರೆ": "Affection",
            "ಚಿಗರಿ": "Deer",
            "ಬೆದರಿ": "Scared"
        }
    },
    56: {
        "title": "Deepavu Ninnade",
        "poet": "K.S. Narasimhaswamy",
        "content": "ದೀಪವು ನಿನ್ನದೆ, ಗಾಳಿಯು ನಿನ್ನದೆ, ಆರದಿರಲಿ ಬೆಳಕು |\nಕಡಲು ನಿನ್ನದೆ, ಹಡಗು ನಿನ್ನದೆ, ಮುಳುಗದಿರಲಿ ಬದುಕು ||\n\nಬೆಟ್ಟವು ನಿನ್ನದೆ, ಬಯಲು ನಿನ್ನದೆ, ಹಬ್ಬಿ ನಗಲಿ ಪ್ರೀತಿ |\nನೆರಳು ಬಿಸಿಲು ಎಲ್ಲವೂ ನಿನ್ನವೆ, ಇರಲಿ ಏಕರೀತಿ ||\n\nಆಗೊಂದು ಸಿಡಿಲು, ಈಗೊಂದು ಮುಗಿಲು, ನಿನಗೆ ಅಲಂಕಾರ |\nಅಲ್ಲೊಂದು ಹಕ್ಕಿ, ಇಲ್ಲೊಂದು ಮುಗುಳು, ನಿನಗೆ ನಮಸ್ಕಾರ ||",
        "meanings": {
            "ದೀಪ": "Lamp",
            "ಗಾಳಿ": "Wind",
            "ಬೆಳಕು": "Light",
            "ಕಡಲು": "Sea",
            "ಹಡಗು": "Ship",
            "ಬದುಕು": "Life",
            "ಬಯಲು": "Field",
            "ಪ್ರೀತಿ": "Love",
            "ಸಿಡಿಲು": "Thunder",
            "ನಮಸ್ಕಾರ": "Salutation"
        }
    }
}

for item in data:
    id_val = item.get('id')
    if id_val in updates:
        item.update(updates[id_val])

with open(file_path, 'w', encoding='utf-8') as f:
    json.dump(data, f, indent=2, ensure_ascii=False)

print("Bulk update of poems complete.")
