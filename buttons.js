// a generic class for interface buttons
class ButtonM {

    constructor(xpos, ypos, hsize, vsize, c, name) {
        this.xpos = xpos;
        this.ypos = ypos;
        this.col = c;
        this.hsize = hsize;
        this.vsize = vsize;
        this.name = name;
        this.active = false;
    }

    display() {
        push();
        // noStroke();
        rectMode(CENTER);
        //textMode(CENTER);
        stroke(this.col)
        fill(this.col.toString());
        rect(this.xpos, this.ypos, this.hsize, this.vsize, 10);
        fill(0);
        text(this.name, this.xpos, this.ypos);
        pop();
    }

    over(x, y) {
        var b;
        if (x > this.xpos - this.hsize / 2 && x < this.xpos + this.hsize / 2 && y > this.ypos - this.vsize / 2 && y < this.ypos + this.vsize / 2) {
            b = true;
        } else {
            b = false;
        }
        return b;
    }
}

// a button that selects the color
class ButtonM1 extends ButtonM {


    constructor(xpos, ypos, hsize, vsize, col, name, maTable) {
        super(xpos, ypos, hsize, vsize, col, name);
        this.maTable = maTable;
        this.active = false;
    }

    update() {
        if (this.over(mouseX, mouseY)) {
            noFill();
            stroke(this.col);
            strokeWeight(2);
            rect(this.xpos, this.ypos, this.hsize + 10, this.vsize + 10, 15);
            if (mouseIsPressed && millis() > gtime + 200) {
                gtime = millis();
                this.active = true;
                currentTable = this.maTable;
                colors = currentTable;
                background(0);
                 splash = newRectTriangulation(0,0, width, height, color(0), floor(random(100)));
                colorName = this.name;
                menu = 2;
            } else {
                this.active = false;
            }
        }
    }
}

// a button that selects a grid dimension
class ButtonM2 extends ButtonM {


    constructor(xpos, ypos, hsize, vsize, col, name, value) {
        super(xpos, ypos, hsize, vsize, col, name);
        this.value = value;
    }

    update() {
        if (this.over(mouseX, mouseY)) {
            noFill();
            stroke(180);
            strokeWeight(2);
            rect(this.xpos, this.ypos, this.hsize + 10, this.vsize + 10, 15);
            if (mouseIsPressed && millis() > gtime + 200) {
                gtime = millis();
                colors = loadTable("assets/allcolors.csv", "header");
                background(0);
                splash = newRectTriangulation(0,0, width, height, color(0), floor(random(100)));
                game = new Grid_game(currentTable, this.value);
                dimensions = this.name;
                menu = 3;
                score = 0;
            }
        }
    }
}

// a home button
class ButtonM3 extends ButtonM {

    constructor(xpos, ypos, hsize, vsize, col, name, value) {
        super(xpos, ypos, hsize, vsize, col, name);
        this.value = value;
    }

    update() {
        if (this.over(mouseX, mouseY)) {
            // first draw a rectangle around the button on over
            noFill();
            stroke(180);
            strokeWeight(2);
            rect(this.xpos, this.ypos, this.hsize + 10, this.vsize + 10, 15);
            // then click
            if (mouseIsPressed && millis() > gtime + 200) {
                gtime = millis();
                menu = 0;
                colors = loadTable("assets/allcolors.csv", "header");
                pg = new newRoundTriangulation(floor(random(100)), 650, color(0));
            }
        }
    }
}

//a button to start playing selects the color
class ButtonM4 extends ButtonM {

    constructor(xpos, ypos, hsize, vsize, col, name) {
        super(xpos, ypos, hsize, vsize, col, name);
    }

    update() {
        if (this.over(mouseX, mouseY)) {
            noFill();
            stroke(180);
            strokeWeight(2);
            rect(this.xpos, this.ypos, this.hsize + 10, this.vsize + 10, 15);
            if (mouseIsPressed && millis() > gtime + 200) {
                gtime = millis();
                this.active = true;
                colorName = this.name;
                menu = 1;
            } else {
                this.active = false;
            }
        }
    }
}

//a button to start playing selects the color
class ButtonM5 extends ButtonM {

    constructor(xpos, ypos, hsize, vsize, col, name) {
        super(xpos, ypos, hsize, vsize, col, name);
    }

    update() {
        if (this.over(mouseX, mouseY)) {
            noFill();
            stroke(180);
            strokeWeight(2);
            rect(this.xpos, this.ypos, this.hsize + 10, this.vsize + 10, 15);
            if (mouseIsPressed && millis() > gtime + 200) {
                gtime = millis();
                background(255);
                this.active = true;
                colorName = this.name;
                menu = 5;
            } else {
                this.active = false;
            }
        }
    }
}

//a tile Class that is actually a buttonM
class Tile extends ButtonM {


    constructor(xpos, ypos, siz, col, name) {
        super(xpos, ypos, siz, siz, col, name);
        this.siz = siz;
        this.dead = false;
        this.last_click = false;
    }


    display() {
        if (!this.dead) {
            push();
            rectMode(CORNER);
            noStroke();
            fill(this.col.toString());

            rect(this.xpos, this.ypos, this.siz, this.siz, 10);
            pop();
        }
    }

    over(x, y) {
        var b;
        if (x > this.xpos && x < this.xpos + this.siz && y > this.ypos && y < this.ypos + this.siz && mousePressed) {
            b = true;
        } else {
            b = false;
        }
        return b;
    }

    update() {
        if (!this.dead) {
            if (this.over(mouseX, mouseY)) {
                push();
                rectMode(CORNER);
                noFill();
                stroke(this.col.toString());
                strokeWeight(2);
                rect(this.xpos - 5, this.ypos - 5, this.siz + 9, this.siz + 9, 15);
                pop();
                /////////////////////////////////////////////////////////////////////////////////////////////// score updating
                if (mouseIsPressed && millis() > gtime + 200) {
                    gtime = millis();
                    //var m = match(name, reference.name); // compare name to reference name
                    var dE = deltaE(this.col, reference.col);
                    //console.log(dE)
                    if (dE == 0) {
                        this.col = color(0);
                        this.dead = true;
                        score += 50 + bonusTime * 5;
                        bonusTime = 10;
                        bonusClock = true;
                        lastSec = second();
                    } else {
                        score -= (10 + dE + bonusTime * 2);
                        // gNotificationManager.notify(1, gNotification);
                        bonusTime = 0;
                        bonusClock = false;
                        /////////////////////// need for vibration
                    }
                }
            }
        }
    }
} //endclass
