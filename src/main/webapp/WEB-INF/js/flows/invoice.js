$(function () {
    $.ajax({
        url: '/api/common/list',
        type: 'GET',
        dataType: 'json',
        data: "obj=DeviceType",
        contentType: 'text',
        success: function (data) {
            $.each(data, function (i, val) {
                $('#asset-form-type').append('<option value="' + data[i].type + '">' + data[i].type + '</option>');
            });
        },
        error: function (jqXHR, textStatus, errorThrown) {
            console.log(textStatus, errorThrown);
        }
    });
});

$('#asset-form-type').change(function () {
    var type = $(this).val();
    $('#asset-form-brand').empty();
    $.ajax({
        url: '/api/common/brandbytype',
        type: 'GET',
        dataType: 'json',
        contentType: 'text',
        data: 'devicetype=' + type,
        success: function (data) {
            $('#asset-form-brand').empty();
            $('#asset-form-model').empty();
            $.each(data, function (i, val) {
                $('#asset-form-brand').append('<option value="' + data[i] + '">' + data[i] + '</option>');
            });
            fillModel();
        },
        error: function (jqXHR, textStatus, errorThrown) {
            console.log(textStatus, errorThrown);
        }
    })
});

$('#asset-form-brand').change(function () {
    fillModel();
});

function fillModel() {
    var brand = $('#asset-form-brand').val();
    var type = $('#asset-form-type').val();
    $.ajax({
        url: '/api/common/modelsbytypeandbrand',
        type: 'GET',
        dataType: 'json',
        contentType: 'text',
        data: 'devicetype=' + type + '&devicebrand=' + brand,
        success: function (data) {
            $('#asset-form-model').empty();
            $.each(data, function (i, val) {
                $('#asset-form-model').append('<option value="' + data[i].model + '">' + data[i].model + '</option>');
            })
        },
        error: function (jqXHR, textStatus, errorThrown) {
            console.log(textStatus, errorThrown);
        }
    });
}


