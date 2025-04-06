document.addEventListener("DOMContentLoaded", function () {
    const rows = document.querySelectorAll(".appointment-row");
    const dialog = document.getElementById("appointmentDialog");
    const closeDialog = document.getElementById("closeDialog");

    rows.forEach(row => {
        row.addEventListener("click", function () {
            document.getElementById("dateSpan").innerText = this.dataset.date;
            document.getElementById("timeSpan").innerText = this.dataset.time;
            
            // Hidden fields
            document.getElementById("dialogDate").value = this.dataset.date;
            document.getElementById("dialogTime").value = this.dataset.time;
            document.getElementById("dialogShiftId").value = this.dataset.shift;
            
            dialog.showModal();
        });
    });

    closeDialog.addEventListener("click", function () {
        dialog.close();
    });
});