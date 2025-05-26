import { init, resizeCanvas } from "./../shared/canvas.js"

export default () => {
    const htmlContent = `
    <canvas id="canvas"></canvas>
    
    <section class="container-md text-center py-5 my-5">
        <img class="img-fluid" src="/shared/kit_logo_stripped_dark.png" alt="Kit Tracker">
        <div class="d-inline-block my-5">
            <h1 class="px-5 py-2 slogan">TRACK - RESOLVE - EVOLVE</h1>
        </div>
    </section>
       
    <section class="container-fluid font-monospace bg-light">
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
                        <b>GITHUB</b>
                    </a>
                    <a class="btn btn-lg px-4 button">
                        <i class="bi bi-file-earmark-arrow-down"></i>
                        <b>REPORT</b>
                    </a>
                </div>
            </div>
            <div class="col-lg-5 col-12 text-end d-lg-block d-none">
                <img class="img-fluid" src="/shared/kit_features.png" width="512" height="512" alt="Kit Features">
            </div>
        </div>
    </section>
    
    <br> <br>
    
    <section class="container-fluid text-center font-monospace bg-light">
        <div class="row py-5">
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

    setTimeout(() => {
        init();
    }, 0);

    window.addEventListener('resize', () => {
        resizeCanvas();
    });

    return htmlContent;
}