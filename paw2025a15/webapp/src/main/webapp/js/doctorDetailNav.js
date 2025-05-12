function submitFormWithAction(actionValue) {
    const buttons = document.querySelectorAll('.navigation-button');
    buttons.forEach(btn => btn.disabled = true);
    
    const previusIndex = parseInt(document.getElementById('indexField').value);
    if(previusIndex !== 0 && actionValue === 'previous') {
        document.getElementById('indexField').value = previusIndex - 1;
    } else if(previusIndex !== 10 && actionValue === 'next') {
        document.getElementById('indexField').value = previusIndex + 1;
    }
    document.querySelector('.week-navigator-div').submit();
}