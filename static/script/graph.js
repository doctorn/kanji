var canvas = document.getElementById('graph');
var ctx = canvas.getContext('2d');

canvas.width  = window.innerWidth;
canvas.height = window.innerHeight;

function Node(value) {
    this.x = 0;
    this.y = 0;
    this.size = 20;
    this.value = value;
    this.neighbours = [];

    this.render = function(ctx) {
        ctx.strokeStyle = "white";
        for (var neighbour in this.neighbours) {
            ctx.beginPath();
            ctx.moveTo(this.x, this.y);
            ctx.lineTo(this.neighbours[neighbour].x, this.neighbours[neighbour].y);
            ctx.stroke();
        }

        ctx.fillStyle = "white";
        ctx.beginPath();
        ctx.arc(this.x, this.y, this.size, 0, Math.PI * 2, true);
        ctx.fill();

        ctx.fillStyle = "black";
        ctx.font = "24px sans-serif";
        ctx.textAlign = "center";
        ctx.fillText(this.value, this.x, this.y + 8);
    }

    this.update = function() {

    }

    this.addNeighbour = function(node) {
        this.neighbours.push(node);
    }

    this.setPosition = function(x, y) {
        this.x = x;
        this.y = y;
    }
}

var nodes = [];

function update() {
    canvas.width  = window.innerWidth;
    canvas.height = window.innerHeight;
    for (var node in nodes) nodes[node].update(); 
    render();
}

function render() {
    ctx.fillStyle = "#BBB";
    ctx.fillRect(0, 0, canvas.width, canvas.height);
    for (var node in nodes) nodes[node].render(ctx); 
}

function addNode(node) {
    nodes.push(node);
}

setInterval(update, 20);
