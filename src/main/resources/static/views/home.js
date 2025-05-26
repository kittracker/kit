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