const state = {
    authToken: localStorage.getItem("abs_access_token") || "",
    authUserId: localStorage.getItem("abs_user_id") || "",
    authUserEmail: localStorage.getItem("abs_user_email") || "",
    authUserRole: localStorage.getItem("abs_user_role") || "",
    theme: localStorage.getItem("abs_theme") || "light",
    authMode: "login",
    securityOpen: false,
    patientProfileOpen: false,
    doctorProfileOpen: false,
    doctors: [],
    clinics: [],
    patientProfile: null,
    doctorProfile: null,
    patientAppointments: [],
    doctorAppointments: [],
    patientDetailsById: {},
    publicClinicsLoaded: false,
    publicClinicsLoading: null,
    dashboardLoading: false,
    autoRefreshTimer: null
};

const AUTO_REFRESH_MS = 30000;

const statusBar = document.getElementById("statusBar");
const authState = document.getElementById("authState");
const themeToggle = document.getElementById("themeToggle");
const refreshAll = document.getElementById("refreshAll");
const logoutBtn = document.getElementById("logoutBtn");
const guestView = document.getElementById("guestView");
const loginCard = document.getElementById("loginCard");
const registerCard = document.getElementById("registerCard");
const resetCard = document.getElementById("resetCard");
const securitySlot = document.getElementById("securitySlot");
const accountSecurityView = document.getElementById("accountSecurityView");
const securityToggle = document.getElementById("securityToggle");
const securityContent = document.getElementById("securityContent");
const patientView = document.getElementById("patientView");
const doctorView = document.getElementById("doctorView");
const patientProfileCard = document.getElementById("patientProfileCard");
const doctorProfileCard = document.getElementById("doctorProfileCard");
const patientProfileToggle = document.getElementById("patientProfileToggle");
const doctorProfileToggle = document.getElementById("doctorProfileToggle");
const patientProfileContent = document.getElementById("patientProfileContent");
const doctorProfileContent = document.getElementById("doctorProfileContent");
const loginRoleInput = document.getElementById("loginRole");
const registerRoleSelect = document.getElementById("registerRole");
const patientRegisterFields = document.getElementById("patientRegisterFields");
const doctorRegisterFields = document.getElementById("doctorRegisterFields");
const registerDoctorClinicSelect = document.getElementById("registerDoctorClinicIds");
const registerPatientAddressInput = document.getElementById("registerPatientAddress");
const registerPatientHistoryInput = document.getElementById("registerPatientHistory");
const registerDoctorSpecializationInput = document.getElementById("registerDoctorSpecialization");
const registerDoctorAvailabilityInput = document.getElementById("registerDoctorAvailability");
const patientDoctorSelect = document.getElementById("patientDoctorSelect");
const patientClinicSelect = document.getElementById("patientClinicSelect");
const patientAppointmentDate = document.getElementById("patientAppointmentDate");
const patientAppointmentTime = document.getElementById("patientAppointmentTime");
const patientAppointmentDuration = document.getElementById("patientAppointmentDuration");
const appointmentTimeHint = document.getElementById("appointmentTimeHint");

const DAY_NAMES = {
    sun: 0,
    sunday: 0,
    mon: 1,
    monday: 1,
    tue: 2,
    tues: 2,
    tuesday: 2,
    wed: 3,
    wednesday: 3,
    thu: 4,
    thur: 4,
    thurs: 4,
    thursday: 4,
    fri: 5,
    friday: 5,
    sat: 6,
    saturday: 6
};

async function api(path, options = {}) {
    const headers = { "Content-Type": "application/json", ...(options.headers || {}) };
    if (!options.skipAuth && state.authToken) {
        headers.Authorization = `Bearer ${state.authToken}`;
    }

    const response = await fetch(path, { ...options, headers });
    const raw = await response.text();
    const contentType = response.headers.get("content-type") || "";
    const data = contentType.includes("application/json") && raw ? JSON.parse(raw) : raw;

    if (!response.ok) {
        if (response.status === 401 || response.status === 403) {
            clearAuth();
            updateLayout();
        }
        const message = typeof data === "string" ? data : (data.message || "Request failed");
        throw new Error(message);
    }

    return data;
}

function showStatus(message, isError = false) {
    statusBar.textContent = message;
    statusBar.style.background = isError ? "#742a2a" : "#10241a";
    statusBar.classList.add("visible");
    clearTimeout(showStatus.timer);
    showStatus.timer = setTimeout(() => statusBar.classList.remove("visible"), 2600);
}

function clearAuth() {
    state.authToken = "";
    state.authUserId = "";
    state.authUserEmail = "";
    state.authUserRole = "";
    localStorage.removeItem("abs_access_token");
    localStorage.removeItem("abs_user_id");
    localStorage.removeItem("abs_user_email");
    localStorage.removeItem("abs_user_role");
}

function setAuth(response, fallbackRole) {
    state.authToken = response.accessToken || "";
    state.authUserId = response.userId ? String(response.userId) : "";
    state.authUserEmail = response.email || "";
    state.authUserRole = response.role || fallbackRole || "";

    localStorage.setItem("abs_access_token", state.authToken);
    localStorage.setItem("abs_user_id", state.authUserId);
    localStorage.setItem("abs_user_email", state.authUserEmail);
    localStorage.setItem("abs_user_role", state.authUserRole);
}

function applyTheme() {
    document.documentElement.dataset.theme = state.theme;
    const themeLabel = themeToggle.querySelector(".theme-label");
    const themeIcon = themeToggle.querySelector(".theme-icon");
    const nextTheme = state.theme === "dark" ? "light" : "dark";
    themeLabel.textContent = state.theme === "dark" ? "Light" : "Dark";
    themeIcon.dataset.icon = state.theme === "dark" ? "sun" : "moon";
    themeToggle.setAttribute("aria-label", `Switch to ${nextTheme} theme`);
}

function toggleTheme() {
    state.theme = state.theme === "dark" ? "light" : "dark";
    localStorage.setItem("abs_theme", state.theme);
    applyTheme();
}

function setAuthMode(mode) {
    state.authMode = mode;
    loginCard.classList.toggle("hidden", mode !== "login");
    registerCard.classList.toggle("hidden", mode !== "register");
    resetCard.classList.toggle("hidden", mode !== "reset");
    document.querySelectorAll("[data-auth-mode]").forEach((button) => {
        const active = button.dataset.authMode === mode;
        button.classList.toggle("active", active);
        button.setAttribute("aria-pressed", String(active));
    });
}

function setSecurityOpen(open) {
    state.securityOpen = open;
    securityContent.classList.toggle("hidden", !open);
    securityToggle.classList.toggle("active", open);
    securityToggle.setAttribute("aria-expanded", String(open));
}

function setProfileOpen(role, open) {
    const isDoctor = role === "DOCTOR";
    const content = isDoctor ? doctorProfileContent : patientProfileContent;
    const toggle = isDoctor ? doctorProfileToggle : patientProfileToggle;
    const card = isDoctor ? doctorProfileCard : patientProfileCard;

    if (isDoctor) {
        state.doctorProfileOpen = open;
    } else {
        state.patientProfileOpen = open;
    }

    content.classList.toggle("hidden", !open);
    toggle.classList.toggle("active", open);
    card.classList.toggle("profile-open", open);
    toggle.setAttribute("aria-expanded", String(open));
}

function closeProfiles() {
    setProfileOpen("PATIENT", false);
    setProfileOpen("DOCTOR", false);
}

function placeSecurityPanel(loggedIn) {
    if (!loggedIn) {
        securitySlot.appendChild(accountSecurityView);
        accountSecurityView.classList.add("hidden");
        setSecurityOpen(false);
        return;
    }

    const target = state.authUserRole === "DOCTOR" ? doctorProfileContent : patientProfileContent;
    target.appendChild(accountSecurityView);
    accountSecurityView.classList.remove("hidden");
}

function updateLayout() {
    const loggedIn = Boolean(state.authToken);
    authState.textContent = loggedIn
        ? `Authenticated: ${state.authUserEmail} (${state.authUserRole})`
        : "Please authenticate to continue";
    refreshAll.classList.toggle("hidden", !loggedIn);
    logoutBtn.classList.toggle("hidden", !loggedIn);
    refreshAll.disabled = !loggedIn || state.dashboardLoading;
    logoutBtn.disabled = !loggedIn;

    guestView.classList.toggle("hidden", loggedIn);
    patientView.classList.toggle("hidden", !(loggedIn && state.authUserRole === "PATIENT"));
    doctorView.classList.toggle("hidden", !(loggedIn && state.authUserRole === "DOCTOR"));
    placeSecurityPanel(loggedIn);
}

function setLoginRole(role) {
    loginRoleInput.value = role;
    document.querySelectorAll("[data-login-role]").forEach((button) => {
        const active = button.dataset.loginRole === role;
        button.classList.toggle("active", active);
        button.setAttribute("aria-pressed", String(active));
    });
}

function setRegisterRole(role) {
    registerRoleSelect.value = role;
    document.querySelectorAll("[data-register-role]").forEach((button) => {
        const active = button.dataset.registerRole === role;
        button.classList.toggle("active", active);
        button.setAttribute("aria-pressed", String(active));
    });
    setRegisterRoleFields();
}

function setRegisterRoleFields() {
    const role = registerRoleSelect.value;
    const isPatient = role === "PATIENT";
    const isDoctor = role === "DOCTOR";

    patientRegisterFields.classList.toggle("hidden", !isPatient);
    doctorRegisterFields.classList.toggle("hidden", !isDoctor);

    registerPatientAddressInput.disabled = !isPatient;
    registerPatientHistoryInput.disabled = !isPatient;
    registerPatientAddressInput.required = isPatient;

    registerDoctorSpecializationInput.disabled = !isDoctor;
    registerDoctorAvailabilityInput.disabled = !isDoctor;
    registerDoctorClinicSelect.disabled = !isDoctor;
    registerDoctorSpecializationInput.required = isDoctor;
    registerDoctorAvailabilityInput.required = isDoctor;
    registerDoctorClinicSelect.required = isDoctor;

    if (isDoctor) {
        renderDoctorRegisterClinicOptions();
        void loadPublicClinics().catch((error) => showStatus(error.message, true));
    }
}

function setDateMin() {
    const tomorrow = new Date();
    tomorrow.setDate(tomorrow.getDate() + 1);
    patientAppointmentDate.min = tomorrow.toISOString().split("T")[0];
}

function bindPasswordToggles() {
    document.querySelectorAll("[data-password-toggle]").forEach((button) => {
        button.addEventListener("click", () => {
            const input = document.getElementById(button.dataset.passwordToggle);
            const showPassword = input.type === "password";

            input.type = showPassword ? "text" : "password";
            button.textContent = showPassword ? "Hide" : "Show";
            button.setAttribute("aria-label", showPassword ? "Hide password" : "Show password");
            button.setAttribute("aria-pressed", String(showPassword));
        });
    });
}

function option(value, label) {
    const item = document.createElement("option");
    item.value = String(value);
    item.textContent = label;
    return item;
}

function selectedDoctor() {
    if (!patientDoctorSelect.value) {
        return null;
    }
    return state.doctors.find((item) => Number(item.id) === Number(patientDoctorSelect.value)) || null;
}

function normalizeDayName(value) {
    if (!value) {
        return null;
    }
    const normalized = value.trim().toLowerCase();
    return Object.prototype.hasOwnProperty.call(DAY_NAMES, normalized) ? DAY_NAMES[normalized] : null;
}

function dayRangeContains(startDay, endDay, targetDay) {
    let current = startDay;
    while (true) {
        if (current === targetDay) {
            return true;
        }
        if (current === endDay) {
            return false;
        }
        current = current === 6 ? 0 : current + 1;
    }
}

function parseTimeMinutes(hourValue, minuteValue) {
    const hour = Number(hourValue);
    const minute = minuteValue === undefined || minuteValue === "" ? 0 : Number(minuteValue);
    if (!Number.isInteger(hour) || !Number.isInteger(minute) || hour < 0 || hour > 23 || minute < 0 || minute > 59) {
        return null;
    }
    return hour * 60 + minute;
}

function addTimeRanges(text, windows) {
    const timeRangePattern = /(\d{1,2})(?::(\d{2}))?\s*-\s*(\d{1,2})(?::(\d{2}))?/g;
    let match;
    while ((match = timeRangePattern.exec(text)) !== null) {
        const start = parseTimeMinutes(match[1], match[2]);
        const end = parseTimeMinutes(match[3], match[4]);
        if (start !== null && end !== null && start < end) {
            windows.push({ start, end });
        }
    }
}

function parseAvailabilityWindows(schedule, dateValue) {
    if (!schedule || !dateValue) {
        return [];
    }

    const date = new Date(`${dateValue}T00:00:00`);
    if (Number.isNaN(date.getTime())) {
        return [];
    }

    const targetDay = date.getDay();
    const windows = [];
    const daySchedulePattern = /^\s*([A-Za-z]{3,9})(?:\s*-\s*([A-Za-z]{3,9}))?\s*:?\s*(.+)$/i;

    schedule.split(";")
        .map((part) => part.trim())
        .filter(Boolean)
        .forEach((part) => {
            const dayMatch = daySchedulePattern.exec(part);
            if (dayMatch) {
                const startDay = normalizeDayName(dayMatch[1]);
                if (startDay !== null) {
                    const endDay = normalizeDayName(dayMatch[2]) ?? startDay;
                    if (dayRangeContains(startDay, endDay, targetDay)) {
                        addTimeRanges(dayMatch[3], windows);
                    }
                    return;
                }
            }
            addTimeRanges(part, windows);
        });

    return windows;
}

function minutesToTime(minutes) {
    const hour = Math.floor(minutes / 60);
    const minute = minutes % 60;
    return `${String(hour).padStart(2, "0")}:${String(minute).padStart(2, "0")}`;
}

function refreshAppointmentTimeOptions() {
    const doctor = selectedDoctor();
    const dateValue = patientAppointmentDate.value;
    const durationMinutes = Number(patientAppointmentDuration.value) || 30;
    const previousValue = patientAppointmentTime.value;

    patientAppointmentTime.innerHTML = "";

    if (!doctor || !dateValue) {
        const empty = option("", "Select doctor and date first");
        empty.disabled = true;
        empty.selected = true;
        patientAppointmentTime.appendChild(empty);
        patientAppointmentTime.disabled = true;
        appointmentTimeHint.textContent = "Select a doctor and date to see available times.";
        return;
    }

    const windows = parseAvailabilityWindows(doctor.availabilitySchedule, dateValue);
    const slots = [];
    const seen = new Set();
    windows.forEach((window) => {
        for (let start = window.start; start + durationMinutes <= window.end; start += 15) {
            const value = minutesToTime(start);
            if (!seen.has(value)) {
                seen.add(value);
                slots.push(value);
            }
        }
    });

    if (slots.length === 0) {
        const empty = option("", "No times inside schedule");
        empty.disabled = true;
        empty.selected = true;
        patientAppointmentTime.appendChild(empty);
        patientAppointmentTime.disabled = true;
        appointmentTimeHint.textContent = `Schedule: ${doctor.availabilitySchedule || "not configured"}`;
        return;
    }

    patientAppointmentTime.disabled = false;
    const prompt = option("", "Select a time");
    prompt.disabled = true;
    prompt.selected = true;
    patientAppointmentTime.appendChild(prompt);
    slots.forEach((slot) => patientAppointmentTime.appendChild(option(slot, slot)));

    if (previousValue && slots.includes(previousValue)) {
        patientAppointmentTime.value = previousValue;
    }
    appointmentTimeHint.textContent = `Schedule: ${doctor.availabilitySchedule}`;
}

function fillSelect(element, options, emptyMessage, placeholder = "", selectedValue = "") {
    element.innerHTML = "";
    if (options.length === 0) {
        const empty = option("", emptyMessage);
        empty.disabled = true;
        empty.selected = true;
        element.appendChild(empty);
        return;
    }

    if (placeholder) {
        const prompt = option("", placeholder);
        prompt.disabled = true;
        prompt.selected = true;
        element.appendChild(prompt);
    }

    options.forEach((entry) => {
        const item = option(entry.value, entry.label);
        if (selectedValue && String(entry.value) === String(selectedValue)) {
            item.selected = true;
        }
        element.appendChild(item);
    });

    if (!selectedValue && !placeholder && element.options.length > 0) {
        element.options[0].selected = true;
    }
}

function renderDoctorRegisterClinicOptions() {
    registerDoctorClinicSelect.innerHTML = "";
    if (state.publicClinicsLoading) {
        const loading = option("", "Loading clinics...");
        loading.disabled = true;
        loading.selected = true;
        registerDoctorClinicSelect.appendChild(loading);
        return;
    }
    if (!Array.isArray(state.clinics) || state.clinics.length === 0) {
        const empty = option("", "No clinics available");
        empty.disabled = true;
        empty.selected = true;
        registerDoctorClinicSelect.appendChild(empty);
        return;
    }

    state.clinics.forEach((clinic) => {
        registerDoctorClinicSelect.appendChild(option(clinic.id, clinic.name));
    });
}

async function loadPublicClinics() {
    if (state.publicClinicsLoaded) {
        renderDoctorRegisterClinicOptions();
        return state.clinics;
    }
    if (state.publicClinicsLoading) {
        return state.publicClinicsLoading;
    }

    state.publicClinicsLoading = api("/clinics", { skipAuth: true })
        .then((clinics) => {
            state.clinics = Array.isArray(clinics) ? clinics : [];
            state.publicClinicsLoaded = true;
            renderDoctorRegisterClinicOptions();
            return state.clinics;
        })
        .finally(() => {
            state.publicClinicsLoading = null;
        });

    renderDoctorRegisterClinicOptions();
    return state.publicClinicsLoading;
}

function formatDateTime(dateValue, timeValue) {
    return `${dateValue || "-"} ${timeValue || "-"}`.trim();
}

function tableCell(label, value) {
    return `<td data-label="${label}">${value}</td>`;
}

function setLoadingText(elementId, message = "Loading...") {
    const element = document.getElementById(elementId);
    element.innerHTML = `<div class="loading-line">${message}</div>`;
}

function setTableLoading(tbodyId, colSpan, message = "Loading...") {
    const body = document.getElementById(tbodyId);
    body.innerHTML = `<tr><td colspan="${colSpan}" class="loading-cell">${message}</td></tr>`;
}

function setDashboardLoading(isLoading) {
    state.dashboardLoading = isLoading;
    updateLayout();
}

function startAutoRefresh() {
    stopAutoRefresh();
    if (!state.authToken) {
        return;
    }
    state.autoRefreshTimer = setInterval(() => {
        if (!state.authToken || state.dashboardLoading) {
            return;
        }
        void refreshChangedSections({ silent: true }).catch((error) => showStatus(error.message, true));
    }, AUTO_REFRESH_MS);
}

function stopAutoRefresh() {
    if (state.autoRefreshTimer) {
        clearInterval(state.autoRefreshTimer);
        state.autoRefreshTimer = null;
    }
}

function renderPatientProfile() {
    const container = document.getElementById("patientProfile");
    container.innerHTML = "";

    if (!state.patientProfile) {
        container.textContent = "Patient profile unavailable.";
        return;
    }

    const profile = state.patientProfile;
    const block = document.createElement("div");
    block.className = "list-item";
    block.innerHTML = `
        <strong>${profile.user.name}</strong><br>
        <small>Email: ${profile.user.email}</small><br>
        <small>Phone: ${profile.user.phoneNumber}</small><br>
        <small>Address: ${profile.address || "-"}</small><br>
        <small>Medical history: ${profile.medicalHistory || "-"}</small>
    `;
    container.appendChild(block);
}

function renderDoctorProfile() {
    const container = document.getElementById("doctorProfile");
    container.innerHTML = "";

    if (!state.doctorProfile) {
        container.textContent = "Doctor profile unavailable.";
        return;
    }

    const profile = state.doctorProfile;
    const block = document.createElement("div");
    block.className = "list-item";
    block.innerHTML = `
        <strong>${profile.user.name}</strong><br>
        <small>Email: ${profile.user.email}</small><br>
        <small>Phone: ${profile.user.phoneNumber}</small><br>
        <small>Specialization: ${profile.specialization}</small><br>
        <small>Availability: ${profile.availabilitySchedule}</small>
    `;
    container.appendChild(block);
}

function renderDoctorsTable() {
    const body = document.getElementById("doctorsTableBody");
    body.innerHTML = "";

    if (state.doctors.length === 0) {
        body.innerHTML = `<tr><td colspan="4" class="empty-cell">No doctors available.</td></tr>`;
        return;
    }

    state.doctors.forEach((doctor) => {
        const clinicNames = (doctor.clinicIds || [])
            .map((clinicId) => {
                const clinic = state.clinics.find((item) => Number(item.id) === Number(clinicId));
                return clinic ? clinic.name : `#${clinicId}`;
            })
            .join(", ");

        const row = document.createElement("tr");
        row.innerHTML = `
            ${tableCell("Doctor", doctor.user.name)}
            ${tableCell("Specialization", doctor.specialization || "-")}
            ${tableCell("Schedule", doctor.availabilitySchedule || "-")}
            ${tableCell("Clinics", clinicNames || "-")}
        `;
        body.appendChild(row);
    });
}

function renderPatientAppointments() {
    const body = document.getElementById("patientAppointmentsBody");
    body.innerHTML = "";

    if (state.patientAppointments.length === 0) {
        body.innerHTML = `<tr><td colspan="8" class="empty-cell">No appointments yet.</td></tr>`;
        return;
    }

    state.patientAppointments.forEach((appointment) => {
        const doctor = state.doctors.find((item) => item.id === appointment.doctorId);
        const clinic = state.clinics.find((item) => item.id === appointment.clinicId);
        const row = document.createElement("tr");
        const cancelButton = appointment.status === "CANCELLED"
            ? ""
            : `<button class="btn btn-danger cancel-appointment" data-id="${appointment.id}" type="button">Cancel</button>`;

        row.innerHTML = `
            ${tableCell("ID", appointment.id)}
            ${tableCell("Doctor", doctor ? doctor.user.name : appointment.doctorId)}
            ${tableCell("Clinic", clinic ? clinic.name : appointment.clinicId)}
            ${tableCell("Date", appointment.appointmentDate || "-")}
            ${tableCell("Time", appointment.appointmentTime || "-")}
            ${tableCell("Duration", `${appointment.durationMinutes || 30} min`)}
            ${tableCell("Status", appointment.status || "-")}
            ${tableCell("Action", cancelButton || "-")}
        `;
        body.appendChild(row);
    });
}

function renderDoctorAppointments() {
    const body = document.getElementById("doctorAppointmentsBody");
    body.innerHTML = "";

    if (state.doctorAppointments.length === 0) {
        body.innerHTML = `<tr><td colspan="10" class="empty-cell">No appointments yet.</td></tr>`;
        return;
    }

    state.doctorAppointments.forEach((appointment) => {
        const patient = state.patientDetailsById[appointment.patientId];
        const row = document.createElement("tr");
        row.innerHTML = `
            ${tableCell("Appointment", `#${appointment.id}`)}
            ${tableCell("Date", appointment.appointmentDate || "-")}
            ${tableCell("Time", appointment.appointmentTime || "-")}
            ${tableCell("Duration", `${appointment.durationMinutes || 30} min`)}
            ${tableCell("Status", appointment.status || "-")}
            ${tableCell("Patient", patient ? patient.user.name : appointment.patientId)}
            ${tableCell("Email", patient ? patient.user.email : "-")}
            ${tableCell("Phone", patient ? patient.user.phoneNumber : "-")}
            ${tableCell("Address", patient ? patient.address : "-")}
            ${tableCell("Medical History", patient ? (patient.medicalHistory || "-") : "-")}
        `;
        body.appendChild(row);
    });
}

function getClinicsForSelectedDoctor() {
    if (!patientDoctorSelect.value) {
        return [];
    }
    const doctorId = Number(patientDoctorSelect.value);
    const doctor = state.doctors.find((item) => item.id === doctorId);
    if (!doctor || !Array.isArray(doctor.clinicIds) || doctor.clinicIds.length === 0) {
        return state.clinics;
    }
    const allowed = new Set(doctor.clinicIds);
    return state.clinics.filter((clinic) => allowed.has(clinic.id));
}

function refreshPatientAppointmentSelectors() {
    const previousDoctorValue = patientDoctorSelect.value;
    const previousClinicValue = patientClinicSelect.value;

    fillSelect(
        patientDoctorSelect,
        state.doctors.map((doctor) => ({
            value: doctor.id,
            label: `${doctor.user.name} (${doctor.specialization})`
        })),
        "No doctors available",
        "Select a doctor",
        previousDoctorValue
    );

    const clinics = getClinicsForSelectedDoctor();
    fillSelect(
        patientClinicSelect,
        clinics.map((clinic) => ({
            value: clinic.id,
            label: clinic.name
        })),
        "No clinics available",
        clinics.length > 0 ? "Select a clinic" : "",
        previousClinicValue
    );
    refreshAppointmentTimeOptions();
}

async function loadPatientDashboard() {
    setLoadingText("patientProfile");
    setTableLoading("doctorsTableBody", 4);
    setTableLoading("patientAppointmentsBody", 8);

    const [patientProfile, doctors, clinics, appointments] = await Promise.all([
        api(`/patients/${state.authUserId}`),
        api("/doctors"),
        api("/clinics"),
        api("/appointments/mine")
    ]);

    state.patientProfile = patientProfile;
    state.doctors = Array.isArray(doctors) ? doctors : [];
    state.clinics = Array.isArray(clinics) ? clinics : [];
    state.publicClinicsLoaded = true;
    state.patientAppointments = Array.isArray(appointments) ? appointments : [];

    renderPatientProfile();
    renderDoctorsTable();
    refreshPatientAppointmentSelectors();
    renderPatientAppointments();
}

async function loadPatientAppointments(options = {}) {
    if (options.showLoading) {
        setTableLoading("patientAppointmentsBody", 8);
    }
    const appointments = await api("/appointments/mine");
    state.patientAppointments = Array.isArray(appointments) ? appointments : [];
    renderPatientAppointments();
}

async function loadDoctorAppointments(options = {}) {
    if (options.showLoading) {
        setTableLoading("doctorAppointmentsBody", 10);
    }
    const appointments = await api("/appointments/mine");
    state.doctorAppointments = Array.isArray(appointments) ? appointments : [];

    const patientIds = [...new Set(state.doctorAppointments.map((item) => item.patientId))];
    const patients = await Promise.all(patientIds.map((id) => api(`/patients/${id}`)));
    state.patientDetailsById = {};
    patients.forEach((patient) => {
        state.patientDetailsById[patient.id] = patient;
    });

    renderDoctorAppointments();
}

async function loadDoctorDashboard() {
    setLoadingText("doctorProfile");
    setTableLoading("doctorAppointmentsBody", 10);

    state.doctorProfile = await api(`/doctors/${state.authUserId}`);
    await loadDoctorAppointments();
    renderDoctorProfile();
}

async function refreshChangedSections(options = {}) {
    if (!state.authToken) {
        if (!options.silent) {
            showStatus("Please authenticate first.", true);
        }
        return;
    }

    setDashboardLoading(true);
    try {
        if (state.authUserRole === "PATIENT") {
            await loadPatientAppointments({ showLoading: !options.silent });
        } else if (state.authUserRole === "DOCTOR") {
            await loadDoctorAppointments({ showLoading: !options.silent });
        }
        if (!options.silent) {
            showStatus("Updated current section.");
        }
    } finally {
        setDashboardLoading(false);
    }
}

async function loadDashboardData(options = {}) {
    if (!state.authToken) {
        if (!options.silent) {
            showStatus("Please authenticate first.", true);
        }
        return;
    }

    setDashboardLoading(true);
    try {
        if (state.authUserRole === "PATIENT") {
            await loadPatientDashboard();
            return;
        }
        if (state.authUserRole === "DOCTOR") {
            await loadDoctorDashboard();
        }
        if (!options.silent) {
            showStatus("Data refreshed.");
        }
    } finally {
        setDashboardLoading(false);
    }
}

async function handleLogin(event) {
    event.preventDefault();
    const role = document.getElementById("loginRole").value;
    const endpoint = role === "DOCTOR" ? "/auth/doctor/login" : "/auth/patient/login";
    const payload = {
        email: document.getElementById("loginEmail").value.trim(),
        password: document.getElementById("loginPassword").value.trim()
    };

    const response = await api(endpoint, {
        method: "POST",
        body: JSON.stringify(payload),
        skipAuth: true
    });

    setAuth(response, role);
    closeProfiles();
    updateLayout();
    startAutoRefresh();
    showStatus("Login successful. Loading dashboard...");
    void loadDashboardData({ silent: true }).catch((error) => showStatus(error.message, true));
}

async function handlePasswordChange(event) {
    event.preventDefault();

    if (!state.authToken) {
        throw new Error("Please authenticate before changing your password.");
    }

    const currentPassword = document.getElementById("currentPassword").value;
    const newPassword = document.getElementById("newPassword").value;
    const confirmNewPassword = document.getElementById("confirmNewPassword").value;

    if (newPassword !== confirmNewPassword) {
        throw new Error("The new password confirmation does not match.");
    }

    const form = event.currentTarget;
    const submitButton = form.querySelector('button[type="submit"]');
    const originalButtonText = submitButton.textContent;
    submitButton.disabled = true;
    submitButton.textContent = "Changing...";

    try {
        await api("/users/me/password", {
            method: "PUT",
            body: JSON.stringify({ currentPassword, newPassword })
        });
        form.reset();
        showStatus("Password changed.");
    } finally {
        submitButton.disabled = false;
        submitButton.textContent = originalButtonText;
    }
}

async function handlePasswordResetRequest(event) {
    event.preventDefault();

    const form = event.currentTarget;
    const submitButton = form.querySelector('button[type="submit"]');
    const originalButtonText = submitButton.textContent;
    submitButton.disabled = true;
    submitButton.textContent = "Sending...";

    try {
        await api("/auth/password-reset/request", {
            method: "POST",
            body: JSON.stringify({ email: document.getElementById("resetEmail").value.trim() }),
            skipAuth: true
        });
        showStatus("If that email exists, a reset link has been sent.");
    } finally {
        submitButton.disabled = false;
        submitButton.textContent = originalButtonText;
    }
}

async function handlePasswordResetConfirm(event) {
    event.preventDefault();

    const newPassword = document.getElementById("resetNewPassword").value;
    const confirmPassword = document.getElementById("resetConfirmPassword").value;
    if (newPassword !== confirmPassword) {
        throw new Error("The new password confirmation does not match.");
    }

    const form = event.currentTarget;
    const submitButton = form.querySelector('button[type="submit"]');
    const originalButtonText = submitButton.textContent;
    submitButton.disabled = true;
    submitButton.textContent = "Resetting...";

    try {
        await api("/auth/password-reset/confirm", {
            method: "POST",
            body: JSON.stringify({
                token: document.getElementById("resetToken").value.trim(),
                newPassword
            }),
            skipAuth: true
        });
        form.reset();
        window.history.replaceState({}, document.title, window.location.pathname);
        setAuthMode("login");
        showStatus("Password reset. You can login now.");
    } finally {
        submitButton.disabled = false;
        submitButton.textContent = originalButtonText;
    }
}

function loadPasswordResetTokenFromUrl() {
    const params = new URLSearchParams(window.location.search);
    const token = params.get("resetToken");
    if (!token) {
        return false;
    }
    stopAutoRefresh();
    clearAuth();
    document.getElementById("resetToken").value = token;
    setAuthMode("reset");
    window.history.replaceState({}, document.title, window.location.pathname);
    return true;
}

async function handleRegister(event) {
    event.preventDefault();
    const role = registerRoleSelect.value;
    const commonUser = {
        name: document.getElementById("registerName").value.trim(),
        email: document.getElementById("registerEmail").value.trim(),
        password: document.getElementById("registerPassword").value.trim(),
        phoneNumber: document.getElementById("registerPhone").value.trim(),
        role
    };

    if (role === "PATIENT") {
        const payload = {
            user: commonUser,
            address: document.getElementById("registerPatientAddress").value.trim(),
            medicalHistory: document.getElementById("registerPatientHistory").value.trim()
        };
        await api("/patients/register", {
            method: "POST",
            body: JSON.stringify(payload),
            skipAuth: true
        });
    } else {
        if (state.clinics.length === 0) {
            await loadPublicClinics();
        }

        const clinicIds = Array.from(registerDoctorClinicSelect.selectedOptions)
            .map((optionElement) => Number(optionElement.value))
            .filter((value) => Number.isFinite(value));

        if (clinicIds.length === 0) {
            throw new Error("Doctor registration needs at least one selected clinic.");
        }

        const payload = {
            user: commonUser,
            specialization: document.getElementById("registerDoctorSpecialization").value.trim(),
            availabilitySchedule: document.getElementById("registerDoctorAvailability").value.trim(),
            clinicIds
        };
        await api("/doctors/register", {
            method: "POST",
            body: JSON.stringify(payload),
            skipAuth: true
        });
    }

    document.getElementById("registerForm").reset();
    setRegisterRoleFields();
    showStatus("Account created. You can login now.");
}

async function handleCreatePatientAppointment(event) {
    event.preventDefault();
    const form = event.currentTarget;
    const submitButton = form.querySelector('button[type="submit"]');
    if (!submitButton || submitButton.disabled) {
        return;
    }

    if (state.authUserRole !== "PATIENT") {
        throw new Error("Only patients can create appointments.");
    }
    if (!patientDoctorSelect.value || !patientClinicSelect.value) {
        throw new Error("Select both doctor and clinic.");
    }

    const payload = {
        patientId: Number(state.authUserId),
        doctorId: Number(patientDoctorSelect.value),
        clinicId: Number(patientClinicSelect.value),
        appointmentDate: document.getElementById("patientAppointmentDate").value,
        appointmentTime: patientAppointmentTime.value,
        durationMinutes: Number(patientAppointmentDuration.value),
        status: "BOOKED"
    };

    if (!payload.appointmentTime) {
        throw new Error("Select an appointment time inside the doctor's schedule.");
    }

    const originalButtonText = submitButton.textContent;
    submitButton.disabled = true;
    submitButton.textContent = "Creating...";
    form.setAttribute("aria-busy", "true");

    let appointmentCreated = false;
    try {
        await api("/appointments", {
            method: "POST",
            body: JSON.stringify(payload)
        });
        appointmentCreated = true;

        form.reset();
        setDateMin();
        refreshPatientAppointmentSelectors();
        await loadPatientAppointments({ showLoading: true });
        showStatus("Appointment created.");
    } catch (error) {
        if (appointmentCreated) {
            throw new Error(`Appointment created, but the list could not be refreshed: ${error.message}`);
        }
        throw error;
    } finally {
        submitButton.disabled = false;
        submitButton.textContent = originalButtonText;
        form.removeAttribute("aria-busy");
    }
}

async function handlePatientAppointmentsClick(event) {
    const target = event.target;
    if (!(target instanceof HTMLElement) || !target.classList.contains("cancel-appointment")) {
        return;
    }

    const appointmentId = target.getAttribute("data-id");
    if (!appointmentId) {
        return;
    }
    if (target.disabled) {
        return;
    }

    const originalText = target.textContent;
    target.disabled = true;
    target.textContent = "Cancelling...";
    try {
        await api(`/appointments/${appointmentId}/cancel`, { method: "DELETE" });
        state.patientAppointments = state.patientAppointments.map((appointment) => (
            String(appointment.id) === String(appointmentId)
                ? { ...appointment, status: "CANCELLED" }
                : appointment
        ));
        renderPatientAppointments();
        showStatus("Appointment cancelled.");
    } catch (error) {
        target.disabled = false;
        target.textContent = originalText;
        throw error;
    }
}

function bindEvents() {
    bindPasswordToggles();

    themeToggle.addEventListener("click", toggleTheme);

    document.querySelectorAll("[data-auth-mode]").forEach((button) => {
        button.addEventListener("click", () => setAuthMode(button.dataset.authMode));
    });

    document.querySelectorAll("[data-login-role]").forEach((button) => {
        button.addEventListener("click", () => setLoginRole(button.dataset.loginRole));
    });

    document.querySelectorAll("[data-register-role]").forEach((button) => {
        button.addEventListener("click", () => setRegisterRole(button.dataset.registerRole));
    });

    document.getElementById("loginForm").addEventListener("submit", async (event) => {
        try {
            await handleLogin(event);
        } catch (error) {
            showStatus(error.message, true);
        }
    });

    document.getElementById("registerForm").addEventListener("submit", async (event) => {
        try {
            await handleRegister(event);
        } catch (error) {
            showStatus(error.message, true);
        }
    });

    logoutBtn.addEventListener("click", () => {
        stopAutoRefresh();
        clearAuth();
        document.getElementById("passwordChangeForm").reset();
        setSecurityOpen(false);
        closeProfiles();
        updateLayout();
        showStatus("Logged out.");
    });

    refreshAll.addEventListener("click", async () => {
        try {
            if (!state.authToken) {
                throw new Error("Please authenticate first.");
            }
            await refreshChangedSections();
        } catch (error) {
            showStatus(error.message, true);
        }
    });

    patientDoctorSelect.addEventListener("change", refreshPatientAppointmentSelectors);
    patientAppointmentDate.addEventListener("change", refreshAppointmentTimeOptions);
    patientAppointmentDuration.addEventListener("change", refreshAppointmentTimeOptions);

    document.getElementById("patientAppointmentForm").addEventListener("submit", async (event) => {
        try {
            await handleCreatePatientAppointment(event);
        } catch (error) {
            showStatus(error.message, true);
        }
    });

    document.getElementById("passwordResetRequestForm").addEventListener("submit", async (event) => {
        try {
            await handlePasswordResetRequest(event);
        } catch (error) {
            showStatus(error.message, true);
        }
    });

    document.getElementById("passwordResetConfirmForm").addEventListener("submit", async (event) => {
        try {
            await handlePasswordResetConfirm(event);
        } catch (error) {
            showStatus(error.message, true);
        }
    });

    document.getElementById("passwordChangeForm").addEventListener("submit", async (event) => {
        try {
            await handlePasswordChange(event);
        } catch (error) {
            showStatus(error.message, true);
        }
    });

    securityToggle.addEventListener("click", () => {
        setSecurityOpen(!state.securityOpen);
    });

    patientProfileToggle.addEventListener("click", () => {
        setProfileOpen("PATIENT", !state.patientProfileOpen);
    });

    doctorProfileToggle.addEventListener("click", () => {
        setProfileOpen("DOCTOR", !state.doctorProfileOpen);
    });

    document.getElementById("patientAppointmentsBody").addEventListener("click", async (event) => {
        try {
            await handlePatientAppointmentsClick(event);
        } catch (error) {
            showStatus(error.message, true);
        }
    });
}

async function init() {
    try {
        applyTheme();
        setDateMin();
        bindEvents();
        setAuthMode("login");
        setLoginRole(loginRoleInput.value || "PATIENT");
        setRegisterRole(registerRoleSelect.value || "PATIENT");
        const resetLinkOpened = loadPasswordResetTokenFromUrl();
        updateLayout();
        if (state.authToken && !resetLinkOpened) {
            startAutoRefresh();
            void loadDashboardData({ silent: true }).catch((error) => showStatus(error.message, true));
        } else {
            void loadPublicClinics().catch((error) => showStatus(error.message, true));
        }
        showStatus("Portal ready.");
    } catch (error) {
        showStatus(error.message, true);
    }
}

void init();
