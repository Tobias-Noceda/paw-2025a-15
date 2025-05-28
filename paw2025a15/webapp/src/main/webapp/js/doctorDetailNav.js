
function submitFormWithAction(actionValue) {
    const buttons = document.querySelectorAll('.navigation-button');
    buttons.forEach(btn => btn.disabled = true);
    const previusIndex = parseInt(document.getElementById('indexField').value);
    if( actionValue === 'previous') {
        document.getElementById('indexField').value = 0;
        document.getElementById('actionField').value = "previous"
    } else if( actionValue === 'next') {
        document.getElementById('indexField').value = 0;
        document.getElementById('actionField').value = "next"
    }
        document.querySelector('.week-navigator-div').submit();
}