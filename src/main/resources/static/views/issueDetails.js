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
        return `<span class="badge bg-${statusMap[status] || "secondary"}">${status}</span>`;
    };

    return `
        <div class="d-flex container-fluid mt-5">
            <div class="container-fluid h-100">
                <div class="text-center">
                    <h1>${issue.title}</h1>
                    <br /> <br />
                    <p>${issue.description}</p>
                    <br />
                    <p>${getStatusBadge(issue.status)}</p>
                    <br />
                    <p>@${issue.createdBy.username}</p>
                </div>
                
                <br /> <br />
            
                ${issue.comments.length > 0 ? `
                    <ul class="list-group text-center scrollarea">
                        ${issue.comments.map(comment => `
                            <li class="list-group-item">
                                <strong>@${comment.author.username}</strong>
                                <br /> <br />
                                <p>${comment.text}</p>
                            </li>
                        `).join("")}
                    </ul>
                ` : `<div class="text-center"> <b>No Comments Yet</b> </div>` }
            </div>
            
            <div class="d-flex flex-column align-items-stretch flex-shrink-0 h-100" style="width: 380px;">
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
    `;
}
