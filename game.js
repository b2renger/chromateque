class Grid_game {


    constructor(table, num) {
        this.tiles = [];
        this.c;
        this.row_index =0;
        this.num = num
        this.table = table;
        this.gap = 10; // select a random gap between each square
        // calculate the size of each square for the given number of squares and gap between them
        var cellsize = int((width/2 - (this.num + 1) * this.gap) /  this.num);

        for (var i = 0; i < this.num * this.num; i++) {
            var xpos = i % this.num;
            var ypos = int(i / this.num);
            var random_index = floor(random(this.table.getRowCount()));
            var name = table.getString(random_index, 0);
            var newC = "#" + table.getString(random_index, 2);
            var col = (newC);
            var t = new Tile(this.gap * (xpos + 1) + cellsize * xpos, this.gap * (ypos + 1) + cellsize * ypos, cellsize, col, name)
            this.tiles.push( t);
        }
        var newColor = "#" +  this.table.get(this.row_index, 2);
        var hi = (newColor);
        var name =  this.table.get(this.row_index, 0);
        reference = new Tile(width*3/4, height * 1 / 4, 120, hi, name);
    }

    display() {
        background(0);
        reference.display();
        fill(255);
        text("x" + nb_lasting, this.gap + width * 3 / 4, height * 1 / 4 + 75);
        text("Bonus points : " + bonusTime, this.gap + width * 3 / 4, height * 2 / 4 + 250);
        fill(255, 0, 0);
        text("score : " + score, this.gap + width * 3 / 4, height * 3 / 4 + 75, 400, 400);
        for (var i = 0; i < this.tiles.length; i++) {
            this.tiles[i].display();
            this.tiles[i].update();
        }
        this.check_grid();
        this.check_tiles();
    }

    check_grid() {
        var occurence = 0;
        for (var i = 0; i < this.tiles.length; i++) {
            if (!this.tiles[i].dead) {
                if (reference.col == this.tiles[i].col) {
                    occurence += 1;
                }
            }
        }
        if (occurence == 0) {
            var t = this.tiles[int(random(this.tiles.length))]
            while(t.dead && this.check_tiles() < this.tiles.length ){
                t = this.tiles[int(random(this.tiles.length))]
            }
            reference = new Tile(width * 3 / 4 - 180, height * 1 / 4, 120, t.col, '');
        }
        nb_lasting = occurence;
    }

    check_tiles() {
        var nb_dead = 0;
        for (var i = 0; i < this.tiles.length; i++) {
            if (this.tiles[i].dead) {
                nb_dead++;
            }
        }
        if (nb_dead >= this.tiles.length) {
            menu = 4;
            colors = currentTable;
            //newRoundTriangulation(floor(random(100)), 650, color(255));
        }
        return nb_dead
    }
}
