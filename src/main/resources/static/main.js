import home from "./views/home.js";
import users from "./views/users.js"
import userDetails from "./views/userDetails.js";
import projects from "./views/projects.js";
import projectDetails from "./views/projectDetails.js";
import issueDetails from "./views/issueDetails.js";

let placeholder = () => `
    <h1>This page is under construction</h1>
`

const routes = {
    "/": { title: "Home", render: home },
    "/projects": { title: "Projects", render: projects },
    "/projects/:id": { title: "Project Details", render: projectDetails },
    "/issues/:id": { title: "Issue Details", render: issueDetails },
    "/users": { title: "Users", render: users },
    "/users/:id": { title: "User Details", render: userDetails },  // Dynamic ID
};

const app = document.getElementById("app");

function matchRoute(path) {
    for (const route in routes) {
        // Convert route pattern "/users/:id" to regex "/users/(\d+)"
        const pattern = "^" + route.replace(/:\w+/g, "([^/]+)") + "$";
        const regex = new RegExp(pattern);
        const match = path.match(regex);

        if (match) {
            const paramValues = match.slice(1); // Extract matched values
            const paramKeys = (route.match(/:\w+/g) || []).map(k => k.substring(1)); // Extract keys

            const params = Object.fromEntries(paramKeys.map((key, i) => [key, paramValues[i]]));
            return { route: routes[route], params };
        }
    }
    return null;
}

async function router() {
    const path = location.pathname;
    const matched = matchRoute(path);

    if (matched) {
        document.title = matched.route.title;
        // Homepage set this class to the body, not necessary for other pages
        document.body.classList.remove("home-bg");
        // Show loading state
        app.innerHTML = `
            <div class="text-center">
                <div class="spinner-border" role="status"></div>
            </div>
        `;
        app.innerHTML = await matched.route.render(matched.params); // Pass params to render
    } else {
        app.innerHTML = `
            <div class="text-center">
                <h2>404 - Page Not Found</h2>
            </div>
        `;
    }
}

// Handle SPA navigation
window.addEventListener("click", e => {
    const link = e.target.closest("[data-link]");
    if (link) {
        e.preventDefault();
        history.pushState({}, "", link.getAttribute("href"));
        router();
    }
});

// Handle browser navigation
window.addEventListener("popstate", router);
window.addEventListener("DOMContentLoaded", router);
