const loginBtn = document.getElementById("login-btn");
const registerBtn = document.getElementById("register-btn");
const logoutBtn = document.getElementById("logout-btn");
const logoutBtnUser = document.getElementById("logout-btn-user");

let accessToken = localStorage.getItem("accessToken") || "";
let userRole = localStorage.getItem("userRole") || "";

// Login
loginBtn?.addEventListener("click", async () => {
    const username = document.getElementById("login-username").value;
    const password = document.getElementById("login-password").value;

    try {
        const res = await fetch("/auth/login", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ username, password })
        });

        if(res.ok) {
            const data = await res.json();
            accessToken = data.accessToken;
            localStorage.setItem("accessToken", accessToken);

			userRole = data.role || (username.toLowerCase().includes("admin") ? "ADMIN" : "USER");
            localStorage.setItem("userRole", userRole);

            showPanel();
        } else {
            const err = await res.json();
            alert("Login başarısız: " + (err.message || res.status));
        }
    } catch(e) {
        alert("Login sırasında bir hata oluştu: " + e.message);
    }
});

// Register
registerBtn?.addEventListener("click", async () => {
    const username = document.getElementById("register-username").value;
    const password = document.getElementById("register-password").value;

    try {
        const res = await fetch("/auth/register", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ username, password })
        });

        if(res.ok) {
            alert("Kayıt başarılı! Login yapabilirsiniz.");
        } else {
            const err = await res.json();
            alert("Kayıt başarısız: " + (err.message || res.status));
        }
    } catch(e) {
        alert("Register sırasında bir hata oluştu: " + e.message);
    }
});

// Logout
logoutBtn?.addEventListener("click", () => {
    localStorage.removeItem("accessToken");
    localStorage.removeItem("userRole");
    window.location.reload();
});
logoutBtnUser?.addEventListener("click", () => {
    localStorage.removeItem("accessToken");
    localStorage.removeItem("userRole");
    window.location.reload();
});

// Show panel according to role
function showPanel() {
    document.getElementById("auth-container")?.classList.add("hidden");
    if(userRole === "ADMIN") {
        document.getElementById("admin-panel")?.classList.remove("hidden");
    } else {
        document.getElementById("user-panel")?.classList.remove("hidden");
        loadKategoriler();
    }
}

// Admin: Kategori ekle
document.getElementById("kategori-ekle-btn")?.addEventListener("click", async () => {
    const ad = document.getElementById("kategori-ad").value;
    try {
        const res = await fetch("/api/kategori/ekle", {
            method: "POST",
            headers: { 
                "Content-Type": "application/json", 
                "Authorization": "Bearer " + localStorage.getItem("accessToken") 
            },
            body: JSON.stringify({ ad })
        });

        if(res.ok) {
            alert("Kategori eklendi!");
            loadKategoriler();
        } else {
            const err = await res.json();
            alert("Kategori ekleme başarısız: " + (err.message || res.status));
        }
    } catch(e) {
        alert("Kategori ekleme sırasında hata: " + e.message);
    }
});

// Admin: Sorun ekle
document.getElementById("sorun-ekle-btn")?.addEventListener("click", async () => {
    const baslik = document.getElementById("sorun-baslik").value;
    const sorun = document.getElementById("sorun-icerik").value;
    const cozum = document.getElementById("sorun-cozum").value;
    const kategoriId = parseInt(document.getElementById("sorun-kategori-id").value);

    try {
        const res = await fetch("/api/sorun", {
            method: "POST",
            headers: { 
                "Content-Type": "application/json", 
                "Authorization": "Bearer " + localStorage.getItem("accessToken") 
            },
            body: JSON.stringify({ baslik, sorun, cozum, kategoriId })
        });

        if(res.ok) {
            alert("Sorun eklendi!");
            loadSorunlar(kategoriId);
        } else {
            const err = await res.json();
            alert("Sorun ekleme başarısız: " + (err.message || res.status));
        }
    } catch(e) {
        alert("Sorun ekleme sırasında hata: " + e.message);
    }
});

// User: Kategoriler ve sorunlar
async function loadKategoriler() {
    try {
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
    } catch(e) {
        console.error("Kategori yüklenirken hata: ", e);
    }
}

async function loadSorunlar(kategoriId) {
    try {
        const res = await fetch(`/api/sorun/kategori/${kategoriId}?page=0&size=10`);
        const data = await res.json();
        const ul = document.getElementById("sorun-list");
        ul.innerHTML = "";
        data.content.forEach(s => {
            const li = document.createElement("li");
            li.textContent = `${s.baslik} - ${s.sorun} - ${s.cozum}`;
            ul.appendChild(li);
        });
    } catch(e) {
        console.error("Sorunlar yüklenirken hata: ", e);
    }
}