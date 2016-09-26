function showMessage(response) {
    $('#messagebody-block')
        .addClass("alert alert-" + response.messageLevel.toLowerCase() + " info fade in")
        .html(response.messageBody)
        .append('<a href="#" class="close" onclick="$(this).closest(\'div\').html(\'\')' +
            '.removeClass();" aria-label="close">&times;</a>');
}
