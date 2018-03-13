var canvas = document.getElementById('graph');
var ctx = canvas.getContext('2d');

canvas.width  = window.innerWidth;
canvas.height = window.innerHeight;

function Node(value) {
    this.x = canvas.width / 2;
    this.y = canvas.height / 2;
    this.Fx = 0;
    this.Fy = 0;
    this.size = 20;
    this.value = value;
    this.neighbours = [];
    this.highlight = false;
    this.hover = false;
    this.selected = false;

    this.setHighlight = function(highlight) {
        this.highlight = highlight;
    }

    this.renderEdges = function(ctx) {
        ctx.strokeStyle = "white";
        ctx.lineWidth = 3;
        for (var neighbour in this.neighbours) {
            ctx.beginPath();
            ctx.moveTo(this.x, this.y);
            ctx.lineTo(this.neighbours[neighbour].x, this.neighbours[neighbour].y);
            ctx.stroke();
        }
    }

    this.bounds = function(x, y) {
        return (distance(x, this.x, y, this.y) <= this.size * this.size); 
    }

    this.render = function(ctx) {
        if (!this.highlight) ctx.fillStyle = "white";
        else ctx.fillStyle = "#03A9F4";
        ctx.beginPath();
        ctx.arc(this.x, this.y, this.size, 0, Math.PI * 2, true);
        ctx.fill();

        if (!this.highlight) ctx.fillStyle = "black";
        else ctx.fillStyle = "white";
        ctx.font = (this.size + 4) + "px sans-serif";
        ctx.textAlign = "center";
        ctx.fillText(this.value, this.x, this.y + 8);
    }

    this.update = function() {
        if (this.hover && this.size < 24) this.size += 1;
        else if (!this.hover && this.size > 20) this.size -= 1;
        if (!this.selected) this.x += this.Fx;
        if (!this.selected) this.y += this.Fy;
    }

    this.addNeighbour = function(node) {
        if (!this.neighbours.includes(node)) this.neighbours.push(node);
    }

    this.setPosition = function(x, y) {
        this.x = x;
        this.y = y;
    }
}

var nodes = [];

function distance(x1, x2, y1, y2) {
    return (x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1);
}

function nDistance(node1, node2) {
    return distance(node1.x, node2.x, node1.y, node2.y);
} 

function update() {
    canvas.width  = window.innerWidth;
    canvas.height = window.innerHeight;

    for (var node in nodes) {
        var frepx = 0;
        var frepy = 0;
        var fspringx = 0;
        var fspringy = 0; 

        for (var neighbour in nodes) {
            if ((nodes[node].neighbours.includes(nodes[neighbour])
                    || nodes[neighbour].neighbours.includes(nodes[node]))
                    && nodes[neighbour] !== nodes[node]) {
                var temp = nDistance(nodes[neighbour], nodes[node]);
                fspringx += 0.0025 * Math.log(temp) 
                        * (nodes[neighbour].x - nodes[node].x);
                fspringy += 0.0025 * Math.log(temp)
                        * (nodes[neighbour].y - nodes[node].y);
            }
        }
        
        for (var other in nodes) {
            if (!nodes[node].neighbours.includes(other) 
                    && !nodes[other].neighbours.includes(node)
                    && nodes[other] !== nodes[node]) {
                frepx += 100 / (nDistance(nodes[other], nodes[node]))
                        * (nodes[node].x - nodes[other].x);
                frepy += 100 / (nDistance(nodes[other], nodes[node]))
                        * (nodes[node].y - nodes[other].y);
            }
        }

        nodes[node].Fx = frepx + fspringx;
        nodes[node].Fy = frepy + fspringy;
    }      

    for (var node in nodes) nodes[node].update(); 
    render();
}

function render() {
    ctx.fillStyle = "#BBB";
    ctx.fillRect(0, 0, canvas.width, canvas.height);
    for (var node in nodes) nodes[node].renderEdges(ctx); 
    for (var node in nodes) nodes[node].render(ctx); 
}

function addNode(node) {
    nodes.push(node);
}

function getMousePos(canvas, evt) {
    var rect = canvas.getBoundingClientRect();
    return {
        x: evt.clientX - rect.left,
        y: evt.clientY - rect.top
    };
}

canvas.addEventListener('mousemove', function(evt) {
    var mousePos = getMousePos(canvas, evt);
    for (var node in nodes) {
        if (nodes[node].bounds(mousePos.x, mousePos.y))
            nodes[node].hover = true;
        else nodes[node].hover = false;

        if (nodes[node].selected) {
            nodes[node].x = mousePos.x;
            nodes[node].y = mousePos.y;
        }
    }
}, false);

canvas.addEventListener('mousedown', function(evt) {
    var mousePos = getMousePos(canvas, evt);
    for (var node in nodes) {
        if (nodes[node].bounds(mousePos.x, mousePos.y)) {
            nodes[node].selected = true;
            break;
        }
    }
}, false);

canvas.addEventListener('mouseup', function(evt) {
    for (var node in nodes)
        nodes[node].selected = false;
}, false);

setInterval(update, 20);
