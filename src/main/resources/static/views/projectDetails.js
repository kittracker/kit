export default async ({ id })=> {
    const response = await fetch(`/projects/${id}`);
    if (!response.ok) return `<h2>Project not found</h2>`;

    const project = await response.json();

    // Function to get badge class based on status
    const getStatusBadge = (status) => {
        const statusMap = {
            "OPEN": "success",
            "CLOSED": "danger",
            "IN_PROGRESS": "warning"
        };
        return `<span class="badge bg-${statusMap[status] || "secondary"}">${status}</span>`;
    };

    return `
        <div class="container mt-5">
            <h1>${project.name}</h1>
            <p>${project.description}</p>

            <h4>Owner</h4>
            <p>${project.owner.username} (${project.owner.emailAddress})</p>

            <h4>Collaborators</h4>
            <ul class="list-group">
                ${project.collaborators.map(user => `
                    <li class="list-group-item">${user.username} (${user.emailAddress})</li>
                `).join("")}
            </ul>

            <h4 class="mt-4">Issues</h4>
            ${project.issues.length > 0 ? `
                <ul class="list-group">
                    ${project.issues.map(issue => `
                        <li class="list-group-item">
                            <h5><a href="/issues/${issue.id}" data-link>${issue.title}</a></h5>
                            <p>${issue.description}</p>
                            <p><strong>Status:</strong> ${getStatusBadge(issue.status)}</p>
                            <p><strong>Created by:</strong> ${issue.createdBy.username} (${issue.createdBy.emailAddress})</p>
                            
                            <h6>Comments: <strong>${issue.comments.length}</strong></h6>
                        </li>
                    `).join("")}
                </ul>
            ` : `<p>No issues found.</p>`}

            <a href="/projects" data-link class="btn btn-secondary mt-3">Back to Projects</a>
        </div>
    `;
}
