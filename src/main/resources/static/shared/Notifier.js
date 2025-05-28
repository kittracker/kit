export default class Notifier {
    static Icon = {
        Information: `<i class="bi bi-info-square-fill text-primary"></i>`,
        Success: `<i class="bi bi-check-circle-fill texy-success"></i>`,
        Warning: `<i class="bi bi-exclamation-triangle-fill text-warning"></i>`,
        Danger: `<i class="bi bi-exclamation-octagon-fill text-danger"></i>`
    }

    static send(title, body, icon, options = {}) {
        if (!this.notificationTray) {
            console.error("Notifier: notificationTray was not attached.");
            return;
        }
        const temp = document.createElement('div');
        temp.innerHTML = `
            <div class="toast" role="alert" aria-live="assertive" aria-atomic="true">
                <div class="toast-header">
                    ${icon}
                    <strong class="me-auto p-1">${title}</strong>
                    <button type="button" class="btn-close" data-bs-dismiss="toast" aria-label="Close"></button>
                </div>
                <div class="toast-body">
                    ${body}
                </div>
            </div>
        `;
        const toast = temp.firstElementChild;
        this.notificationTray.appendChild(toast);
        // Inizializza e mostra il toast con Bootstrap
        const bsToast = new bootstrap.Toast(toast, options);
        bsToast.show();

        // gemini ha detto di aggiungere
        toast.addEventListener('hidden.bs.toast', () => {
            toast.remove(); // Rimuove l'elemento dal DOM
        });
        // e io sono d'accordo
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
