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
        rectMode(CENTER);
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

    resize(x, y, h, v) {
        this.xpos = x
        this.ypos = y

        this.hsize = h;
        this.vsize = v;
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
                background(0);
                splash = newRectTriangulation(0, 0, width, height, color(0), floor(random(100)));
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
                background(0);
                splash = newRectTriangulation(0, 0, width, height, color(0), floor(random(100)));
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
                currentTable = allcolors
                newHighscore = false
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

//a button to jump to highscores screen
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

//a button to display highscores for each color
class ButtonM6 extends ButtonM {

    constructor(xpos, ypos, hsize, vsize, col, name) {
        super(xpos, ypos, hsize, vsize, col, name);
    }

    update() {
        if (this.over(mouseX, mouseY)) {
            noFill();
            stroke(180);
            strokeWeight(2);
            rect(this.xpos, this.ypos, this.hsize + 10, this.vsize + 10, 15);
            displayHighscore(this.col, this.name, width*2 /3, 0)



        }
    }
}

function displayHighscore(col, name, x, y) {
    push()
    textSize(20)
    textAlign(LEFT, CENTER)
    rectMode(CENTER)
    noStroke()

    fill(red(col), green(col), blue(col), alpha(col)-25);


    var hGrid = height / (14 + 4)
    var wGrid = width / 6;
    //rect(x + width/20,y+ hGrid + 7*hGrid , width/3, 7 *hGrid*2 )
    textSize(hGrid)
    //fill(0)
    for (var i = 0; i < 7; i++) {
        var s = name + "-" + (i + 3) + "x" + (i + 3)
        text( (i + 3) + "x" + (i + 3) + " : " + localStorage.getItem(s), x, y +    hGrid*2 + i * hGrid*2)

    }


    pop()

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

    resize(x, y, s) {
        this.xpos = x
        this.ypos = y
        this.siz = s;
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
                // score updating
                if (mouseIsPressed && millis() > gtime + 200) {
                    gtime = millis();
                    var dE = deltaE(this.col, reference.col);
                    if (dE == 0) {
                        this.col = color(0);
                        this.dead = true;
                        score += 50 + bonusTime * 5;
                        bonusTime = 10;
                        bonusClock = true;
                        lastSec = second();
                    } else {
                        score -= (10 + dE + bonusTime * 2);
                        bonusTime = 0;
                        bonusClock = false;

                    }
                }
            }
        }
    }
}
