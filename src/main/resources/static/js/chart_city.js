function drawLineChart(label, series, responseTime, country, city, width, height) {
    Highcharts.chart("container", {
        chart: {
            type: "line",
            width,
            height,
            zoomType: "x"
        },
        title: {
            text: "Particulate matter data of city"
        },
        subtitle: {
            text: "City: " + city + ", " + country + " - (Response time: " + responseTime + " ms)"
        },
        xAxis: {
            title: {
                text: "Time"
            },
            type: "datetime"
        },
        yAxis: {
            title: {
                text: "Values"
            }
        },
        series: [{
            name: label,
            data: series
        }]
    });
}

// -------------------------------------------------- Main code --------------------------------------------------------

// Get url parameter
var params = getAllUrlParams();
var urlSuffix = encodeQueryData(params);
var country = params.country;
var city = params.city;
var width = params.width ? params.width : 800;
var height = params.height ? params.height : 500;

// Execute request for data
$.ajax({
    url: "data/chart?" + urlSuffix,
    success: (result) => {
        var field = JSON.parse(result).field;
        var values = JSON.parse(result).values;
        var responseTime = JSON.parse(result).responseTime;
        drawLineChart(field, values, responseTime, country, city, width, height);
    }
});