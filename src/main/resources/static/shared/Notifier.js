export default class Notifier {
    static Icon = {
        Information: `<i class="bi bi-info-square-fill text-primary me-2"></i>`,
        Success: `<i class="bi bi-check-circle-fill text-success me-2"></i>`,
        Warning: `<i class="bi bi-exclamation-triangle-fill text-warning me-2"></i>`,
        Danger: `<i class="bi bi-exclamation-octagon-fill text-danger me-2"></i>`
    }

    static send(title, body, icon, options = {}) {
        if (!this.notificationTray) {
            console.error("Notifier: notificationTray was not attached.");
            return;
        }

        const temp = document.createElement('div');
        temp.innerHTML = `
            <div class="toast p-2" role="alert" aria-live="assertive" aria-atomic="true">
                <div class="toast-header">
                    ${icon}
                    <strong class="me-auto p-1 fg-dark">${title}</strong>
                    <button type="button" class="btn-close" data-bs-dismiss="toast" aria-label="Close"></button>
                </div>
                <div class="toast-body">
                    ${body}
                </div>
            </div>
        `;

        if (icon === this.Icon.Danger) {
            temp.firstElementChild.classList.add("kit_toast_err");
        } else {
            temp.firstElementChild.classList.add("kit_toast");
        }

        const toast = temp.firstElementChild;
        this.notificationTray.appendChild(toast);

        const bsToast = new bootstrap.Toast(toast, options);
        bsToast.show();

        toast.addEventListener('hidden.bs.toast', () => {
            toast.remove();
        });
    }

    static info(title, body) {
        this.send(title, body, this.Icon.Information);
    }

    static success(title, body) {
        this.send(title, body, this.Icon.Success);
    }

    static warning(title, body) {
        this.send(title, body, this.Icon.Warning);
    }

    static danger(title, body) {
        this.send(title, body, this.Icon.Danger);
    }

    static attach(notificationTray) {
        this.notificationTray = notificationTray;
    }
}
