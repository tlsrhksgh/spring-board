function init() {
    $.ajax({
        type: 'GET',
        url: '/api/v1/comments/comment-list'
    }).done((data) => {
        displayCommentTable(data);
    }).fail((error) => {
        alert(error.responseJSON.message);
        return false;
    });
}

function displayCommentTable(data) {
    const container = $("#comment-list_container");

    $.each(data, function(index, comment) {
        const row = $("<tr>");

        $("<td>").append($("<a>").attr("href", "/posts/find/" + comment.postId)
            .text(comment.postTitle)).appendTo(row);
        $("<td>").attr("id", comment.commentId).text(comment.content).appendTo(row);
        $("<td>").text(comment.childrenCount).appendTo(row);
        $("<td>").text(comment.createdDate).appendTo(row);

        row.appendTo(container.find("tbody"));
    });
}

$('#more-view').on('click', () => {
    const container = $("#comment-list_container");
    const tbodyChildLength = container.find("tbody").children().length;

    const tbodyLastChild = container.find("tbody").children()[tbodyChildLength - 1];
    const lastCommentId = tbodyLastChild.querySelectorAll("td")[1].id;

    $.ajax({
        type: 'GET',
        url: '/api/v1/comments/comment-list',
        data: {
            commentId: lastCommentId
        }
    }).done((data) => {
        displayCommentTable(data);
    }).fail((error) => {
        alert(error.responseJSON.message);
        return false;
    });
})

init();