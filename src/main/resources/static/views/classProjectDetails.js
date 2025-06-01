import Notifier from "../shared/Notifier.js";

export default class ProjectDetails {
    constructor(id) {
        this.id = id;
        this.project = null;
        this.container = document.createElement("div");

        const navbar = document.getElementById("navbar");

        const options = {
            root: null,
            rootMargin: `-${navbar.offsetHeight}px 0px 0px 0px`,
            threshold: [0, 1]
        };

        const observe = (entries, observer) => {
            const sticky = document.getElementById("sticky-info");
            if (!sticky) {
                console.error("Error: Could not retrieve sticky-info component.");
                return;
            }

            const navbar = document.getElementById("navbar");
            if (!navbar) {
                console.error("Error: Could not retrieve navbar component.");
                return;
            }

            entries.forEach(entry => {
                if (!entry.isIntersecting) {
                    sticky.style.top = navbar.offsetHeight.toString();

                    sticky.classList.remove("d-none");
                    sticky.classList.add("d-flex");
                } else {
                    sticky.classList.remove("d-flex");
                    sticky.classList.add("d-none");
                }
            });
        }

        this.observer = new IntersectionObserver(observe, options);
    }

    async fetchProject() {
        const response = await fetch(`/api/projects/${this.id}`);
        if (response.ok) {
            this.project = await response.json();
            console.log(this.project.collaborators);
        }
        this.render()
    }

    renderIssueStatus(status) {
        switch (status) {
            case "OPEN":
                return `<i class="bi bi-circle-fill text-success"></i>`;
            case "IN_PROGRESS":
                return `<i class="bi bi-circle-fill text-warning"></i>`;
            case "CLOSED":
                return `<i class="bi bi-slash-circle text-secondary"></i>`;
            default:
                console.error(`Error: Invalid status ${status}`);
                return "";
        }
    }

    renderIssues(element, status) {
        element.innerHTML = `
            ${this.project.issues.length > 0 ? `
                ${this.project.issues.map(issue => `
                    ${issue.status === status ? `
                        <div class="card mb-3 p-2 project-card" href="/issues/${issue.id}" data-link>
                            <div class="card-body">
                                <div class="d-flex flex-md-row gap-md-0 gap-3 flex-column justify-content-between">
                                    <div class="d-flex gap-3">
                                        ${this.renderIssueStatus(status)}
                                        <h5 class="card-title">${issue.title}</h5>
                                    </div>
                                    <h6 class="card-subtitle mb-2 text-body-tertiary">@${issue.createdBy.username}</h6>
                                </div>
                                <br />
                                <p class="card-text">${issue.description}</p>
                            </div>
                        </div>
                    `
                : ``}
                `).join("")} `
            :
            `<h5 class="text-center p-5">No projects yet</h5>`
        }   
        `;

        if (element.innerHTML.trim() === "") {
            element.innerHTML = `
                <h5 class="text-center p-5">No issues yet</h5>
            `;
        }
    }

    renderOpenIssues() {
        const openIssues = document.getElementById("pills-open");
        if (!openIssues) {
            console.error("Error: Could not retrieve pills-open component.");
            return;
        }

        this.renderIssues(openIssues, "OPEN");
    }

    renderInProgressIssues() {
        const inProgressIssues = document.getElementById("pills-in-progress");
        if (!inProgressIssues) {
            console.error("Error: Could not retrieve pills-in-progress component.");
            return;
        }

        this.renderIssues(inProgressIssues, "IN_PROGRESS");
    }

    renderClosedIssues() {
        const closedIssues = document.getElementById("pills-closed");
        if (!closedIssues) {
            console.error("Error: Could not retrieve pills-closed component.");
            return;
        }

        this.renderIssues(closedIssues, "CLOSED");
    }

    renderCollaborators() {
        const collaborators = document.getElementById("pills-collaborators");
        if (!collaborators) {
            console.error("Error: Could not retrieve pills-collaborators component.");
            return;
        }

        // collaborators.innerHTML = `
        //     ${this.project.collaborators.length > 0 ? `
        //         ${this.project.issues.map(issue => `
        //             ${issue.status === status ? `
        //                 <div class="card mb-3 p-2 project-card" href="/issues/${issue.id}" data-link>
        //                     <div class="card-body">
        //                         <div class="d-flex flex-md-row gap-md-0 gap-3 flex-column justify-content-between">
        //                             <div class="d-flex gap-3">
        //                                 ${this.renderIssueStatus(status)}
        //                                 <h5 class="card-title">${issue.title}</h5>
        //                             </div>
        //                             <h6 class="card-subtitle mb-2 text-body-tertiary">@${issue.createdBy.username}</h6>
        //                         </div>
        //                         <br />
        //                         <p class="card-text">${issue.description}</p>
        //                     </div>
        //                 </div>
        //             `
        //         : ``}
        //         `).join("")} `
        //     :
        //     `<h5 class="text-center p-5">No projects yet</h5>`
        // }
        // `;
    }

    render() {
        this.container.innerHTML =  `
            <section class="container-fluid m-0 p-0 g-0 align-items-center justify-content-center project-sticky border-bottom-primary" id="sticky-info">
                <h5 class="text-wrap text-break">${this.project.name}</h5>
            </section>
            
            <div class="m-0 g-0 pt-3 pb-1 p-0 min-vh-100">
                <div class="row m-0 p-0 g-0">
                    <div class="row m-0 p-5 g-0">
                        <h1 class="text-center text-wrap text-break"><strong id="project-title">${this.project.name}</strong></h1>
                    </div>
                    
                    <div class="row m-0 p-3 g-0">
                        <h5 class="fs-5 text-center text-wrap text-break">${this.project.description}</h5>
                    </div>
                </div>
                
                <div class="row flex-xl-row flex-column-reverse gap-xl-0 gap-4 mt-5 p-0 g-0">
                    <section class="col-xl-8 col-12 d-flex flex-column gap-3 px-3 min-vh-100" id="content-section">
                        <div class="tab-content" id="pills-tabContent">
                            <div class="tab-pane fade show active" id="pills-open" role="tabpanel" aria-labelledby="pills-open-tab" tabindex="0"></div>
                            <div class="tab-pane fade" id="pills-in-progress" role="tabpanel" aria-labelledby="pills-in-progress-tab" tabindex="0"></div>
                            <div class="tab-pane fade" id="pills-closed" role="tabpanel" aria-labelledby="pills-closed-tab" tabindex="0"></div>
                            <div class="tab-pane fade" id="pills-collaborators" role="tabpanel" aria-labelledby="pills-collaborators-tab" tabindex="0"></div>
                        </div>
                    </section>
                    <section class="col-xl-4 col-12 px-3">
                        <div class="d-flex flex-column gap-5 p-5 project-details-filters">
                            <div>
                                <ul class="nav nav-pills nav-justified" id="pills-tab" role="tablist">
                                    <li class="nav-item" role="presentation">
                                        <button class="nav-link button active" id="pills-open-tab" data-bs-toggle="pill" data-bs-target="#pills-open" type="button" role="tab" aria-controls="pills-open" aria-selected="true">OPEN</button>
                                    </li>
                                    <li class="nav-item" role="presentation">
                                        <button class="nav-link button" id="pills-in-progress-tab" data-bs-toggle="pill" data-bs-target="#pills-in-progress" type="button" role="tab" aria-controls="pills-open" aria-selected="true">IN PROGRESS</button>
                                    </li>
                                    <li class="nav-item" role="presentation">
                                        <button class="nav-link button" id="pills-closed-tab" data-bs-toggle="pill" data-bs-target="#pills-closed" type="button" role="tab" aria-controls="pills-closed" aria-selected="false">CLOSED</button>
                                    </li>
                                    <li class="nav-item" role="presentation">
                                        <button class="nav-link button" id="pills-collaborators-tab" data-bs-toggle="pill" data-bs-target="#pills-collaborators" type="button" role="tab" aria-controls="pills-collaborators" aria-selected="false">COLLABORATORS</button>
                                    </li>
                                </ul>
                            </div>
                            <div>
                                <p>Search</p>
                                <form class="input-group">
                                    <input type="text" class="form-control search-bar" placeholder="Search" aria-label="Search" aria-describedby="Search">
                                    <button class="btn button" type="submit">Search</button>
                                </form> 
                                <p class="py-3" id="activeResearch"></p>
                            </div>
                            <form>
                                <button class="container-fluid btn button" type="submit">RESET</button>
                            </form>
                        </div>
                    </section>
                </div>
            </div>
            
            <br>
        `;

        this.renderOpenIssues();
        this.renderInProgressIssues();
        this.renderClosedIssues();
        this.renderCollaborators();

        const projectTitle = document.getElementById("project-title");
        if (!projectTitle) {
            console.error("Error: Could not retrieve project-title component.");
            return;
        }

        this.observer.unobserve(projectTitle);
        this.observer.observe(projectTitle);
    }

    unmount() {
        const projectTitle = document.getElementById("project-title");
        if (!projectTitle) {
            console.error("Error: Could not retrieve project-title component.");
            return;
        }

        this.observer.unobserve(projectTitle);
        this.observer.disconnect();
    }

    mount(root) {
        root.innerHTML = ""; // Clear previous content
        root.appendChild(this.container);
        this.fetchProject(); // Fetch issue data and render
    }
}
