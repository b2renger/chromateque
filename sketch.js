// for those reading this : sorry for the ugliness of this code
// I started this project long ago when I started programming, I began with processing, the went to android, got back to processing and finally switched to js


/*

todo :

- remove unwanted colors
- add animation when breaking a tile ( triangulation of random points inside a tile and shattering)
- add sounds
- arrange the way highscores are displayed


*/


var seed


// menu buttons
var blanc, bleu, brun, gris, jaune, orange, rose, rouge, vert, violet; // color selection buttons
var trois, quatre, cinq, six, sept, huit, neuf; // grid size buttons
var goback; // navigation button go back to main menu
var play; // start game

var menu = 0
var score = 0
var bonusTime = 10;
var lastSec = 0;
var bonusClock = true;

var gtime = 0; // holds time to prevent from multiple firing when clicking
var currentTable; // holds the selected table
var allcolors
var colorName; // holds the current colorName that is being played
var dimensions; // holds the dimesion of the grid
var colors;
var newHighscore = false;


var buttonSize = 80;
var anim = 0;

var game; // a grid of tiles for the game
var reference; // a reference tile, you need to match this coulour in the grid
var nb_lasting = 0; // keep track of how much tiles of the same colour we still have

var pg
var splash

function preload() {
    currentTable = loadTable("assets/allcolors.csv", "header");
    allcolors = currentTable

}


function setup() {
    createCanvas(windowWidth, windowHeight)
    background(0)
    seed = random(99999)


    rectMode(CENTER);
    textAlign(CENTER, CENTER);
    imageMode(CENTER);
    pixelDensity(1)

    menu = 0;
    lastSec = second();
    pg = newRoundTriangulation(250, 250, 500)
    splash = newRectTriangulation(0, 0, width, height, color(0), floor(random(100)));

    var alpha = 180
    // init color selection buttons
     var xpos = width / 2
    var wColor = width / 4
    var hColor = height / (20 + 4);
    var hGrid = height / (14 + 4)
    var wGrid = width / 6;

    textSize(hColor/2)

    blanc = new ButtonM1(xpos, hColor * 2, wColor, hColor, color(255, alpha), "white", loadTable("assets/White.csv", "header"));
    bleu = new ButtonM1(xpos, hColor * 4, wColor, hColor, color(0, 0, 255, alpha), "blue", loadTable("assets/Blue.csv", "header"));
    brun = new ButtonM1(xpos, hColor * 6, wColor, hColor, color(149, 80, 5, alpha), "brown", loadTable("assets/Brown.csv", "header"));
    gris = new ButtonM1(xpos, hColor * 8, wColor, hColor, color(150, alpha), "gray", loadTable("assets/Gray.csv", "header"));
    jaune = new ButtonM1(xpos, hColor * 10, wColor, hColor, color(255, 255, 2, alpha), "yellow", loadTable("assets/Yellow.csv", "header"));
    orange = new ButtonM1(xpos, hColor * 12, wColor, hColor, color(255, 136, 5, alpha), "orange", loadTable("assets/Orange.csv", "header"));
    rose = new ButtonM1(xpos, hColor * 14, wColor, hColor, color(255, 180, 180, alpha), "pink", loadTable("assets/Pink.csv", "header"));
    rouge = new ButtonM1(xpos, hColor * 16, wColor, hColor, color(255, 0, 0, alpha), "red", loadTable("assets/Red.csv", "header"));
    vert = new ButtonM1(xpos, hColor * 18, wColor, hColor, color(0, 255, 0, alpha), "green", loadTable("assets/Green.csv", "header"));
    violet = new ButtonM1(xpos, hColor * 20, wColor, hColor, color(255, 0, 255, alpha), "purple", loadTable("assets/Purple.csv", "header"));
    // init grid dimension buttons
    trois = new ButtonM2(xpos, hGrid * 2, wGrid, hGrid, color(255, alpha), "3x3", 3);
    quatre = new ButtonM2(xpos, hGrid * 4, wGrid, hGrid, color(255, alpha), "4x4", 4);
    cinq = new ButtonM2(xpos, hGrid * 6, wGrid, hGrid, color(255, alpha), "5x5", 5);
    six = new ButtonM2(xpos, hGrid * 8, wGrid, hGrid, color(255, alpha), "6x6", 6);
    sept = new ButtonM2(xpos, hGrid * 10, wGrid, hGrid, color(255, alpha), "7x7", 7);
    huit = new ButtonM2(xpos, hGrid * 12, wGrid, hGrid, color(255, alpha), "8x8", 8);
    neuf = new ButtonM2(xpos, hGrid * 14, wGrid, hGrid, color(255, alpha), "9x9", 9);
    // init go back to main menu button
    goback = new ButtonM3(width / 2, height * 11 / 12, wColor, hColor, color(255), "Go back", 8);
    play = new ButtonM4(width / 2, height * 11 / 12, wColor, hColor, color(255), "Play");
     highscores = new ButtonM5(width / 2, height * 10 / 12, wColor, hColor, color(255), "Highscores");


}

function draw() {

    randomSeed(seed)
    background(0);
    anim += 0.0065;

    if (menu == 0) { // show access to gplay menu (ie menu 5, or to the game)
        background(0);
        push();
        translate(width / 2, height /2);
        stroke(255)
        image(pg, 0, 0);
        pop();
        score = 0;
        play.display();
        play.update();
        highscores.display();
        highscores.update();

    } else if (menu == 1) { // display color selection buttons
        push();
        translate(width / 2, height / 2);
        image(splash, 0, 0, width, height);
        pop();
        blanc.display();
        blanc.update();
        bleu.display();
        bleu.update();
        brun.display();
        brun.update();
        gris.display();
        gris.update();
        jaune.display();
        jaune.update();
        orange.display();
        orange.update();
        rose.display();
        rose.update();
        rouge.display();
        rouge.update();
        vert.display();
        vert.update();
        violet.display();
        violet.update();

    } else if (menu == 2) { // display dimension selection buttons
        push();
        translate(width / 2, height / 2);
        image(splash, 0, 0, width, height);
        pop();
        trois.display();
        trois.update();
        quatre.display();
        quatre.update();
        cinq.display();
        cinq.update();
        six.display();
        six.update();
        sept.display();
        sept.update();
        huit.display();
        huit.update();
        neuf.display();
        neuf.update();

    } else if (menu == 3) { // play the game (the whole game logic is coded in the class
        if (bonusClock) {
            if (second() == (lastSec + 1) % 60) {
                lastSec = second();
                bonusTime -= 1; // check Tile score update to reset
                bonusTime = constrain(bonusTime, 0, 10);
            }
        }
        game.display();

    } else if (menu == 4) {
        background(255);
        push();
        translate(width / 2, height /2);
        rotate(anim);
        image(pg, 0, 0);
        pop();
        noStroke()
        fill(0);
        score = int (score)
        if(newHighscore){
            text(" CONGRATULATIONS this a new personal best ! : " , width / 2, height * 3 / 24);
        }
        text(" You Scored : " +score + " points", width / 2, height * 1 / 24);

        goback.display();
        goback.update();
    }
    else if (menu == 5){

        for (var i = 0 ; i < localStorage.length ; i++){
            noStroke()
            fill(255)
            text(localStorage.key(i) + " : " + localStorage.getItem(localStorage.key(i)), width/2, 10 + i*20)

        }
        goback.display();
        goback.update();
    }

}

function mousePressed() {
    seed = random(9999)
    pg = newRoundTriangulation(250, 250, 500)
    splash = newRectTriangulation(0, 0, width, height, color(0), floor(random(100)));

}


function windowResized() {
    resizeCanvas(windowWidth, windowHeight)

    var xpos = width / 2
    var wColor = width / 4
    var hColor = height / (20 + 4);
    var hGrid = height / (14 + 4)
    var wGrid = width / 6;

    blanc.resize(xpos, hColor * 2, wColor, hColor);
    bleu.resize(xpos, hColor * 4, wColor, hColor);
    brun.resize(xpos, hColor * 6, wColor, hColor);
    gris.resize(xpos, hColor * 8, wColor, hColor);
    jaune.resize(xpos, hColor * 10, wColor, hColor);
    orange.resize(xpos, hColor * 12, wColor, hColor);
    rose.resize(xpos, hColor * 14, wColor, hColor);
    rouge.resize(xpos, hColor * 16, wColor, hColor);
    vert.resize(xpos, hColor * 18, wColor, hColor);
    violet.resize(xpos, hColor * 20, wColor, hColor);
    // init grid dimension buttons
    trois.resize(xpos, hGrid * 2, wGrid, hGrid);
    quatre.resize(xpos, hGrid * 4, wGrid, hGrid);
    cinq.resize(xpos, hGrid * 6, wGrid, hGrid);
    six.resize(xpos, hGrid * 8, wGrid, hGrid);
    sept.resize(xpos, hGrid * 10, wGrid, hGrid);
    huit.resize(xpos, hGrid * 12, wGrid, hGrid);
    neuf.resize(xpos, hGrid * 14, wGrid, hGrid);
    // init go back to main menu button
    goback = new ButtonM3(width / 2, height * 11 / 12, wColor, hColor, color(255), "Go back", 8);
    play = new ButtonM4(width / 2, height * 11 / 12, wColor, hColor, color(255), "Play");

    if (game != null) {
        var cellsize = int((width *0.5 - (game.num + 1) * game.gap) / game.num);
        var yoffset = (height / (game.num+2)) / 2
        for (var i = 0; i < game.num * game.num; i++) {
            var xpos = game.gap + i % game.num * (cellsize+game.gap);
            var ypos = yoffset + game.gap  + int(i / game.num) * (cellsize+game.gap);
            game.tiles[i].resize(xpos, ypos, cellsize, cellsize)

        }
        reference.siz = cellsize
    }

    textSize(hColor/2);
}
