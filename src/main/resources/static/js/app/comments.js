var main = {
    init : function () {
        var _this = this;
        $('#btn-save').on('click', () => {
            _this.save();
        });
        $('.create-comment_form').on('click', (e) => {
           _this.createCommentForm(e);
        });
        $('.del-comment').on('click', (e) => {
            _this.deleteComment(e);
        });
    },
    save : function () {
        let data = {
            postId: parsePostId(window.location.href.split("/").pop()),
            nickname: $('#comment-author').text(),
            content: $('#comment-content').val()
        };

        $.ajax({
            type: 'POST',
            url: '/api/v1/comments',
            dataType: 'json',
            contentType: 'application/json; charset=utf-8',
            async: true,
            data: JSON.stringify(data)
        }).done(function () {
            alert("댓글이 등록되었습니다.");
        }).fail(function (error) {
            alert("권한이 없습니다.");
        });
    },
    replyCommentSave: function (e) {
        const parentDiv = e.target.parentElement;
        const ancestorLiId = $(parentDiv).parent().attr('dataset-id');
        const content = $(parentDiv).children().first().val();

        let data = {
            postId: parsePostId(window.location.href.split("/").pop()),
            parentId: ancestorLiId,
            nickname: $('#comment-author').text(),
            secret: false,
            async: true,
            content
        };

        $.ajax({
            type: 'POST',
            url: '/api/v1/comments',
            dataType: 'json',
            contentType:'application/json; charset=utf-8',
            data: JSON.stringify(data)
        }).done(function () {
            alert("답글이 등록 되었습니다.");
            window.location.href = '/posts/find/' + data.postId;
        }).fail(function (error) {
            console.log(error);
            alert("답글 등록에 실패 하였습니다. 다시 시도 해주세요");
        })
    },
    createCommentForm: function (e) {
        const parent = e.target.parentElement;
        const li = document.createElement("li");
        const textarea = document.createElement("textarea");
        const button = document.createElement("button");
        textarea.className = "comment-content";
        button.type = "button";
        button.className = "reply-comment_btn";
        button.innerText = "등록";
        var _this = this;
        button.addEventListener('click', (e) => {
            _this.replyCommentSave(e);
        })

        li.appendChild(textarea);
        li.appendChild(button);
        parent.appendChild(li);
    },
    deleteComment: function (e) {
        let parentDiv = e.target.parentElement;
        let datasetId = $(parentDiv).attr('dataset-id');
        const postId = parsePostId(window.location.href.split("/").pop());

        $.ajax({
            type: 'DELETE',
            url: '/api/v1/comments/'+ datasetId,
            dataType: 'json',
            contentType:'application/json; charset=utf-8',
        }).done(function () {
            alert("댓글이 삭제되었습니다.");
            window.location.href = "/posts/find/" + postId
        }).fail(function(error) {
            console.log(error);
            alert(JSON.stringify("삭제가 실패되었습니다."));
        })
    }
};

function parsePostId(postId) {
    const regex = /[^0-9]/g;
    return postId.replace(regex, "");
}


main.init();