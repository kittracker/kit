export default async ({ id }) => {
    const response = await fetch(`/api/issues/${id}`);
    if (!response.ok) {
        return `
            <div class="text-center">
                <h2>Issue Not Found</h2>
            </div>
        `;
    }

    const issue = await response.json();

    // Function to get badge color for status
    const getStatusBadge = (status) => {
        const statusMap = {
            "OPEN": "success",
            "CLOSED": "danger",
            "IN_PROGRESS": "warning"
        };
        return `<span class="badge rounded-pill bg-${statusMap[status] || "secondary"}">${status}</span>`;
    };

    return `
        <div class="container-fluid">
            <div class="row border-bottom m-3">
                <h1>${issue.title} #${issue.id}</h1>
                <h3>${getStatusBadge(issue.status)}</h3>
                <p class="m-2">${issue.description}</p>
                <p>@${issue.createdBy.username}</p>
            </div>
            
            <br />
            
            <div class="row m-3">
                <div class="col-8">
                    ${issue.comments.length > 0 ? `
                            ${issue.comments.map(comment => `
                                <div class="card mb-3">
                                    <div class="card-header">
                                        <strong>@${comment.author.username}</strong>
                                    </div>
                                    <div class="card-body">
                                        <p class="card-text">${comment.text}</p>
                                    </div>
                                </div>
                            `).join("")}
                    ` : `<div class="text-center"> <b>No Comments Yet</b> </div>` }
                </div>
                
                <div class="col-4">
                    <a class="d-flex align-items-center flex-shrink-0 p-3 link-body-emphasis text-decoration-none border-bottom">
                        <span class="fs-5 fw-semibold">Related Issues</span>
                    </a>
                    <div class="list-group list-group-flush border-bottom scrollarea">
                        ${issue.links.map(link => `
                            <a href="/issues/${link.id}" class="list-group-item list-group-item-action py-3 lh-sm" data-link>
                                <div class="col-10 mb-1 small"><b>#${link.id} ${link.title}</b></div>
                            </a>
                        `).join("")}
                    </div>
                </div>
            </div>
        </div>
    `;

}
