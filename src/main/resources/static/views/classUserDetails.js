import Notifier from "../shared/Notifier.js";
import ModalBuilder from "../shared/ModalBuilder.js";
import NavbarManager from "../shared/NavbarManager.js";

export default class UserDetails {
    constructor(username) {
        this.username = username;
        this.container = document.createElement("div");
    }

    async update() {
        const response = await fetch(`/api/users/${this.username}`);
        if (response.ok) {
            this.user = await response.json();
            console.log(this.user);
        }
    }

    async fetchUser() {
        await this.update();
        this.render()
    }

    render() {
        this.container.innerHTML =  `
            <div class="container mt-5">
                <div class="border rounded p-4">
                    <h2>@${this.user.username}</h2>
                    <br />
                    <p>${this.user.emailAddress || "N/A"}</p>
                </div>
            </div>
        `;

    }

    unmount() {
        NavbarManager.unloadCommonButtons();
    }

    async mount(root) {
        root.innerHTML = ""; // Clear previous content
        root.appendChild(this.container);
        NavbarManager.loadCommonButtons();
        await this.fetchUser(); // Fetch issue data and render
    }
}
