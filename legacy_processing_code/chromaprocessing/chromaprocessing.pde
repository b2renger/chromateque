
import java.lang.reflect.Field;
import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PGraphics;
import processing.core.PVector;
import processing.data.Table;


////////////////////////////////////////////////////////////////////////////////////////////////////	
///////////////////////////////////////////////////////////////////////////////////////////////////	
//Processing stuff starts here

int hsize ;
int vsize ;

PFont title;

Grid_game game; // a grid of tiles for the game
Tile reference; // a reference tile, you need to match this coulour in the grid
int nb_lasting = 0; // keep track of how much tiles of the same colour we still have

//navigation menu index (0 is home, 1 is color selection, 2 is dimension selection, 3 is game , 4 is splash score + submit show
int menu = 0 ; 

Table currentTable; // holds the selected table
String colorName; // holds the current colorName that is being played
String dimensions; // holds the dimesion of the grid
int score = 0; // holds the score !

float gtime = 0; // holds time to prevent from multiple firing when clicking

ButtonM1 blanc, bleu, brun, gris, jaune, orange, rose, rouge, vert, violet; // color selection buttons
ButtonM2 trois, quatre, cinq, six, sept, huit, neuf; // grid size buttons

ButtonM3  goback; // navigation button go back to main menu

ButtonM4 play;


// triangulate beautifull splash screens
CTriangulator triangulator;
int mNbVertices = 100;
Table colors;
float noise = random(500);
PGraphics pg;
PGraphics splash;


// game stuff
int bonusTime = 10;
int lastSec = 0;
boolean bonusClock =  true;
int buttonSize = 80;

float anim = 0;

public void setup() {
  background(0);
  colors = loadTable("allcolors.csv", "header");
  pg = createGraphics(650, 650);
  splash = createGraphics(width, height);
  newRoundTriangulation(floor(random(100)), 650, color(0));
  newSquareTriangulation(floor(random(100)), width, height, color(0));




  // connect the play services on startup

  // processing stuff

  hsize = displayWidth;
  vsize = displayHeight;
  title = loadFont("Tunga-Bold-48.vlw");
  size(displayWidth, displayHeight);
  textFont(title);
  textSize(56);
  rectMode(CENTER);
  textAlign(CENTER, CENTER);
  imageMode(CENTER);

  float alpha = 100;
  // init color selection buttons
  blanc = new ButtonM1 (width/2, height*2/12, 500, buttonSize, color(255, alpha), "white", loadTable("White.csv", "header"));
  bleu = new ButtonM1 (width/2, height*3/12, 500, buttonSize, color(0, 0, 255, alpha), "blue", loadTable("Blue.csv", "header"));
  brun = new ButtonM1 (width/2, height*4/12, 500, buttonSize, color(149, 80, 5, alpha), "brown", loadTable("Brown.csv", "header"));
  gris = new ButtonM1 (width/2, height*5/12, 500, buttonSize, color(150, alpha), "gray", loadTable("Gray.csv", "header"));
  jaune = new ButtonM1 (width/2, height*6/12, 500, buttonSize, color(255, 255, 2, alpha), "yellow", loadTable("Yellow.csv", "header"));
  orange = new ButtonM1 (width/2, height*7/12, 500, buttonSize, color(255, 136, 5, alpha), "orange", loadTable("Orange.csv", "header"));
  rose = new ButtonM1 (width/2, height*8/12, 500, buttonSize, color(255, 180, 180, alpha), "pink", loadTable("Pink.csv", "header"));
  rouge = new ButtonM1 (width/2, height*9/12, 500, buttonSize, color(255, 0, 0, alpha), "red", loadTable("Red.csv", "header"));
  vert = new ButtonM1 (width/2, height*10/12, 500, buttonSize, color(0, 255, 0, alpha), "green", loadTable("Green.csv", "header"));
  violet = new ButtonM1 (width/2, height*11/12, 500, buttonSize, color(255, 0, 255, alpha), "purple", loadTable("Purple.csv", "header"));

  // init grid dimension buttons
  trois = new ButtonM2 (width/2, height*2/10, 250, buttonSize, color(255, alpha), "3x3", 3);
  quatre = new ButtonM2 (width/2, height*3/10, 250, buttonSize, color(255, alpha), "4x4", 4);
  cinq = new ButtonM2 (width/2, height*4/10, 250, buttonSize, color(255, alpha), "5x5", 5);
  six = new ButtonM2 (width/2, height*5/10, 250, buttonSize, color(255, alpha), "6x6", 6);
  sept = new ButtonM2 (width/2, height*6/10, 250, buttonSize, color(255, alpha), "7x7", 7);
  huit = new ButtonM2 (width/2, height*7/10, 250, buttonSize, color(255, alpha), "8x8", 8);
  neuf = new ButtonM2 (width/2, height*8/10, 250, buttonSize, color(255, alpha), "9x9", 15);

  // init go back to main menu button
  goback =  new ButtonM3 (width/2, height*11/12, 500, buttonSize, color(255), "Go back", 8);
  play =  new ButtonM4 (width/2, height*9/12, 500, buttonSize, color(255), "Play");
  //gplus =  new ButtonM5 (width/2, height*11/12, 500, buttonSize, color(255), "Connect to G+");

  // start on menu 0
  menu = 0 ;
  lastSec=second();
}

public void draw() {

  background(0);
  anim += 0.015;

  if (menu == 0) { // show access to gplay menu (ie menu 5, or to the game)
    // hideSignInOutBar();
    background(0);
    pushMatrix();
    translate(width/2, height*4/12);

    image(pg, 0, 0);
    popMatrix();	
    score = 0; 
    play.display();
    play.update();
    //gplus.display();
    //gplus.update();
  } else if (menu == 1) { // display color selection buttons
    pushMatrix();
    translate(width/2, height/2);
    image(splash, 0, 0);
    popMatrix();	

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
  } else if (menu ==2) { // display dimension selection buttons
    pushMatrix();
    translate(width/2, height/2);
    image(splash, 0, 0);
    popMatrix();
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
      if (second() == (lastSec +1)%60) {
        lastSec = second();
        bonusTime -=1; // check Tile score update to reset
        bonusTime = constrain(bonusTime, 0, 10);
      }
    }
    game.display();
  } else if (menu == 4) {		
    background(255);
    pushMatrix();
    translate(width/2, height*3/12);
    rotate(anim);
    image(pg, 0, 0, 300, 300);
    popMatrix();	
    fill(0);
    text (" CONGRATULATIONS ! ", width/2, height*1/12);
    text (" You Scored : "+ score +" points", width/2, height*5/12);
    //text (score , width/2+25, height*3/10);
    goback.display();
    goback.update();
    // submit and show score buttons
  }
}

public void mousePressed() {
}

public void keyPressed() {
  menu = 0;


  //println("kekekekekek : " + key);
  //newTriangulation(floor(random(100)),600,);
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// The Game !! Finally !
class Grid_game {

  Table currentTable ;
  Tile[] tiles;
  int c;
  int row_index;
  int gap;

  Grid_game(Table table, int num) {
    currentTable = table;
    gap = 10; // select a random gap between each square
    // calculate the size of each square for the given number of squares and gap between them
    int cellsize = PApplet.parseInt(( vsize - (num + 1) * gap ) / (float)num);
    tiles = new Tile[num*num];

    for (int i=0; i<num*num; i++) {
      int xpos = i%num;
      int ypos = PApplet.parseInt(i/num);
      int random_index = floor(random(table.getRowCount()));
      String name = table.getString(random_index, 0);
      String newC = "FF"+table.getString(random_index, 2);
      int col = unhex(newC);
      tiles[i] = new Tile(gap * (xpos+1) + cellsize * xpos, gap * (ypos+1) + cellsize * ypos, cellsize, col, name );
    }

    String newColor = "FF"+table.getString(row_index, 2);
    int hi = unhex(newColor);
    String name = table.getString(row_index, 0);
    reference = new Tile(hsize*3/4-180, vsize*1/4, 120, hi, name);
  }

  public void display() {
    background(0);
    reference.display();  
    fill(255); 
    text("x"+nb_lasting, gap + hsize*3/4, vsize*1/4 +75);
    //text((currentTable.getString(row_index, 0)), hsize*2/3 , 200);
    //text(currentTable.getString(row_index, 1), hsize*2/3 , 250, 400, 400  );	
    text("Bonus points : " + bonusTime, gap + hsize*3/4, vsize*2/4 + 250);		
    fill(255, 0, 0);
    text("score : "+ score, gap +hsize*3/4, vsize*3/4 +75, 400, 400  );

    for (int i=0; i<tiles.length; i++) {
      tiles[i].display();
      tiles[i].update();
    }
    check_grid();
    check_tiles();
  }

  public void check_grid() {
    int occurence = 0 ;
    for (int i = 0; i < tiles.length; i++) {
      if (!tiles[i].dead) {
        if (reference.col == tiles[i].col) {
          occurence +=1;
        }
      }
    } 
    if (occurence == 0) {
      row_index = floor(random(currentTable.getRowCount()));
      String newColor = "FF"+currentTable.getString(row_index, 2);
      int hi = unhex(newColor);
      String name = currentTable.getString(row_index, 0);
      reference = new Tile(hsize*3/4-180, vsize*1/4, 120, hi, name);
    }
    nb_lasting = occurence;
  }

  public void check_tiles() {
    int nb_dead = 0;
    for (int i = 0; i < tiles.length; i++) {
      if (tiles[i].dead) {
        nb_dead ++;
      }
    }
    if (nb_dead >= tiles.length) {  
      menu = 4;
      colors = currentTable;
      newRoundTriangulation(floor(random(100)), 650, color(255));
    }
  }
}
