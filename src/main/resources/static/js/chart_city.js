/*
 * Copyright © Marc Auberer 2019 - 2020. All rights reserved
 */

function drawLineChart(label, series, responseTime, country, city, sensorCount, width, height, type, chartType) {
    let config = {
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
            text: "City: " + city + ", " + country + " - (Response time: " + responseTime + " ms) - " + sensorCount + " sensors"
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
let params = getAllUrlParams();
let urlSuffix = encodeQueryData(params);
let country = params.country;
let city = params.city;
let width = params.width ? params.width : 800;
let height = params.height ? params.height : 600;
let type = params.type ? params.type : "line";
let chartType = params.chartType ? params.chartType : "chart";

// Execute request for data
$.ajax({
    url: "data/chart?" + urlSuffix,
    success: (result) => {
        var field = JSON.parse(result).field;
        var values = JSON.parse(result).values;
        var responseTime = JSON.parse(result).responseTime;
        var sensorCount = JSON.parse(result).sensorCount;
        drawLineChart(field, values, responseTime, country, city, sensorCount, width, height, type, chartType);
    }
});