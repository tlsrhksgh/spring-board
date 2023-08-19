let fileArr = [];
var main = {
    init : function() {
        var _this = this;
        $('.post-save_btn').on('click', () => {
            _this.save();
        });

        $('.post-update_btn').on('click', () => {
            _this.update();
        });

        $('.post-del_btn').on('click', (e) => {
            _this.delete(e);
        });

        $('#file-input').on('change', () => {
            let files = $('#file-input')[0].files;
            _this.displayFileInfo(files);
        })

        $('.del-image_btn').on('click', (e) => {
            _this.deleteFile(e);
        })

        $('#drop-zone').on('dragover', (e) => {
            e.preventDefault();
            e.stopPropagation();

            const dropZone = $("#drop-zone");
            dropZone.css("background-color", "#D8D9DA");
        }).on('click', () => {
            const fileInput = document.getElementById("file-input");
            fileInput.click();
        }).on('dragleave', (e) => {
            e.preventDefault();
            e.stopPropagation();

            const dropZone = $("#drop-zone");
            dropZone.css('background-color', '#FFFFFF');

        }).on('dragenter', (e) => {
            e.preventDefault();
            e.stopPropagation();

            const dropZone = $("#drop-zone");
            dropZone.css('background-color', '#D8D9DA');

        }).on('drop', (e) => {
            e.preventDefault();

            let files = e.originalEvent.dataTransfer.files;
            if(files.length !== 0) {
                _this.displayFileInfo(files);
            }
        });


    },
    save : function() {
        const formData = this.createFormData();

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

    update : function () {
        const formData = this.createFormData();

        const id = $('#id').text();

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

    delete : function (e) {
        const postId = e.target.parentNode.parentNode.childNodes.item(1).textContent;

        $.ajax({
            type: 'DELETE',
            url: '/api/v1/posts/'+postId,
            dataType: 'json',
            contentType:'application/json; charset=utf-8',
        }).done(function () {
            alert("글이 삭제되었습니다.");
            window.location.href = '/';
        }).fail(function(error) {
            alert(error.responseJSON.message);
            return false;
        })
    },

    displayFileInfo : function (files) {
        $("#drop-zone p").remove();
        for (let i = 0; i < files.length; i++) {
            let file = files[i];
            fileArr.push(file);
            const fileName = file.name;
            let fileSize = file.size / 1024 / 1024;
            fileSize = fileSize < 1 ? fileSize.toFixed(3) : fileSize.toFixed(1);
            const div = document.createElement("div");
            div.className = "file";
            div.style.display = "flex";
            div.style.justifyContent = "space-between";
            const fileNameSpan = document.createElement("span");
            fileNameSpan.className = "fileName";
            fileNameSpan.innerText = fileName;
            const fileSizeSpan = document.createElement("span");
            fileSizeSpan.className = "fileSize";
            fileSizeSpan.innerText = fileSize + " MB";
            const delBtn = document.createElement("button");
            delBtn.className = "del-image_btn btn btn-danger";
            delBtn.style.color = "while";
            delBtn.innerText = "삭제";
            let _this = this;
            delBtn.addEventListener('click', (e) => {
                _this.deleteFile(e);
            })
            div.appendChild(fileNameSpan);
            div.appendChild(fileSizeSpan);
            div.appendChild(delBtn);
            const dropZone = $("#drop-zone");
            dropZone.append(div);
        }
    },

    deleteFile : function (e) {
        e.stopPropagation();

        const filename = e.target.parentNode.childNodes[0].outerText;

        for (let i = 0; i < fileArr.length; i++) {
            if(filename === fileArr[i].name) {
                fileArr.splice(i);
            }
        }

        const fileElement = e.target.parentNode;
        fileElement.remove();
    },

    createFormData : function () {
        let formData = new FormData();
        formData.append('title', $('#title').val());
        formData.append('author', $('#author').text());
        formData.append('content', $('#content').val());
        fileArr.forEach(file => {
            formData.append('files', file)
        });
        return formData;
    }
};

main.init();