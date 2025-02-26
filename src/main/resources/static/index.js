window.onload = (event) => {
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

function projects() {
    document.getElementById("title-bar").innerText = "Projects";
    const callback = (data) => {
        console.log(data);
        let list = "";
        $.each(data, (_, val) => {
            list += "<div class=\"standard-dialog center scale-down\">";
            list += "<h1 class=\"dialog-text\">" + val.name + "</h1>";
            list += "<div class=\"separator\"></div>";
            list += "<h5 class=\"dialog-text\"><i>Owner:</i> " + val.owner.username + "</h5>";
            list += "<h5 class=\"dialog-text\"><i>ID:</i> " + val.id + "</h5>";
            list += "<h5 class=\"dialog-text\"><i>Description:</i> " + val.description + "</h5>";
            list += "<h5 class=\"dialog-text\"><i>Archived:</i> " + val.archived + "</h5>";
            list += "</div><br/>"
        });
        document.getElementById("content-list").innerHTML = list;
    }

    fetch("projects", "GET", callback);
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