$('#form input').on('change', function () {
    if ($('input[name=exists]:checked', '#form').val() === "false") {
        $('#form-select').prop('disabled', true);
        $('#new-input').prop('disabled', false);
    } else {
        $('#form-select').prop('disabled', false);
        $('#new-input').prop('disabled', true);
    }
});