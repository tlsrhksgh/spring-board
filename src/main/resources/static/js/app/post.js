let post = {
    init: function () {
        let _this = this;

        _this.fetchPostRanking();

        $('.post-save_btn').on('click', () => {
            _this.save();
        });

        $('.post-update_btn').on('click', () => {
            _this.update();
        });

        $('.post-del_btn').on('click', (e) => {
            _this.delete(e);
        });
    },
    save: function () {
        const formData = this.createForm();

        $.ajax({
            type: 'POST',
            url: '/api/v1/posts',
            processData: false,
            contentType: false,
            cache: false,
            data: formData
        }).done(() => {
            alert("글이 등록되었습니다.");
            window.location.href = '/';
        }).fail((error) => {
            alert(error.responseJSON.message);

            return false;
        });
    },

    update: function () {
        const formData = this.createForm();
        if (oldFileNameArr.length > 0) {
            let dtoObj = {};
            for (let i = 0; i < oldFileNameArr.length; i++) {
                dtoObj[oldFileNameArr[i].id] = oldFileNameArr[i].originalName;
            }
            formData.append('oldFileNames', JSON.stringify(dtoObj));
        } else {
            formData.append('oldFilenames', "");
        }

        const id = $('#id').text();

        $.ajax({
            method: 'PATCH',
            url: '/api/v1/posts/' + id,
            processData: false,
            contentType: false,
            cache: false,
            data: formData
        }).done(function () {
            alert("글이 수정되었습니다.");
            window.location.href = '/posts/find/' + id;
        }).fail(function (error) {
            alert(error.responseJSON.message);
            return false;
        })
    },

    delete: function (e) {
        if(!confirm("해당 게시글을 정말 삭제 하시겠습니까?")) {
            return false;
        }

        const postId = e.target.parentNode.parentNode.childNodes.item(1).textContent;

        $.ajax({
            type: 'DELETE',
            url: '/api/v1/posts/' + postId,
            dataType: 'json',
            contentType: 'application/json; charset=utf-8',
        }).done(function () {
            alert("글이 삭제되었습니다.");
            window.location.href = '/';
        }).fail(function (error) {
            alert(error.responseJSON.message);
            return false;
        })
    },

    displayFileInfo: function (files) {
        $("#drop-zone p").remove();
        for (let i = 0; i < files.length; i++) {
            let file = files[i];
            newFileArr.push(file);
            const fileName = file.name;
            let fileSize = file.size / 1024 / 1024;
            fileSize = fileSize < 1 ? fileSize.toFixed(3) : fileSize.toFixed(1);
            const div = document.createElement("div");
            div.className = "file";
            div.style.display = "flex";
            div.style.justifyContent = "space-between";
            div.style.marginBottom = "10px";
            const fileNameSpan = document.createElement("span");
            fileNameSpan.innerText = fileName;
            const fileSizeSpan = document.createElement("span");
            fileSizeSpan.className = "fileSize";
            fileSizeSpan.innerText = fileSize + " MB";
            const delBtn = document.createElement("button");
            delBtn.className = "del-image_btn btn btn-danger";
            delBtn.innerText = "삭제";
            delBtn.addEventListener('click', (e) => {
                deleteFile(e);
            })
            div.appendChild(fileNameSpan);
            div.appendChild(fileSizeSpan);
            div.appendChild(delBtn);
            const dropZone = $("#drop-zone");
            dropZone.append(div);
        }
    },

    createForm: function () {
        let formData = new FormData();
        formData.append('title', $('#title').val());
        formData.append('author', $('#author').text());
        formData.append('content', $('#content').val());
        newFileArr.forEach(file => {
            formData.append('files', file);
        });
        return formData;
    },

    fetchPostRanking: function () {
        $.ajax({
            method: 'GET',
            url: '/api/v1/posts/ranking',
            dataType: 'json',
            context: this
        }).done((data) => {
            post.displayPostRanking(data)
        }).fail((error) => {
            alert(error.responseJSON.message);
            return false;
        });
    },

    displayPostRanking: function (data) {
        const rankingContainer = $("#ranking-container");
        const existPostList = $('#ranking-container__list');
        if (!existPostList.is(':empty')) {
            existPostList.remove();
        }

        const postList = $("<div>").attr({
            id: "ranking-container__list"
        }).appendTo(rankingContainer);

        if(data.length === 0) {
            $("<span>").attr({
                style: "opacity: 0.6"
            }).text("존재하는 게시물이 없습니다.").appendTo(postList);
        }

        $.each(data, function (index, post) {
            const link = $("<a>").attr({
                href: "/posts/find/" + post.id,
                style: "display: block"
            }).text(post.title).appendTo(postList);
        });
    }
};

post.init();