import urllib.request
import os

authors = {
    "kuvempu": "Kuvempu",
    "dvgundappa": "D V Gundappa",
    "drbendre": "D R Bendre",
    "basavanna": "Basavanna",
    "akkamahadevi": "Akka Mahadevi",
    "purandaradasa": "Purandara Dasa",
    "gsshivarudrappa": "G S Shivarudrappa",
    "ksnissarahmed": "K S Nissar Ahmed",
    "gopalakrishnaadiga": "Gopalakrishna Adiga",
    "jayantkaikini": "Jayanth Kaikini",
    "bksumitra": "B K Sumitra",
    "shishunalasharif": "Shishunala Sharif",
    "nslakshminarayanabhatta": "N S Lakshminarayana",
    "sathyanandapathrota": "Sathyananda Pathrota",
    "channabasavanna": "Channabasavanna",
    "vanand": "V Anand",
    "chiudayashankar": "Chi Udayashankar",
    "folk": "Folk"
}

out_dir = "/home/zygisk/AndroidStudioProjects/Kavya_kanaja/app/src/main/res/drawable"
os.makedirs(out_dir, exist_ok=True)
headers = {'User-Agent': 'Mozilla/5.0'}

for key, name in authors.items():
    safe_name = urllib.parse.quote(name)
    url = f"https://ui-avatars.com/api/?name={safe_name}&background=random&color=fff&size=256&font-size=0.4"
    try:
        req = urllib.request.Request(url, headers=headers)
        with urllib.request.urlopen(req) as resp, open(os.path.join(out_dir, f"author_{key}.png"), 'wb') as f:
            f.write(resp.read())
        print(f"Success: {key}")
    except Exception as e:
        print(f"Error {key}: {e}")
