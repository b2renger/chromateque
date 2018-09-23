class Grid_game {

    constructor(table, num) {
        this.tiles = [];
        this.c;
        this.row_index =0;
        this.num = num
        this.table = table;
        this.gap = 10; // select a random gap between each square
        // calculate the size of each square for the given number of squares and gap between them
        var cellsize = int((width*0.5 - (this.num + 1) * this.gap) /  this.num);
        this.yoffset = (height / (this.num +2)) / 2
        for (var i = 0; i < this.num * this.num; i++) {
            var xpos = i % this.num;
            var ypos = int(i / this.num);
            //this.yoffset = (height / (this.num +2)) / 2
            var random_index = floor(random(this.table.getRowCount()));
            var name = table.getString(random_index, 0);
            var newC = "#" + table.getString(random_index, 2);

            var col = (newC);
            var t = new Tile(this.gap * (xpos + 1) + cellsize * xpos, this.yoffset + this.gap * (ypos + 1) + cellsize * ypos, cellsize, col, name)
            this.tiles.push( t);
        }
        var newColor = "#" +  this.table.get(this.row_index, 2);
        var hi = (newColor);
        var name =  this.table.get(this.row_index, 0);
        reference = new Tile(width*3/4, height * 1 / 4, cellsize, hi, name);
    }

    display() {
        push()
        background(0);

        reference.display();
        reference.xpos = width - this.gap - reference.siz *2 ;
        reference.ypos = height/2 - reference.siz/2
        fill(255);

        textAlign(LEFT , CENTER)
        text("x" + nb_lasting, reference.xpos + reference.siz *5 /4, reference.ypos + reference.siz/2);

        textAlign(RIGHT , CENTER)
        text("Bonus timer : " + bonusTime, - this.gap + width *3/3 , this.yoffset/2);

        fill(255);
        textAlign(LEFT , CENTER)
        text("Score : " + int(score), this.gap ,this.yoffset/2);
        for (var i = 0; i < this.tiles.length; i++) {
            this.tiles[i].display();
            this.tiles[i].update();
        }
        this.check_grid();
        this.check_tiles();
        pop()
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
            pg = newRoundTriangulation(250, 250, 500)
            menu =4
        }
        return nb_dead
    }
}
