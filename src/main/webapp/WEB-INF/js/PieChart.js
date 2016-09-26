$(function () {

    var seriesData = [];
    seriesData.data = [];

    $.ajax({
        url: '/api/asset/chartdata',
        method: 'GET',
        contentType: 'json',
        success: function (response) {
            var temp = [];
            $.each(response.data, function (i, val) {
                temp[i] = {'name': response.data[i].name, 'y': response.data[i].data};
            });
            seriesData[0] = {'name': 'Usage', 'colorByPoint': response.colorByPoint, 'data': temp};
            drawChart(seriesData);
        }
    });

    function drawChart(seriesData) {
        $('#model-chart').highcharts({
            chart: {
                plotBackgroundColor: null,
                plotBorderWidth: null,
                plotShadow: false,
                type: 'pie'
            },
            title: {
                text: 'Device Usage',
            },
            credits: {
                enabled: false
            },
            tooltip: {
                pointFormat: 'Quantity : <b>{point.y}pc.</b>'
            },
            plotOptions: {
                pie: {
                    allowPointSelect: true,
                    cursor: 'pointer',
                    dataLabels: {
                        enabled: true,
                        format: '<b>{point.name}</b>: {point.percentage:.1f} %',
                        style: {
                            color: (Highcharts.theme && Highcharts.theme.contrastTextColor) || 'black'
                        }
                    }
                }
            },
            series: seriesData,
        });
    }

});

