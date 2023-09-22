let postAndComment = {
    init : function () {
        let _this = this;
        $.ajax({
            type: 'GET',
            url: '/api/v1/posts/count'
        }).done(function (data) {
            _this.displayCount(data);
        }).fail(function () {
            alert("게시글 건수를 가져올 수 없습니다.");
            return false;
        });
    },

    displayCount : function (data) {
        const postAndCommentCountContainer = document.getElementById('post_comment_count');
        const postCountDiv = document.createElement("div");
        const postCountSpan = document.createElement("span");
        postCountSpan.textContent = "게시글 작성 건: ";
        const postCountLink = document.createElement("a");
        postCountLink.href = "/post-list"
        postCountLink.text = data.postCount + "건";
        postCountDiv.append(postCountSpan, postCountLink);

        const commentCountDiv = document.createElement("div");
        const commentCountSpan = document.createElement("span");
        commentCountSpan.textContent = "댓글 작성 건: ";
        const commentCountLink = document.createElement("a");
        commentCountLink.href = "/comment-list"
        commentCountLink.text = data.commentCount + "건";
        commentCountDiv.append(commentCountSpan, commentCountLink);
        postAndCommentCountContainer.append(postCountDiv, commentCountDiv);
    }
}

postAndComment.init();