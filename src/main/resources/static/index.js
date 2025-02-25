window.onload = (event) => {
    document.getElementById("title-bar").innerText = "Projects";
    jQuery.ajax({
        url: "projects",
        type: "GET",
        dataType: "json",
        async: true,
        success: (data) => {
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
    });
}