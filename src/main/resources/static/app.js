const loginBtn = document.getElementById("login-btn");
const registerBtn = document.getElementById("register-btn");
const logoutBtn = document.getElementById("logout-btn");
const logoutBtnUser = document.getElementById("logout-btn-user");

let accessToken = "";
let userRole = "";

// Login
loginBtn.addEventListener("click", async () => {
    const username = document.getElementById("login-username").value;
    const password = document.getElementById("login-password").value;

    const res = await fetch("/auth/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ username, password })
    });

    if(res.ok) {
        const data = await res.json();
        accessToken = data.accessToken;
        // Basit rol kontrolü: admin veya user
        userRole = username.toLowerCase().includes("admin") ? "ADMIN" : "USER";
        showPanel();
    } else {
        alert("Login başarısız!");
    }
});

// Register
registerBtn.addEventListener("click", async () => {
    const username = document.getElementById("register-username").value;
    const password = document.getElementById("register-password").value;

    const res = await fetch("/auth/register", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ username, password })
    });

    if(res.ok) {
        alert("Kayıt başarılı! Login yapabilirsiniz.");
    } else {
        alert("Kayıt başarısız!");
    }
});

// Logout
logoutBtn?.addEventListener("click", () => window.location.reload());
logoutBtnUser?.addEventListener("click", () => window.location.reload());

// Show panel according to role
function showPanel() {
    document.getElementById("auth-container").classList.add("hidden");
    if(userRole === "ADMIN") {
        document.getElementById("admin-panel").classList.remove("hidden");
    } else {
        document.getElementById("user-panel").classList.remove("hidden");
        loadKategoriler();
    }
}

// Admin: Kategori ekle
document.getElementById("kategori-ekle-btn")?.addEventListener("click", async () => {
    const ad = document.getElementById("kategori-ad").value;
    await fetch("/api/kategori/ekle", {
        method: "POST",
        headers: { "Content-Type": "application/json", "Authorization": "Bearer " + accessToken },
        body: JSON.stringify({ ad })
    });
    alert("Kategori eklendi!");
});

// Admin: Sorun ekle
document.getElementById("sorun-ekle-btn")?.addEventListener("click", async () => {
    const baslik = document.getElementById("sorun-baslik").value;
    const sorun = document.getElementById("sorun-icerik").value;
    const cozum = document.getElementById("sorun-cozum").value;
    const kategoriId = parseInt(document.getElementById("sorun-kategori-id").value);

    await fetch("/api/sorun", {
        method: "POST",
        headers: { "Content-Type": "application/json", "Authorization": "Bearer " + accessToken },
        body: JSON.stringify({ baslik, sorun, cozum, kategoriId })
    });
    alert("Sorun eklendi!");
});

// User: Kategoriler ve sorunlar
async function loadKategoriler() {
    const res = await fetch("/api/kategori/goster?page=0&size=10");
    const data = await res.json();
    const ul = document.getElementById("kategori-list");
    ul.innerHTML = "";
    data.content.forEach(k => {
        const li = document.createElement("li");
        li.textContent = `${k.id}: ${k.ad}`;
        ul.appendChild(li);
        loadSorunlar(k.id);
    });
}

async function loadSorunlar(kategoriId) {
    const res = await fetch(`/api/sorun/kategori/${kategoriId}?page=0&size=10`);
    const data = await res.json();
    const ul = document.getElementById("sorun-list");
    data.content.forEach(s => {
        const li = document.createElement("li");
        li.textContent = `${s.baslik} - ${s.sorun} - ${s.cozum}`;
        ul.appendChild(li);
    });
}