var main = {
    init : function () {
        var _this = this;
        $('#btn-save').on('click', () => {
            _this.save();
        });
        $('.create-comment_form').on('click', (e) => {
           _this.createCommentForm(e);
        });
        $('.del-comment').on('click', () => {
            _this.deleteComment();
        });
    },
    save : function () {
        let data = {
            postId: window.location.href.split("/").pop().substring(0, 1),
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
        const saveBtn = e.target;
        const ancestorLiId = $(saveBtn).closest("li").attr('dataset-id');
        const content = $(saveBtn).parent("div").children().first().val();

        let data = {
            postId: window.location.href.split("/").pop().substring(0, 1),
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
        const div = document.createElement("div");
        const textarea = document.createElement("textarea");
        const button = document.createElement("button");
        div.className = "mb-3 form-group";
        textarea.className = "comment-content";
        button.type = "button";
        button.className = "reply-comment_btn";
        button.innerText = "등록";
        var _this = this;
        button.addEventListener('click', (e) => {
            _this.replyCommentSave(e);
        })

        div.appendChild(textarea);
        div.appendChild(button);
        parent.appendChild(div);
    },
    deleteComment: function () {
        $(".del-btn").click(function () {
            let parentDiv = $(this).parent();
            let parentLi = parentDiv.parent("li")[0];

            let datasetId = $(parentLi).attr('dataset-id');

            $.ajax({
                type: 'DELETE',
                url: '/api/v1/comments/'+ datasetId,
                dataType: 'json',
                contentType:'application/json; charset=utf-8',
            }).done(function () {
                parentLi.remove();
                alert("댓글이 삭제되었습니다.");
            }).fail(function(error) {
                console.log(error);
                alert(JSON.stringify("삭제가 실패되었습니다."));
            })
        });
    }
};


main.init();