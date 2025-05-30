import Notifier from "../shared/Notifier.js";

export default class ProjectDetails {
    constructor(id) {
        this.id = id;
        this.project = null;
        this.container = document.createElement("div");


        const options = {
            root: null,
            rootMargin: '0px',
            threshold: 1
        };

        const observe = (entries, observer) => {
            const sticky = document.getElementById("sticky-info");
            if (!sticky) {
                console.error("Error: Could not retrieve sticky-info component.");
                return;
            }

            entries.forEach(entry => {
                if (entry.isIntersecting) {
                    sticky.classList.add("d-none");
                } else {
                    sticky.classList.remove("d-none");
                }
            });
        }

        this.observer = new IntersectionObserver(observe, options);
    }

    async fetchProject() {
        const response = await fetch(`/api/projects/${this.id}`);
        if (response.ok) {
            this.project = await response.json();
        }
        this.render()
    }


    render() {
        this.container.innerHTML =  `
            <!-- d-flex & d-none are bootstrap classes and positioned at the end to avoid breaking behaviour with js -->
            <section class="m-0 p-0 g-0 align-items-center justify-content-center project-sticky border-bottom-primary d-flex d-none" id="sticky-info">
                <h5 class="text-wrap text-break">${this.project.name}</h5>
            </section>

            <div class="m-0 g-0 pt-3 pb-1 px-3 min-vh-100">
                <div class="row m-0 p-0 g-0">
                    <div class="row m-0 p-5 g-0">
                        <h1 class="text-center text-wrap text-break"><strong id="project-title">${this.project.name}</strong></h1>
                    </div>
                    
                    <div class="row m-0 p-3 g-0">
                        <h5 class="fs-5 text-center text-wrap text-break">${this.project.description}</h5>
                    </div>
                </div>
                
                <div class="row mt-5 p-0 g-0">
                    <section class="col-xl-8 col-12 d-flex flex-column gap-3 px-3 min-vh-100">
                    </section>
                    <section class="col-xl-4 col-12">
                        <div class="d-flex flex-column gap-5 p-5 project-details-filters">
                            <div>
                                <ul class="nav nav-pills nav-justified" id="pills-tab" role="tablist">
                                    <li class="nav-item" role="presentation">
                                        <button class="nav-link button active" id="pills-open-tab" data-bs-toggle="pill" data-bs-target="#pills-open" type="button" role="tab" aria-controls="pills-open" aria-selected="true">OPEN</button>
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
