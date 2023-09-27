function init() {

    $.ajax({
        type: 'GET',
        url: '/api/v1/posts/post-list'
    }).done((data) => {
        displayPostTable(data);

        $('#select-all').on('click', function() {
            let checked = $(this).is(':checked');

            $('#post-list input[type=checkbox]').each(function (k, v) {
                $(this).prop('checked', checked);
            });
        });
    }).fail((error) => {
        alert(error.responseJSON.message);
        return false;
    });
}

function displayPostTable(data) {
    const container = $("#post-list_container");

    $.each(data, function(index, post) {
        const row = $("<tr>");

        $("<td>").append($("<input>").attr({
            type: "checkbox",
            value: post.id
        })).appendTo(row);
        $("<td>").text(post.id).appendTo(row);
        $("<td>").append($("<a>").attr("href", "/posts/find/" + post.id).text(post.title)).appendTo(row);
        $("<td>").text(post.viewCount).appendTo(row);
        $("<td>").text(post.createdDate).appendTo(row);

        row.appendTo(container.find("tbody"));
    });
};

$('.post-list-del_btn').on('click', function () {
    let checked = $('#post-list input[type=checkbox]:checked');
    if (checked.length < 1) {
        alert('삭제할 게시글을 선택해 주세요');
        return false;
    }

    if (!confirm('게시글 외에 파일 댓글 등 모든 데이터가 삭제됩니다. 그래도 삭제 하시겠습니까?')) {
        return false;
    }

    let idList = [];
    $.each(checked, function (k, v) {
        idList.push($(this).val());
    });
    console.log(idList);

    $.ajax({
        type: 'DELETE',
        url: '/api/v1/posts',
        contentType: 'application/json; charset=utf-8',
        data: JSON.stringify(idList)
    }).done(() => {
        alert("선택하신 게시글이 삭제 되었습니다.");
        window.location.href='/post-list';
    }).fail((error) => {
        alert(error.responseJSON.message);
        return false;
    });

});

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