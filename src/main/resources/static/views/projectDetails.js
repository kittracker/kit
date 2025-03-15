// <li className="list-group-item d-flex justify-content-between align-items-center list-group-item-action"
//     href="/issues/${issue.id}" data-link aria-current="true">
//     <h5>${getStatusIcon(issue.status)} <strong>${issue.title}</strong></h5>
//     <p>${issue.description}</p>
//     <span className="badge text-bg-primary rounded-pill">${issue.comments.length}</span>
// </li>

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
    const getStatusIcon = (status) => {
        const statusMap = {
            "OPEN": "<i class=\"bi bi-record-circle text-success\"></i>",
            "CLOSED": "<i class=\"bi bi-check-circle text-danger\"></i>",
            "IN_PROGRESS": "<i class=\"bi bi-fast-forward-circle text-warning\"></i>"
        };
        return statusMap[status];
    }

    return `
        <div class="container h-100">
        
            <div class="row border-bottom m-3 gy-3" style="padding: 3%">
                <h1>${project.name}</h1>
                <p>${project.description}</p>
            </div>
            
            <div class="row m-3">
                <div class="col-8">
                ${project.issues.length > 0 ? `
                    <ul class="list-group scrollarea">
                        ${project.issues.map(issue => `
                            <li class="list-group-item list-item-action" href="/issues/${issue.id}" data-link>
                                <br />
                                <div class="d-flex justify-content-between">
                                    <h4>${getStatusIcon(issue.status)}</h4>
                                    <div class="container">
                                        <h5>${issue.title}</h5>
                                        <p>${issue.description}</p>
                                    </div>
                                    <span class="badge text-bg-primary rounded-pill align-content-center">${issue.comments.length}</span>
                                </div>
                                <br />
                            </li>
                        `).join("")}
                    </ul>
                ` : `<div class="text-center"> <b>No Issues Yet</b> </div>`}
                </div> 
            
                <div class="col-4">
                    <a class="d-flex align-items-center flex-shrink-0 p-3 link-body-emphasis text-decoration-none rounded-top section-bg">
                        <span class="fs-5 fw-semibold">Collaborators</span>
                    </a>
                    <div class="list-group list-group-flush border-bottom scrollarea">
                        ${project.collaborators.map(user => `
                            <a class="list-group-item py-3 lh-sm list-item-action" href="/users/${user.id}" data-link>
                                <div class="d-flex w-100 align-items-center justify-content-between text-center">
                                    <strong class="mb-1">${user.username} ${project.owner.username === user.username ? "(owner)" : ""}</strong>
                                </div>
                                <div class="mb-1 small">${user.emailAddress}</div>
                            </a>
                        `).join("")}
                    </div>
                </div>
            </div>
        </div>
    `;
}
