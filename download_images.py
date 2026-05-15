import urllib.request
import os

images = {
    "kuvempu": "https://upload.wikimedia.org/wikipedia/commons/c/c6/Kuvempu.jpg",
    "dvgundappa": "https://upload.wikimedia.org/wikipedia/commons/d/d5/DV_Gundappa_1988_stamp_of_India.jpg",
    "drbendre": "https://upload.wikimedia.org/wikipedia/commons/b/b3/%E0%B2%A6._%E0%B2%B0%E0%B2%BE._%E0%B2%AC%E0%B3%87%E0%B2%82%E0%B2%A6%E0%B3%8D%E0%B2%B0%E0%B3%86_-_2.jpg",
    "basavanna": "https://upload.wikimedia.org/wikipedia/commons/thumb/a/a5/Basavanna.JPG/800px-Basavanna.JPG",
    "akkamahadevi": "https://upload.wikimedia.org/wikipedia/commons/a/a1/Akka_Mahadevi.png",
    "purandaradasa": "https://upload.wikimedia.org/wikipedia/commons/b/b3/Purandara_Dasa_Pitamaha_of_Carnatic_music_Indian_classical.jpg",
    "gsshivarudrappa": "https://www.oneindia.com/img/2013/12/23-gs-shivarudrappa.jpg",
    "ksnissarahmed": "https://static.oneindia.com/img/2020/05/nissar-ahmed-1588501258.jpg",
    "gopalakrishnaadiga": "https://upload.wikimedia.org/wikipedia/commons/a/a1/G_Adiga.jpg",
    "jayantkaikini": "https://upload.wikimedia.org/wikipedia/commons/a/a1/Jayanth_Kaikini.jpg",
    "bksumitra": "https://yt3.googleusercontent.com/ytc/AIdro_nFqW2m_7X9Vv9p5o7o0W8v5E5k1W7p7N5v6G8v=s900-c-k-c0x00ffffff-no-rj",
    "shishunalasharif": "https://upload.wikimedia.org/wikipedia/commons/thumb/a/a1/Shishunala_Sharif.jpg/440px-Shishunala_Sharif.jpg",
    "nslakshminarayanabhatta": "https://upload.wikimedia.org/wikipedia/commons/a/a1/Dr._N_S_Lakshmi_Narayana_Bhatta.JPG",
    "sathyanandapathrota": "https://upload.wikimedia.org/wikipedia/commons/a/a0/%E0%B2%B8%E0%B2%A4%E0%B3%8D%E0%B2%AF%E0%B2%BE%E0%B2%A8%E0%B2%82%E0%B2%A6_%E0%B2%AA%E0%B2%BE%E0%B2%A4%E0%B3%8D%E0%B2%B0%E0%B3%8B%E0%B2%9F.jpg",
    "channabasavanna": "https://www.lingayatreligion.com/Images/Sharanaru/Channabasavanna.jpg",
    "vanand": "https://chiloka.com/images/people/v-anand.jpg",
    "folk": "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d3/Yakshagana.jpg/800px-Yakshagana.jpg"
}

out_dir = "/home/zygisk/AndroidStudioProjects/Kavya_kanaja/app/src/main/res/drawable"
os.makedirs(out_dir, exist_ok=True)

headers = {'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64)'}

for name, url in images.items():
    ext = url.split('.')[-1].split('?')[0].lower()
    if ext not in ['jpg', 'png', 'jpeg']:
        ext = 'jpg'
    file_path = os.path.join(out_dir, f"{name}.{ext}")
    try:
        req = urllib.request.Request(url, headers=headers)
        with urllib.request.urlopen(req) as response, open(file_path, 'wb') as out_file:
            out_file.write(response.read())
        print(f"Downloaded {name}")
    except Exception as e:
        print(f"Failed to download {name} from {url}: {e}")

