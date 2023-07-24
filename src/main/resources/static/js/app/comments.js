var main = {
    init : function() {
        var _this = this;
        $('#btn-save').on('click', () => {
            _this.save();
        });
    },
    save : function() {
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
            data: JSON.stringify(data)
        }).done(() => {
            alert("댓글이 등록되었습니다.");
        }).fail((error) => {
            alert("권한이 없습니다.");
        });
    }
};


main.init();