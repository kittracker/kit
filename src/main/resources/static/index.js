window.onload = (event) => {
    homepage();
}

function homepage() {
    const callback = (data) => {
        console.log(data);
        let content = "";

        content += "<div class='standard-dialog inner-border'>";
        content += "<h1 class='heading center'>Projects</h1>";
        content += "</div>";
        content += "<br/>";

        $.each(data, (_, val) => {
            content += "<div class='standard-dialog item' onclick='project(" + val.id + ")' id='project_" + val.id + "'>";
            content += "<h4>" + val.name + "</h4>";
            content += "<div class='separator'></div>"
            content += "<p>" + val.description + "</p>";
            content += "</div>";
            content += "<br/>";
        });

        document.getElementById("page").innerHTML = content;
    }

    fetch("projects", "GET", callback);
}

function project(id) {
    const callback = (data) => {
        console.log(data);
        let content = "";

        content += "<div class='container'>";
        content += "<div class='layout'>";
        // --- main
        content += "<div class='standard-dialog'>";
        content += "<h1 class='heading center'>" + data.name + "</h1>";
        content += "<p class='desc center'>" + data.description + "</p>";
        content += "<div class='separator'></div>";
        content += "<br/>";
        $.each(data.issues, (_, val) => {
            let text = "#" + val.id + " " + val.createdBy.username + " " + val.status;

            content += "<blockquote class='item' onclick='issue(" + val.id + ")'>";
            content += "<h3>" + val.title + "</h3>";
            content += "<div class='hbox'>";
            content += "<p class='dialog-text text'>" + text + "</p>";
            content += "<div style='display: inline-flex; align-items: center; gap: 5px;'>";
            content += "<p class='dialog-text text'>" + val.comments.length + "</p>";
            content += "<svg class='img' viewBox='0 -4 16 16'>";
            content += "<path d='M1 2.75C1 1.784 1.784 1 2.75 1h10.5c.966 0 1.75.784 1.75 1.75v7.5A1.75 1.75 0 0 1 13.25 12H9.06l-2.573 2.573A1.458 1.458 0 0 1 4 13.543V12H2.75A1.75 1.75 0 0 1 1 10.25Zm1.75-.25a.25.25 0 0 0-.25.25v7.5c0 .138.112.25.25.25h2a.75.75 0 0 1 .75.75v2.19l2.72-2.72a.749.749 0 0 1 .53-.22h4.5a.25.25 0 0 0 .25-.25v-7.5a.25.25 0 0 0-.25-.25Z'></path>";
            content += "</svg>";
            content += "</div>";
            content += "</div>";
            content += "</blockquote>";
        });
        // --- main
        content += "</div>";
        // --- sidebar
        content += "<aside class='window sidebar'>";
        content += "<div class='title-bar'>";
        content += "<button aria-label='Close' class='close'></button>";
        content += "<h1 class='title'>Collaborators</h1>";
        content += "<button aria-label='Resize' class='resize'></button>";
        content += "</div>";
        content += "<div class='window-pane'>";
        content += "<ul class='menu-items'>";
        $.each(data.collaborators, (_, val) => {
            let user = val.username;
            if (val.username === data.owner.username) user += " (owner)";
            content += "<li class='listItem' onclick='user(" + val.id + ")'>" + user + "</li>";
        });
        content += "</ul>";
        content += "</div>";
        content += "</aside>";
        // --- sidebar
        content += "</div>"
        content += "</div>";

        document.getElementById("page").innerHTML = content;
    }

    fetch("projects/" + id, "GET", callback);
}

function issue(id) {
    const callback = (data) => {
        let content = "";

        content += "<div class='container'>";
        content += "<div class='layout'>";
        // --- main
        content += "<div class='standard-dialog'>";
        content += "<h1 class='heading center'>" + data.title + "</h1>";
        content += "<p class='desc center'>" + data.description + "</p>";
        content += "<p class='desc center'>" + data.status + "</p>";
        content += "<div class='separator'></div>";
        content += "<br/>";
        $.each(data.comments, (_, val) => {
            content += "<blockquote>";
            content += "<h3>@" + val.author.username + "</h3>";
            content += "<p class='dialog-text text'>" + val.text + "</p>";
            content += "</blockquote>";
        });
        // --- main
        content += "</div>";
        // --- sidebar
        content += "<aside class='window sidebar'>";
        content += "<div class='title-bar'>";
        content += "<button aria-label='Close' class='close'></button>";
        content += "<h1 class='title'>Links</h1>";
        content += "<button aria-label='Resize' class='resize'></button>";
        content += "</div>";
        content += "<div class='window-pane'>";
        content += "<ul class='menu-items'>";
        $.each(data.links, (_, val) => {
            let link = val.title + " #" + val.id;
            content += "<li class='listItem' onclick='issue(" + val.id + ")'>" + link + "</li>";
        });
        content += "</ul>";
        content += "</div>";
        content += "</aside>";
        // --- sidebar
        content += "</div>"
        content += "</div>";

        document.getElementById("page").innerHTML = content;
    };

    fetch("issues/" + id, "GET", callback);
}

function user(id) {

}

function fetch(url, method, callback) {
    jQuery.ajax({
        url: url,
        type: method,
        dataType: "json",
        async: true,
        success: (data) => { callback(data) }
    });
}