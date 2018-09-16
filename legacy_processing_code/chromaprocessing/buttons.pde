
// a generic class for interface buttons
class ButtonM {
  int xpos, ypos;
  int col;
  int hsize;
  int vsize;
  String name;
  boolean active;

  ButtonM(int xpos, int ypos, int hsize, int vsize, int col, String name) {
    this.xpos = xpos ;
    this.ypos = ypos;
    this.col = col;
    this.hsize = hsize ;
    this.vsize = vsize ;
    this.name = name;
    active = false;
  }

  public void display() {  
    pushStyle();
    noStroke();
    rectMode(CENTER);
    //textMode(CENTER);
    fill(col);
    rect(xpos, ypos, hsize, vsize, 10);
    fill(0);
    text(name, xpos, ypos);
    popStyle();
  }

  public boolean over( float x, float y) {
    boolean b;
    if (x>xpos-hsize/2 && x < xpos+hsize/2 && y>ypos-vsize/2 && y<ypos+vsize/2) { 
      b = true;
    } else {
      b = false;
    }
    return b;
  }
}

// a button that selects the color
class ButtonM1 extends ButtonM {
  Table maTable;

  ButtonM1(int xpos, int ypos, int hsize, int vsize, int col, String name, Table maTable) {
    super(xpos, ypos, hsize, vsize, col, name);   
    this.maTable = maTable;
    active = false;
  }

  public void update() {  
    if (over(mouseX, mouseY)) {
      noFill();
      stroke(180);
      strokeWeight(2);
      rect(xpos, ypos, hsize+10, vsize +10, 15);       
      if (mousePressed && millis()> gtime+100) {
        gtime = millis();
        active = true ;
        currentTable = maTable;
        colors = currentTable;
        background(0);
        newSquareTriangulation(floor(random(100)), width, height, color(0));
        colorName = name;
        menu = 2;
      } else { 
        active = false;
      }
    }
  }
}

// a button that selects a grid dimension
class ButtonM2 extends ButtonM {  
  int value;

  ButtonM2(int xpos, int ypos, int hsize, int vsize, int col, String name, int value) {
    super(xpos, ypos, hsize, vsize, col, name);   
    this.value = value;
  }

  public void update() {   
    if (over(mouseX, mouseY)) {
      noFill();
      stroke(180);
      strokeWeight(2);
      rect(xpos, ypos, hsize+10, vsize +10, 15);       
      if (mousePressed && millis()> gtime +100) {
        gtime = millis();
        colors = loadTable("allcolors.csv", "header");
        background(0);
        newSquareTriangulation(floor(random(100)), width, height, color(0));
        game = new Grid_game(currentTable, value);
        dimensions = name;
        menu = 3;
        score = 0 ;
      }
    }
  }
}

// a home button
class ButtonM3 extends ButtonM {
  int value;

  ButtonM3(int xpos, int ypos, int hsize, int vsize, int col, String name, int value) {
    super(xpos, ypos, hsize, vsize, col, name);   
    this.value = value;
  }

  public void update() {  
    if (over(mouseX, mouseY)) {
      noFill();
      stroke(180);
      strokeWeight(2);
      rect(xpos, ypos, hsize+10, vsize +10, 15);       
      if (mousePressed && millis()> gtime +100) {
        gtime = millis();
        menu = 0;
        colors = loadTable("allcolors.csv", "header");
        newRoundTriangulation(floor(random(100)), 650, color(0));
      }
    }
  }
}

//a button to start playing selects the color
class ButtonM4 extends ButtonM {

  ButtonM4(int xpos, int ypos, int hsize, int vsize, int col, String name) {
    super(xpos, ypos, hsize, vsize, col, name);
  }

  public void update() {  
    if (over(mouseX, mouseY)) {
      noFill();
      stroke(180);
      strokeWeight(2);
      rect(xpos, ypos, hsize+10, vsize +10, 15);       
      if (mousePressed && millis()> gtime+100) {
        gtime = millis();
        active = true ;
        colorName = name;
        menu = 1;
      } else { 
        active = false;
      }
    }
  }
}

//a button to start playing selects the color
class ButtonM5 extends ButtonM {

  ButtonM5(int xpos, int ypos, int hsize, int vsize, int col, String name) {
    super(xpos, ypos, hsize, vsize, col, name);
  }

  public void update() {  
    if (over(mouseX, mouseY)) {
      noFill();
      stroke(180);
      strokeWeight(2);
      rect(xpos, ypos, hsize+10, vsize +10, 15);       
      if (mousePressed && millis()> gtime+100) {
        gtime = millis();
        background(255);
        active = true ;
        colorName = name;
        menu = 5;
      } else { 
        active = false;
      }
    }
  }
}

//a tile Class that is actually a buttonM
class Tile extends ButtonM {
  int size;
  boolean dead;
  boolean last_click = false;

  Tile(int xpos, int ypos, int size, int col, String name) {
    super(xpos, ypos, size, size, col, name);   
    this.size = size;
    dead = false;
    last_click = false;
  }

  public void display() {
    if (!dead) {
      pushStyle();
      rectMode(CORNER);
      noStroke();
      fill(col);
      rect(xpos, ypos, size, size, 10);
      popStyle();
    }
  }

  public boolean over( float x, float y) {
    boolean b;
    if (x>xpos && x < xpos+size && y>ypos && y<ypos+size) { 
      b = true;
    } else {
      b = false;
    }
    return b;
  }

  public void update() {
    if (!dead) { 
      if (over(mouseX, mouseY)) {
        pushStyle();
        rectMode(CORNER);
        noFill();
        stroke(180);
        strokeWeight(2);
        rect(xpos-5, ypos-5, size+9, size +9, 15);
        popStyle();

        /////////////////////////////////////////////////////////////////////////////////////////////// score updating
        if (mousePressed && millis()> gtime + 100) {
          gtime = millis();
          String [] m =match(name, reference.name);  
          float dE = deltaE(col, reference.col);      
          if (m != null) {         
            col = color(0); 
            dead = true;
            score += 50 + bonusTime*5;
            bonusTime = 10;
            bonusClock = true;
            lastSec= second();
          } else {
            score -= (10+dE + bonusTime*2);
            // gNotificationManager.notify(1, gNotification); 
            bonusTime = 0;
            bonusClock = false;
            /////////////////////// need for vibration
          }
        }
      }
    }
  }
}//endclass
