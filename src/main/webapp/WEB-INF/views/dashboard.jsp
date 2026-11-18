
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>ScamShield | Dashboard</title>
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<div class="layout">
    <aside class="sidebar">
        <div class="brand">🛡 ScamShield</div>
        <div class="brand-subtitle">Your digital safety companion</div>

        <div class="nav-label">WORKSPACE</div>
        <a href="#home" class="nav-link active">▦ Dashboard</a>
        <a href="#report" class="nav-link">🚨 Report a Scam</a>
        <a href="#alerts" class="nav-link">⚠ Scam Alerts</a>
        <a href="#myReports" class="nav-link">📋 My Reports</a>

        <div class="nav-label">ACCOUNT</div>
        <a href="#profile" class="nav-link">♙ Connection Status</a>
        <a href="#" id="logoutLink" class="nav-link">↪ Logout</a>
    </aside>

    <main class="main" id="home">
        <header class="topbar">
            <div>
                <h1>Dashboard</h1>
                <p class="muted">Monitor threats. Stay informed. Help others stay safe.</p>
            </div>
            <div class="status">🛡 ScamShield Workspace</div>
        </header>

        <section class="welcome">
            <h2>Stay one step ahead of scammers.</h2>
            <p>Report suspicious activity and explore scam alerts in your city.</p>
        </section>

        <div class="section-heading">
            <h2>Quick Overview</h2>
        </div>

        <section class="cards">
            <div class="card">
                <div class="card-icon">📋</div>
                <h3>My Reports</h3>
                <p id="reportCount">Loading your reports...</p>
                <a href="#myReports" class="button button-secondary">View Reports →</a>
            </div>

            <div class="card">
                <div class="card-icon">⚠️</div>
                <h3>City Scam Alerts</h3>
                <p>Check scam alerts reported for a city.</p>
                <a href="#alerts" class="button button-secondary">Explore Alerts →</a>
            </div>

            <div class="card">
                <div class="card-icon">🚨</div>
                <h3>Report a Scam</h3>
                <p>Help others recognize suspicious activity.</p>
                <a href="#report" class="button">Submit a Report →</a>
            </div>
        </section>

        <div class="section-heading" id="report">
            <h2>Report a Scam</h2>
        </div>

        <section class="panel">
            <form id="reportForm">
                <label for="reportTitle">Report Title</label>
                <input id="reportTitle" type="text" maxlength="150"
                       placeholder="Example: Suspicious UPI payment request" required>

                <label for="scamType">Scam Type</label>
                <select id="scamType" required>
                    <option value="">Select scam type</option>
                    <option value="UPI_FRAUD">UPI Fraud</option>
                    <option value="SMS_FRAUD">SMS Fraud</option>
                    <option value="PHISHING">Phishing</option>
                    <option value="JOB_FRAUD">Job Fraud</option>
                    <option value="IDENTITY_THEFT">Identity Theft</option>
                    <option value="OTHER">Other</option>
                </select>

                <label for="reportDescription">Description</label>
                <textarea id="reportDescription" maxlength="2000"
                          rows="4" placeholder="Describe what happened..." required></textarea>

                <label for="suspiciousPhone">Suspicious Phone (optional)</label>
                <input id="suspiciousPhone" type="text" maxlength="20"
                       placeholder="Enter suspicious phone number">

                <label for="incidentCity">Incident City (optional)</label>
                <input id="incidentCity" type="text" maxlength="100"
                       placeholder="Enter city">

                <button type="submit" id="submitReportButton">Submit Report</button>
                <p id="reportMessage" role="status" aria-live="polite"></p>
            </form>
        </section>

        <div class="section-heading" id="myReports">
            <h2>My Reports</h2>
            <button type="button" id="refreshReportsButton">Refresh</button>
        </div>

        <section class="panel">
            <p id="reportsMessage" class="muted">Loading your reports...</p>
            <div id="reportsContainer"></div>
        </section>

        <div class="section-heading" id="alerts">
            <h2>City Scam Alerts</h2>
        </div>

        <section class="panel">
            <form id="alertsForm">
                <label for="alertCity">City</label>
                <input id="alertCity" type="text" maxlength="100"
                       placeholder="Enter city name" required>
                <button type="submit">Search Alerts</button>
            </form>
            <p id="alertsMessage" class="muted">Enter a city to view its alerts.</p>
            <div id="alertsContainer"></div>
        </section>

        <div class="section-heading" id="profile">
            <h2>Connection Status</h2>
        </div>

        <section class="panel">
            <p id="apiStatus" class="muted">Checking protected API...</p>
        </section>

        <footer class="footer">
            ScamShield · Stay alert. Stay protected.
        </footer>
    </main>
</div>

<script>
const token = sessionStorage.getItem("scamshieldToken");
const contextPath = "${pageContext.request.contextPath}";
const apiStatus = document.getElementById("apiStatus");

function apiUrl(path) {
    return contextPath + path;
}

async function apiRequest(path, options = {}) {
    const headers = new Headers(options.headers || {});
    headers.set("Authorization", "Bearer " + token);

    if (options.body) {
        headers.set("Content-Type", "application/json");
    }

    const response = await fetch(apiUrl(path), {
        ...options,
        headers: headers
    });

    if (response.status === 401 || response.status === 403) {
        sessionStorage.removeItem("scamshieldToken");
        window.location.replace(apiUrl("/login"));
        throw new Error("Your session is invalid. Please log in again.");
    }

    if (!response.ok) {
        const message = await response.text();
        throw new Error(message || "Request failed: HTTP " + response.status);
    }

    if (response.status === 204) {
        return null;
    }

    const text = await response.text();
    if (!text) return null;

    try {
        return JSON.parse(text);
    } catch {
        return text;
    }
}

function addTextElement(parent, tag, text, className) {
    const element = document.createElement(tag);
    element.textContent = text == null || text === "" ? "Not provided" : String(text);

    if (className) {
        element.className = className;
    }

    parent.appendChild(element);
    return element;
}

function formatDate(value) {
    if (!value) return "Not available";

    const date = new Date(value);
    return Number.isNaN(date.getTime())
        ? String(value)
        : date.toLocaleString();
}

async function loadReports() {
    const message = document.getElementById("reportsMessage");
    const container = document.getElementById("reportsContainer");
    const count = document.getElementById("reportCount");

    message.textContent = "Loading your reports...";
    container.replaceChildren();

    try {
        const reports = await apiRequest("/api/reports/my");

        if (!Array.isArray(reports)) {
            throw new Error("Unexpected response from reports API.");
        }

        count.textContent = reports.length + " report(s) submitted";
        message.textContent = reports.length
            ? "Your submitted reports:"
            : "You have not submitted any reports yet.";

        reports.forEach(function(report) {
            const card = document.createElement("article");
            card.className = "card";

            addTextElement(card, "h3", report.title);
            addTextElement(card, "p", "Type: " + (report.scamType || "Not provided"));
            addTextElement(card, "p", "Status: " + (report.status || "Not available"));
            addTextElement(card, "p", "Risk level: " + (report.riskLevel || "Not available"));
            addTextElement(card, "p", "Risk score: " +
                (report.riskScore == null ? "Not available" : report.riskScore));
            addTextElement(card, "p", "City: " + (report.incidentCity || "Not provided"));
            addTextElement(card, "p", "Reported: " + formatDate(report.reportedAt));

            addTextElement(card, "p", report.description || "No description provided.");

            const actions = document.createElement("div");

            const editButton = document.createElement("button");
            editButton.type = "button";
            editButton.textContent = "Edit";
            editButton.addEventListener("click", function() {
                document.getElementById("reportTitle").value = report.title || "";
                document.getElementById("scamType").value = report.scamType || "";
                document.getElementById("reportDescription").value = report.description || "";
                document.getElementById("suspiciousPhone").value = report.suspiciousPhone || "";
                document.getElementById("incidentCity").value = report.incidentCity || "";

                document.getElementById("reportForm").dataset.editId = report.id;
                document.getElementById("submitReportButton").textContent = "Update Report";
                document.getElementById("report").scrollIntoView({ behavior: "smooth" });
            });

            const deleteButton = document.createElement("button");
            deleteButton.type = "button";
            deleteButton.textContent = "Delete";
            deleteButton.addEventListener("click", async function() {
                if (!window.confirm("Delete this report? This cannot be undone.")) {
                    return;
                }

                try {
                    await apiRequest("/api/reports/" + encodeURIComponent(report.id), {
                        method: "DELETE"
                    });
                    await loadReports();
                } catch (error) {
                    window.alert(error.message);
                }
            });

            actions.append(editButton, deleteButton);
            card.appendChild(actions);
            container.appendChild(card);
        });
    } catch (error) {
        count.textContent = "Unavailable";
        message.textContent = "Could not load reports: " + error.message;
    }
}

document.getElementById("reportForm").addEventListener("submit", async function(event) {
    event.preventDefault();

    const form = event.currentTarget;
    const message = document.getElementById("reportMessage");
    const button = document.getElementById("submitReportButton");
    const editId = form.dataset.editId;

    const report = {
        title: document.getElementById("reportTitle").value.trim(),
        scamType: document.getElementById("scamType").value,
        description: document.getElementById("reportDescription").value.trim(),
        suspiciousPhone: document.getElementById("suspiciousPhone").value.trim(),
        incidentCity: document.getElementById("incidentCity").value.trim()
    };

    message.textContent = "Submitting report...";
    button.disabled = true;

    try {
        await apiRequest(editId
            ? "/api/reports/" + encodeURIComponent(editId)
            : "/api/reports", {
            method: editId ? "PUT" : "POST",
            body: JSON.stringify(report)
        });

        message.textContent = editId
            ? "Report updated successfully."
            : "Report submitted successfully.";

        form.reset();
        delete form.dataset.editId;
        button.textContent = "Submit Report";

        await loadReports();
    } catch (error) {
        message.textContent = "Could not save report: " + error.message;
    } finally {
        button.disabled = false;
    }
});

document.getElementById("refreshReportsButton").addEventListener("click", loadReports);

document.getElementById("alertsForm").addEventListener("submit", async function(event) {
    event.preventDefault();

    const city = document.getElementById("alertCity").value.trim();
    const message = document.getElementById("alertsMessage");
    const container = document.getElementById("alertsContainer");

    container.replaceChildren();
    message.textContent = "Loading alerts...";

    try {
        const alerts = await apiRequest("/api/alerts?city=" + encodeURIComponent(city));

        if (!Array.isArray(alerts)) {
            throw new Error("Unexpected response from alerts API.");
        }

        message.textContent = alerts.length
            ? "Found " + alerts.length + " alert(s) for " + city + "."
            : "No alerts found for " + city + ".";

        alerts.forEach(function(alert) {
            const card = document.createElement("article");
            card.className = "card";

            addTextElement(card, "h3", alert.scamType || "Scam Alert");
            addTextElement(card, "p", "City: " + (alert.city || city));
            addTextElement(card, "p", "Verified reports: " + alert.verifiedReports);
            addTextElement(card, "p", alert.message || "No additional details.");

            container.appendChild(card);
        });
    } catch (error) {
        message.textContent = "Could not load alerts: " + error.message;
    }
});

document.getElementById("logoutLink").addEventListener("click", function(event) {
    event.preventDefault();
    sessionStorage.removeItem("scamshieldToken");
    window.location.replace(apiUrl("/login"));
});

if (!token) {
    window.location.replace(apiUrl("/login"));
} else {
    apiRequest("/api/test/protected")
        .then(function() {
            apiStatus.textContent = "Protected API connected successfully.";
        })
        .catch(function(error) {
            apiStatus.textContent = "Connection check failed: " + error.message;
        });

    loadReports();
}
</script>
</body>
</html>
