export default async ()=> {
    const response = await fetch("/api/projects", { method: "GET" });
    const projects = await response.json();

    return `
        <div class="container">
            ${projects.map(project => `
                <div class="card mb-3 text-center project-card" href="/projects/${project.id}" data-link>
                    <div class="card-body">
                        <h5 class="card-title">${project.name}</h5>
                        <h6 class="card-subtitle mb-2 text-body-secondary">${project.owner.username}</h6>
                        <p class="card-text">${project.description}</p>
                    </div>
                </div>
            `).join("")}
        </div>
    `;
}
