export default async ({ id }) => {
    const response = await fetch(`/issues/${id}`);
    if (!response.ok) return `<h2>Issue not found</h2>`;

    const issue = await response.json();

    // Function to get badge color for status
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
            <h1>${issue.title}</h1>
            <p>${issue.description}</p>
            
            <p><strong>Status:</strong> ${getStatusBadge(issue.status)}</p>
            <p><strong>Created by:</strong> ${issue.createdBy.username} (${issue.createdBy.emailAddress})</p>

            <h4 class="mt-4">Comments</h4>
            ${issue.comments.length > 0 ? `
                <ul class="list-group">
                    ${issue.comments.map(comment => `
                        <li class="list-group-item">
                            <strong>${comment.author.username}:</strong> ${comment.text}
                        </li>
                    `).join("")}
                </ul>
            ` : `<p>No comments yet.</p>`}

            <h4 class="mt-4">Related Issues</h4>
            ${issue.links.length > 0 ? `
                <ul class="list-group">
                    ${issue.links.map(link => `
                        <li class="list-group-item"><a href="/issues/${link.id}" data-link>#${link.id} ${link.title}</a></li>
                    `).join("")}
                </ul>
            ` : `<p>No links available.</p>`}

            <a href="/projects" data-link class="btn btn-secondary mt-3">Back to Projects</a>
        </div>
    `;
}
