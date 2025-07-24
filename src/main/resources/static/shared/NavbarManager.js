import Auth from "../shared/Auth.js";

export default class NavbarManager {

    static newButtonWithoutAttach(id, label) {
        let button = document.createElement("button");
        button.classList.add("btn", "px-4", "button-reverse");
        button.id = id;
        button.innerHTML = label;

        return button;
    }

    static newButton(id, label) {
        let button = NavbarManager.newButtonWithoutAttach(id, label);

        const navActions = document.getElementById("navActions");
        navActions.appendChild(button);

        let buttonMobile = button.cloneNode(true);
        buttonMobile.id = `${id}-mobile`;

        const navActionsCollapse = document.getElementById("navActionsCollapseContent");
        navActionsCollapse.appendChild(buttonMobile);

        return button;
    }

    static newLink(id, label, href) {
        let link = document.createElement("a");
        link.classList.add("btn", "px-4", "button-reverse");
        link.id = id;
        link.innerHTML = label;

        link.href = href;
        link.setAttribute("data-link", "");

        const navActions = document.getElementById("navActions");
        navActions.appendChild(link);

        let linkMobile = link.cloneNode(true);
        linkMobile.id = `${id}-mobile`;

        const navActionsCollapse = document.getElementById("navActionsCollapseContent");
        navActionsCollapse.appendChild(linkMobile);

        return link;
    }

    static newDropdown(id, label, content) {
        let dropdown = document.createElement("div");
        dropdown.classList.add("dropdown-center");
        dropdown.id = id;

        let button = NavbarManager.newButtonWithoutAttach(`${id}-button`, label);
        button.classList.add("dropdown-toggle");
        button.setAttribute("data-bs-toggle", "dropdown");
        button.setAttribute("aria-expanded", "false");

        dropdown.appendChild(button);

        let ul = document.createElement("ul");
        ul.classList.add("dropdown-menu", "text-center", "mt-2", "overflow-hidden", "borders-primary");
        ul.id = `${id}-content`;

        ul.innerHTML = content;

        dropdown.appendChild(ul);

        const navActions = document.getElementById("navActions");
        navActions.appendChild(dropdown);

        let dropdownMobile = dropdown.cloneNode(true);
        dropdownMobile.id = `${id}-mobile`;

        const navActionsCollapse = document.getElementById("navActionsCollapseContent");
        navActionsCollapse.appendChild(dropdownMobile);

        return dropdown;
    }

    static dispose(element) {
        if (!element) return;

        const mobileElement = document.getElementById(`${element.id}-mobile`);
        if (mobileElement) {
            mobileElement.remove();
        }

        element.remove();
    }

    static clear() {
        const navActions = document.getElementById("navActions");
        navActions.innerHTML = "";

        const navActionsCollapse = document.getElementById("navActionsCollapseContent");
        navActionsCollapse.innerHTML = "";
    }

    static loadCommonButtons() {
        if (!Auth.isLoggedIn()) {
            this.signin = this.newButton("signUp", "SIGN UP");
            this.signup = this.newButton("signIn", "SIGN IN");
        } else {
            this.logout = this.newButton("logout", "LOGOUT");
            this.logout.onclick = async () => {
                await Auth.logout();
                location.href = "/";
            }
            this.profile = this.newLink("profile", "PROFILE", `/users/${Auth.getCurrentUser().username}`);
            this.dashboard = this.newLink("dashboard", "DASHBOARD", `/projects`);
        }
    }

    static unloadCommonButtons() {
        this.dispose(this.signin);
        this.dispose(this.signup);
        this.dispose(this.logout);
        this.dispose(this.profile);
        this.dispose(this.dashboard);
    }
}
