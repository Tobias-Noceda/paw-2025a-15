    function confirmDeleteStudy(message, secondaryMessage, accept, cancel, name, value, id) {
        const form = document.getElementById('deleteStudy_' + id);

        let actionInput = form.querySelector('input[name="action"]');
        if (!actionInput) {
            actionInput = document.createElement('input');
            actionInput.type = 'hidden';
            actionInput.name = 'action';
            form.appendChild(actionInput);
        }
        actionInput.value = value || 'delete';

        const event = new Event('submit', { cancelable: true });
        if (form.dispatchEvent(event)) {
            openConfirmDialog(form, message, secondaryMessage, accept, cancel);
        }
    }
