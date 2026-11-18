
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Community Stories | ScamShield</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        .stories-container {
            max-width: 1200px;
            margin: 0 auto;
            padding: 24px;
        }
        .stories-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            gap: 16px;
            flex-wrap: wrap;
            margin-bottom: 24px;
        }
        .stories-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
            gap: 18px;
            margin-top: 20px;
        }
        .story-card, .story-form-card {
            padding: 22px;
            border: 1px solid #303747;
            border-radius: 14px;
            background: #171d2b;
            color: #e5e7eb;
        }
        .story-form-card {
            margin-top: 28px;
        }
        .story-card h3, .story-form-card h2 {
            margin-top: 0;
            color: #ffffff;
        }
        .story-meta {
            color: #a5afc2;
            font-size: 13px;
            margin: 8px 0;
        }
        .story-text {
            white-space: pre-wrap;
            overflow-wrap: anywhere;
            line-height: 1.6;
        }
        .story-lesson {
            margin-top: 16px;
            padding: 12px;
            border-left: 3px solid #60a5fa;
            background: #202a3b;
            border-radius: 5px;
        }
        .stories-container input,
        .stories-container select,
        .stories-container textarea {
            width: 100%;
            box-sizing: border-box;
            margin: 7px 0 15px;
            padding: 11px;
            border-radius: 7px;
            border: 1px solid #394357;
            background: #101624;
            color: #f3f4f6;
        }
        .stories-container textarea {
            min-height: 110px;
            resize: vertical;
        }
        .stories-container button,
        .stories-container .action-link {
            cursor: pointer;
            padding: 10px 16px;
            border: 0;
            border-radius: 7px;
            background: #2563eb;
            color: white;
            text-decoration: none;
            display: inline-block;
        }
        .stories-container button:disabled {
            opacity: 0.6;
            cursor: not-allowed;
        }
        .filter-row {
            display: flex;
            align-items: center;
            gap: 10px;
            flex-wrap: wrap;
        }
        .filter-row input {
            width: 220px;
            margin: 0;
        }
        .status-message {
            margin: 14px 0;
            padding: 12px;
            border-radius: 7px;
            display: none;
        }
        .empty-message {
            color: #a5afc2;
            padding: 20px 0;
        }
        .story-tag {
            display: inline-block;
            background: #26334a;
            color: #93c5fd;
            border-radius: 20px;
            padding: 5px 10px;
            font-size: 12px;
        }
        .back-link {
            color: #93c5fd;
            text-decoration: none;
        }
        @media (max-width: 600px) {
            .stories-container { padding: 14px; }
            .filter-row input { width: 100%; }
        }
    </style>
</head>
<body>
    <main class="stories-container">
        <div class="stories-header">
            <div>
                <h1>Community Scam Stories</h1>
                <p>Real experiences can help others recognize and avoid scams.</p>
            </div>
            <a class="back-link" href="/dashboard">← Back to Dashboard</a>
        </div>

        <div id="pageMessage" class="status-message" role="status"></div>

        <section>
            <div class="stories-header">
                <h2>Stories from the Community</h2>
                <div class="filter-row">
                    <input type="text" id="cityFilter"
                           placeholder="Filter by city">
                    <button type="button" id="filterButton">Search</button>
                    <button type="button" id="clearFilterButton">Show All</button>
                </div>
            </div>

            <p id="loadingMessage">Loading community stories...</p>
            <div id="storiesGrid" class="stories-grid"></div>
            <p id="emptyMessage" class="empty-message" hidden>
                No stories found. Try another city or check back later.
            </p>

            <div class="filter-row" style="margin-top: 22px;">
                <button type="button" id="previousButton">Previous</button>
                <span id="pageInfo">Page 1</span>
                <button type="button" id="nextButton">Next</button>
            </div>
        </section>

        <section class="story-form-card" id="submitStorySection">
            <h2>Share Your Experience</h2>
            <p>Share what happened and what others can learn from it.</p>

            <form id="storyForm">
                <label for="title">Story Title *</label>
                <input id="title" name="title" type="text"
                       maxlength="150" required
                       placeholder="Example: Fake UPI payment screenshot">

                <label for="scamType">Scam Type *</label>
                <select id="scamType" name="scamType" required>
                    <option value="">Select scam type</option>
                    <option value="UPI_FRAUD">UPI Fraud</option>
                    <option value="SMS_FRAUD">SMS Fraud</option>
                    <option value="PHISHING">Phishing</option>
                    <option value="JOB_FRAUD">Job Fraud</option>
                    <option value="IDENTITY_THEFT">Identity Theft</option>
                    <option value="OTHER">Other</option>
                </select>

                <label for="incidentCity">City</label>
                <input id="incidentCity" name="incidentCity"
                       type="text" maxlength="100"
                       placeholder="City where it happened">

                <label for="story">Your Story *</label>
                <textarea id="story" name="story" required
                          placeholder="Explain what happened..."
                          maxlength="5000"></textarea>

                <label for="lesson">Lesson for Others *</label>
                <textarea id="lesson" name="lesson" required
                          placeholder="What should others learn from this?"
                          maxlength="2000"></textarea>

                <label for="financialLoss">Financial Loss (optional)</label>
                <input id="financialLoss" name="financialLoss"
                       type="number" min="0" step="0.01"
                       placeholder="Amount in INR, if applicable">

                <button type="submit" id="submitStoryButton">
                    Submit Story
                </button>
            </form>
        </section>
    </main>

    <script>
        const token = sessionStorage.getItem("scamshieldToken");

        let currentPage = 0;
        const pageSize = 9;
        let totalPages = 0;

        const storiesGrid = document.getElementById("storiesGrid");
        const loadingMessage = document.getElementById("loadingMessage");
        const emptyMessage = document.getElementById("emptyMessage");
        const pageInfo = document.getElementById("pageInfo");
        const pageMessage = document.getElementById("pageMessage");

        function showMessage(message, success) {
            pageMessage.textContent = message;
            pageMessage.style.display = "block";
            pageMessage.style.background = success ? "#163b2c" : "#482329";
            pageMessage.style.color = success ? "#bbf7d0" : "#fecaca";
        }

        async function apiRequest(url, options = {}) {
            const headers = new Headers(options.headers || {});

            if (options.body) {
                headers.set("Content-Type", "application/json");
            }

            if (token) {
                headers.set("Authorization", "Bearer " + token);
            }

            const response = await fetch(url, {
                ...options,
                headers
            });

            if (response.status === 401 || response.status === 403) {
                throw new Error(
                    "Request unauthorized. Please log in again if required."
                );
            }

            return response;
        }

        function addTextElement(parent, tag, text, className) {
            const element = document.createElement(tag);
            element.textContent = text == null || text === ""
                ? "Not provided"
                : String(text);

            if (className) {
                element.className = className;
            }

            parent.appendChild(element);
            return element;
        }

        function createStoryCard(story) {
            const card = document.createElement("article");
            card.className = "story-card";

            addTextElement(card, "h3", story.title);
            addTextElement(card, "span", story.scamType, "story-tag");

            const city = story.incidentCity || "City not specified";
            addTextElement(card, "p", "City: " + city, "story-meta");

            if (story.submittedAt) {
                addTextElement(
                    card, "p",
                    "Submitted: " + story.submittedAt,
                    "story-meta"
                );
            }

            addTextElement(card, "p", story.story, "story-text");

            if (story.lesson) {
                const lesson = document.createElement("div");
                lesson.className = "story-lesson";
                addTextElement(lesson, "strong", "Lesson for others");
                addTextElement(lesson, "p", story.lesson);
                card.appendChild(lesson);
            }

            if (story.financialLoss !== null &&
                story.financialLoss !== undefined) {
                addTextElement(
                    card, "p",
                    "Reported financial loss: ₹" + story.financialLoss
                );
            }

            if (story.status) {
                addTextElement(card, "p", "Status: " + story.status,
                               "story-meta");
            }

            return card;
        }

        async function loadStories() {
            loadingMessage.hidden = false;
            loadingMessage.textContent = "Loading community stories...";
            storiesGrid.replaceChildren();
            emptyMessage.hidden = true;

            const city = document.getElementById("cityFilter").value.trim();
            const params = new URLSearchParams({
                page: String(currentPage),
                size: String(pageSize)
            });

            if (city) {
                params.set("city", city);
            }

            try {
                const response = await fetch(
                    "/api/stories?" + params.toString()
                );

                if (!response.ok) {
                    throw new Error("Could not load stories (" +
                                    response.status + ").");
                }

                const data = await response.json();
                const stories = Array.isArray(data.content)
                    ? data.content
                    : [];

                totalPages = Number(data.totalPages || 0);
                pageInfo.textContent = totalPages > 0
                    ? "Page " + (currentPage + 1) + " of " + totalPages
                    : "Page 1";

                document.getElementById("previousButton").disabled =
                    currentPage <= 0;

                document.getElementById("nextButton").disabled =
                    totalPages === 0 || currentPage >= totalPages - 1;

                stories.forEach(function(story) {
                    storiesGrid.appendChild(createStoryCard(story));
                });

                emptyMessage.hidden = stories.length !== 0;
                loadingMessage.hidden = true;

            } catch (error) {
                loadingMessage.hidden = false;
                loadingMessage.textContent = error.message;
                showMessage(error.message, false);
            }
        }

        document.getElementById("filterButton").addEventListener(
            "click", function() {
                currentPage = 0;
                loadStories();
            }
        );

        document.getElementById("clearFilterButton").addEventListener(
            "click", function() {
                document.getElementById("cityFilter").value = "";
                currentPage = 0;
                loadStories();
            }
        );

        document.getElementById("previousButton").addEventListener(
            "click", function() {
                if (currentPage > 0) {
                    currentPage--;
                    loadStories();
                }
            }
        );

        document.getElementById("nextButton").addEventListener(
            "click", function() {
                if (currentPage < totalPages - 1) {
                    currentPage++;
                    loadStories();
                }
            }
        );

        document.getElementById("storyForm").addEventListener(
            "submit", async function(event) {
                event.preventDefault();

                if (!token) {
                    showMessage(
                        "Please log in before submitting a story.",
                        false
                    );
                    return;
                }

                const financialLossValue =
                    document.getElementById("financialLoss").value;

                const payload = {
                    title: document.getElementById("title").value.trim(),
                    scamType: document.getElementById("scamType").value,
                    incidentCity:
                        document.getElementById("incidentCity").value.trim(),
                    story: document.getElementById("story").value.trim(),
                    lesson: document.getElementById("lesson").value.trim()
                };

                if (financialLossValue !== "") {
                    payload.financialLoss = Number(financialLossValue);
                }

                const submitButton =
                    document.getElementById("submitStoryButton");

                submitButton.disabled = true;

                try {
                    const response = await apiRequest("/api/stories", {
                        method: "POST",
                        body: JSON.stringify(payload)
                    });

                    const responseText = await response.text();

                    if (!response.ok) {
                        throw new Error(
                            responseText || "Story submission failed."
                        );
                    }

                    showMessage(
                        "Story submitted successfully. It may appear after moderation.",
                        true
                    );

                    this.reset();
                    currentPage = 0;
                    await loadStories();

                } catch (error) {
                    showMessage(error.message, false);
                } finally {
                    submitButton.disabled = false;
                }
            }
        );

        loadStories();
    </script>
</body>
</html>
