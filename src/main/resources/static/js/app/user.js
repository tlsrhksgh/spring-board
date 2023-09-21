$('#user-image').on('click', () => {
    const fileInput = document.getElementById('picture');
    fileInput.click();
});

$('#picture').on('change', (e) => {
    const imagePreview = document.getElementById("image-preview");
    const file = e.target.files[0];
    console.log(file);
    console.log(e);

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

        $.ajax({
            type: 'GET',
            url: '/api/v1/posts/count'
        }).done(function (data) {
            _this.displayCount(data);
        }).fail(function () {
           alert("게시글 내역을 가져올 수 없습니다.");
           return false;
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
    },

    displayCount : function (data) {
        const postAndCommentCountContainer = document.getElementById('post_comment_count');
        const postCountDiv = document.createElement("div");
        const postCountSpan = document.createElement("span");
        postCountSpan.textContent = "게시글 작성 건: ";
        const postCountLink = document.createElement("a");
        postCountLink.href = "/post_count-list"
        postCountLink.text = data.postCount + "건";
        postCountDiv.append(postCountSpan, postCountLink);

        const commentCountDiv = document.createElement("div");
        const commentCountSpan = document.createElement("span");
        commentCountSpan.textContent = "댓글 작성 건: ";
        const commentCountLink = document.createElement("a");
        commentCountLink.href = "/post_count-list"
        commentCountLink.text = data.commentCount + "건";
        commentCountDiv.append(commentCountSpan, commentCountLink);
        postAndCommentCountContainer.append(postCountDiv, commentCountDiv);
    }
};

user.init();