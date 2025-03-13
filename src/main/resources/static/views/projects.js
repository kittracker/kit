export default async ()=> {
    const response = await fetch("/api/projects", { method: "GET" });
    const projects = await response.json();

    return `
        <div class="container-fluid mt-5">
            <ul class="list-group">
                ${projects.map(project => `
                    <li class="list-group-item">
                        <h4>${project.name}</h4>
                        <br />
                        <p>${project.description}</p>
                        <br />
                        <a href="/projects/${project.id}" data-link class="btn btn-primary">View Details</a>
                    </li>
                `).join("")}
            </ul>
        </div>
    `;
}
