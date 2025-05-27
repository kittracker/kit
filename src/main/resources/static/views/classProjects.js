const projectName = "projectName";
const projectNameFilter = "projectNameFilter";
const authorName = "authorName";
const authorNameFilter = "authorNameFilter";

const reset = "reset";

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
        let regex = new RegExp(document.getElementById(projectName).value, "i");

        this.projects = this.projects.filter(project => regex.test(project.name));
        console.log(this.projects);

        this.render();
    }

    filterProjectsByAuthor() {
        let regex = new RegExp(document.getElementById(authorName).value, "i");

        this.projects = this.projects.filter(project => regex.test(project.owner.username));
        console.log(this.projects);

        this.render();
    }

    callback(e) {
        e.preventDefault();

        if (e.submitter.id === projectNameFilter) this.filterProjectsByName();
        else if (e.submitter.id === authorNameFilter) this.filterProjectsByAuthor();
        else if (e.submitter.id === reset) this.fetchProjects();
        else console.error("Error: Unknown callback requested.");
    }

    render() {
        this.container.innerHTML =  `
            <div class="row flex-xl-row flex-column-reverse gap-xl-0 gap-4 mt-5 p-0 g-0">
                <section class="col-xl-8 col-12 d-flex flex-column gap-3 px-3 min-vh-100">
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
                        `<h5 class="text-center p-5">No projects yet</h4>`
                    }
                </section>
                <section class="col-xl-4 col-12 px-3">
                    <div class="d-flex flex-column gap-5 filter-section p-5">
                        <div>
                            <p>Filter by Project Name</p>
                            <form class="input-group">
                                <input type="text" class="form-control search-bar" id=${projectName} placeholder="Project Name" aria-label="Project Name" aria-describedby="projectNameFilter">
                                <button class="btn button" type="submit" id=${projectNameFilter}>Search</button>
                            </form> 
                        </div>
                        <div>
                            <p>Filter by Author Name</p>
                            <form class="input-group">
                                <span class="input-group-text fg-dark" id="visible-addon">@</span>
                                <input type="text" class="form-control search-bar" id=${authorName} placeholder="Author Name" aria-label="Author Name" aria-describedby="authorNameFilter">
                                <button class="btn button" type="submit" id=${authorNameFilter}>Search</button>
                            </form> 
                        </div>
                        <form>
                            <button class="container-fluid btn button" type="submit" id=${reset}>RESET</button>
                        </form>
                    </div>
                </section>
            </div>
            
            <br>
        `;

        this.container.onsubmit = (e) => this.callback(e);
    }

    mount(root) {
        root.innerHTML = ""; // Clear previous content
        root.appendChild(this.container);
        this.fetchProjects(); // Fetch issue data and render
    }
}
