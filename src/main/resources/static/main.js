import home from "./views/home.js";
import users from "./views/users.js"
import userDetails from "./views/userDetails.js";
import projects from "./views/projects.js";
import projectDetails from "./views/projectDetails.js";
import IssueDetails from "./views/classIssueDetails.js";
import Projects from "./views/classProjects.js";
import Home from "./views/classHome.js"
import Notifier from "./shared/Notifier.js"

let placeholder = () => `
    <h1>This page is under construction</h1>
`

const routes = {
    //"/": { title: "Home", render: home },
    "/": { title: "Home", component: (_) => new Home() },
    // "/projects": { title: "Projects", render: projects },
    "/projects": { title: "Project Details", component: (_) => new Projects() },
    "/projects/:id": { title: "Project Details", render: projectDetails },
    "/issues/:id": { title: "Issue Details", component: (params) => new IssueDetails(params.id) },
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

let currentlyLoadedComponent = null;

async function router() {
    const path = location.pathname;
    const matched = matchRoute(path);

    if (matched) {
        document.title = matched.route.title;
        // Show loading state
        app.innerHTML = `
            <div class="text-center">
                <div class="spinner-border" role="status"></div>
            </div>
        `;
        if (currentlyLoadedComponent && "unmount" in currentlyLoadedComponent) {
            currentlyLoadedComponent.unmount();
            currentlyLoadedComponent = null;
        }
        if ("component" in matched.route) {
            currentlyLoadedComponent = matched.route.component(matched.params) // calls constructor
            currentlyLoadedComponent.mount(app);
        } else {
            app.innerHTML = await matched.route.render(matched.params); // Pass params to render
        }
    } else {
        app.innerHTML = `
            <div class="text-center">
                <h2>404 - Page Not Found</h2>
            </div>
        `;
    }
}

// Handle SPA navigation
window.addEventListener("click", async e => {
    const link = e.target.closest("[data-link]");
    if (link) {
        e.preventDefault();
        history.pushState({}, "", link.getAttribute("href"));
        await router();
    }
});

// Handle browser navigation
window.addEventListener("popstate", router);
window.addEventListener("DOMContentLoaded", () => {
    const tray = document.getElementById("notification-tray");
    Notifier.attach(tray);
    router();
});

window.addEventListener("resize", () => {
    const navActionsCollapse = document.getElementById("navActionsCollapse");
    const collapse = bootstrap.Collapse.getInstance(navActionsCollapse);

    if (collapse) collapse.hide();
});