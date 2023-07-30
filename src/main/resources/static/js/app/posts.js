var main = {
    init : function() {
        var _this = this;
        $('#btn-save').on('click', () => {
            _this.save();
        });

        $('#btn-update').on('click', () => {
            _this.update();
        })

        $('.del-comment').on('click', (e) => {
            _this.delete(e);
        })
    },
    save : function() {
        let data = {
            title: $('#title').val(),
            author: $('#author').text(),
            content: $('#content').val()
        };

        $.ajax({
            type: 'POST',
            url: '/api/v1/posts',
            dataType: 'json',
            contentType: 'application/json; charset=utf-8',
            data: JSON.stringify(data)
        }).done(() => {
            alert("글이 등록되었습니다.");
            window.location.href = '/';
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

    delete : function (e) {
        const postId = e.target.parentNode.childNodes.item(1).textContent;

        $.ajax({
            type: 'DELETE',
            url: '/api/v1/posts/'+postId,
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