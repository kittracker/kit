export default async ({id}) => {
    const response = await fetch(`/api/projects/${id}`);
    if (!response.ok) {
        return `
            <div class="text-center">
                <h2>Project Not Found</h2>
            </div>
        `;
    }

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
        <div class="d-flex container-fluid mt-5">
            <div class="container-fluid h-100">
                <div class="text-center">
                    <h1>${project.name}</h1>
                    <br /> <br />
                    <h6>${project.description}</h6>
                </div>
                
                <br /> <br />

                ${project.issues.length > 0 ? `
                    <ul class="list-group text-center scrollarea">
                        ${project.issues.map(issue => `
                            <li class="list-group-item list-group-item-action" href="/issues/${issue.id}" data-link>
                                <h5>${issue.title}</h5>
                                <br />
                                <p>${issue.description}</p>
                                <p><i>@${issue.createdBy.username}</i> ${getStatusBadge(issue.status)} ✏️ ${issue.comments.length}</p>
                            </li>
                        `).join("")}
                    </ul>
                ` : `<div class="text-center"> <b>No Issues Yet</b> </div>` }
            </div>
            
            <div class="d-flex flex-column align-items-stretch flex-shrink-0 h-100" style="width: 380px;">
                <a class="d-flex align-items-center flex-shrink-0 p-3 link-body-emphasis text-decoration-none border-bottom">
                    <span class="fs-5 fw-semibold">Collaborators</span>
                </a>
                <div class="list-group list-group-flush border-bottom scrollarea">
                    ${project.collaborators.map(user => `
                        <a class="list-group-item list-group-item-action py-3 lh-sm">
                            <div class="d-flex w-100 align-items-center justify-content-between">
                                <strong class="mb-1">${user.username} ${project.owner.username === user.username ? "(owner)" : ""}</strong>
                            </div>
                            <div class="col-10 mb-1 small">${user.emailAddress}</div>
                        </a>
                    `).join("")}
                </div>
            </div>
        </div>
    `;
}
