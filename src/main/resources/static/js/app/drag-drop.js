$('#file-input').on('change', () => {
    let files = $('#file-input')[0].files;
    post.displayFileInfo(files);
});

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
        post.displayFileInfo(files);
    }
});

function createDropZoneDescription() {
    const dropZone = document.getElementById("drop-zone");
    const p = document.createElement("p");
    p.innerText = "여기를 클릭하거나 파일을 드래그앤드롭 하세요\n" +
        "용량은 한 파일 당 10MB씩 50MB 까지 업로드가 가능합니다.";
    p.style.opacity = "0.7";
    dropZone.appendChild(p);
}