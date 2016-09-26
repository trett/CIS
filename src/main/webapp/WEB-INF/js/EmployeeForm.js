$(document).ready(function () {
    var costCenterList;
    $.ajax({
        url: '/api/common/list',
        dataType: 'json',
        type: 'GET',
        contentType: 'text',
        data: "obj=CostCenter",
        success: function (data) {
            $.each(data, function (i, val) {
                $('#employee-form-cost-center').append('<option value="' + val.number + '">' + val.number + '</option>');
            });
            costCenterList = data;
        },
        error: function (jqXHR, textStatus, errorThrown) {
            console.log(textStatus, errorThrown);
        }
    });
    $('#employee-form-cost-center').change(function () {
        $('#cost-center-description').remove();
        var cc = $.grep(costCenterList, function (obj) {
            return obj.number == $('#employee-form-cost-center').val();
        });
        console.log(cc[0].description);
        $('#cost-center-select').append("<p class='form-header bg-info' id='cost-center-description' style='margin-top: 10px'>"
            + cc[0].description + "</p>");
    })
});