let fileArr = [];
var main = {
    init : function() {
        var _this = this;
        $('.user-update_btn').on('click', () => {
            _this.update();
        });
    },

    update : function () {
        const formData = this.createFormData();

        formData.append('email', $('email').text());
        formData.append('name', $('#name').val());
        formData.append('picture', fileArr[0]);

        $.ajax({
            type: 'PATCH',
            url: '/api/v1/posts/'+id,
            processData: false,
            contentType: false,
            cache: false,
            data: formData
        }).done(function () {
            alert("글이 수정되었습니다.");
            window.location.href = '/posts/find/' + id;
        }).fail(function(error) {
            alert(error.responseJSON.message);
            return false;
        })
    },
};

main.init();