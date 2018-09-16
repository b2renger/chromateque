
function randomPointsOnDisk(nb,x,y,radius){
    var pts = []
    for (var i = 0; i < nb; i++) {
        var r = random(radius)
        var  angle = random(TWO_PI)
        var xpos = x+r*cos(angle)
        var ypos = y+r*sin(angle)
        var coords = [xpos, ypos]
        pts[i] = coords
    }
    return pts

}

function randomPointsOnPlane(nb, x,y,w,h){
    var pts = []
     for (var i = 0; i < nb; i++) {
        var coords = [random(x,x+w), random(y,y+h)]
        pts[i] = coords
    }
    pts.push([x, y])
    pts.push([x+w, y])
    pts.push([x+w, y+h])
    pts.push([x, y+h])

    return pts
}

function drawDelaunay(coordinates) {
    for (var i = 0; i < coordinates.length; i += 1) {
        noStroke()
        fill(random(360), 100, 100)
        triangle(coordinates[i][0][0], coordinates[i][0][1],
            coordinates[i][1][0], coordinates[i][1][1],
            coordinates[i][2][0], coordinates[i][2][1]
        )
    }
}

function returnDelaunayCoordinates(points) {
    var coords = []
    for (let i = 0; i < delaunay.triangles.length; i += 3) {
        coords.push([
        points[delaunay.triangles[i]],
        points[delaunay.triangles[i + 1]],
        points[delaunay.triangles[i + 2]]
    ]);
    }
    return coords
}
