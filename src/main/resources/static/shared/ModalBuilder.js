export default class ModalBuilder {

    static newModal(id, text, body) {
        let modal = document.createElement("div");
        modal.classList.add("modal", "fade");
        modal.id = id;

        modal.setAttribute("tabindex", "-1");
        modal.setAttribute("data-bs-backdrop", "static");

        let modal_dialog = document.createElement("div");
        modal_dialog.classList.add("modal-dialog", "modal-lg", "modal-dialog-centered");

        let modal_content = document.createElement("div");
        modal_content.classList.add("modal-content", "kit-modal");

        let modal_header = document.createElement("div");
        modal_header.classList.add("modal-header");

        let title = document.createElement("h1");
        title.classList.add("modal-title", "fs-5", "ms-3");
        title.id = id + "-title";
        title.innerHTML = text;

        let modal_body = document.createElement("div");
        modal_body.classList.add("modal-body");
        modal_body.id = id + "-body";
        modal_body.innerHTML = body;

        let modal_footer = document.createElement("form");
        modal_footer.classList.add("modal-footer", "me-3");
        modal_footer.id = id + "-footer";

        let cancel = document.createElement("button");
        cancel.classList.add("btn", "button");
        cancel.textContent = "Cancel";

        cancel.setAttribute("type", "button");
        cancel.setAttribute("data-bs-dismiss", "modal");

        let done = document.createElement("button");
        done.classList.add("btn", "button");
        done.textContent = "Done";
        done.id = id + "-done";

        done.setAttribute("type", "submit");
        done.setAttribute("data-bs-dismiss", "modal");

        modal_header.append(title);
        modal_footer.append(cancel, done);

        modal_content.append(modal_header, modal_body, modal_footer);

        modal_dialog.append(modal_content);

        modal.append(modal_dialog);

        document.body.appendChild(modal);

        return new bootstrap.Modal(`#${id}`);
    }

    static dispose(modal) {
        modal._element.remove();
        modal.dispose();
    }

    static newModalEmpty(id) {
        return this.newModal(id, "", "");
    }

    static newModalWithTitleAndBody(id, title, body) {
        return this.newModal(id, title, body);
    }
}
