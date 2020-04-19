function drawLineChart(label, series, responseTime, country, sensorCount, width, height) {
    Highcharts.chart("container", {
        chart: {
            type: "line",
            width,
            height,
            zoomType: "x"
        },
        title: {
            text: "Particulate matter data of country"
        },
        subtitle: {
            text: "Country: " + country + " - (Response time: " + responseTime + " ms) - " + sensorCount + " sensors"
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
var width = params.width ? params.width : 800;
var height = params.height ? params.height : 500;

// Execute request for data
$.ajax({
    url: "data/chart?" + urlSuffix,
    success: (result) => {
        var field = JSON.parse(result).field;
        var values = JSON.parse(result).values;
        var responseTime = JSON.parse(result).responseTime;
        var sensorCount = JSON.parse(result).sensorCount;
        drawLineChart(field, values, responseTime, country, sensorCount, width, height);
    }
});