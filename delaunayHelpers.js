function newRectTriangulation(x, y, w, h) {
    var pg = createGraphics(w,h)
    var points = randomPointsOnPlane(x, y, w, h, 200)
    var delaunay = Delaunator.from(points);
    console.log (delaunay)
    console.log(points)
    var coordinates = returnDelaunayCoordinates(points,delaunay)
     pg = drawDelaunay(coordinates, pg)

}

function randomPointsOnDisk(nb, x, y, radius) {
    var pts = []
    for (var i = 0; i < nb; i++) {
        var r = random(radius)
        var angle = random(TWO_PI)
        var xpos = x + r * cos(angle)
        var ypos = y + r * sin(angle)
        var coords = [xpos, ypos]
        pts[i] = coords
    }
    return pts

}

function randomPointsOnPlane(nb, x, y, w, h) {
    var pts = []
    for (var i = 0; i < nb; i++) {
        var coords = [random(x, x + w), random(y, y + h)]
        pts[i] = coords
    }
    pts.push([x, y])
    pts.push([x + w, y])
    pts.push([x + w, y + h])
    pts.push([x, y + h])

    return pts
}

function drawDelaunay(coordinates, pg) {

    for (var i = 0; i < coordinates.length; i += 1) {
        noStroke()

        var random_index = floor(random(currentTable.getRowCount()));
        var newC = "#" + currentTable.get(random_index, 2);
        pg.fill(newC)
       // fill(random(360), 100, 100)
        pg.triangle(coordinates[i][0][0], coordinates[i][0][1],
            coordinates[i][1][0], coordinates[i][1][1],
            coordinates[i][2][0], coordinates[i][2][1]
        )
    }
    return pg
}

function returnDelaunayCoordinates(points,delaunay) {
    var coords = []
    //console.log(points)
    for (let i = 0; i < delaunay.triangles.length; i += 3) {
        coords.push([
        points[delaunay.triangles[i]],
        points[delaunay.triangles[i + 1]],
        points[delaunay.triangles[i + 2]]
    ]);
    }
    return coords
}
