var canvas = document.getElementById('selectionArea');
var context = canvas.getContext('2d');
var selectedDataArea = document.getElementById('selectedDataArea');
var selectedDataAreaContext = selectedDataArea.getContext('2d');
var circles = [];

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

for (var j = 1; j < 9; j++) {
    for (var i = 1; i < 11; i++) {
        drawCircle(context, 60 * i, 60 * j, "yellow", 20, 1, "#003300", "black", "center", "bold 20px Arial", ((j - 1) * 10) + i, circles);
    }
}

var selected = [];
var howMany = 0;

$('#selectionArea').click(function (e) {
    var clickedX = e.pageX - this.offsetLeft;
    var clickedY = e.pageY - this.offsetTop;
    for (var i = 0; i < circles.length; i++) {
        if (clickedX < circles[i].right && clickedX > circles[i].left && clickedY > circles[i].top && clickedY < circles[i].bottom) {

            if (howMany <= 11) {
                if (!selected.contains(i + 1)) {
                    howMany++;
                    selected.push(i + 1)
                    draw(selectedDataAreaContext, 50 * howMany, 30, "green", 20, 1, "#003300", "black", "center", "bold 20px Arial", i + 1);
                }
            }
        }
    }
});

function myFunction() {
    var x = document.getElementById("myBtn").value;
    var arr = {
        numbers: selected
    };
    $.ajax({
        url: '/api/v1/json/ticket',
        type: 'POST',
        data: JSON.stringify(arr),
        contentType: 'application/json; charset=utf-8',
        dataType: 'json',
        async: false,
        success: function (msg) {
            var elem = document.getElementById("drawId");
            elem.value = msg.ticketNo;
            window.alert("Your ticket has been submitted for draw "+ msg.drawNo);
        }
    });



}

Array.prototype.contains = function (obj) {
    var i = this.length;
    while (i--) {
        if (this[i] === obj) {
            return true;
        }
    }
    return false;
}