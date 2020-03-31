function drawLineChart(label, category, series, responseTime, chipId, width, height) {
    Highcharts.chart("container", {
        chart: {
            type: "line",
            width: width,
            height: height
        },
        title: {
            text: "Particulate matter data of country"
        },
        subtitle: {
            text: "Country: " + chipId + " - (Response time: " + responseTime + " ms)"
        },
        xAxis: {
            categories: category,
            title: {
                text: "Time"
            }
        },
        yAxis: {
            title: {
                text: "PM vales"
            }
        },
        tooltip: {
            formatter: function() {
                return "<strong>"+this.x+": </strong>"+ this.y;
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
var width = params.width ? params.width : 800;
var height = params.height ? params.height : 500;

// Execute request for data
$.ajax({
    url: "data/chart?" + urlSuffix,
    success: (result) => {
        var country
        var field = JSON.parse(result).field;
        var time = JSON.parse(result).time;
        var values = JSON.parse(result).values;
        var responseTime = JSON.parse(result).responseTime;
        drawLineChart(field, time, values, responseTime, country, width, height);
    }
});