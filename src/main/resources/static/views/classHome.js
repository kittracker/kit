import { init, resizeCanvas, destroy } from "../shared/canvas.js";
import NavbarManager from "../shared/NavbarManager.js";
import ModalBuilder from "../shared/ModalBuilder.js";
import Notifier from "../shared/Notifier.js";
import Auth from "../shared/Auth.js";

export default class Home {
    constructor() {
        this.loginModal = ModalBuilder.newModalWithTitleAndBody("loginModal", "Sign In", `
            <div class="d-flex flex-column gap-5 p-3">
                <div>
                    <p>Username</p>
                    <input type="text" class="form-control search-bar" id="login-username-input" placeholder="Username" aria-label="Username" aria-describedby="username">
                </div>
                <div>
                    <p>Password</p>
                    <input type="password" class="form-control search-bar" id="login-password-input" placeholder="Password">
                </div>
            </div>
        `);

        this.registerModal = ModalBuilder.newModalWithTitleAndBody("registerModal", "Sign Up", `
            <div class="d-flex flex-column gap-5 p-3">
                <div>
                    <p>Email</p>
                    <input type="text" class="form-control search-bar" id="register-email-input" placeholder="Email" aria-label="Email" aria-describedby="email">
                </div>
                <div>
                    <p>Username</p>
                    <input type="text" class="form-control search-bar" id="register-username-input" placeholder="Username" aria-label="Username" aria-describedby="username">
                </div>
                <div>
                    <p>Password</p>
                    <input type="password" class="form-control search-bar" id="register-password-input" placeholder="Password">
                </div>
            </div>
        `);

        this.container = document.createElement("div");
    }

    async login(e) {
        e.preventDefault();

        const username_input = document.getElementById("login-username-input");
        const username = username_input.value;

        const password_input = document.getElementById("login-password-input");
        const password = password_input.value;

        await fetch("/login", {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                "username": username,
                "password": password
            })
        }).then(async (res) => {
            if (res.ok) {
                location.pathname = "/projects";
            } else {
                Notifier.danger("Sign In", "Invalid username or password");
            }
        });
    }

    async register(e) {
        e.preventDefault();

        const email_input = document.getElementById("register-email-input");
        const email = email_input.value;

        const username_input = document.getElementById("register-username-input");
        const username = username_input.value;

        const password_input = document.getElementById("register-password-input");
        const password = password_input.value;

        await fetch("/register", {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                "email": email,
                "username": username,
                "password": password
            })
        }).then(async (res) => {
            if (res.ok) {
                location.pathname = "/projects";
            } else {
                Notifier.danger("Sign Up", "Registration failed, please try again");
            }
        });
    }

    render() {
        this.container.innerHTML = `
            <canvas id="canvas"></canvas>
    
            <section class="container-md text-center py-5 my-5">
                <img class="img-fluid" src="/shared/images/kit_logo_stripped_dark.png" alt="Kit Tracker">
                <div class="d-inline-block my-5">
                    <h1 class="px-5 py-2 slogan">TRACK - RESOLVE - EVOLVE</h1>
                </div>
            </section>
               
            <section class="container-fluid m-0 p-0 g-0 bg-light">
                <div class="row m-0 py-5 g-0">
                    <div class="col-lg-7 col-12">
                        <p class="py-5 ms-lg-5 px-lg-0 px-3">
                            This project focuses on developing an issue tracking system tailored for software projects. It
                            allows users to create and manage multiple projects, which can be set as either private or
                            public. The application runs on a centralized server managed by a single administrator.
                            Users can invite collaborators to their projects, ensuring seamless teamwork. The system
                            facilitates tracking and resolving issues by enabling users to create detailed issues with titles
                            and descriptions. Additionally, collaborators can comment on open issues to provide
                            updates, discuss solutions, or offer feedback.
                        </p>
                        <div class="d-flex gap-3 ms-lg-5 justify-content-lg-start justify-content-center">
                            <a class="btn btn-lg px-4 button" target="_blank" href="https://www.github.com/kittracker/kit">
                                <i class="bi bi-github"></i>
                                <b>GITHUB</b>
                            </a>
                            <a class="btn btn-lg px-4 button">
                                <i class="bi bi-file-earmark-arrow-down"></i>
                                <b>REPORT</b>
                            </a>
                        </div>
                    </div>
                    <div class="col-lg-5 col-12 text-end d-lg-block d-none">
                        <img class="img-fluid" src="/shared/images/kit_features.png" width="512" height="512" alt="Kit Features">
                    </div>
                </div>
            </section>
            
            <br> <br>
            
            <section class="container-fluid m-0 p-0 g-0 text-center bg-light">
                <div class="row m-0 py-5 g-0">
                    <article class="col-lg-4 col-12 my-lg-0 my-5">
                        <div class="d-flex gap-lg-0 gap-md-5 gap-0 flex-lg-column flex-md-row flex-column align-items-center justify-content-center">
                            <a target="_blank" href="https://www.github.com/spectrev333">
                                <img class="my-3 rounded-circle author-img" width="140" height="140" src="https://www.github.com/spectrev333.png" alt="Github Avatar"/>
                            </a>
                            <div class="d-flex flex-column gap-3 mt-2">
                                <h4>@spectrev333</h4>
                                <p> <i>Bessi Leonardo</i> </p>
                                <a class="btn button px-5" target="_blank" href="https://www.github.com/spectrev333">
                                    <i class="bi bi-github"></i>
                                    <b>PROFILE</b>
                                </a>
                            </div>
                        </div>
                    </article>
                    <article class="col-lg-4 col-12 my-lg-0 my-5">
                        <div class="d-flex gap-lg-0 gap-md-5 gap-0 flex-lg-column flex-md-row flex-column align-items-center justify-content-center">
                            <a target="_blank" href="https://www.github.com/mircocaneschi">
                                <img class="my-3 rounded-circle author-img" width="140" height="140" src="https://www.github.com/mircocaneschi.png" alt="Github Avatar"/>
                            </a>
                            <div class="d-flex flex-column gap-3 mt-2">
                                <h4>@mircocaneschi</h4>
                                <p> <i>Caneschi Mirco</i> </p>
                                <a class="btn button px-5" target="_blank" href="https://www.github.com/mircocaneschi">
                                    <i class="bi bi-github"></i>
                                    <b>PROFILE</b>
                                </a>
                            </div>
                        </div>
                    </article>
                    <article class="col-lg-4 col-12 my-lg-0 my-5">
                        <div class="d-flex gap-lg-0 gap-md-5 gap-0 flex-lg-column flex-md-row flex-column align-items-center justify-content-center">
                            <a target="_blank" href="https://www.github.com/cardisk">
                                <img class="my-3 rounded-circle author-img" width="140" height="140" src="https://www.github.com/cardisk.png" alt="Github Avatar"/>
                            </a>
                            <div class="d-flex flex-column gap-3 mt-2">
                                <h4>@cardisk</h4>
                                <p> <i>Cardinaletti Matteo</i> </p>
                                <a class="btn button px-5" target="_blank" href="https://www.github.com/cardisk">
                                    <i class="bi bi-github"></i>
                                    <b>PROFILE</b>
                                </a>
                            </div>
                        </div>
                    </article>
                </div>
            </section>
            
            <br> <br>
    
        `;
    }

    configure() {
        if (!Auth.isLoggedIn()) {
            const signIn = document.getElementById("signIn");
            signIn.onclick = () => { this.loginModal.show(); };
            const signInMobile = document.getElementById("signIn-mobile");
            signInMobile.onclick = () => { this.loginModal.show(); };

            const loginFooter = document.getElementById("loginModal-footer");
            loginFooter.onsubmit = async (e) => this.login(e);

            const signUp = document.getElementById("signUp");
            signUp.onclick = () => { this.registerModal.show(); };
            const signUpMobile = document.getElementById("signUp-mobile");
            signUpMobile.onclick = () => { this.loginModal.show(); };

            const registerFooter = document.getElementById("registerModal-footer");
            registerFooter.onsubmit = async (e) => this.register(e);
        }
    }

    async mount(root) {
        root.innerHTML = "";
        root.appendChild(this.container);
        NavbarManager.loadCommonButtons();
        this.configure();
        this.render();
        setTimeout(() => { init(); }, 0);
        this._resizeHandler = () => resizeCanvas();
        window.addEventListener("resize", this._resizeHandler);
    }

    unmount() {
        ModalBuilder.dispose(this.loginModal);
        ModalBuilder.dispose(this.registerModal);

        NavbarManager.unloadCommonButtons();

        destroy();
        window.removeEventListener("resize", this._resizeHandler);
    }
}