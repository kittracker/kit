window.onload = (event) => {
    homepage();
}

function issue(id) {
    const callback = (data) => {
        document.getElementById("title-bar").innerText = data.title + " #" + data.id;
        let content = "";
        content += "<div class=\"standard-dialog center scale-down\">";
        content += "<h5 class=\"dialog-text\"><i>Description:</i> " + data.description + "</h5>";
        content += "<h5 class=\"dialog-text\"><i>Status:</i> " + data.status + "</h5>";
        content += "<h5 class=\"dialog-text\"><i>Created By:</i> " + data.createdBy.username + "</h5>";
        content += "</div><br/>";

        content += "<div class=\"standard-dialog center scale-down\">";
        content += "<h1 class=\"modal-text\">Comments</h1>";
        content += "<div class=\"separator\"></div>";
        $.each(data.comments, (_, val) => {
            content += "<h4 class=\"dialog-text\">" + val.author.username + "</h4>"
            content += "<h5 class=\"dialog-text\">" + val.text + "</h5>"
            content += "<div class=\"separator\"></div>";
        });
        content += "</div><br/>";

        content += "<div class=\"standard-dialog center scale-down\">";
        content += "<h1 class=\"modal-text\">Links</h1>";
        content += "<div class=\"separator\"></div>";
        $.each(data.links, (_, val) => {
            content += "<h4 class=\"dialog-text\">" + val.title + " #" + val.id  + "</h4>"
        });
        content += "</div><br/>";

        document.getElementById("content-list").innerHTML = content;
    };

    fetch("issues/" + id, "GET", callback);
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
            content += "<div class='standard-dialog project' onclick='project(" + val.id + ")' id='project_" + val.id + "'>";
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

        content += "<aside class='window sidebar'>";
        content += "<div class='title-bar'>";
        content += "<button aria-label='Close' class='close'></button>";
        content += "<h1 class='title'>Collaborators</h1>";
        content += "<button aria-label='Resize' class='resize'></button>";
        content += "</aside>";

        content += "<div class='standard-dialog'>";
        content += "<h1 class='heading center'>" + data.name + "</h1>"
        content += "<p class='desc center'>" + data.description + "</p>"
        content += "<div class=\"separator\"></div>";
        content += "</div>";

        content += "</div>"
        content += "</div>";

        document.getElementById("page").innerHTML = content;
    }

    fetch("projects/" + id, "GET", callback);
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