import UserDetails from "./views/classUserDetails.js";
import ProjectDetails from "./views/classProjectDetails.js";
import IssueDetails from "./views/classIssueDetails.js";
import Projects from "./views/classProjects.js";
import Home from "./views/classHome.js"
import Notifier from "./shared/Notifier.js"

let placeholder = () => `
    <h1>This page is under construction</h1>
`

const routes = {
    "/": { title: "Home", component: (_) => new Home() },
    "/projects": { title: "Projects", component: (_) => new Projects() },
    "/projects/:id": { title: "Project Details", component: (params) => new ProjectDetails(params.id) },
    "/issues/:id": { title: "Issue Details", component: (params) => new IssueDetails(params.id) },
    "/users/:username": { title: "User Details", component: (params) => new UserDetails(params.username) },
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

        // If unmount needs to access components, the content is not changed yet here
        if (currentlyLoadedComponent && "unmount" in currentlyLoadedComponent) {
            currentlyLoadedComponent.unmount();
            currentlyLoadedComponent = null;
        }

        // Show loading state
        app.innerHTML = `
            <div class="text-center pt-5 min-vh-100">
                <div class="spinner-border" role="status"></div>
            </div>
        `;

        if ("component" in matched.route) {
            currentlyLoadedComponent = matched.route.component(matched.params) // calls constructor
            await currentlyLoadedComponent.mount(app);
        } else {
            app.innerHTML = await matched.route.render(matched.params); // Pass params to render
        }

        requestAnimationFrame(() => {
            window.scrollTo(0, 0);
        });
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