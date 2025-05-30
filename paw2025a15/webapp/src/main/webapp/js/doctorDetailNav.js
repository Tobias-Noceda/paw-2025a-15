function submitFormWithAction(actionValue) {
    const buttons = document.querySelectorAll('.navigation-button');
    buttons.forEach(btn => btn.disabled = true);
    let previousDate = new Date(document.getElementById('dateSelector').value);
    // increment or decrement on 1 the date based on the action
    if (actionValue === 'next') {
        previousDate.setDate(previousDate.getDate() + 1);
    } else if (actionValue === 'previous') {
        previousDate.setDate(previousDate.getDate() - 1);
    }
    document.getElementById('dateSelector').value = previousDate.toISOString().split('T')[0];
    document.querySelector('.week-navigator-div').submit();
}