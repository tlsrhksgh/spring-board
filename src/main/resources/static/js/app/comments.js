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
        const secretCheckBox = document.getElementsByClassName('comment-secretYn');
        const checked = $(secretCheckBox).is(':checked');

        let data = {
            postId: window.location.href.split("/").pop().substring(0, 1),
            nickname: $('#comment-author').text(),
            content: $('#comment-content').val(),
            secret: checked
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
            alert(error.responseJSON.message);
            return false;
        });
    },
    replyCommentSave: function (e) {
        const parentDiv = e.target.parentElement;
        const ancestorLiId = $(parentDiv).parent().attr('dataset-id');
        const content = $(parentDiv).children().first().val();

        let data = {
            postId: window.location.href.split("/").pop().substring(0, 1),
            parentId: ancestorLiId,
            nickname: $('#comment-author').text(),
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
            alert(error.responseJSON.message);
            return false;
        })
    },
    createCommentForm: function (e) {
        const parent = e.target.parentElement;
        const li = document.createElement("li");
        const textarea = document.createElement("textarea");
        const button = document.createElement("button");
        const input = document.createElement("input");
        input.type="checkbox";
        input.name="secret";
        input.id=""
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
        const postId = window.location.href.split("/").pop().substring(0, 1);

        $.ajax({
            type: 'DELETE',
            url: '/api/v1/comments/'+ datasetId,
            dataType: 'json',
            contentType:'application/json; charset=utf-8',
        }).done(function () {
            alert("댓글이 삭제되었습니다.");
            window.location.href = "/posts/find/" + postId
        }).fail(function(error) {
            alert(error.responseJSON.message);
            return false;
        })
    }
};


main.init();