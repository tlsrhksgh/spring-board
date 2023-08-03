let fileLst = [];
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

        $('#drop-zone').on('dragover', (e) => {
            e.preventDefault();
            e.stopPropagation();
            $(this).css("background-color", "#FFD8D8");
        }).on('click', () => {
            const fileInput = document.getElementById("file-input");
            fileInput.click();
        }).on('dragleave', (e) => {
            e.preventDefault();
            e.stopPropagation();

            let dropZone = $("#drop-zone");
            dropZone.css('background-color', '#FFFFFF');

        }).on('dragenter', (e) => {
            e.preventDefault();
            e.stopPropagation();

            let dropZone = $("#drop-zone");
            dropZone.css('background-color', '#E3F2FC');

        }).on('drop', (e) => {
            e.preventDefault();

            let files = e.originalEvent.dataTransfer.files;
            if(files != null && files != undefined) {
                let tag = "";
                for (let i = 0; i < files.length; i++) {
                    let file = files[i];
                    fileLst.push(file);
                    let fileName = file.name;
                    let fileSize = file.size / 1024 / 1024;
                    fileSize =  fileSize < 1 ? fileSize.toFixed(3) : fileSize.toFixed(1);
                    tag +=
                        "<div class='fileList'>" +
                        "<span class='fileName'>"+fileName+"</span>" +
                        "<span class='fileSize'>"+fileSize+" MB</span>" +
                        "<span class='clear'></span>" +
                        "</div>";
                }
                $(this).append(tag);
            }
        });

        $('#file-input').on('change', (e) => {
            let files = e.target.files;
            let formData = new FormData();
            formData.append("file", files[0]);
            _this.save(formData);
        })
    },
    save : function() {

        const form = $('#uploadForm');
        const formData = new FormData(form);
        for(let i = 0; i < uploadFileList.length; i++){
            formData.append('files', fileList[uploadFileList[i]]);
        }

        let data = {
            title: $('#title').val(),
            author: $('#author').text(),
            content: $('#content').val(),
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