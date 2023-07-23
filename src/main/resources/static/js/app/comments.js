var main = {
    init : function() {
        var _this = this;
        $('#btn-save').on('click', () => {
            _this.save();
        });

        $('#btn-update').on('click', () => {
            _this.update();
        })

        $('#btn-delete').on('click', () => {
            _this.delete();
        })
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
    },

    update : function () {
        let data = {
            title: $('#title').val(),
            content: $('#content').val()
        };

        let id = $('#id').val();

        $.ajax({
            type: 'PUT',
            url: '/api/v1/posts/'+id,
            dataType: 'json',
            contentType:'application/json; charset=utf-8',
            data: JSON.stringify(data)
        }).done(function () {
            alert("글이 수정되었습니다.");
            window.location.href = '/';
        }).fail(function(error) {
            alert(JSON.stringify(error));
        })
    },

    delete : function () {
        let id = $('#id').val();

        $.ajax({
            type: 'DELETE',
            url: '/api/v1/posts/'+id,
            dataType: 'json',
            contentType:'application/json; charset=utf-8',
        }).done(function () {
            alert("글이 삭제되었습니다.");
            window.location.href = '/';
        }).fail(function(error) {
            alert(JSON.stringify(error));
        })
    }

};

main.init();