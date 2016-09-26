var Table = (function (data, cols) {

    var table;

    return {
        init: function () {

            $.extend(true, $.fn.dataTable.defaults, {
                "ordering": false
            });

            table = $('#datatable').DataTable({
                initComplete: function () {
                    var api = this.api();
                    $('#datatable_filter input')
                        .off('.DT')
                        .on('keyup.DT', function (e) {
                            if (e.keyCode == 13) {
                                api.search(this.value).draw();
                            }
                        });
                },
                lengthMenu: [ 10, 20, 50,100 ],
                select: {
                    style: 'os'
                },
                pageLength: 20,
                serverSide: true,
                ajax: {
                    url: '/api/common/table',
                    type: 'GET',
                    data: data
                },
                columns: cols
            });
        },
        locate: function (url) {
            $('#datatable tbody').on('click', 'tr', function () {
                location.href = url + table.row(this).data().id;
            });
        }
    }

});