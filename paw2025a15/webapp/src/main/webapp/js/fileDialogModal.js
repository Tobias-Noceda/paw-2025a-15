document.addEventListener("DOMContentLoaded", function () {
    const fileDialog = document.getElementById("fileDialog");
    const closeFileDialog = document.getElementById("closeFileDialog");
    const fileViewer = document.getElementById("fileViewer");

    document.querySelectorAll(".study-row").forEach(row => {
        row.addEventListener("click", function () {
            fileViewer.src = this.dataset.fileurl;
            fileDialog.showModal();
        });
    });

    closeFileDialog.addEventListener("click", function () {
        fileDialog.close();
        fileViewer.src = "";
    });
});