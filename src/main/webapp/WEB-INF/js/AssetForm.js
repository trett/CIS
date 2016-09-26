function update(id) {
    console.log(id);
    var status = $('#status-select option:selected').val();
    var comment = $('#comment').val();
    console.log(status);
    $.ajax({
        url: '/api/asset/update',
        data: "id=" + id + "&status=" + status + "&comment=" + comment,
        type: 'POST',
        success: function (response) {
           showMessage(response)
        },
        error: function (jqXHR, textStatus, errorThrown) {
            console.log(textStatus, errorThrown);
        }
    })
}
