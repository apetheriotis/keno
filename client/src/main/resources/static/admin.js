 var chart;
 var startTime;
 $(document).ready(function () {

     $.ajax({
         url: '/api/v1/json/admin/stats',
         dataType: 'json',
         success: function (data) {
             startTime = data.nowTime;
         },
         cache: false,
         async: false
     });

     chart = new Highcharts.Chart({
         chart: {
             type: 'column',
             renderTo: 'container',
             zoomType: 'x',
             events: {
                 load: function () {

                     setInterval(function () {
                         $.ajax({
                             url: '/api/v1/json/admin/stats',
                             dataType: 'json',
                             success: function (data) {
                                 var elem = document.getElementById("drawId");
                                 elem.innerHTML = "Stats for draw: "+ data.drawTime;
                                 var categories = [];
                                 var seriesData = [];
                                 $.each(data.numbers, function (i, e) {
                                     categories.push(i);
                                     seriesData.push(e);
                                 });
                                 chart.series[0].setData(seriesData);
                                 chart.xAxis[0].setCategories(categories);
                             },
                             cache: false
                         });
                     }, 2000);
                 }
             }
         },
         title: {
             text: 'Stats'
         },
         credits: {
             enabled: false
         },
         xAxis: {
             type: 'category',
             labels: {
                 rotation: -90,
                 style: {
                     fontSize: '13px',
                     fontFamily: 'Verdana, sans-serif'
                 }
             }
         },
         yAxis: {
             title: {
                 text: 'Times'
             }
         },
         legend: {
             enabled: true
         },
         tooltip: {
             enabled: true
         },
         plotOptions: {
             area: {
                 fillColor: {
                     linearGradient: {
                         x1: 0,
                         y1: 0,
                         x2: 0,
                         y2: 1
                     },
                     stops: [
                         [0, Highcharts.getOptions().colors[0]],
                         [1, Highcharts.Color(Highcharts.getOptions().colors[0]).setOpacity(0).get('rgba')]
                     ]
                 },
                 marker: {
                     radius: 2
                 },
                 lineWidth: 1,

                 threshold: null
             }
         },
         series: [{
             name: 'Times',
             dataLabels: {
                 enabled: false,
                 rotation: -90,
                 color: '#FFFFFF',
                 align: 'right',
                 x: 4,
                 y: 10,
                 style: {
                     fontSize: '13px',
                     fontFamily: 'Verdana, sans-serif',
                     textShadow: '0 0 3px black'
                 }
             }
         }]
     });
 });