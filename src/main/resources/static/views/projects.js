export default async ()=> {
    const response = await fetch("/api/projects", { method: "GET" });
    const projects = await response.json();

    return `
        <div class="container">
            <br />
            <h1 class="mb-4">PROJECTS</h1>
            <br />
            ${projects.map(project => `
                <div class="card mb-3 list-card" href="/projects/${project.id}" data-link>
                    <div class="card-body">
                        <div class="d-flex justify-content-between">
                            <h5 class="card-title">${project.name}</h5>
                            <h6 class="card-subtitle mb-2 text-body-tertiary">@${project.owner.username}</h6>
                        </div>
                        <br />
                        <p class="card-text">${project.description}</p>
                    </div>
                </div>
            `).join("")}
        </div>
    `;
}
