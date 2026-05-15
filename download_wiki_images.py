import urllib.request
import json
import os

authors = {
    "kuvempu": "Kuvempu",
    "dvgundappa": "D. V. Gundappa",
    "drbendre": "D. R. Bendre",
    "basavanna": "Basava",
    "akkamahadevi": "Akka Mahadevi",
    "purandaradasa": "Purandara Dasa",
    "gsshivarudrappa": "G. S. Shivarudrappa",
    "ksnissarahmed": "K. S. Nissar Ahmed",
    "gopalakrishnaadiga": "Gopalakrishna Adiga",
    "jayantkaikini": "Jayanth Kaikini",
    "bksumitra": "B. K. Sumitra",
    "shishunalasharif": "Shishunala Sharif",
    "nslakshminarayanabhatta": "N. S. Lakshminarayana Bhatta",
    "sathyanandapathrota": "Sathyananda Pathrota",
    "channabasavanna": "Channabasavanna",
    "vanand": "V. Anand",
    "chiudayashankar": "Chi. Udayashankar",
    "folk": "Yakshagana"
}

out_dir = "/home/zygisk/AndroidStudioProjects/Kavya_kanaja/app/src/main/res/drawable"
os.makedirs(out_dir, exist_ok=True)
headers = {'User-Agent': 'KavyaKanajaApp/1.0 (contact@example.com) Python-urllib/3.8'}

for key, title in authors.items():
    api_url = f"https://en.wikipedia.org/w/api.php?action=query&titles={urllib.parse.quote(title)}&prop=pageimages&format=json&pithumbsize=400"
    try:
        req = urllib.request.Request(api_url, headers=headers)
        with urllib.request.urlopen(req) as resp:
            data = json.loads(resp.read())
            pages = data['query']['pages']
            page = list(pages.values())[0]
            if 'thumbnail' in page:
                img_url = page['thumbnail']['source']
                img_req = urllib.request.Request(img_url, headers=headers)
                with urllib.request.urlopen(img_req) as img_resp, open(os.path.join(out_dir, f"{key}.jpg"), 'wb') as f:
                    f.write(img_resp.read())
                print(f"Success: {key}")
            else:
                print(f"No image found for {title}")
    except Exception as e:
        print(f"Error {title}: {e}")
