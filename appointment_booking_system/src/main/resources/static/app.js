const state = {
    authToken: localStorage.getItem("abs_access_token") || "",
    authUserId: localStorage.getItem("abs_user_id") || "",
    authUserEmail: localStorage.getItem("abs_user_email") || "",
    authUserRole: localStorage.getItem("abs_user_role") || "",
    doctors: [],
    clinics: [],
    patientProfile: null,
    doctorProfile: null,
    patientAppointments: [],
    doctorAppointments: [],
    patientDetailsById: {}
};

const statusBar = document.getElementById("statusBar");
const authState = document.getElementById("authState");
const logoutBtn = document.getElementById("logoutBtn");
const guestView = document.getElementById("guestView");
const patientView = document.getElementById("patientView");
const doctorView = document.getElementById("doctorView");
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
const patientAppointmentDuration = document.getElementById("patientAppointmentDuration");

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

function updateLayout() {
    const loggedIn = Boolean(state.authToken);
    authState.textContent = loggedIn
        ? `Authenticated: ${state.authUserEmail} (${state.authUserRole})`
        : "Not authenticated";
    logoutBtn.disabled = !loggedIn;

    guestView.classList.toggle("hidden", loggedIn);
    patientView.classList.toggle("hidden", !(loggedIn && state.authUserRole === "PATIENT"));
    doctorView.classList.toggle("hidden", !(loggedIn && state.authUserRole === "DOCTOR"));
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
    }
}

function setDateMin() {
    const tomorrow = new Date();
    tomorrow.setDate(tomorrow.getDate() + 1);
    patientAppointmentDate.min = tomorrow.toISOString().split("T")[0];
}

function option(value, label) {
    const item = document.createElement("option");
    item.value = String(value);
    item.textContent = label;
    return item;
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
    const clinics = await api("/clinics", { skipAuth: true });
    state.clinics = Array.isArray(clinics) ? clinics : [];
    renderDoctorRegisterClinicOptions();
}

function formatDateTime(dateValue, timeValue) {
    return `${dateValue || "-"} ${timeValue || "-"}`.trim();
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

    state.doctors.forEach((doctor) => {
        const clinicNames = (doctor.clinicIds || [])
            .map((clinicId) => {
                const clinic = state.clinics.find((item) => Number(item.id) === Number(clinicId));
                return clinic ? clinic.name : `#${clinicId}`;
            })
            .join(", ");

        const row = document.createElement("tr");
        row.innerHTML = `
            <td>${doctor.user.name}</td>
            <td>${doctor.specialization || "-"}</td>
            <td>${doctor.availabilitySchedule || "-"}</td>
            <td>${clinicNames || "-"}</td>
        `;
        body.appendChild(row);
    });
}

function renderPatientAppointments() {
    const body = document.getElementById("patientAppointmentsBody");
    body.innerHTML = "";

    state.patientAppointments.forEach((appointment) => {
        const doctor = state.doctors.find((item) => item.id === appointment.doctorId);
        const clinic = state.clinics.find((item) => item.id === appointment.clinicId);
        const row = document.createElement("tr");
        const cancelButton = appointment.status === "CANCELLED"
            ? ""
            : `<button class="btn btn-danger cancel-appointment" data-id="${appointment.id}" type="button">Cancel</button>`;

        row.innerHTML = `
            <td>${appointment.id}</td>
            <td>${doctor ? doctor.user.name : appointment.doctorId}</td>
            <td>${clinic ? clinic.name : appointment.clinicId}</td>
            <td>${appointment.appointmentDate || "-"}</td>
            <td>${appointment.appointmentTime || "-"}</td>
            <td>${appointment.durationMinutes || 30} min</td>
            <td>${appointment.status || "-"}</td>
            <td>${cancelButton}</td>
        `;
        body.appendChild(row);
    });
}

function renderDoctorAppointments() {
    const body = document.getElementById("doctorAppointmentsBody");
    body.innerHTML = "";

    state.doctorAppointments.forEach((appointment) => {
        const patient = state.patientDetailsById[appointment.patientId];
        const row = document.createElement("tr");
        row.innerHTML = `
            <td>#${appointment.id}</td>
            <td>${appointment.appointmentDate || "-"}</td>
            <td>${appointment.appointmentTime || "-"}</td>
            <td>${appointment.durationMinutes || 30} min</td>
            <td>${appointment.status || "-"}</td>
            <td>${patient ? patient.user.name : appointment.patientId}</td>
            <td>${patient ? patient.user.email : "-"}</td>
            <td>${patient ? patient.user.phoneNumber : "-"}</td>
            <td>${patient ? patient.address : "-"}</td>
            <td>${patient ? (patient.medicalHistory || "-") : "-"}</td>
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
}

async function loadPatientDashboard() {
    const [patientProfile, doctors, clinics, appointments] = await Promise.all([
        api(`/patients/${state.authUserId}`),
        api("/doctors"),
        api("/clinics"),
        api("/appointments/mine")
    ]);

    state.patientProfile = patientProfile;
    state.doctors = Array.isArray(doctors) ? doctors : [];
    state.clinics = Array.isArray(clinics) ? clinics : [];
    state.patientAppointments = Array.isArray(appointments) ? appointments : [];

    renderPatientProfile();
    renderDoctorsTable();
    refreshPatientAppointmentSelectors();
    renderPatientAppointments();
}

async function loadDoctorDashboard() {
    const [doctorProfile, appointments] = await Promise.all([
        api(`/doctors/${state.authUserId}`),
        api("/appointments/mine")
    ]);

    state.doctorProfile = doctorProfile;
    state.doctorAppointments = Array.isArray(appointments) ? appointments : [];

    const patientIds = [...new Set(state.doctorAppointments.map((item) => item.patientId))];
    const patients = await Promise.all(patientIds.map((id) => api(`/patients/${id}`)));
    state.patientDetailsById = {};
    patients.forEach((patient) => {
        state.patientDetailsById[patient.id] = patient;
    });

    renderDoctorProfile();
    renderDoctorAppointments();
}

async function loadDashboardData() {
    if (!state.authToken) {
        return;
    }

    if (state.authUserRole === "PATIENT") {
        await loadPatientDashboard();
        return;
    }
    if (state.authUserRole === "DOCTOR") {
        await loadDoctorDashboard();
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
    updateLayout();
    await loadDashboardData();
    showStatus("Login successful.");
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
        appointmentTime: document.getElementById("patientAppointmentTime").value,
        durationMinutes: Number(patientAppointmentDuration.value),
        status: "BOOKED"
    };

    await api("/appointments", {
        method: "POST",
        body: JSON.stringify(payload)
    });

    document.getElementById("patientAppointmentForm").reset();
    setDateMin();
    await loadPatientDashboard();
    showStatus("Appointment created.");
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

    await api(`/appointments/${appointmentId}/cancel`, { method: "DELETE" });
    await loadPatientDashboard();
    showStatus("Appointment cancelled.");
}

function bindEvents() {
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

    registerRoleSelect.addEventListener("change", setRegisterRoleFields);

    logoutBtn.addEventListener("click", () => {
        clearAuth();
        updateLayout();
        showStatus("Logged out.");
    });

    document.getElementById("refreshAll").addEventListener("click", async () => {
        try {
            if (!state.authToken) {
                throw new Error("Login first.");
            }
            await loadDashboardData();
            showStatus("Data refreshed.");
        } catch (error) {
            showStatus(error.message, true);
        }
    });

    patientDoctorSelect.addEventListener("change", refreshPatientAppointmentSelectors);

    document.getElementById("patientAppointmentForm").addEventListener("submit", async (event) => {
        try {
            await handleCreatePatientAppointment(event);
        } catch (error) {
            showStatus(error.message, true);
        }
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
        setDateMin();
        await loadPublicClinics();
        bindEvents();
        setRegisterRoleFields();
        updateLayout();
        if (state.authToken) {
            await loadDashboardData();
        }
        showStatus("Portal ready.");
    } catch (error) {
        showStatus(error.message, true);
    }
}

void init();
