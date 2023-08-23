var main = {
    init : function() {
        var _this = this;
        $('.user-update_btn').on('click', () => {
            _this.update();
        });
    },

    update : function () {
        let formData = new FormData();

        const email = $('#email').text();
        const imageFile = $('#picture')[0];
        console.log(imageFile.files);

        formData.append('email',email);
        formData.append('name', $('#name').val());
        if(imageFile.files.length > 0) {
            formData.append('picture', imageFile.files[0]);
        }

        $.ajax({
            type: 'PATCH',
            url: '/api/v1/user',
            processData: false,
            contentType: false,
            cache: false,
            data: formData
        }).done(function () {
            alert("글이 수정되었습니다.");
            window.location.href = '/user/info/' + email;
        }).fail(function(error) {
            alert(error.responseJSON.message);
            return false;
        })
    },
};

main.init();