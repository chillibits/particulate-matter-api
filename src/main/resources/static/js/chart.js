/*
 * Copyright © Marc Auberer 2019 - 2020. All rights reserved
 */

function drawLineChart(label, series, responseTime, chipId, width, height, type, chartType) {
    var config = {
        chart: {
            type: "line",
            width,
            height,
            zoomType: "x"
        },
        title: {
            text: "Particulate matter data"
        },
        subtitle: {
            text: "Sensor-ID: " + chipId + " - (Response time: " + responseTime + " ms)"
        },
        xAxis: {
            title: {
                text: "Values"
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
            type: type,
            data: series,
            tooltip: {
                valueDecimals: 2
            }
        }],
        rangeSelector: {
            selected: 1
        }
    };
    if(chartType === "stock") {
        Highcharts.stockChart("container", config);
    } else {
        Highcharts.chart("container", config);
    }
}

// -------------------------------------------------- Main code --------------------------------------------------------

// Get url parameter
var params = getAllUrlParams();
var urlSuffix = encodeQueryData(params);
var chipId = params.chipId;
var width = params.width ? params.width : 800;
var height = params.height ? params.height : 600;
var type = params.type ? params.type : "line";
var chartType = params.chartType ? params.chartType : "chart";

// Execute request for data
$.ajax({
    url: "data/chart?" + urlSuffix,
    success: (result) => {
        var field = JSON.parse(result).field;
        var values = JSON.parse(result).values;
        var responseTime = JSON.parse(result).responseTime;
        drawLineChart(field, values, responseTime, chipId, width, height, type, chartType);
    }
});