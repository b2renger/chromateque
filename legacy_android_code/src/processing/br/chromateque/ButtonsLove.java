package processing.br.chromateque;


import processing.core.*;
import processing.data.*;
import processing.event.*;
import processing.opengl.*;
import java.util.*;
import java.io.*;
import processing.br.chromateque.Chromateque.Executor;

public class ButtonsLove {


	// a Button Class with an executor attached for submit and show
	public static class Button {
		int CENTER_X =150;
		int WIDTH = 500;
		int HEIGHT = 80;
		int BASE_COLOR = 0;
		int TEXT_COLOR = 255;
		int TEXT_SIZE  = 56;
		int y, w, h;
		String label;
		Executor executor;
		PApplet parent;



		Button(int x, int y, String label, Executor executor, PApplet p) {
			this.y = y;
			CENTER_X = x;
			this.label = label;
			this.executor = executor;
			parent = p;
		}

		void exec() {
			executor.run();
		}
		void draw() {

			parent.pushMatrix();

			parent.rectMode(parent.CENTER);
			parent.fill(BASE_COLOR);
			parent.rect(CENTER_X, y, WIDTH, HEIGHT,10);

			parent.fill(TEXT_COLOR);
			parent.textAlign(parent.CENTER, parent.CENTER);
			parent.textSize(TEXT_SIZE);
			parent.text(label, CENTER_X, y, WIDTH, HEIGHT);

			parent.popMatrix();

		}
		boolean mouseOver() {
			return (parent.mouseX >= (CENTER_X - WIDTH/2) &&
					parent.mouseX <= (CENTER_X + WIDTH/2) &&
					parent.mouseY >= (y - HEIGHT/2) &&
					parent.mouseY <= (y + HEIGHT/2));
		}
	}

}
