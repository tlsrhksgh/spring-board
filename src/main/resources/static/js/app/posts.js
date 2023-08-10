let fileArr = [];
var main = {
    init : function() {
        var _this = this;
        $('#btn-save').on('click', () => {
            _this.save();
        });

        $('#btn-update').on('click', () => {
            _this.update();
        });

        $('.post-del_btn').on('click', (e) => {
            _this.delete(e);
        });

        $('#file-input').on('change', () => {
            let files = $('#file-input')[0].files;
            _this.displayFileInfo(files);
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
        let formData = new FormData();
        formData.append('title', $('#title').val());
        formData.append('author', $('#author').text());
        formData.append('content', $('#content').val());
        fileArr.forEach(file => {
            formData.append('files', file)
        });

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
            alert(error.responseJSON.message);
            return false;
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
            alert(error.responseJSON.message);
            return false;
        })
    },

    displayFileInfo : function (files) {
        $("#drop-zone p").remove();
        let tag = "";
        for (let i = 0; i < files.length; i++) {
            let file = files[i];
            fileArr.push(file);
            const fileName = file.name;
            let fileSize = file.size / 1024 / 1024;
            fileSize = fileSize < 1 ? fileSize.toFixed(3) : fileSize.toFixed(1);
            tag +=
                "<div class='fileList'>" +
                "<span class='fileName'>"+fileName+"</span>" +
                "<span class='fileSize'>"+fileSize+" MB</span>" +
                "</div>";
        }

        const dropZone = $("#drop-zone");
        dropZone.append(tag);
    }
};

main.init();