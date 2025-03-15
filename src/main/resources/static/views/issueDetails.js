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
        <div class="container h-100">
        
            <div class="row border-bottom m-3 gy-3" style="padding: 3%">
                <div class="d-flex justify-content-between">
                    <h1>${issue.title}</h1>
                    <div class="d-flex align-items-center justify-content-end">
                        <h1 class="text-body-tertiary">#${issue.id}</h1>
                        <h3 class="px-4 author-hover" href="/users/${issue.createdBy.id}" data-link>@${issue.createdBy.username}</h3>
                        <h3>${getStatusBadge(issue.status)}</h3>
                    </div>
                </div>
                <p>${issue.description}</p>
            </div>
            
            <br />
            
            <div class="row m-3">
                <div class="col-8">
                    ${issue.comments.length > 0 ? `
                            ${issue.comments.map(comment => `
                                <div class="card mb-3">
                                    <div class="card-header align-middle section-bg-2">
                                        <h5><strong class="author-hover" href="/users/${comment.author.id}" data-link>@${comment.author.username}</strong></h5>
                                    </div>
                                    <div class="card-body">
                                        <p class="card-text">${comment.text}</p>
                                    </div>
                                </div>
                            `).join("")}
                    ` : `<div class="text-center"> <b>No Comments Yet</b> </div>` }
                </div>
                
                <div class="col-4">
                    <a class="d-flex align-items-center flex-shrink-0 p-3 link-body-emphasis text-decoration-none rounded-top section-bg">
                        <span class="fs-5 fw-semibold">Related Issues</span>
                    </a>
                    <div class="list-group list-group-flush border-bottom scrollarea">
                        ${issue.links.map(link => `
                            <a href="/issues/${link.id}" class="list-group-item py-3 lh-sm list-item-action" data-link>
                                <div class="mb-1"><strong>#${link.id} ${link.title}</strong></div>
                            </a>
                        `).join("")}
                    </div>
                </div>
            </div>
        </div>
    `;

}
