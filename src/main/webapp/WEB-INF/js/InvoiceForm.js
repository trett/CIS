function _delete(invoiceId, message) {
    $.blockUI({message: message});
    location.href = '/invoice/' + invoiceId + '/delete';
}

function print(employeeId, issuerId, invoiceId) {
    $.ajax({
        url: '/api/invoice/print',
        type: 'POST',
        data: 'employee=' + employeeId + '&issuer=' + issuerId + '&invoice=' + invoiceId,
        success: function (response) {
            showMessage(response);
        }
    })
}

function publish(invoice) {
    $.ajax({
        url: '/api/invoice/publish',
        type: 'POST',
        data: "invoiceId=" + invoice,
        success: function (response) {
            showMessage(response);
            $('#status').html(response.customData);
        }
    })
}

function rowClicked(value) {
    location.href = "/asset/" + value;
}

$(document).ready(function () {
    var response = JSON.parse(localStorage.getItem("response"));
    if (response.messageBody) {
        showMessage(response);
        localStorage.removeItem("response");
    }
});