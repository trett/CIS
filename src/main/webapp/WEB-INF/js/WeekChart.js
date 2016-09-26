$(function () {

    var seriesData = [];
    seriesData.data = [];

    $.ajax({
        url: '/api/invoice/chartdata',
        method: 'GET',
        contentType: 'json',
        success: function (response) {
            var temp = [];
            var categories = [];
            $.each(response.data, function (i, val) {
                temp[i] = response.data[i].data;
                categories[i] = response.data[i].name;
            });
            seriesData[0] = {'name': 'Invoices', 'data': temp};
            drawChart(seriesData, categories);
        }
    });

    function drawChart(seriesData, categories) {
        $('#week-chart').highcharts({
            title: {
                text: 'Day Statistics',
            },
            credits: {
                enabled: false
            },
            yAxis: {
                title: {
                    text: 'Quantities'
                },
                plotLines: [{
                    value: 0,
                    width: 1,
                    color: '#808080'
                }]
            },
            xAxis: {
                categories: categories
            },
            series: seriesData
        });
    }

});

