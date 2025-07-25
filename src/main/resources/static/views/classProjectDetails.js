import Notifier from "../shared/Notifier.js";
import ModalBuilder from "../shared/ModalBuilder.js";
import NavbarManager from "../shared/NavbarManager.js";
import Auth from "../shared/Auth.js";

export default class ProjectDetails {
    constructor(id) {
        this.id = id;
        this.project = null;
        this.newCollaborators = [];
        this.container = document.createElement("div");

        this.isFiltered = false;

        const navbar = document.getElementById("navbar");

        const options = {
            root: null,
            rootMargin: `-${navbar.offsetHeight}px 0px 0px 0px`,
            threshold: [0, 1]
        };

        const observe = (entries, _) => {
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

        this.editModalFunc = () => this.fillModal();

        this.issueModal = ModalBuilder.newModalWithTitleAndBody("newIssueModal", "New Issue", `
            <div class="d-flex flex-column gap-5 p-3">
                <div>
                    <p>Issue Title</p>
                    <input type="text" class="form-control search-bar" id="issueTitle" placeholder="Issue title" aria-label="Issue Name" aria-describedby="issueTitle" required>
                </div>
                <div>
                    <p>Description</p>
                    <textarea class="form-control search-bar" id="issueDescription" placeholder="Use Markdown to format your description" rows="5" ></textarea>
                </div>
            </div>
        `);

        this.collaboratorsModal = ModalBuilder.newModalWithTitleAndBody("newCollaboratorsModal", "New Collaborators", `
            <div class="d-flex flex-column gap-5 p-3">
                <ul class="list-group text-center" id="usersList">
                </ul>
                <form class="input-group" id="collaboratorsForm">
                    <span class="input-group-text fg-dark" id="visible-addon">@</span>
                    <input type="text" class="form-control search-bar" id="collaborator-input" placeholder="Username" aria-label="Username">
                    <button class="btn button" type="submit">Add</button>
                </form>
            </div>
        `);

        this.editModal = ModalBuilder.newModalWithTitleAndBody("editProjectModal", "Edit Project", `
            <div class="d-flex flex-column gap-5 p-3">
                <div>
                    <p>Project Name</p>
                    <input type="text" class="form-control search-bar" id="editProjectName" placeholder="Project name" aria-label="Project name" required>
                </div>
                <select class="form-select search-bar" id="statusSelector">
                    <option value="0">IN PROGRESS</option>
                    <option value="1">ARCHIVED</option>
                </select>
                <div>
                    <p>Description</p>
                    <textarea class="form-control search-bar" id="editProjectDescription" placeholder="Project description" rows="5" ></textarea>
                </div>
            </div>
        `);
    }

    async update() {
        const response = await fetch(`/api/projects/${this.id}`);
        if (response.ok) {
            this.project = await response.json();
        }
    }

    async fetchProject() {
        await this.update();
        this.isFiltered = false;
        this.render()
    }

    search() {
        const element = document.getElementById("searchBar");

        let regex = new RegExp(element.value, "i");
        this.project.issues = this.project.issues.filter(issue => regex.test(issue.title) || regex.test(issue.createdBy.username) || regex.test(issue.description));
        this.project.collaborators = this.project.collaborators.filter(user => regex.test(user.username) || regex.test(user.emailAddress));

        const active = document.getElementById("activeResearch");
        active.textContent = `Active: ${element.value}`;
        this.isFiltered = true;

        element.value = "";

        this.renderOpenIssues();
        this.renderInProgressIssues();
        this.renderClosedIssues();
        this.renderCollaborators();
    }

    callback(e) {
        e.preventDefault();

        if (e.submitter.id === "searchFilter") this.search();
        else if (e.submitter.id === "reset") this.fetchProject();
        else console.error("Error: Unknown callback requested.");
    }

    fillModal() {
        const title = document.getElementById("editProjectName");
        title.value = this.project.name;

        const status = document.getElementById("statusSelector");
        if (this.project.archived) status.value = 1;
        else status.value = 0;

        const description = document.getElementById("editProjectDescription");
        description.value = this.project.description;
    }

    async editProject(e) {
        e.preventDefault();

        const titleItem = document.getElementById("editProjectName");
        const title = titleItem.value;

        const statusItem = document.getElementById("statusSelector");
        let archived;

        switch (statusItem.value) {
            case "0":
                archived = false;
                break;
            case "1":
                archived = true;
                break;
        }

        const descriptionItem = document.getElementById("editProjectDescription");
        const description = descriptionItem.value;

        if (title === this.project.name &&
            archived === this.project.archived &&
            description === this.project.description) {

            this.editModal.hide();
            return;
        }

        await fetch("/api/projects", {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                "id": this.project.id,
                "name": title,
                "description": description,
                "archived": archived,
                "ownerID": this.project.owner.id
            })
        }).then(async (res) => {
            if (res.ok) {
                await this.update();
                await this.render();

                Notifier.success(title, "Project edited successfully");
            }
        }).catch((reason) => {
            Notifier.danger("Error", reason);
        });

        this.editModal.hide();
    }

    async postIssue(e) {
        e.preventDefault();

        const issueTitleModal = document.getElementById("issueTitle");

        const issueTitle = issueTitleModal.value;
        const issueDescription = document.getElementById("issueDescription").value;

        if (issueTitle.length === 0) {
            issueTitleModal.focus();
            return;
        }

        await fetch("/api/issues", {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                "title": issueTitle,
                "description": issueDescription,
                "createdBy": Auth.getCurrentUser().id,
                "projectID": this.project.id,
            })
        }).then(async (res) => {
            const modalElement = document.getElementById("modal");
            const modal = bootstrap.Modal.getInstance(modalElement);
            if (modal) modal.hide();

            if (res.ok) {
                Notifier.success(issueTitle, "Issue created successfully");
            } else {
                Notifier.danger(issueTitle, await res.text());
            }

            await this.update();
            this.renderOpenIssues();
        });
    }

    async postCollaborators(e) {
        e.preventDefault();

        const input = document.getElementById("collaborator-input");

        if (this.newCollaborators.length === 0) {
            input.focus();
            return;
        }

        for (const user of this.newCollaborators) {
            if (user.id === this.project.owner.id) continue;

            try {
                const res = await fetch("/api/collaborators", {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify({
                        "userID": user.id,
                        "projectID": this.project.id,
                    })
                });

                if (res.ok) {
                    Notifier.success(user.username, "Collaborator added successfully");
                } else {
                    const errorText = await res.text();
                    Notifier.danger(user.username, errorText);
                }
            } catch (error) {
                Notifier.danger(user.username, `Request failed: ${error.message}`);
            }
        }

        await this.update();
        this.renderCollaborators();

        this.collaboratorsModal.hide();
    }

    clearNewCollaborators(_) {
        const list = document.getElementById("usersList");
        list.innerHTML = "";

        const input = document.getElementById("collaborator-input");
        input.value = "";

        this.newCollaborators.length = 0;
    }

    async selectCollaborator(e) {
        e.preventDefault();

        const input = document.getElementById("collaborator-input");
        let username = input.value;
        input.value = "";

        const response = await fetch(`/api/users/${username}`);
        if (!response.ok) {
            Notifier.danger(username, "User not found");
            return;
        }

        let user = await response.json();

        if (this.newCollaborators.includes(user)) {
            Notifier.warning(user.username, "User already selected");
            return;
        }

        this.newCollaborators.push(user);

        const list = document.getElementById("usersList");
        const item = document.createElement("li");
        item.classList.add("list-group-item", "list-group-item-action", "list-item");
        item.textContent = user.username;
        item.dataset.userID = user.id;

        item.addEventListener("click", (e) => {
            const item = e.currentTarget;
            const id = item.dataset.userID;

            this.newCollaborators = this.newCollaborators.filter(user => user.id.toString() !== id);
            item.remove();

            Notifier.info(item.textContent, "User deselected");
        });

        list.append(item);
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
            ${this.project.issues.map(issue => `
                ${issue.status === status ? `
                    <div class="card mb-3 p-2 kit-card" href="/issues/${issue.id}" data-link>
                        <div class="card-body">
                            <div class="d-flex flex-md-row gap-md-0 gap-3 flex-column justify-content-between">
                                <div class="d-flex gap-3" style="min-width: 0;">
                                    ${this.renderIssueStatus(status)}
                                    <h5 class="card-title d-block text-truncate">${issue.title}</h5>
                                </div>
                                <h6 class="card-subtitle mb-2 text-body-tertiary">@${issue.createdBy.username}</h6>
                            </div>
                            <br />
                            <p class="card-text d-block text-truncate">${issue.description}</p>
                        </div>
                    </div>
                `
            : ``}
            `).join("")}
        `;

        if (element.innerHTML.trim() === "") {
            element.innerHTML = `
                <h5 class="text-center p-5 text-secondary">No issues yet</h5>
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

        collaborators.innerHTML = `
            ${!this.isFiltered ? `
                <div class="card mb-3 p-2 kit-card" href="/users/${this.project.owner.username}" data-link>
                    <div class="card-body">
                        <div class="d-flex flex-md-row gap-md-0 gap-3 flex-column justify-content-between">
                            <div class="d-flex gap-3">
                                <i class="bi bi-star-fill"></i>
                                <h5 class="card-title d-block text-truncate">${this.project.owner.username}</h5>
                            </div>
                        </div>
                        <br/>
                        <p class="card-text d-block text-truncate">${this.project.owner.emailAddress}</p>
                    </div>
                </div>
                `
                : ``
            }
            ${this.project.collaborators.map(user => `
                <div class="card mb-3 p-2 kit-card" href="/users/${user.id}" data-link>
                    <div class="card-body">
                        <div class="d-flex flex-md-row gap-md-0 gap-3 flex-column justify-content-between">
                            <div class="d-flex gap-3">
                                <i class="bi bi-person-circle"></i>
                                <h5 class="card-title">${user.username}</h5>
                            </div>
                        </div>
                        <br />
                        <p class="card-text">${user.emailAddress}</p>
                    </div>
                </div>
            `).join("")}
        `;
    }

    render() {
        this.container.innerHTML = `
            <section class="container-fluid d-none m-0 p-0 g-0 align-items-center justify-content-${this.project.archived ? "evenly" : "center"} project-sticky border-bottom-primary" id="sticky-info">
                <h5 class="d-block px-3 text-truncate">${this.project.name}</h5>
                ${this.project.archived ? `
                    <div class="row m-0 p-2 g-0">
                        <div class="d-block text-center">
                            <div class="badge rounded-pill px-2 bg-warning">
                                <h6 class="m-0 p-0 g-0">ARCHIVED</h6>
                            </div>
                        </div>
                    </div>
                ` : ``}
            </section>
            
            <section class="m-0 g-0 pt-3 pb-1 min-vh-100">
                <div class="row m-0 p-0 g-0">
                    <div class="row m-0 p-5 g-0">
                        <h1 class="text-center text-wrap text-break"><strong id="project-title">${this.project.name}</strong></h1>
                        ${this.project.archived ? `
                            <div class="row m-0 p-3 g-0">
                                <div class="d-block text-center">
                                    <div class="badge rounded-pill px-4 bg-warning">
                                        <h5 class="m-0 p-0 g-0">ARCHIVED</h5>
                                    </div>
                                </div>
                            </div>
                        ` : ``}
                    </div>
                    
                    <div class="row m-0 p-3 g-0">
                        <h5 class="fs-5 text-center text-wrap text-break">
                            ${this.project.description.length > 0 ?
                                `${this.project.description}`
                                :
                                `<p class="text-secondary">No description provided</p>`
                            }
                        </h5>
                    </div>
                </div>
                
                <div class="row flex-xl-row flex-column-reverse gap-xl-0 gap-4 mt-5 p-0 g-0">
                    <section class="col-xl-8 col-12 d-flex flex-column gap-3 px-3 min-vh-100" id="content-section">
                        <div class="tab-content" id="pills-tabContent">
                            <div class="tab-pane show active" id="pills-open" role="tabpanel" aria-labelledby="pills-open-tab" tabindex="0"></div>
                            <div class="tab-pane" id="pills-in-progress" role="tabpanel" aria-labelledby="pills-in-progress-tab" tabindex="0"></div>
                            <div class="tab-pane" id="pills-closed" role="tabpanel" aria-labelledby="pills-closed-tab" tabindex="0"></div>
                            <div class="tab-pane" id="pills-collaborators" role="tabpanel" aria-labelledby="pills-collaborators-tab" tabindex="0"></div>
                        </div>
                    </section>
                    <section class="col-xl-4 col-12 px-3">
                        <div class="d-flex flex-column gap-5 p-5 project-details-filters">
                            <div>
                                <ul class="nav nav-pills nav-justified" id="pills-tab" role="tablist">
                                    <li class="nav-item" role="presentation">
                                        <button class="btn nav-link button active" id="pills-open-tab" data-bs-toggle="pill" data-bs-target="#pills-open" type="button" role="tab" aria-controls="pills-open" aria-selected="true">OPEN</button>
                                    </li>
                                    <li class="nav-item" role="presentation">
                                        <button class="btn nav-link button" id="pills-in-progress-tab" data-bs-toggle="pill" data-bs-target="#pills-in-progress" type="button" role="tab" aria-controls="pills-open" aria-selected="true">IN PROGRESS</button>
                                    </li>
                                    <li class="nav-item" role="presentation">
                                        <button class="btn nav-link button" id="pills-closed-tab" data-bs-toggle="pill" data-bs-target="#pills-closed" type="button" role="tab" aria-controls="pills-closed" aria-selected="false">CLOSED</button>
                                    </li>
                                    <li class="nav-item" role="presentation">
                                        <button class="btn nav-link button" id="pills-collaborators-tab" data-bs-toggle="pill" data-bs-target="#pills-collaborators" type="button" role="tab" aria-controls="pills-collaborators" aria-selected="false">COLLABORATORS</button>
                                    </li>
                                </ul>
                            </div>
                            <div>
                                <p>Search</p>
                                <form class="input-group">
                                    <input type="text" class="form-control search-bar" id="searchBar" placeholder="Search" aria-label="Search" aria-describedby="Search">
                                    <button class="btn button" type="submit" id="searchFilter">Search</button>
                                </form> 
                                <p class="py-3" id="activeResearch"></p>
                            </div>
                            <form>
                                <button class="container-fluid btn button" type="submit" id="reset">RESET</button>
                            </form>
                        </div>
                    </section>
                </div>
            </section>
            
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

    configure() {
        if (this.project.owner.id === Auth.getCurrentUser().id) {
            this.dropdown = NavbarManager.newDropdown("newDropdown", "NEW", `
                <li><button class="dropdown-item py-2 px-4 newIssue">ISSUE</button></li>
                <li><button class="dropdown-item py-2 px-4 newCollaborator">COLLABORATOR</button></li>
            `);

            this.edit = NavbarManager.newButton("editProject", "EDIT")
            this.edit.onclick = () => this.editModal.show();

            const editProjectMobile = document.getElementById("editProject-mobile");
            editProjectMobile.onclick = () => this.editModal.show();

            const newIssueButtons = document.querySelectorAll(".newIssue");
            newIssueButtons.forEach(button => { button.onclick = () => this.issueModal.show(); });

            const newCollaboratorButtons = document.querySelectorAll(".newCollaborator");
            newCollaboratorButtons.forEach(button => { button.onclick = () => this.collaboratorsModal.show(); });

            const collaboratorsForm = document.getElementById("collaboratorsForm");
            collaboratorsForm.onsubmit = (e) => this.selectCollaborator(e);

            this.collaboratorsModal._element.addEventListener("hidden.bs.modal", (e) => this.clearNewCollaborators(e));

            const collaboratorsModalFooter = document.getElementById("newCollaboratorsModal-footer");
            collaboratorsModalFooter.onsubmit = (e) => this.postCollaborators(e);

            this.editModal._element.addEventListener('show.bs.modal', this.editModalFunc);

            const editModalFooter = document.getElementById("editProjectModal-footer");
            editModalFooter.onsubmit = async (e) => this.editProject(e);

        } else {
            this.newIssue = NavbarManager.newButton("newIssue", "NEW ISSUE")
            this.newIssue.onclick = () => this.issueModal.show();

            const newIssueMobile = document.getElementById("newIssue-mobile");
            newIssueMobile.onclick = () => this.issueModal.show();
        }

        const issueModalFooter = document.getElementById("newIssueModal-footer");
        issueModalFooter.onsubmit = (e) => this.postIssue(e);

        this.container.onsubmit = (e) => this.callback(e);
    }

    unmount() {
        const projectTitle = document.getElementById("project-title");
        if (!projectTitle) {
            console.error("Error: Could not retrieve project-title component.");
            return;
        }

        this.observer.unobserve(projectTitle);
        this.observer.disconnect();

        NavbarManager.unloadCommonButtons();
        if (this.project.owner.id === Auth.getCurrentUser().id) {
            NavbarManager.dispose(this.dropdown);
            NavbarManager.dispose(this.edit);
        } else {
            NavbarManager.dispose(this.newIssue);
        }

        ModalBuilder.dispose(this.issueModal);
        ModalBuilder.dispose(this.collaboratorsModal);
        ModalBuilder.dispose(this.editModal);
    }

    async mount(root) {
        root.innerHTML = ""; // Clear previous content
        root.appendChild(this.container);
        await this.fetchProject(); // Fetch issue data and render

        NavbarManager.loadCommonButtons();
        this.configure();
    }
}
