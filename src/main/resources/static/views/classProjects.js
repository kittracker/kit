import Notifier from "../shared/Notifier.js";

const projectName = "projectName";
const projectNameFilter = "projectNameFilter";
const activeProjectNameFilter = "activeProjectNameFilter";
const authorName = "authorName";
const authorNameFilter = "authorNameFilter";
const activeAuthorNameFilter = "activeAuthorNameFilter";

const projectNameModal = "projectNameModal";
const projectDescriptionModal = "projectDescriptionModal";

const reset = "reset";
const projectSection = "projectSection";

export default class Projects {
    constructor() {
        this.projects = null;
        this.percentage = 0; // Used to avoid project percentage recalculation twice
        this.container = document.createElement("div");
    }

    async fetchProjects() {
        const response = await fetch(`/api/projects`);
        if (response.ok) {
            this.projects = await response.json();
        }
        this.render()
    }

    renderProjects() {
        const projects = document.getElementById(projectSection);

        projects.innerHTML = `
            ${this.projects.length > 0 ? `
                ${this.projects.map(project => `
                    <div class="card mb-3 p-2 project-card" href="/projects/${project.id}" data-link>
                        <div class="card-body">
                            <div class="d-flex flex-md-row gap-md-0 gap-3 flex-column justify-content-between">
                                <h5 class="card-title">${project.name}</h5>
                                <h6 class="card-subtitle mb-2 text-body-tertiary">@${project.owner.username}</h6>
                            </div>
                            <br />
                            <p class="card-text">${project.description}</p>
                            <div class="progress" role="progressbar" aria-label="Project Completion" aria-valuenow="${this.getProjectCompletePercentage(project)}" aria-valuemin="0" aria-valuemax="100">
                                <div class="progress-bar bg-reverse" style="width: ${this.percentage}%">${this.percentage}%</div>
                            </div>
                        </div>
                    </div>
                `).join("")} `
                :
                `<h5 class="text-center p-5">No projects yet</h5>`
            }
        `;
    }

    getProjectCompletePercentage(project) {
        if (project.issues.length === 0) {
            this.percentage = 100;
        } else {
            let issuesCompleted = 0;
            for (let i in project.issues) {
                if (i.status === "CLOSED") issuesCompleted++;
            }

            this.percentage = Math.floor((issuesCompleted / project.issues.length) * 100);
        }

        return this.percentage;
    }

    filterProjectsByName() {
        const element = document.getElementById(projectName);

        let regex = new RegExp(element.value, "i");
        this.projects = this.projects.filter(project => regex.test(project.name));

        const active = document.getElementById(activeProjectNameFilter);
        active.textContent = `Active: ${element.value}`;

        element.value = "";

        this.renderProjects();
    }

    filterProjectsByAuthor() {
        const element = document.getElementById(authorName);

        let regex = new RegExp(element.value, "i");
        this.projects = this.projects.filter(project => regex.test(project.owner.username));

        const active = document.getElementById(activeAuthorNameFilter);
        active.textContent = `Active: ${element.value}`;

        element.value = "";

        this.renderProjects();
    }

    callback(e) {
        e.preventDefault();

        if (e.submitter.id === projectNameFilter) this.filterProjectsByName();
        else if (e.submitter.id === authorNameFilter) this.filterProjectsByAuthor();
        else if (e.submitter.id === reset) this.fetchProjects();
        else console.error("Error: Unknown callback requested.");
    }

    async postProject(e) {
        e.preventDefault();

        const projectNameBar = document.getElementById(projectNameModal);

        const projectName = projectNameBar.value;
        const projectDescription = document.getElementById(projectDescriptionModal).value;

        if (projectName.length === 0) {
            projectNameBar.focus();
            return;
        }

        await fetch("/api/projects", {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                "name": projectName,
                "description": projectDescription,
            })
        }).then(async (res) => {
            const modalElement = document.getElementById("modal");
            const modal = bootstrap.Modal.getInstance(modalElement);
            if (modal) modal.hide();

            if (res.ok) {
                Notifier.success(projectName, "Project created successfully");
            } else {
                Notifier.danger(projectName, await res.text());
            }

            await this.fetchProjects();
        });
    }

    render() {
        this.container.innerHTML =  `
            <div class="row flex-xl-row flex-column-reverse gap-xl-0 gap-4 mt-5 p-0 g-0">
                <section class="col-xl-8 col-12 d-flex flex-column gap-3 px-3 min-vh-100" id=${projectSection}>
                </section>
                <section class="col-xl-4 col-12 px-3">
                    <div class="d-flex flex-column gap-5 p-5 filter-section">
                        <div>
                            <p>Filter by Project Name</p>
                            <form class="input-group">
                                <input type="text" class="form-control search-bar" id=${projectName} placeholder="Project Name" aria-label="Project Name" aria-describedby="projectNameFilter">
                                <button class="btn button" type="submit" id=${projectNameFilter}>Search</button>
                            </form> 
                            <p class="py-3" id=${activeProjectNameFilter}></p>
                        </div>
                        <div>
                            <p>Filter by Author Name</p>
                            <form class="input-group">
                                <span class="input-group-text fg-dark" id="visible-addon">@</span>
                                <input type="text" class="form-control search-bar" id=${authorName} placeholder="Author Name" aria-label="Author Name" aria-describedby="authorNameFilter">
                                <button class="btn button" type="submit" id=${authorNameFilter}>Search</button>
                            </form> 
                            <p class="py-3" id=${activeAuthorNameFilter}></p>
                        </div>
                        <form>
                            <button class="container-fluid btn button" type="submit" id=${reset}>RESET</button>
                        </form>
                    </div>
                </section>
            </div>
            
            <br>
        `;

        this.renderProjects();

        const modalTitle = document.getElementById("modalTitle");
        modalTitle.textContent = "New Project";

        const modalBody = document.getElementById("modalBody");
        modalBody.innerHTML = `
            <div class="d-flex flex-column gap-5 p-3">
                <div>
                    <p>Project Name</p>
                    <input type="text" class="form-control search-bar" id=${projectNameModal} placeholder="Project Name" aria-label="Project Name" aria-describedby="projectNameModal" required>
                </div>
                <div>
                    <p>Description</p>
                    <textarea class="form-control search-bar" id=${projectDescriptionModal} placeholder="Project Description" rows="4" ></textarea>
                </div>
            </div>
        `;

        const modalFooter = document.getElementById("modalFooter");
        modalFooter.onsubmit = (e) => this.postProject(e);

        const newButton = document.getElementById("newButton");
        newButton.classList.remove("d-none");

        const newProjectCollapse = document.getElementById("newProjectCollapse");
        newProjectCollapse.classList.remove("d-none");

        this.container.onsubmit = (e) => this.callback(e);
    }

    mount(root) {
        root.innerHTML = ""; // Clear previous content
        root.appendChild(this.container);
        this.fetchProjects(); // Fetch issue data and render
    }
}
