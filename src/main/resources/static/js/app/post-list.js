function init() {
    $.ajax({
        type: 'GET',
        url: '/api/v1/posts/post-list'
    }).done((data) => {
        displayPostTable(data);
    }).fail((error) => {
        alert(error.responseJSON.message);
        return false;
    });
}

function displayPostTable(data) {
    const container = $("#post-list_container");

    $.each(data, function(index, post) {
        const row = $("<tr>");

        $("<td>").text(post.id).appendTo(row);
        $("<td>").append($("<a>").attr("href", "/posts/find/" + post.id).text(post.title)).appendTo(row);
        $("<td>").text(post.viewCount).appendTo(row);
        $("<td>").text(post.createdDate).appendTo(row);

        row.appendTo(container.find("tbody"));
    });
}

$('#more-view').on('click', () => {
    const container = $("#post-list_container");
    const tbodyChildLength = container.find("tbody").children().length;

    const tbodyLastChild = container.find("tbody").children()[tbodyChildLength - 1];
    const lastPostId = tbodyLastChild.querySelectorAll("td")[0].textContent;

    $.ajax({
        type: 'GET',
        url: '/api/v1/posts/post-list',
        data: {
            postId: lastPostId
        }
    }).done((data) => {
        displayPostTable(data);
    }).fail((error) => {
        alert(error.responseJSON.message);
        return false;
    });
})

init();