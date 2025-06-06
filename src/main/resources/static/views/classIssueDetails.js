import Notifier from "../shared/Notifier.js";

export default class IssueDetails {
    constructor(issueId) {
        this.issueId = issueId;
        this.issue = null;
        this.container = document.createElement("div");

        this.editModalFunc = () => this.fillModal();

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
    }

    async update() {
        const response = await fetch(`/api/issues/${this.issueId}`);
        if (response.ok) {
            this.issue = await response.json();
        }
    }

    async fetchIssue() {
        await this.update();
        this.render()
    }

    getStatusBadge(status) {
        const statusMap = {
            "OPEN": "success",
            "CLOSED": "danger",
            "IN PROGRESS": "warning"
        };

        if (status === "IN_PROGRESS") status = "IN PROGRESS";

        return `<div class="badge rounded-pill p-2 bg-${statusMap[status] || "secondary"}">${status}</div>`;
    };

    renderComments() {
        const commentsSection = document.getElementById("comments-section");

        commentsSection.innerHTML = `
            ${this.issue.comments.map(comment => `
                <div class="d-flex gap-3">
                    <div class="d-flex flex-column m-0 p-0 g-0">
                        <i class="bi bi-person-circle h1"
                            data-bs-toggle="popover"
                            data-bs-placement="right"
                            data-bs-trigger="hover focus"
                            data-bs-custom-class="font-monospace user-popover"
                            data-bs-title="@${comment.author.username}"
                            data-bs-content="${comment.author.emailAddress}"
                        ></i>
                    </div>
                    <div class="card flex-grow-1 comment">
                        <div class="card-header d-flex align-items-center justify-content-start">
                            <h6 class="m-0 p-0 g-0"
                                data-bs-toggle="popover"
                                data-bs-placement="right"
                                data-bs-trigger="hover focus"
                                data-bs-custom-class="font-monospace user-popover"
                                data-bs-title="@${comment.author.username}"
                                data-bs-content="${comment.author.emailAddress}"
                            >@${comment.author.username}</h6>
                        </div>
                        <div class="card-body">
                            <github-md>${comment.text}</github-md>
                        </div>
                    </div>
                </div>
            `).join("")}
            <div class="d-flex gap-3">
                <i class="bi bi-person-circle h1"></i>
                <div class="card flex-grow-1 comment">
                    <div class="card-header">
                        <ul class="nav nav-pills" role="tablist">
                            <li class="nav-item" role="presentation">
                                <button class="btn nav-link button active" id="write-tab" data-bs-toggle="tab"
                                        data-bs-target="#write-tab-pane" type="button" role="tab"
                                        aria-controls="write-tab-pane" aria-selected="true">Write
                                </button>
                            </li>
                            <li class="nav-item" role="presentation">
                                <button class="btn nav-link button" id="preview-tab" data-bs-toggle="tab"
                                        data-bs-target="#preview-tab-pane" type="button" role="tab"
                                        aria-controls="preview-tab-pane" aria-selected="false">Preview
                                </button>
                            </li>
                        </ul>
                    </div>
                    <div class="card-body">
                        <div class="tab-content">
                            <div class="tab-pane show active" id="write-tab-pane" role="tabpanel"
                                 aria-labelledby="write-tab" tabIndex="0">
                                <textarea class="form-control search-bar" id="commentArea" aria-label="textarea"
                                          placeholder="Use Markdown to format your comment" rows="5"></textarea>
                            </div>
                            <div class="tab-pane" id="preview-tab-pane" role="tabpanel" aria-labelledby="preview-tab"
                                 tabIndex="0">...
                            </div>
                        </div>
                        <form class="d-flex align-items-center justify-content-end m-0 g-0 pt-3">
                            <button class="btn button" type="submit">Comment</button>
                        </form>
                    </div>
                </form>
            </div>
            `;

        let popoverTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="popover"]'))
        let popoverList = popoverTriggerList.map(popoverTriggerEl => {
            return new bootstrap.Popover(popoverTriggerEl)
        })

        renderMarkdown();
    }

    async addComment(ev) {
        ev.preventDefault();

        const textarea = document.getElementById("commentArea");
        const comment = textarea.value;

        const writeButton = document.getElementById("write-tab");
        const writeButtonTab = bootstrap.Tab.getOrCreateInstance(writeButton);

        if (comment.length === 0) {
            writeButtonTab.show();
            textarea.focus();
            return;
        }

        await fetch("/api/comments", {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                "author": 2,
                "text": comment,
                "issueID": this.issue.id
            })
        }).then(async (res) => {
            if (res.ok) {
                await this.update();
                this.renderComments();
            }

            textarea.value = "";
            writeButtonTab.show();
        }).catch((reason) => {
            Notifier.danger("Error", reason);
        });

        // TODO: switch the author ID when proper authentication is done
    }

    fillModal() {
        const title = document.getElementById("issueTitle");
        title.value = this.issue.title;

        const status = document.getElementById("statusSelector");
        switch (this.issue.status) {
            case "OPEN":
                status.value = "0";
                break;
            case "IN_PROGRESS":
                status.value = "1";
                break;
            case "CLOSED":
                status.value = "2";
                break;
        }

        const description = document.getElementById("issueDescription");
        description.value = this.issue.description;
    }

    async editIssue(e) {
        e.preventDefault();

        const titleItem = document.getElementById("issueTitle");
        const title = titleItem.value;

        const statusItem = document.getElementById("statusSelector");
        let status;

        switch (statusItem.value) {
            case "0":
                status = "OPEN";
                break;
            case "1":
                status = "IN_PROGRESS";
                break;
            case "2":
                status = "CLOSED";
                break;
        }

        const descriptionItem = document.getElementById("issueDescription");
        const description = descriptionItem.value;

        if (title === this.issue.title &&
            status === this.issue.status &&
            description === this.issue.description) {

            const modalElement = document.getElementById("modal");
            const modal = bootstrap.Modal.getInstance(modalElement);
            if (modal) modal.hide();
            return;
        }

        await fetch("/api/issues", {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                "id": this.issue.id,
                "title": title,
                "status": status,
                "description": description
            })
        }).then(async (res) => {
            if (res.ok) {
                await this.update();
                await this.render();

                Notifier.success(title, "Issue edited successfully");
            }
        }).catch((reason) => {
            Notifier.danger("Error", reason);
        });

        const modalElement = document.getElementById("modal");
        const modal = bootstrap.Modal.getInstance(modalElement);
        if (modal) modal.hide();
    }

    render() {
        this.container.innerHTML = `
            <section class="container-fluid d-none m-0 px-3 g-0 align-items-center justify-content-evenly project-sticky border-bottom-primary" id="sticky-info">
                <h5 class="d-block my-0 text-truncate">${this.issue.title}</h5>
                <div class="d-flex gap-3 align-items-center justify-content-center">
                    <h5 class="my-0 text-body-tertiary">#${this.issue.id}</h5>
                    ${this.getStatusBadge(this.issue.status)}
                </div>
            </section>
            
            <section class="m-0 g-0 pt-3 pb-1 min-vh-100">
                <div class="d-flex flex-column gap-4 m-0 g-0 py-5 px-3" id="issue-details">
                    <div class="d-flex align-items-center justify-content-between">
                        <h1>${this.issue.title}</h1>
                        <h3 class="p-0 m-0 g-0 text-body-tertiary">#${this.issue.id}</h3>
                    </div>
                    <h3>${this.getStatusBadge(this.issue.status)}</h3>
                </div>
                
                <div class="row flex-xl-row flex-column gap-xl-0 gap-4 mt-5 p-0 g-0">
                    <section class="col-xl-8 col-12 d-flex flex-column gap-5 px-3" id="comment-section">
                        <div class="d-flex gap-3">
                            <div class="d-flex flex-column m-0 p-0 g-0">
                                <i class="bi bi-person-circle h1"
                                    data-bs-toggle="popover"
                                    data-bs-placement="right"
                                    data-bs-trigger="hover focus"
                                    data-bs-custom-class="font-monospace user-popover"
                                    data-bs-title="@${this.issue.createdBy.username}"
                                    data-bs-content="${this.issue.createdBy.emailAddress}"
                                ></i>
                            </div>
                            <div class="card flex-grow-1 comment">
                                <div class="card-header d-flex align-items-center justify-content-between">
                                    <h6 class="m-0 p-0 g-0"
                                        data-bs-toggle="popover"
                                        data-bs-placement="right"
                                        data-bs-trigger="hover focus"
                                        data-bs-custom-class="font-monospace user-popover"
                                        data-bs-title="@${this.issue.createdBy.username}"
                                        data-bs-content="${this.issue.createdBy.emailAddress}"
                                    >@${this.issue.createdBy.username}</h6>
                                    <h6 class="m-0 p-0 g-0">AUTHOR</h6>
                                </div>
                                <div class="card-body">
                                    ${this.issue.description.length > 0 ? `
                                        <github-md>${this.issue.description}</github-md>
                                        `
                                        :
                                        `<p class="m-0 p-0 g-0 text-secondary text-center">No description provided</p>`
                                    }
                                </div>
                            </div>
                        </div>
                        <div class="d-flex flex-column gap-5 m-0 p-0 g-0" id="comments-section">
                        </div>
                    </section>
                    <section class="col-xl-4 col-12 px-3 mt-xl-0 mt-5">
                        <h3 class="m-0 pb-5 g-0 text-center">Related Issues</h3>
                        ${this.issue.links.length > 0 ? `
                            ${this.issue.links.map(link => `
                                <div class="card mb-3 p-2 kit-card" href="/issues/${link.id}" data-link>
                                    <div class="card-body">
                                        <div class="d-flex flex-md-row gap-md-0 gap-3 flex-column justify-content-between">
                                            <div class="d-flex gap-3">
                                                <i class="bi bi-link-45deg"></i>
                                                <h5 class="card-title d-block text-truncate">${link.title}</h5>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            `)}
                            `
                            :
                            `<h5 class="text-secondary text-center">No links yet</h5>`
                        }
                    </section>
                </div>
            </section>
            
            <br>
        `;

        // TODO: add popover for comment form when authentication is properly done

        this.renderComments();

        const previewButton = document.getElementById("preview-tab");
        previewButton.addEventListener("shown.bs.tab", event => {
            const textarea = document.getElementById("commentArea");
            const comment = textarea.value.trim();

            const previewPane = document.getElementById("preview-tab-pane");

            previewPane.innerHTML = `
                <github-md>${comment}</github-md>
            `;

            renderMarkdown();
        })

        const editButton = document.getElementById("editButton");
        editButton.classList.remove("d-none");

        const modalTitle = document.getElementById("modalTitle");
        modalTitle.textContent = "Edit Issue";

        const modalBody = document.getElementById("modalBody");
        modalBody.innerHTML = `
            <div class="d-flex flex-column gap-5 p-3">
                <div>
                    <p>Issue Title</p>
                    <input type="text" class="form-control search-bar" id="issueTitle" placeholder="Issue Name" aria-label="Issue Name" aria-describedby="issueTitle" required>
                </div>
                <select class="form-select search-bar" id="statusSelector">
                    <option value="0">OPEN</option>
                    <option value="1">IN PROGRESS</option>
                    <option value="2">CLOSED</option>
                </select>
                <div>
                    <p>Description</p>
                    <textarea class="form-control search-bar" id="issueDescription" placeholder="Use Markdown to format your description" rows="5" ></textarea>
                </div>
            </div>
        `;

        const modal = document.getElementById('modal');
        modal.addEventListener('show.bs.modal', this.editModalFunc);

        const modalFooter = document.getElementById("modalFooter");
        modalFooter.onsubmit = async (e) => this.editIssue(e);

        this.container.onsubmit = (e) => this.addComment(e);

        const issueDetails = document.getElementById("issue-details");
        if (!issueDetails) {
            console.error("Error: Could not retrieve issue-details component.");
            return;
        }

        this.observer.unobserve(issueDetails);
        this.observer.observe(issueDetails);
    }

    unmount() {
        const editButton = document.getElementById("editButton");
        editButton.classList.add("d-none");

        const modal = document.getElementById('modal');
        modal.removeEventListener('show.bs.modal', this.editModalFunc);

        const issueDetails = document.getElementById("issue-details");
        if (!issueDetails) {
            console.error("Error: Could not retrieve issue-details component.");
            return;
        }

        this.observer.unobserve(issueDetails);
        this.observer.disconnect();
    }

    async mount(root) {
        root.innerHTML = ""; // Clear previous content
        root.appendChild(this.container);
        await this.fetchIssue(); // Fetch issue data and render
    }

}