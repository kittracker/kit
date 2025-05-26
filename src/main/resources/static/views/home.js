import { init } from "./../shared/canvas.js"

export default () => {
    const htmlContent = `
    <canvas id="canvas"></canvas>
    
    <section class="container-md text-center py-5 my-5">
        <img class="img-fluid" src="/shared/kit_logo_stripped_dark.png" alt="Kit Tracker">
        <div class="d-inline-block my-5">
            <h1 class="px-5 py-2 slogan">TRACK - RESOLVE - EVOLVE</h1>
        </div>
    </section>
       
    <section class="container-fluid bg-light">
        <div class="row py-5">
            <div class="col-lg-7 col-12">
                <p class="py-5 ms-lg-5 px-lg-0 px-3 font-monospace">
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
                        <b class="font-monospace">GITHUB</b>
                    </a>
                    <a class="btn btn-lg px-4 button">
                        <i class="bi bi-file-earmark-arrow-down"></i>
                        <b class="font-monospace">REPORT</b>
                    </a>
                </div>
            </div>
            <div class="col-lg-5 col-12 text-end d-lg-block d-none">
                <img class="img-fluid" src="/shared/kit_features.png" width="512" height="512" alt="Kit Features">
            </div>
        </div>
    </section>
    
    <br> <br>
    
    <div class="container-fluid text-center bg-light py-5">
        <div class="row">
            <div class="col-lg-4">
                <a target="_blank" href="https://www.github.com/cardisk">
                    <img class="profile-img my-3 rounded-circle" width="140" height="140" src="https://www.github.com/cardisk.png" alt="Github Avatar"/>
                </a>
                <h2 class="py-1">@cardisk</h2>
                <p class="py-2"> <i>Matteo Cardinaletti</i> </p>
                <p>
                    <a class="button-dark btn" target="_blank" href="https://www.github.com/cardisk">
                        <i class="bi bi-github"></i>
                        <b>PROFILE</b>
                    </a>
                </p>
            </div>
            <div class="col-lg-4">
                <a target="_blank" href="https://www.github.com/spectrev333">
                    <img class="profile-img my-3 rounded-circle" width="140" height="140" src="https://www.github.com/spectrev333.png" alt="Github Avatar"/>
                </a>
                <h2 class="py-1">@spectrev333</h2>
                <p class="py-2"> <i>Leonardo Bessi</i> </p>
                <p>
                    <a class="button-dark btn" target="_blank" href="https://www.github.com/spectrev333">
                        <i class="bi bi-github"></i>
                        <b>PROFILE</b>
                    </a>
                </p>
            </div>
            <div class="col-lg-4">
                <a target="_blank" href="https://www.github.com/mircocaneschi">
                    <img class="profile-img my-3 rounded-circle" width="140" height="140" src="https://www.github.com/mircocaneschi.png" alt="Github Avatar"/>
                </a>
                <h2 class="py-1">@mircocaneschi</h2>
                <p class="py-2"> <i>Mirco Caneschi</i> </p>
                <p>
                    <a class="button-dark btn" target="_blank" href="https://www.github.com/mircocaneschi">
                        <i class="bi bi-github"></i>
                        <b>PROFILE</b>
                    </a>
                </p>
            </div>
        </div>
    </div>
    `;

    setTimeout(() => {
        init();
    }, 0);

    return htmlContent;
}