<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
<link rel="stylesheet"
      href="${pageContext.request.contextPath}/css/style.css">
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login | ScamShield</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/style.css">
</head>

<body class="login-page">

<div class="login-container">

    <div class="brand login-brand">🛡 ScamShield</div>

    <p class="subtitle">Stay alert. Stay protected.</p>

    <form id="loginForm">

        <label for="email">Email Address</label>
        <input type="email"
               id="email"
               placeholder="Enter your email"
               autocomplete="username"
               required>

        <label for="password">Password</label>
        <input type="password"
               id="password"
               placeholder="Enter your password"
               autocomplete="current-password"
               required>

        <button type="submit" id="loginButton">Login</button>

        <div id="message" role="status" aria-live="polite"></div>

    </form>
</div>

<script>
    const loginForm = document.getElementById("loginForm");
    const message = document.getElementById("message");
    const loginButton = document.getElementById("loginButton");

    loginForm.addEventListener("submit", async function(event) {
        event.preventDefault();

        message.textContent = "";
        message.style.color = "#e5e7eb";
        loginButton.disabled = true;
        loginButton.textContent = "Logging in...";

        const email = document.getElementById("email").value.trim();
        const password = document.getElementById("password").value;

        try {
            const response = await fetch("/api/auth/login", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    email: email,
                    password: password
                })
            });

            const result = await response.text();

            if (!response.ok) {
                message.style.color = "#f87171";
                message.textContent = response.status === 401
                    ? "Invalid email or password."
                    : "Login failed. HTTP status: " + response.status;
                return;
            }

            const token = result.trim();

            if (!token) {
                message.style.color = "#f87171";
                message.textContent = "JWT token was not received.";
                return;
            }

            message.textContent = "Checking protected API...";

            const protectedResponse = await fetch("/api/test/protected", {
                method: "GET",
                headers: {
                    "Authorization": "Bearer " + token
                }
            });

            if (!protectedResponse.ok) {
                message.style.color = "#f87171";
                message.textContent =
                    "Protected API failed. Status: " +
                    protectedResponse.status;
                return;
            }

            // Keep the token for dashboard API requests in this tab.
            sessionStorage.setItem("scamshieldToken", token);

            message.style.color = "#34d399";
            message.textContent = "Login successful. Opening dashboard...";

            window.location.href = "/dashboard";

        } catch (error) {
            message.style.color = "#f87171";
            message.textContent =
                "Could not connect to the server. Check that ScamShield is running.";
        } finally {
            loginButton.disabled = false;
            loginButton.textContent = "Login";
        }
    });
</script>

</body>
</html>