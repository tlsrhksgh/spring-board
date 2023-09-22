$('#user-image').on('click', () => {
    const fileInput = document.getElementById('picture');
    fileInput.click();
});

$('#picture').on('change', (e) => {
    const imagePreview = document.getElementById("image-preview");
    const file = e.target.files[0];

    if (file) {
        const reader = new FileReader();
        reader.onload = (e) => {
            imagePreview.src = e.target.result;
        }
        reader.readAsDataURL(file);
    }
});

let user = {
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
            alert("프로필 수정을 완료 하였습니다.");
            window.location.href = '/user/info';
        }).fail(function(error) {
            alert(error.responseJSON.message);
            return false;
        })
    }
};

user.init();