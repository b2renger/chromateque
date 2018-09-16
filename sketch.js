var points = [[]];
var delaunay
var coordinates
var seed

function setup() {
    createCanvas(windowWidth, windowHeight)
    background(0)
    seed = random(99999)
    colorMode(HSB, 360, 100, 100, 100)

    points = randomPointsOnDisk(100, width/2,height/2, 200)
    delaunay = Delaunator.from(points);
    coordinates = returnDelaunayCoordinates(points)

    console.log(deltaE(color(255,0,0), color(255,50,55)))
    console.log(deltaE(color(255,0,0), color(0,0,255)))

}

function draw() {
    background(0)
    randomSeed(seed)

    drawDelaunay(coordinates)

}

function mousePressed(){
    seed = random(9999)
    points = randomPointsOnPlane(100, 50,50, 500, 400)
    delaunay = Delaunator.from(points);
    coordinates = returnDelaunayCoordinates(points)

}


function windowResized() {
    resizeCanvas(windowWidth, windowHeight)

}
