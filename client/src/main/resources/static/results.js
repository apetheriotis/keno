var canvas = document.getElementById('selectionArea');
var context = canvas.getContext('2d');
var selectedDataArea = document.getElementById('selectedDataArea');
var selectedDataAreaContext = selectedDataArea.getContext('2d');
var circles = [];
var selected = [];

var draw = function (context, x, y, fillcolor, radius, linewidth, strokestyle, fontcolor, textalign, fonttype, filltext) {
    context.beginPath();
    context.arc(x, y, radius, 0, 2 * Math.PI, false);
    context.fillStyle = fillcolor;
    context.fill();
    context.lineWidth = linewidth;
    context.strokeStyle = strokestyle;
    context.stroke();
    context.fillStyle = fontcolor;
    context.textAlign = textalign;
    context.font = fonttype;
    context.fillText(filltext, x + 1, y + 6);
};

var Circle = function (x, y, radius) {
    this.left = x - radius;
    this.top = y - radius;
    this.right = x + radius;
    this.bottom = y + radius;
};

var drawCircle = function (context, x, y, fillcolor, radius, linewidth, strokestyle, fontcolor, textalign, fonttype, filltext, circles) {
    draw(context, x, y, fillcolor, radius, linewidth, strokestyle, fontcolor, textalign, fonttype, filltext);
    var circle = new Circle(x, y, radius);
    circles.push(circle);
};


$.ajax({
    url: '/api/v1/json/results',
    type: 'GET',
    contentType: 'application/json; charset=utf-8',
    dataType: 'json',
    async: false,
    success: function (msg) {

        $.each(msg.numbers, function (i, e) {
            selected.push(e)
        });
        console.log(selected)
        for (var j = 1; j < 9; j++) {
            for (var i = 1; i < 11; i++) {
                console.log("lala" + selected.indexOf(((j - 1) * 10) + i))
                if (selected.indexOf(((j - 1) * 10) + i) == -1) {
                    drawCircle(context, 60 * i, 60 * j, "yellow", 20, 1, "#003300", "black", "center", "bold 20px Arial", ((j - 1) * 10) + i, circles);
                } else {
                    drawCircle(context, 60 * i, 60 * j, "green", 20, 1, "#003300", "black", "center", "bold 20px Arial", ((j - 1) * 10) + i, circles);
                }
            }
        }
    }
});


Array.prototype.contains = function (obj) {
    var i = this.length;
    while (i--) {
        if (this[i] === obj) {
            return true;
        }
    }
    return false;
}


var howMany = 0;
function myFunction() {
    var drawId = document.getElementById("drawIdAread").value;
    $.ajax({
        url: '/api/v1/json/ticket/' + drawId,
        type: 'GET',
        contentType: 'application/json; charset=utf-8',
        dataType: 'json',
        async: false,
        success: function (msg) {
            if (msg.drawNo == "-1"){
                window.alert("No results yet for your draw...");
            }
            $.each(msg.numbers, function (i, e) {
                howMany++;
                draw(selectedDataAreaContext, 40 * howMany, 30, "green", 15, 1, "#003300", "black", "center", "bold 20px Arial", e);
            });
//            draw(selectedDataAreaContext, 50 * howMany + 50, 30, "green", 30, 1, "#003300", "black", "center", "bold 20px Arial", msg.winnings);
        }
    });
}