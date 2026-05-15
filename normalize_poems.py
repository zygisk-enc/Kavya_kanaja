import json

file_path = '/home/zygisk/AndroidStudioProjects/Kavya_kanaja/app/src/main/assets/poems.json'

with open(file_path, 'r', encoding='utf-8') as f:
    data = json.load(f)

norm_map = {
    "D. V. Gundappa": "D.V. Gundappa",
    "Da. Ra. Bendre": "D.R. Bendre",
    "G. S. Shivarudrappa": "G.S. Shivarudrappa",
    "G.S.S": "G.S. Shivarudrappa",
    "K. S. Nissar Ahmed": "K.S. Nissar Ahmed",
    "Nissar Ahmed": "K.S. Nissar Ahmed",
    "Unknown (Folk)": "Folk"
}

for item in data:
    poet = item.get('poet')
    if poet in norm_map:
        item['poet'] = norm_map[poet]

with open(file_path, 'w', encoding='utf-8') as f:
    json.dump(data, f, indent=2, ensure_ascii=False)

print("Normalization complete.")
