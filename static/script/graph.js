var canvas = document.getElementById('graph');
var ctx = canvas.getContext('2d');

var x = 0;

function update() {
    //TOOD
    x += 1;
    render();
}

function render() {
    ctx.clearRect(0, 0, canvas.width, canvas.height);
    ctx.fillStyle = 'green';
    ctx.fillRect(x, 0, 10, 10);
    //TOOD
}

setInterval(update, 20);
