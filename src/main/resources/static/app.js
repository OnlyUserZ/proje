const apiBase = "/api";

const AUTH_KEY = "bg_auth_token";

function setAuthToken(token) {
  if (token) localStorage.setItem(AUTH_KEY, token);
  else localStorage.removeItem(AUTH_KEY);
}

function getAuthToken() {
  return localStorage.getItem(AUTH_KEY);
}

function authHeaders() {
  const token = getAuthToken();
  return token ? { Authorization: token } : {};
}

function showAlert(msg, type = "info", timeout = 3500) {
  
  console.log(`[${type.toUpperCase()}] ${msg}`);
  
}


const kategoriForm = document.getElementById("kategoriForm");
const kategoriList = document.getElementById("kategoriList");
const kategoriAd = document.getElementById("kategoriAd");
const kategoriId = document.getElementById("kategoriId");
const kategoriCancel = document.getElementById("kategoriCancel");
const kategoriPagePrev = document.getElementById("kategoriPagePrev");
const kategoriPageNext = document.getElementById("kategoriPageNext");
const kategoriPageNum = document.getElementById("kategoriPageNum");

const sorunKategori = document.getElementById("sorunKategori");
const sorunForm = document.getElementById("sorunForm");
const sorunList = document.getElementById("sorunList");
const sorunId = document.getElementById("sorunId");
const sorunBaslik = document.getElementById("sorunBaslik");
const sorunSorun = document.getElementById("sorunSorun");
const sorunCozum = document.getElementById("sorunCozum");
const sorunCancel = document.getElementById("sorunCancel");
const sorunPagePrev = document.getElementById("sorunPagePrev");
const sorunPageNext = document.getElementById("sorunPageNext");
const sorunPageNum = document.getElementById("sorunPageNum");

const kategoriBulkDeleteBtn = document.getElementById("kategoriBulkDelete");
const sorunBulkDeleteBtn = document.getElementById("sorunBulkDelete");


const loginForm = document.getElementById("loginForm");
const loginUser = document.getElementById("loginUser");
const loginPass = document.getElementById("loginPass");
const registerForm = document.getElementById("registerForm");
const registerUser = document.getElementById("registerUser");
const registerPass = document.getElementById("registerPass");
const logoutBtn = document.getElementById("logoutBtn");

const userViewBtn = document.getElementById("userViewBtn");


const adminPanel = document.getElementById("adminPanel");
const userPanel = document.getElementById("userPanel");
const userKategoriList = document.getElementById("userKategoriList");
const userSorunList = document.getElementById("userSorunList");
const backToCategories = document.getElementById("backToCategories");
const backToAdmin = document.getElementById("backToAdmin");

const haftalikTopBtn = document.getElementById("haftalikTopBtn");
const haftalikTopContainer = document.getElementById("haftalikTopContainer");


let kategoriler = [];
let kategoriPage = 0;
let kategoriSize = 10;
let sorunPage = 0;
let sorunSize = 10;
let selectedKategoriId = "";
let selectedKategorilerForBulk = new Set();
let selectedSorunlarForBulk = new Set();
let currentSorunState = {}; 


async function request(url, options = {}) {
  const headers = options.headers || {};
  Object.assign(headers, { "Content-Type": "application/json" }, authHeaders());
  options.headers = headers;

  try {
    const res = await fetch(url, options);
    if (res.status === 401) {
      showAlert("Yetkisiz. Lütfen giriş yapın.", "error");
      // logout locally
      setAuthToken(null);
      renderAuthState();
      throw new Error("Unauthorized");
    }
    const text = await res.text();
    let data;
    try { data = text ? JSON.parse(text) : null; } catch (e) { data = text; }
    if (!res.ok) {
      const msg = (data && data.message) ? data.message : (typeof data === "string" ? data : "Sunucuda hata oluştu");
      throw new Error(msg);
    }
    return data;
  } catch (err) {
    console.error("Request hata:", err);
    throw err;
  }
}


async function login(username, password) {
  try {
    const ok = await request("/auth/login", {
      method: "POST",
      body: JSON.stringify({ username, password })
    });
    
    const basic = "Basic " + btoa(`${username}:${password}`);
    setAuthToken(basic);
    showAlert("Giriş başarılı", "success");
    renderAuthState();
    await initAll(); 
  } catch (err) {
    showAlert("Giriş başarısız: " + err.message, "error");
  }
}

async function register(username, password) {
  try {
    await request("/auth/register", {
      method: "POST",
      body: JSON.stringify({ username, password })
    });
    showAlert("Kayıt başarılı. Giriş yapabilirsiniz.", "success");
  } catch (err) {
    showAlert("Kayıt başarısız: " + err.message, "error");
  }
}

function logout() {
  setAuthToken(null);
  showAlert("Çıkış yapıldı", "info");
  renderAuthState();
}


async function fetchKategoriler(page = 0, size = kategoriSize) {
  try {
    const data = await request(`${apiBase}/kategori?page=${page}&size=${size}`);
    kategoriler = data.content || [];
    kategoriPage = data.number ?? page;
    renderKategoriList();
    renderKategoriPagination(data);
    renderUserKategoriler(); 
  } catch (err) {
    console.error("fetchKategoriler:", err);
  }
}

function renderKategoriPagination(pageObj) {
  if (!pageObj) return;
  const current = pageObj.number ?? 0;
  const totalPages = pageObj.totalPages ?? 1;
  kategoriPageNum && (kategoriPageNum.textContent = `${current + 1} / ${totalPages}`);
  if (kategoriPagePrev) kategoriPagePrev.disabled = current <= 0;
  if (kategoriPageNext) kategoriPageNext.disabled = current >= totalPages - 1;
}

async function saveKategori(evt) {
  evt && evt.preventDefault();
  const dto = { ad: kategoriAd.value?.trim() };
  if (!dto.ad) return showAlert("Kategori adı boş olamaz", "error");
  try {
    if (kategoriId.value) {
      await request(`${apiBase}/kategori/${kategoriId.value}`, {
        method: "PUT",
        body: JSON.stringify(dto)
      });
      showAlert("Kategori güncellendi", "success");
    } else {
      await request(`${apiBase}/kategori`, {
        method: "POST",
        body: JSON.stringify(dto)
      });
      showAlert("Kategori eklendi", "success");
    }
    kategoriAd.value = "";
    kategoriId.value = "";
    await fetchKategoriler(kategoriPage);
  } catch (err) {
    showAlert("Kategori kaydetme hatası: " + err.message, "error");
  }
}

async function deleteKategorilerBulk() {
  if (selectedKategorilerForBulk.size === 0) return showAlert("Silinecek kategori seçilmedi", "error");
  if (!confirm("Seçili kategorileri silmek istediğinize emin misiniz?")) return;
  const ids = Array.from(selectedKategorilerForBulk);
  try {
    await request(`${apiBase}/kategori/toplu`, {
      method: "DELETE",
      body: JSON.stringify(ids)
    });
    showAlert("Kategoriler silindi", "success");
    selectedKategorilerForBulk.clear();
    await fetchKategoriler(kategoriPage);
    await fetchSorunlar(); 
  } catch (err) {
    showAlert("Toplu silme hatası: " + err.message, "error");
  }
}

function renderKategoriList() {
  if (!kategoriList) return;
  kategoriList.innerHTML = "";
  kategoriler.forEach(k => {
    const li = document.createElement("li");
    li.className = "list-group-item d-flex justify-content-between align-items-center";

    const left = document.createElement("div");
    left.style.display = "flex";
    left.style.alignItems = "center";
    left.style.gap = "8px";

    const chk = document.createElement("input");
    chk.type = "checkbox";
    chk.checked = selectedKategorilerForBulk.has(k.id);
    chk.onchange = (e) => {
      if (e.target.checked) selectedKategorilerForBulk.add(k.id);
      else selectedKategorilerForBulk.delete(k.id);
    };

    const span = document.createElement("span");
    span.textContent = k.ad;

    left.appendChild(chk);
    left.appendChild(span);

    const btnGroup = document.createElement("div");

    const editBtn = document.createElement("button");
    editBtn.className = "btn btn-sm btn-warning me-2";
    editBtn.textContent = "Düzenle";
    editBtn.onclick = () => {
      kategoriId.value = k.id;
      kategoriAd.value = k.ad;
      window.scrollTo({ top: 0, behavior: "smooth" });
    };

    const delBtn = document.createElement("button");
    delBtn.className = "btn btn-sm btn-danger";
    delBtn.textContent = "Sil";
    delBtn.onclick = async () => {
      if (!confirm("Silmek istediğinize emin misiniz?")) return;
      try {
        await request(`${apiBase}/kategori/toplu`, {
          method: "DELETE",
          body: JSON.stringify([k.id])
        });
        showAlert("Kategori silindi", "success");
        await fetchKategoriler(kategoriPage);
        await fetchSorunlar();
      } catch (err) {
        showAlert("Silme hatası: " + err.message, "error");
      }
    };

    btnGroup.appendChild(editBtn);
    btnGroup.appendChild(delBtn);

    li.appendChild(left);
    li.appendChild(btnGroup);
    kategoriList.appendChild(li);
  });

  
  if (sorunKategori) {
    sorunKategori.innerHTML = '<option value="">Kategori Seç</option>';
    kategoriler.forEach(k => {
      const opt = document.createElement("option");
      opt.value = k.id;
      opt.textContent = k.ad;
      sorunKategori.appendChild(opt);
    });
  }
}


async function fetchSorunlar(page = 0, size = sorunSize) {
  if (!selectedKategoriId) {
    sorunList.innerHTML = "<li class='list-group-item'>Kategori seçin</li>";
    return;
  }
  try {
    const data = await request(`${apiBase}/sorun/kategori/${selectedKategoriId}?page=${page}&size=${size}`);
    const sorunlar = data.content || [];
    sorunPage = data.number ?? page;
    renderSorunList(sorunlar);
    renderSorunPagination(data);
  } catch (err) {
    console.error("fetchSorunlar:", err);
  }
}

function renderSorunPagination(pageObj) {
  if (!pageObj) return;
  const current = pageObj.number ?? 0;
  const totalPages = pageObj.totalPages ?? 1;
  sorunPageNum && (sorunPageNum.textContent = `${current + 1} / ${totalPages}`);
  if (sorunPagePrev) sorunPagePrev.disabled = current <= 0;
  if (sorunPageNext) sorunPageNext.disabled = current >= totalPages - 1;
}

function renderSorunList(sorunlar) {
  if (!sorunList) return;
  sorunList.innerHTML = "";
  selectedSorunlarForBulk.clear();

  sorunlar.forEach(s => {
    const li = document.createElement("li");
    li.className = "list-group-item d-flex justify-content-between align-items-start";

    const left = document.createElement("div");
    left.style.flex = "1";

    const title = document.createElement("div");
    title.innerHTML = `<b>${s.baslik}</b>`;

    const content = document.createElement("div");
    content.style.marginTop = "6px";
    content.textContent = s.sorun;

    const solution = document.createElement("div");
    solution.style.marginTop = "6px";
    solution.style.fontStyle = "italic";
    solution.textContent = s.cozum;

    left.appendChild(title);
    left.appendChild(content);
    left.appendChild(solution);

    const right = document.createElement("div");
    right.style.display = "flex";
    right.style.flexDirection = "column";
    right.style.gap = "6px";
    right.style.alignItems = "flex-end";

   
    const chk = document.createElement("input");
    chk.type = "checkbox";
    chk.onchange = (e) => {
      if (e.target.checked) selectedSorunlarForBulk.add(s.id);
      else selectedSorunlarForBulk.delete(s.id);
    };

    
    const likeBtn = document.createElement("button");
    likeBtn.className = "btn btn-sm btn-light";
    likeBtn.textContent = "❤ Beğen";
    likeBtn.onclick = async () => {
      try {
       
        const userId = Number(getLoggedInUserId() || 0); 
        await toggleLike(userId, s.id);
        showAlert("Beğeniler güncellendi", "success");
      } catch (err) {
        showAlert("Beğenme başarısız: " + err.message, "error");
      }
    };

    const editBtn = document.createElement("button");
    editBtn.className = "btn btn-sm btn-warning";
    editBtn.textContent = "Düzenle";
    editBtn.onclick = () => {
      sorunId.value = s.id;
      sorunBaslik.value = s.baslik;
      sorunSorun.value = s.sorun;
      sorunCozum.value = s.cozum;
      sorunKategori.value = s.kategoriId;
      selectedKategoriId = s.kategoriId;
      window.scrollTo({ top: 0, behavior: "smooth" });
    };

    const delBtn = document.createElement("button");
    delBtn.className = "btn btn-sm btn-danger";
    delBtn.textContent = "Sil";
    delBtn.onclick = async () => {
      if (!confirm("Silmek istediğinize emin misiniz?")) return;
      try {
        await request(`${apiBase}/sorun/${s.id}`, { method: "DELETE" });
        showAlert("Sorun silindi", "success");
        await fetchSorunlar(sorunPage);
      } catch (err) {
        showAlert("Silme hatası: " + err.message, "error");
      }
    };

    right.appendChild(chk);
    right.appendChild(likeBtn);
    right.appendChild(editBtn);
    right.appendChild(delBtn);

    li.appendChild(left);
    li.appendChild(right);
    sorunList.appendChild(li);
  });
}

async function saveSorun(evt) {
  evt && evt.preventDefault();
  if (!sorunKategori.value) return showAlert("Kategori seçiniz!", "error");
  const dto = {
    baslik: (sorunBaslik.value || "").trim(),
    sorun: (sorunSorun.value || "").trim(),
    cozum: (sorunCozum.value || "").trim(),
    kategoriId: Number(sorunKategori.value)
  };

  if (!dto.baslik || !dto.sorun || !dto.cozum) return showAlert("Tüm alanları doldurun", "error");

  try {
    if (sorunId.value) {
      await request(`${apiBase}/sorun/${sorunId.value}`, { method: "PUT", body: JSON.stringify(dto) });
      showAlert("Sorun güncellendi", "success");
    } else {
      await request(`${apiBase}/sorun`, { method: "POST", body: JSON.stringify(dto) });
      showAlert("Sorun oluşturuldu", "success");
    }
    // reset
    sorunId.value = "";
    sorunBaslik.value = "";
    sorunSorun.value = "";
    sorunCozum.value = "";
    sorunKategori.value = "";
    await fetchSorunlar(sorunPage);
  } catch (err) {
    showAlert("Sorun kaydetme hatası: " + err.message, "error");
  }
}

async function deleteSorunlarBulk() {
  if (selectedSorunlarForBulk.size === 0) return showAlert("Silinecek sorun seçilmedi", "error");
  if (!confirm("Seçili sorunları silmek istediğinize emin misiniz?")) return;
  const ids = Array.from(selectedSorunlarForBulk);
  try {
    await request(`${apiBase}/sorun/toplu`, { method: "DELETE", body: JSON.stringify(ids) });
    showAlert("Sorunlar silindi", "success");
    selectedSorunlarForBulk.clear();
    await fetchSorunlar(sorunPage);
  } catch (err) {
    showAlert("Toplu silme hatası: " + err.message, "error");
  }
}


async function toggleLike(user_id, sorun_id) {
  try {
    
    const res = await request("/like/toggle-like", {
      method: "POST",
      body: JSON.stringify({ user_id, sorun_id })
    });
    return res;
  } catch (err) {
    throw err;
  }
}

async function getHaftalikTopSorunlar() {
  try {
    const data = await request("/like/haftalik-en-cok-begenilenler");
    renderHaftalikTop(data || []);
  } catch (err) {
    showAlert("Haftalık top alınamadı: " + err.message, "error");
  }
}

function renderHaftalikTop(list) {
  if (!haftalikTopContainer) return;
  haftalikTopContainer.innerHTML = "";
  if (!list.length) {
    haftalikTopContainer.innerHTML = "<p>Bu hafta henüz beğeni yok.</p>";
    return;
  }
  const ol = document.createElement("ol");
  list.forEach(id => {
    const li = document.createElement("li");
    li.textContent = `Sorun ID: ${id}`;
    ol.appendChild(li);
  });
  haftalikTopContainer.appendChild(ol);
}


function renderUserKategoriler() {
  if (!userKategoriList) return;
  userKategoriList.innerHTML = "";
  kategoriler.forEach(k => {
    const card = document.createElement("div");
    card.className = "kategori-card";
    card.textContent = k.ad;
    card.onclick = () => {
      renderUserSorunlar(k.id);
    };
    userKategoriList.appendChild(card);
  });
}

async function renderUserSorunlar(kategoriId) {
  userKategoriList.classList.add("d-none");
  backToCategories.classList.remove("d-none");
  try {
    const data = await request(`${apiBase}/sorun/kategori/${kategoriId}?page=0&size=100`);
    const sorunlar = data.content || [];
    userSorunList.innerHTML = "";
    sorunlar.forEach(s => {
      const div = document.createElement("div");
      div.className = "user-sorun-card";
      div.innerHTML = `<b>${s.baslik}</b><p class="muted">${s.sorun}</p><p class="muted-italic">${s.cozum}</p>`;
      
      div.onclick = () => {
        if (!currentSorunState[s.id]) currentSorunState[s.id] = 0;
        currentSorunState[s.id] = (currentSorunState[s.id] + 1) % 3;
        if (currentSorunState[s.id] === 0) div.innerHTML = `<b>${s.baslik}</b>`;
        if (currentSorunState[s.id] === 1) div.innerHTML = `<p>${s.sorun}</p>`;
        if (currentSorunState[s.id] === 2) div.innerHTML = `<p><i>${s.cozum}</i></p>`;
      };
      // like small btn
      const likeBtn = document.createElement("button");
      likeBtn.className = "btn-like-small";
      likeBtn.textContent = "❤";
      likeBtn.onclick = async (ev) => {
        ev.stopPropagation();
        try {
          const userId = Number(getLoggedInUserId() || 0);
          await toggleLike(userId, s.id);
          showAlert("Beğenildi", "success");
        } catch (err) {
          showAlert("Beğeni başarısız: " + err.message, "error");
        }
      };
      div.appendChild(likeBtn);
      userSorunList.appendChild(div);
    });
  } catch (err) {
    console.error("renderUserSorunlar:", err);
  }
}


function getLoggedInUserId() {
  
  return localStorage.getItem("bg_user_id");
}

function setLoggedInUserId(id) {
  if (id) localStorage.setItem("bg_user_id", id);
  else localStorage.removeItem("bg_user_id");
}


function renderAuthState() {
  const token = getAuthToken();
  if (token) {
    // logged in
    if (logoutBtn) logoutBtn.classList.remove("d-none");
    if (loginForm) loginForm.classList.add("d-none");
    if (registerForm) registerForm.classList.add("d-none");
  } else {
    if (logoutBtn) logoutBtn.classList.add("d-none");
    if (loginForm) loginForm.classList.remove("d-none");
    if (registerForm) registerForm.classList.remove("d-none");
  }
}


kategoriForm && kategoriForm.addEventListener("submit", saveKategori);
kategoriCancel && (kategoriCancel.onclick = () => { kategoriId.value = ""; kategoriAd.value = ""; });

sorunForm && sorunForm.addEventListener("submit", saveSorun);
sorunCancel && (sorunCancel.onclick = () => {
  sorunId.value = ""; sorunBaslik.value = ""; sorunSorun.value = ""; sorunCozum.value = ""; sorunKategori.value = "";
});

kategoriPagePrev && (kategoriPagePrev.onclick = () => fetchKategoriler(Math.max(0, kategoriPage - 1)));
kategoriPageNext && (kategoriPageNext.onclick = () => fetchKategoriler(kategoriPage + 1));
sorunPagePrev && (sorunPagePrev.onclick = () => fetchSorunlar(Math.max(0, sorunPage - 1)));
sorunPageNext && (sorunPageNext.onclick = () => fetchSorunlar(sorunPage + 1));

kategoriBulkDeleteBtn && (kategoriBulkDeleteBtn.onclick = deleteKategorilerBulk);
sorunBulkDeleteBtn && (sorunBulkDeleteBtn.onclick = deleteSorunlarBulk);

loginForm && loginForm.addEventListener("submit", (e) => {
  e.preventDefault();
  login(loginUser.value.trim(), loginPass.value.trim());
});
registerForm && registerForm.addEventListener("submit", (e) => {
  e.preventDefault();
  register(registerUser.value.trim(), registerPass.value.trim());
});
logoutBtn && (logoutBtn.onclick = logout);

userViewBtn && (userViewBtn.onclick = () => {
  adminPanel.classList.add("d-none");
  userPanel.classList.remove("d-none");
  renderUserKategoriler();
});
backToCategories && (backToCategories.onclick = () => {
  userSorunList.innerHTML = "";
  userKategoriList.classList.remove("d-none");
  backToCategories.classList.add("d-none");
});
backToAdmin && (backToAdmin.onclick = () => {
  userPanel.classList.add("d-none");
  adminPanel.classList.remove("d-none");
});

haftalikTopBtn && (haftalikTopBtn.onclick = getHaftalikTopSorunlar);


async function initAll() {
  await fetchKategoriler(0);
  
  if (selectedKategoriId) await fetchSorunlar(0);
  await getHaftalikTopSorunlar();
  renderAuthState();
}

window.addEventListener("DOMContentLoaded", () => {
  renderAuthState();
  initAll();
});
