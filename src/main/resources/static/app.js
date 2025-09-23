const apiBase = "/api";

// ---------- ADMIN PANEL ELEMENTLERİ ----------
const kategoriForm = document.getElementById("kategoriForm");
const kategoriList = document.getElementById("kategoriList");
const kategoriAd = document.getElementById("kategoriAd");
const kategoriId = document.getElementById("kategoriId");
const kategoriCancel = document.getElementById("kategoriCancel");

const sorunKategori = document.getElementById("sorunKategori");
const sorunForm = document.getElementById("sorunForm");
const sorunList = document.getElementById("sorunList");
const sorunId = document.getElementById("sorunId");
const sorunBaslik = document.getElementById("sorunBaslik");
const sorunSorun = document.getElementById("sorunSorun");
const sorunCozum = document.getElementById("sorunCozum");
const sorunCancel = document.getElementById("sorunCancel");

const userViewBtn = document.getElementById("userViewBtn");

// ---------- USER PANEL ELEMENTLERİ ----------
const adminPanel = document.getElementById("adminPanel");
const userPanel = document.getElementById("userPanel");
const userKategoriList = document.getElementById("userKategoriList");
const userSorunList = document.getElementById("userSorunList");
const backToCategories = document.getElementById("backToCategories");
const backToAdmin = document.getElementById("backToAdmin");

// GLOBAL
let kategoriler = [];
let selectedKategoriId = "";
let currentSorunState = {}; // { sorId, state: 0=başlık,1=sorun,2=cozum }

// ---------- ADMIN PANEL FONKSİYONLARI ----------
async function fetchKategoriler() {
    try {
        const res = await fetch(`${apiBase}/kategori?page=0&size=100`);
        const data = await res.json();
        kategoriler = data.content; // <-- Page objesinden array alındı

        
        kategoriList.innerHTML = "";
        sorunKategori.innerHTML = '<option value="">Kategori Seç</option>';

        kategoriler.forEach(k => {
            
            const li = document.createElement("li");
            li.className = "list-group-item d-flex justify-content-between align-items-center";
            li.textContent = k.ad;

            const btnGroup = document.createElement("div");
            const editBtn = document.createElement("button");
            editBtn.className = "btn btn-sm btn-warning me-2";
            editBtn.textContent = "Düzenle";
            editBtn.onclick = () => {
                kategoriId.value = k.id;
                kategoriAd.value = k.ad;
            };

            const delBtn = document.createElement("button");
            delBtn.className = "btn btn-sm btn-danger";
            delBtn.textContent = "Sil";
            delBtn.onclick = async () => {
                await fetch(`${apiBase}/kategori/toplu`, {
                    method: "DELETE",
                    headers: {"Content-Type": "application/json"},
                    body: JSON.stringify([k.id])
                });
                fetchKategoriler();
                fetchSorunlar();
            };

            btnGroup.appendChild(editBtn);
            btnGroup.appendChild(delBtn);
            li.appendChild(btnGroup);
            kategoriList.appendChild(li);

            // Dropdown
            const opt = document.createElement("option");
            opt.value = k.id;
            opt.textContent = k.ad;
            sorunKategori.appendChild(opt);
        });
    } catch (err) {
        console.error("Kategori fetch hatası:", err);
    }
}


kategoriForm.onsubmit = async (e) => {
    e.preventDefault();
    const dto = { ad: kategoriAd.value };
    try {
        if (kategoriId.value) {
            await fetch(`${apiBase}/kategori/${kategoriId.value}`, {
                method: "PUT",
                headers: {"Content-Type": "application/json"},
                body: JSON.stringify(dto)
            });
        } else {
            await fetch(`${apiBase}/kategori`, {
                method: "POST",
                headers: {"Content-Type": "application/json"},
                body: JSON.stringify(dto)
            });
        }
        kategoriId.value = "";
        kategoriAd.value = "";
        fetchKategoriler();
    } catch (err) {
        console.error("Kategori kaydet hatası:", err);
    }
};

kategoriCancel.onclick = () => {
    kategoriId.value = "";
    kategoriAd.value = "";
};


sorunKategori.onchange = () => {
    selectedKategoriId = sorunKategori.value;
    fetchSorunlar();
};

async function fetchSorunlar() {
    if (!selectedKategoriId) {
        sorunList.innerHTML = "";
        return;
    }
    try {
        const res = await fetch(`${apiBase}/sorun/kategori/${selectedKategoriId}?page=0&size=100`);
        const data = await res.json();
        const sorunlar = data.content; // <-- Page objesinden array alındı

        sorunList.innerHTML = "";
        sorunlar.forEach(s => {
            const li = document.createElement("li");
            li.className = "list-group-item d-flex justify-content-between align-items-center";
            li.innerHTML = `<b>${s.baslik}</b> - ${s.sorun} - ${s.cozum}`;

            const btnGroup = document.createElement("div");
            const editBtn = document.createElement("button");
            editBtn.className = "btn btn-sm btn-warning me-2";
            editBtn.textContent = "Düzenle";
            editBtn.onclick = () => {
                sorunId.value = s.id;
                sorunBaslik.value = s.baslik;
                sorunSorun.value = s.sorun;
                sorunCozum.value = s.cozum;
                sorunKategori.value = s.kategoriId;
                selectedKategoriId = s.kategoriId;
            };

            const delBtn = document.createElement("button");
            delBtn.className = "btn btn-sm btn-danger";
            delBtn.textContent = "Sil";
            delBtn.onclick = async () => {
                await fetch(`${apiBase}/sorun/toplu`, {
                    method: "DELETE",
                    headers: {"Content-Type": "application/json"},
                    body: JSON.stringify([s.id])
                });
                fetchSorunlar();
            };

            btnGroup.appendChild(editBtn);
            btnGroup.appendChild(delBtn);
            li.appendChild(btnGroup);
            sorunList.appendChild(li);
        });
    } catch (err) {
        console.error("Sorun fetch hatası:", err);
    }
}

sorunForm.onsubmit = async (e) => {
    e.preventDefault();
    if (!sorunKategori.value) return alert("Kategori seçiniz!");
    const dto = {
        baslik: sorunBaslik.value,
        sorun: sorunSorun.value,
        cozum: sorunCozum.value,
        kategoriId: Number(sorunKategori.value)
    };
    try {
        if (sorunId.value) {
            await fetch(`${apiBase}/sorun/${sorunId.value}`, {
                method: "PUT",
                headers: {"Content-Type": "application/json"},
                body: JSON.stringify(dto)
            });
        } else {
            await fetch(`${apiBase}/sorun`, {
                method: "POST",
                headers: {"Content-Type": "application/json"},
                body: JSON.stringify(dto)
            });
        }
        sorunId.value = "";
        sorunBaslik.value = "";
        sorunSorun.value = "";
        sorunCozum.value = "";
        fetchSorunlar();
    } catch (err) {
        console.error("Sorun kaydet hatası:", err);
    }
};

sorunCancel.onclick = () => {
    sorunId.value = "";
    sorunBaslik.value = "";
    sorunSorun.value = "";
    sorunCozum.value = "";
    sorunKategori.value = "";
};


userViewBtn.onclick = () => {
    adminPanel.classList.add("d-none");
    userPanel.classList.remove("d-none");
    renderUserKategoriler();
};

backToCategories.onclick = () => {
    userSorunList.innerHTML = "";
    userKategoriList.classList.remove("d-none");
    backToCategories.classList.add("d-none");
};

backToAdmin.onclick = () => {
    userPanel.classList.add("d-none");
    adminPanel.classList.remove("d-none");
};

// User görünümü: kategorileri kare kutucukla göster
function renderUserKategoriler() {
    userKategoriList.innerHTML = "";
    kategoriler.forEach(k => {
        const div = document.createElement("div");
        div.textContent = k.ad;
        div.onclick = () => renderUserSorunlar(k.id);
        userKategoriList.appendChild(div);
    });
}


async function renderUserSorunlar(kategoriId) {
    userKategoriList.classList.add("d-none");
    backToCategories.classList.remove("d-none");
    try {
        const res = await fetch(`${apiBase}/sorun/kategori/${kategoriId}?page=0&size=100`);
        const data = await res.json();
        const sorunlar = data.content; // <-- Page objesinden array alındı

        userSorunList.innerHTML = "";
        sorunlar.forEach(s => {
            const div = document.createElement("div");
            div.textContent = s.baslik;
            div.onclick = () => {
                if (!currentSorunState[s.id]) currentSorunState[s.id] = 0;
                currentSorunState[s.id]++;
                if (currentSorunState[s.id] === 1) {
                    div.textContent = s.sorun;
                } else if (currentSorunState[s.id] === 2) {
                    div.textContent = s.cozum;
                } else {
                    div.textContent = s.baslik;
                    currentSorunState[s.id] = 0;
                }
            };
            userSorunList.appendChild(div);
        });
    } catch (err) {
        console.error("User sorun fetch hatası:", err);
    }
}

// ---------- INIT ----------
window.addEventListener("DOMContentLoaded", () => {
    fetchKategoriler();
});
