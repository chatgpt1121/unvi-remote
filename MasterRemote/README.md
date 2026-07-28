# Master Remote — Setup Guide (Hindi/English)

## ⭐ APK GitHub se banane ka tarika (Android Studio install nahi karna padega)

Is folder mein ek "robot" (`.github/workflows/build-apk.yml`) already bana hua
hai jo GitHub ke free servers pe khud APK build kar deta hai.

**Steps:**
1. GitHub par jaake (agar account nahi hai to free banao: github.com) ek
   **naya empty repository** banao — naam kuch bhi do, jaise `master-remote`
   (Public ya Private, dono chalega)
2. Is poore `MasterRemote` folder ka saara content us naye repo mein
   **upload karo**:
   - Sabse aasan: GitHub repo page khol ke "Add file" → "Upload files" →
     is folder ke andar ki saari files/folders drag-drop kar do → "Commit"
   - Ya agar `git` pata hai to:
     ```
     cd MasterRemote
     git init
     git remote add origin <tumhara-repo-ka-link>
     git add .
     git commit -m "first upload"
     git push -u origin main
     ```
3. Upload hote hi (ya commit hote hi) GitHub apne aap build shuru kar dega —
   repo ke upar **"Actions"** tab mein jaake dekho, ek "Build APK" job chalta
   hua dikhega (roughly 3-5 minute lagta hai)
4. Job green tick (✅) ho jaane ke baad, usi job ke andar niche
   **"Artifacts"** section mein `MasterRemote-debug-apk` naam ki file milegi
   — usse download karo, andar `app-debug.apk` hoga
5. Wo `app-debug.apk` apne Xiaomi phone mein bhejo (WhatsApp/email/USB — jo
   bhi aasan lage) aur install kar lo
   (Settings mein pehli baar "Install from unknown sources" allow karna
   padega — phone khud puch lega)

Agar job laal cross (❌) dikhaye (fail ho jaye), to "Actions" tab mein us
job pe click karke jo bhi error line laal dikh rahi ho, wo mujhe copy-paste
karke bhej dena — main turant fix kar dunga.

---

## Alternative: Apne computer pe Android Studio se banao


## Ye app kya karta hai
- Ek button dabate hi app apne database mein saare TV brands ke POWER codes
  ek-ek karke bahut fast bhejta hai (Samsung, LG, Sony, TCL, MI, Panasonic,
  VU, Onida, Videocon, Haier, Thomson).
- Jo bhi TV us range mein hai aur jiska code match ho jaata hai, wo band ho
  jaata hai — bina brand manually select kiye.

## Isse APK kaise banaye (5 minute ka kaam)
1. Free software "Android Studio" download karo: https://developer.android.com/studio
2. Android Studio kholo → "Open" → is poore "MasterRemote" folder ko select karo
3. Studio khud-ba-khud sab dependencies download kar lega (2-3 min lagega, internet chahiye)
4. Upar menu mein "Build" → "Build Bundle(s)/APK(s)" → "Build APK(s)"
5. APK ban jaayega yaha: app/build/outputs/apk/debug/app-debug.apk
6. Ye file apne Xiaomi phone mein copy karo aur install kar lo
   (Settings mein "Install from unknown sources" allow karna padega)

## Zaroori limitation — IMPORTANT
1. Ye app SIRF un phones pe kaam karega jinme built-in IR blaster ho
   (kai Xiaomi/Redmi/POCO models mein hota hai — phone ke top edge pe
   ek chhota dark dot dikhta hai).
2. IR ek "line of sight" technology hai — matlab TV aur phone ke beech
   koi deewar/darwaza nahi hona chahiye, aur range roughly 5-8 meter tak
   hoti hai. Isliye ye ek TV/room ke liye kaam karega, lekin agar tumhare
   100+ TV alag-alag rooms/floors mein hain, to har room mein khada hoke
   ye button dabana padega — phone ka IR ek room ke bahar TV control
   nahi kar sakta.
3. TV_CODES list mein diye gaye address/command values sabse commonly
   published codes hain (LIRC/Flipper-IRDB jaise open databases se), par
   HAR model ke liye 100% guarantee nahi hai. Jo TV band na ho, uska exact
   code neeche diye links se dhundh ke IrCodeDatabase.kt mein add kar sakte ho.

## AC ka status (update: ab real codes hain)
`AcCodeDatabase.kt` mein ab **10 real LG models** aur **8 real Samsung
models** ke "off" codes hain — ye maine khud nahi banaye, balki ek
open-source project **SmartIR** (github.com/smartHomeHub/SmartIR) se liye
hain, jaha real users ne apne AC remotes se Broadlink hub ke through
signal capture/learn karke daale hain. Maine unhe decode karke raw
microsecond pulse format mein convert kiya hai jo Android ka
ConsumerIrManager samajhta hai.

**Honestly samjho iska matlab kya hai:**
- LG ke zyada tar models ka "off" ek chhota toggle signal hai (TV jaisa) —
  isliye brute-force try karna in par kaam kar sakta hai
- Samsung ke zyada tar models "full state" bhejte hain (poora
  mode+temp+fan ek saath) — inka signal lamba hai (~350 values), fir bhi
  "off" ke liye specific captured code hai to try to hoga
- Ye 18 codes kuch specific models ke hain — tumhare exact AC model se
  match ho sakta hai ya nahi bhi ho sakta. Match nahi hua to koi TV/AC
  "silent" reh jayega, error nahi aayega, bas wo band nahi hoga
- **Voltas, Daikin, Hitachi, Blue Star, Carrier abhi bhi missing hain**
  (SmartIR ke us folder mein maine sirf LG/Samsung nikale, jaisa tumne
  bola tha). Chahiye to inhe bhi isi tarah nikaal ke add kar sakta hun.

**Agar tumhara exact AC band nahi hota:**
Sabse pakka tarika ye hai — ek WiFi IR hub (Broadlink RM4, ₹1500-2000)
lo, uske app se apne AC remote ka signal ek baar "learn" karo, aur wo
naya captured code `AcCodeDatabase.kt` mein add kar denge.

## Code database expand karne ke liye (free, open-source sources)
- LIRC remote database: https://lirc-remotes.sourceforge.io/
- Flipper-IRDB (GitHub — search "Flipper-IRDB")
- SmartIR project (GitHub — search "smartir codes")

Naya code add karna ho to bas IrCodeDatabase.kt file mein ek line add karo:
    IrCode("Brand Name", "NEC", 0xADDRESS, 0xCOMMAND)
