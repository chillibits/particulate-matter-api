// Functions

function getAllUrlParams() {
    var queryString = window.location.search.slice(1);
    var obj = {};
    if (queryString) {
        queryString = queryString.split("#")[0];
        var arr = queryString.split("&");

        for (var i = 0; i < arr.length; i++) {
            var a = arr[i].split("=");
            var paramName = a[0];
            var paramValue = typeof (a[1]) === "undefined" ? true : a[1];
            if (paramName.match(/\[(\d+)?\]$/)) {
                var key = paramName.replace(/\[(\d+)?\]/, "");
                if (!obj[key]) obj[key] = [];
                if (paramName.match(/\[\d+\]$/)) {
                    var index = /\[(\d+)\]/.exec(paramName)[1];
                    obj[key][index] = paramValue;
                } else {
                    obj[key].push(paramValue);
                }
            } else {
                if (!obj[paramName]) {
                    obj[paramName] = paramValue;
                } else if (obj[paramName] && typeof obj[paramName] === "string") {
                    obj[paramName] = [obj[paramName]];
                    obj[paramName].push(paramValue);
                } else {
                    obj[paramName].push(paramValue);
                }
            }
        }
    }
    return obj;
}

function drawLineChart(category, series) {
    Highcharts.chart("container", {
        chart: {
            type: "line",
            width: 800
        },

        title: {
            text: "Particulate matter data"
        },

        xAxis: {
            categories: category
        },

        tooltip: {
            formatter: function() {
                return "<strong>"+this.x+": </strong>"+ this.y;
            }
        },

        series: [{
            data: series
        }]
    });
}

// Main code
$.ajax({
    url: "data/chart/" + getAllUrlParams().chipId,
    success: result => {
        var time = JSON.parse(result).time;
        var values = JSON.parse(result).values;
        drawLineChart(time, values);
    }
});