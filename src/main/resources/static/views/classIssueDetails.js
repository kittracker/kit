import Notifier from "../shared/Notifier.js";

export default class IssueDetails {
    constructor(issueId) {
        this.issueId = issueId;
        this.issue = null;
        this.container = document.createElement("div");
    }

    async fetchIssue() {
        const response = await fetch(`/api/issues/${this.issueId}`);
        if (response.ok) {
            this.issue = await response.json();
        }
        this.render()
    }

    getStatusBadge(status) {
        const statusMap = {
            "OPEN": "success",
            "CLOSED": "danger",
            "IN_PROGRESS": "warning"
        };
        return `<span class="badge rounded-pill p-2 bg-${statusMap[status] || "secondary"}">${status}</span>`;
    };

    renderComments() {
        const commentsSection = document.getElementById("comments-section");
        commentsSection.innerHTML = this.issue.comments.map(comment => `
            <div class="card mb-3">
                <div class="card-header align-middle section-bg-2">
                    <h5><strong class="author-hover" href="/users/${comment.author.id}" data-link>@${comment.author.username}</strong></h5>
                </div>
                <div class="card-body">
                    <p class="card-text">${comment.text}</p>
                </div>
            </div>
        `).join("");
    }

    async addComment(ev) {
        ev.preventDefault();
        const commentInput = document.getElementById("comment");
        const comment = commentInput.value.trim();
        if (!comment) return;

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
                this.issue.comments.push(await res.json())
                this.renderComments();
            }
            commentInput.value = "";
        }).catch((reason) => {
            Notifier.danger("Error", reason);
        });
    }

    render() {
        // this.container.innerHTML =  `
        //     <div class="row m-0 p-0 g-0 min-vh-100">
        //
        //         <div class="row border-bottom m-3 gy-3" style="padding: 3%">
        //             <div class="d-flex justify-content-between">
        //                 <h1>${this.issue.title}</h1>
        //                 <div class="d-flex align-items-center justify-content-end">
        //                     <h1 class="text-body-tertiary">#${this.issue.id}</h1>
        //                     <h3 class="px-4 author-hover" href="/users/${this.issue.createdBy.id}" data-link>@${this.issue.createdBy.username}</h3>
        //                     <h3>${this.getStatusBadge(this.issue.status)}</h3>
        //                 </div>
        //             </div>
        //             <p>${this.issue.description}</p>
        //         </div>
        //
        //         <br />
        //
        //         <div class="row m-3">
        //             <div class="col-8">
        //                 <div id="comments-section">
        //                     ${this.issue.comments.length > 0 ? `
        //                             ${this.issue.comments.map(comment => `
        //                                 <div class="card mb-3">
        //                                     <div class="card-header align-middle section-bg-2">
        //                                         <h5><strong class="author-hover" href="/users/${comment.author.id}" data-link>@${comment.author.username}</strong></h5>
        //                                     </div>
        //                                     <div class="card-body">
        //                                         <p class="card-text">${comment.text}</p>
        //                                     </div>
        //                                 </div>
        //                             `).join("")}
        //                     ` : `<div class="text-center"> <b>No Comments Yet</b> </div>`}
        //                 </div>
        //                 <div class="col mt-3">
        //                     <div class="card mb-3">
        //                         <div class="card-body">
        //                             <form>
        //                                 <div class="mb-3">
        //                                     <label for="comment" class="form-label">Your Comment</label>
        //                                     <textarea placeholder="Comment as spectrev333" class="form-control" id="comment" name="comment" rows="4" required></textarea>
        //                                 </div>
        //                                 <button type="submit" class="btn btn-primary">Submit</button>
        //                             </form>
        //                         </div>
        //                     </div>
        //                 </div>
        //             </div>
        //
        //             <div class="col-4">
        //                 <a class="d-flex align-items-center flex-shrink-0 p-3 link-body-emphasis text-decoration-none rounded-top section-bg">
        //                     <span class="fs-5 fw-semibold">Related Issues</span>
        //                 </a>
        //                 <div class="list-group list-group-flush border-bottom scrollarea">
        //                     ${this.issue.links.map(link => `
        //                         <a href="/issues/${link.id}" class="list-group-item py-3 lh-sm list-item-action" data-link>
        //                             <div class="mb-1"><strong>#${link.id} ${link.title}</strong></div>
        //                         </a>
        //                     `).join("")}
        //                 </div>
        //             </div>
        //         </div>
        //     </div>
        // `;

        this.container.innerHTML =  `
            <section class="container-fluid m-0 px-3 g-0 d-flex align-items-center justify-content-evenly project-sticky border-bottom-primary" id="sticky-info">
                <div class="d-flex gap-3 align-items-center justify-content-center">
                    ${this.getStatusBadge(this.issue.status)}
                    <h5 class="d-block my-0 text-truncate">${this.issue.title}</h5>
                </div>
                <h5 class="my-0 text-body-tertiary">#${this.issue.id}</h5>
            </section>
        `;

        this.container.onsubmit = (e) => this.addComment(e);
    }

    async mount(root) {
        root.innerHTML = ""; // Clear previous content
        root.appendChild(this.container);
        await this.fetchIssue(); // Fetch issue data and render
    }

}