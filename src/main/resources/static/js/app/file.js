let oldFileArr = [];

let file = {
    init: function () {
        const url = window.location.href;
        const match = url.match(/\/(\d+)(?:\?|$)/);

        const postId = match[1];

        $(document).ready(() => {
            $.ajax({
                type: 'GET',
                url: "/api/v1/files/" + postId,
                success: (data) => {
                    this.displayFileName(data);
                },
                error: () => {
                    alert("파일을 가져오는데 실패 하였습니다.");
                    return false;
                }
            })
        })
    },

    displayFileName: function (fileNameArr) {
        for (let i = 0; i < fileNameArr.length; i++) {
            let file = fileNameArr[i];
            oldFileArr.push(file);
            const fileName = file.originalName;
            const div = document.createElement("div");
            div.className = "file";
            div.style.display = "flex";
            div.style.justifyContent = "space-between";
            div.style.marginBottom = "10px";
            const fileNameSpan = document.createElement("span");
            fileNameSpan.innerText = fileName;
            const delBtn = document.createElement("button");
            delBtn.className = "del-image_btn btn btn-danger";
            delBtn.innerText = "삭제";
            delBtn.addEventListener('click', (e) => {
                this.deleteFile(e);
            })
            div.appendChild(fileNameSpan);
            div.appendChild(delBtn);
            const dropZone = $("#drop-zone");
            dropZone.append(div);
        }
    },

    deleteFile: function (e) {
        e.stopPropagation();

        const filename = e.target.parentNode.childNodes[0].outerText;

        newFileArr = newFileArr.filter(item => filename !== item.name);
        oldFileArr = oldFileArr.filter(item => filename !== item.originalName);

        const fileElement = e.target.parentNode;
        fileElement.remove();

        if (newFileArr.length === 0 && oldFileArr.length === 0) {
            createDropZoneDescription();
        }
    }
}

file.init();