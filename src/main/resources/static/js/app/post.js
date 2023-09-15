let newFileArr = [];

let post = {
    init : function() {
        let _this = this;
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

    update : function () {
        const formData = this.createForm();
        let dtoObj = {};
        for (let i = 0; i < oldFileArr.length; i++) {
            dtoObj[oldFileArr[i].id] = oldFileArr[i].originalName;
        }
        formData.append('oldFileNames', JSON.stringify(dtoObj));

        const id = $('#id').text();

        $.ajax({
            method: 'PATCH',
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
                this.deleteFile(e);
            })
            div.appendChild(fileNameSpan);
            div.appendChild(fileSizeSpan);
            div.appendChild(delBtn);
            const dropZone = $("#drop-zone");
            dropZone.append(div);
        }
    },

    createForm : function () {
        let formData = new FormData();
        formData.append('title', $('#title').val());
        formData.append('author', $('#author').text());
        formData.append('content', $('#content').val());
        newFileArr.forEach(file => {
            formData.append('files', file);
        });
        return formData;
    },

    deleteFile : function (e) {
    e.stopPropagation();

    const filename = e.target.parentNode.childNodes[0].outerText;

    newFileArr = newFileArr.filter(item => filename !== item.name);

    const fileElement = e.target.parentNode;
    fileElement.remove();

    if(newFileArr.length === 0) {
        $('#file-input').val('');
        createDropZoneDescription();
    }
}

};

function createDropZoneDescription() {
    const dropZone = document.getElementById("drop-zone");
    const p = document.createElement("p");
    p.innerText = "여기를 클릭하거나 파일을 드래그앤드롭 하세요\n" +
        "용량은 한 파일 당 10MB씩 50MB 까지 업로드가 가능합니다.";
    p.style.opacity = "0.7";
    dropZone.appendChild(p);
}

post.init();