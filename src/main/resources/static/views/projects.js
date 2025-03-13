export default async ()=> {
    const response = await fetch("/projects", { method: "GET" });
    const projects = await response.json();

    return `
        <div class="container mt-5">
            <h1 class="mb-4">Projects</h1>
            <ul class="list-group">
                ${projects.map(project => `
                    <li class="list-group-item">
                        <h5>${project.name}</h5>
                        <p>${project.description}</p>
                        <a href="/projects/${project.id}" data-link class="btn btn-primary">View Details</a>
                    </li>
                `).join("")}
            </ul>
        </div>
    `;
}
