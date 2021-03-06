package processing.br.chromateque;

import processing.core.*;
import processing.data.*;
import processing.event.*;
import processing.opengl.*;

import processing.br.*;
import processing.br.chromateque.Ctriangulate.*;
import processing.br.chromateque.ButtonsLove.*;
import processing.br.chromateque.R;


import com.google.android.gms.common.*;
import com.google.android.gms.games.*;
import com.google.android.gms.plus.*;
import com.google.android.gms.common.api.*;

import android.os.Bundle;
import android.util.Log;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.view.View;
import android.view.LayoutInflater;
import android.view.ViewGroup.LayoutParams;
import android.widget.RelativeLayout;

import android.app.Notification;
import android.app.NotificationManager;

import java.util.*;
import java.io.*;
import java.lang.reflect.Field;

public class Chromateque extends PApplet implements
GoogleApiClient.ConnectionCallbacks,
GoogleApiClient.OnConnectionFailedListener,
View.OnClickListener  {

	///////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////
	// DEALING WITH PLAYSERVICES

	private GoogleApiClient myClient;  // our client variable

	private static int RC_SIGN_IN = 9001;
	private static int RC_UNUSED = 5001;

	private static final String TAG = "Chromateque"; // for log tags

	// connecting
	private boolean mResolvingConnectionFailure = false;
	private boolean mAutoStartSignInFlow = false;
	private boolean mSignInClicked = false;
	boolean DEBUG = true;

	// onCreate, onStart, onStop ...
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	protected void onStart() {
		Log.d(TAG, "onStart()");
		super.onStart();
	}

	protected void onStop() {
		Log.d(TAG, "onStop()");
		super.onStop();
		if (myClient.isConnected()) {
			myClient.disconnect();
		}
	}

	// onClick deals with signing in
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.button_sign_in:
			Log.d(TAG, "Sign-in button clicked");
			mSignInClicked = true;
			myClient.connect();
			break;
		case R.id.button_sign_out:
			Log.d(TAG, "Sign-out button clicked");
			mSignInClicked = false;
			Games.signOut(myClient);
			myClient.disconnect();
			showSignInBar();
			break;
		}
	}

	// onConnection , onConnection Suspended
	public void onConnected(Bundle bundle) {
		Log.d(TAG, "onConnected() called. Sign in successful!");
		showSignOutBar();
	}

	public void onConnectionSuspended(int i) {
		Log.d(TAG, "onConnectionSuspended() called. Trying to reconnect.");
		myClient.connect();
	}

	// deal with failing connections
	public void onConnectionFailed(ConnectionResult connectionResult) {
		Log.d(TAG, "onConnectionFailed() called, result: " + connectionResult);
		if (mResolvingConnectionFailure) {
			return;
		}
		if (mSignInClicked || mAutoStartSignInFlow) {
			mAutoStartSignInFlow = false;
			mSignInClicked = false;
			mResolvingConnectionFailure = true;
			if (!resolveConnectionFailure(this, myClient, connectionResult,
					RC_SIGN_IN, getString(R.string.signin_other_error))) {
				mResolvingConnectionFailure = false;
			}
		}
		showSignInBar();
	}

	// resolving connection failures
	public boolean resolveConnectionFailure(Activity activity,
			GoogleApiClient client,
			ConnectionResult result,
			int requestCode,
			String fallbackErrorMessage) {
		if (result.hasResolution()) {
			try {
				result.startResolutionForResult(activity, requestCode);
				return true;
			} catch (IntentSender.SendIntentException e) {
				client.connect();
				return false;
			}
		} else {
			int errorCode = result.getErrorCode();
			Dialog dialog = GooglePlayServicesUtil.getErrorDialog(errorCode,activity, requestCode);
			if (dialog != null) {
				dialog.show();
			} else {
				showAlert(activity, fallbackErrorMessage);
			}
			return false;
		}
	}

	// connect if everything is ok
	protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
		if (requestCode == RC_SIGN_IN) {
			Log.d(TAG,
					"onActivityResult with requestCode == RC_SIGN_IN, responseCode=" +
							responseCode + ", intent=" + intent);
			mSignInClicked = false;
			mResolvingConnectionFailure = false;
			if (responseCode == RESULT_OK) {
				myClient.connect();
			} else {
				Log.d(TAG, "ActivityResultError");
			}
		}
	}

	// alerting user
	public void showAlert(final Activity activity, final String message) {
		runOnUiThread(new Runnable() {
			public void run() {
				(new AlertDialog.Builder(activity)).setMessage(message)
				.setNeutralButton(android.R.string.ok, null).create().show();
			}
		});
	}

	// Sign in bar
	private void showSignInBar() {
		//Log.d(TAG, "Showing sign in bar");
		mSignInClicked =false;
		runOnUiThread(new Runnable() {
			public void run() {
				getWindow().findViewById(R.id.sign_in_bar).setVisibility(View.VISIBLE);
				getWindow().findViewById(R.id.sign_out_bar).setVisibility(View.GONE);
			}
		});
	}

	// sign out bar
	private void showSignOutBar() {
		//Log.d(TAG, "Showing sign out bar");
		mSignInClicked =true;
		runOnUiThread(new Runnable() {
			public void run() {
				getWindow().findViewById(R.id.sign_in_bar).setVisibility(View.GONE);
				getWindow().findViewById(R.id.sign_out_bar).setVisibility(View.VISIBLE);
			}
		});
	}

	private void hideSignInOutBar() {
		//Log.d(TAG, "Showing sign out bar");
		runOnUiThread(new Runnable() {
			public void run() {
				getWindow().findViewById(R.id.sign_in_bar).setVisibility(View.GONE);
				getWindow().findViewById(R.id.sign_out_bar).setVisibility(View.GONE);
			}
		});
	}

	// attach listeners to signin and signout buttons
	boolean setClickListener = false;
	public void trySetClickListenerToSignInOutBar() {
		if (setClickListener) return;
		Log.d(TAG, "try setClickListener");
		SignInButton buttonSignIn = (SignInButton) getWindow().findViewById(R.id.button_sign_in);
		View buttonSignOut = getWindow().findViewById(R.id.button_sign_out);
		if (buttonSignIn != null && buttonSignOut != null) {
			buttonSignIn.setOnClickListener(this);
			buttonSignOut.setOnClickListener(this);
			setClickListener = true;
		} else {
			Log.d(TAG, "could not find buttonSignIn or buttonSignOut, retry next frame.");
		}
	}

	///////////////////////////////////////////////////////////////////////////////
	//executor of activity used for submit and show score buttons
	public class Executor {
		Activity activity;
		public Executor(Activity activity) {
			this.activity = activity;
		}
		public void run() {

		}
	};


	//////////////////////////////////////////////////////////////////////////////////////
	// setting up two buttons : one for submitting the score, and one for showing them
	Button[] buttons = new Button[2];
	public void setupButtons() {
		Executor submitScore = new Executor(this) {
			public void run() {
				Log.d(TAG, "try submit Score");
				////////////////////////////////////////////////////////////////////////////////////////////////////////////score submission
				if (myClient.isConnected()) {
					String leaderboard = "leaderboard"+"_"+colorName +"_"+dimensions;
					Field f;
					String lbid = null;
					try {
						f = R.string.class.getField(leaderboard);
						lbid =   getString(parseInt(f.get(null).toString(),16));
					} catch (NoSuchFieldException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					Games.Leaderboards.submitScore(myClient,
							lbid,
							score);
					showAlert(activity, getString(R.string.submit_succeeded));
				}
			}
		};
		buttons[0] = new Button(displayWidth/2, displayHeight*7/12, getString(R.string.label_submit_score), submitScore,this);

		Executor showScores = new Executor(this) {
			public void run() {
				Log.d(TAG, "try show Scores");
				/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////score request
				if (myClient.isConnected()) {
					String leaderboard = "leaderboard"+"_"+colorName +"_"+dimensions;
					Field f;
					String lbid = null;
					try {
						f = R.string.class.getField(leaderboard);
						lbid =   getString(parseInt(f.get(null).toString(),16));
					} catch (NoSuchFieldException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if(menu ==4){ // show intent of the played game
						startActivityForResult(Games.Leaderboards.getLeaderboardIntent(myClient, lbid),RC_UNUSED );
					}else if (menu ==5){ // show all leaderboards
						startActivityForResult(Games.Leaderboards.getAllLeaderboardsIntent(myClient), RC_UNUSED);
					}
				}
			}
		};
		buttons[1] = new Button(displayWidth/2,displayHeight*9/12, getString(R.string.label_show_scores), showScores,this);
	}

	public String getLeaderBoardName(){
		String result = "leaderboard_"+colorName +" "+dimensions;
		return result;

	}


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
	ButtonM5 gplus;

	View view; // a view for our login to gplus we want to be able to hide and show it

	//Setup vibration globals:
	NotificationManager gNotificationManager;
	Notification gNotification;
	long[] gVibrate = {0,250,50,125,50,62};

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
		pg = createGraphics(650,650);
		splash = createGraphics(width,height);
		newRoundTriangulation(floor(random(100)),650,color(0));
		newSquareTriangulation(floor(random(100)),width,height,color(0));

		// init vibration
		gNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
	    gNotification = new Notification();
	    gNotification.vibrate = gVibrate;

		// init play services client
		myClient = new GoogleApiClient.Builder(this)
		.addApi(Plus.API).addScope(Plus.SCOPE_PLUS_PROFILE)
		.addApi(Games.API).addScope(Games.SCOPE_GAMES)
		.addConnectionCallbacks(this)
		.addOnConnectionFailedListener(this)
		.build();

		// run signin/signout bar
		final LayoutInflater layoutInflater = LayoutInflater.from(this);
		runOnUiThread(new Runnable() {
			public void run() {
				view = layoutInflater.inflate(R.layout.sign_in_out_bar, null);
				RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT,
						LayoutParams.WRAP_CONTENT);
				getWindow().addContentView(view, lp);
			}
		});


		// connect the play services on startup
		myClient.connect();
		hideSignInOutBar();
		// setup submit and show score buttons (not fully processing related)
		setupButtons();

		// processing stuff

		hsize = displayWidth;
		vsize = displayHeight;
		title = loadFont("Tunga-Bold-48.vlw");
		textFont(title);
		textSize(56);
		rectMode(CENTER);
		textAlign(CENTER,CENTER);
		imageMode(CENTER);

		float alpha = 100;
		// init color selection buttons
		blanc = new ButtonM1 (width/2, height*2/12, 500, buttonSize, color(255,alpha), "white", loadTable("White.csv", "header"));
		bleu = new ButtonM1 (width/2, height*3/12, 500, buttonSize, color(0, 0, 255,alpha), "blue", loadTable("Blue.csv", "header"));
		brun = new ButtonM1 (width/2, height*4/12, 500, buttonSize, color(149, 80, 5,alpha), "brown", loadTable("Brown.csv", "header"));
		gris = new ButtonM1 (width/2, height*5/12, 500, buttonSize, color(150,alpha), "gray", loadTable("Gray.csv", "header"));
		jaune = new ButtonM1 (width/2, height*6/12, 500, buttonSize, color(255, 255, 2,alpha), "yellow", loadTable("Yellow.csv", "header"));
		orange = new ButtonM1 (width/2, height*7/12, 500, buttonSize, color(255, 136, 5,alpha), "orange", loadTable("Orange.csv", "header"));
		rose = new ButtonM1 (width/2, height*8/12, 500, buttonSize, color(255, 180, 180,alpha), "pink", loadTable("Pink.csv", "header"));
		rouge = new ButtonM1 (width/2, height*9/12, 500, buttonSize, color(255, 0, 0,alpha), "red", loadTable("Red.csv", "header"));
		vert = new ButtonM1 (width/2, height*10/12, 500, buttonSize, color(0, 255, 0,alpha), "green", loadTable("Green.csv", "header"));
		violet = new ButtonM1 (width/2, height*11/12, 500, buttonSize, color(255, 0, 255,alpha), "purple", loadTable("Purple.csv", "header"));

		// init grid dimension buttons
		trois = new ButtonM2 (width/2, height*2/10, 250, buttonSize, color(255,alpha), "3x3", 3);
		quatre = new ButtonM2 (width/2, height*3/10, 250, buttonSize, color(255,alpha), "4x4", 4);
		cinq = new ButtonM2 (width/2, height*4/10, 250, buttonSize, color(255,alpha), "5x5", 5);
		six = new ButtonM2 (width/2, height*5/10, 250, buttonSize, color(255,alpha), "6x6", 6);
		sept = new ButtonM2 (width/2, height*6/10, 250, buttonSize, color(255,alpha), "7x7", 7);
		huit = new ButtonM2 (width/2, height*7/10, 250, buttonSize, color(255,alpha), "8x8", 8);
		neuf = new ButtonM2 (width/2, height*8/10, 250, buttonSize, color(255,alpha), "9x9", 15);

		// init go back to main menu button
		goback =  new ButtonM3 (width/2, height*11/12, 500, buttonSize, color(255), "Go back", 8);
		play =  new ButtonM4 (width/2, height*9/12, 500, buttonSize, color(255), "Play");
		gplus =  new ButtonM5 (width/2, height*11/12, 500, buttonSize, color(255), "Connect to G+");

		// start on menu 0
		menu = 0 ;
		lastSec=second();
	}

	public void draw() {

		background(0);
		anim += 0.015;

		if (menu == 0){ // show access to gplay menu (ie menu 5, or to the game)
			hideSignInOutBar();
			background(0);
			pushMatrix();
			translate(width/2,height*4/12);

			image(pg,0,0);
			popMatrix();
			score = 0;
			play.display();
			play.update();
			gplus.display();
			gplus.update();

		}
		else if (menu == 5){ // display singin/signout bar, show score button, and goback button

			background(255);

			goback.display();
			goback.update();
			buttons[1].draw();

			// signin, signout bar for google play services
			trySetClickListenerToSignInOutBar();
			if(!mSignInClicked){
				showSignInBar();
			}
			else if(mSignInClicked){
				showSignOutBar();
			}



		}
		else if (menu == 1) { // display color selection buttons
			pushMatrix();
			translate(width/2,height/2);
			image(splash,0,0);
			popMatrix();

			blanc.display(); blanc.update();
			bleu.display(); bleu.update();
			brun.display(); brun.update();
			gris.display(); gris.update();
			jaune.display(); jaune.update();
			orange.display(); orange.update();
			rose.display(); rose.update();
			rouge.display(); rouge.update();
			vert.display(); vert.update();
			violet.display(); violet.update();
		}
		else if (menu ==2) { // display dimension selection buttons
			pushMatrix();
			translate(width/2,height/2);
			image(splash,0,0);
			popMatrix();
			trois.display(); trois.update();
			quatre.display(); quatre.update();
			cinq.display(); cinq.update();
			six.display(); six.update();
			sept.display(); sept.update();
			huit.display(); huit.update();
			neuf.display(); neuf.update();
		}
		else if (menu == 3) { // play the game (the whole game logic is coded in the class
			if(bonusClock){
				if (second() == (lastSec +1)%60){
					lastSec = second();
					bonusTime -=1; // check Tile score update to reset
					bonusTime = constrain(bonusTime,0,10);
				}
			}
			game.display();
		}
		else if (menu == 4){
			background(255);
			pushMatrix();
			translate(width/2,height*3/12);
			rotate(anim);
			image(pg,0,0,300,300);
			popMatrix();
			fill(0);
			text (" CONGRATULATIONS ! " , width/2, height*1/12);
			text (" You Scored : "+ score +" points" , width/2, height*5/12);
			//text (score , width/2+25, height*3/10);
			goback.display();
			goback.update();
			// submit and show score buttons
			for (int i = 0; i < buttons.length; i++) {
				buttons[i].draw();
			}
		}
	}

	public void mousePressed() {


		// those are the submit and show score buttons connecting to google play services
		if (menu == 4){ // prevent from firing up whe not showing
			for (int i = 0; i < buttons.length; i++) {
				if (buttons[i].mouseOver()) {
					buttons[i].exec();
				};
			}
		}
		else if (menu == 5){
			if(buttons[1].mouseOver()){
				buttons[1].exec();
			}
		}
	}

	public void keyPressed() {
		menu = 0;

		if (key == CODED && keyCode == android.view.KeyEvent.KEYCODE_BACK) {
		      keyCode = 0;  // don't quit by default
		    }

		//println("kekekekekek : " + key);
		//newTriangulation(floor(random(100)),600,);
	}

	// a generic class for interface buttons
	class ButtonM {
		int xpos, ypos;
		int col;
		int hsize;
		int vsize;
		String name;
		boolean active;

		ButtonM(int xpos, int ypos, int hsize, int vsize, int col, String name){
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
			rect(xpos,ypos,hsize,vsize,10);
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
	class ButtonM1 extends ButtonM{
		Table maTable;

		ButtonM1(int xpos, int ypos, int hsize, int vsize, int col, String name, Table maTable) {
			super(xpos,ypos,hsize,vsize,col,name);
			this.maTable = maTable;
			active = false;
		}

		public void update(){
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
					newSquareTriangulation(floor(random(100)),width,height,color(0));
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
			super(xpos,ypos,hsize,vsize,col,name);
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
					newSquareTriangulation(floor(random(100)),width,height,color(0));
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
			super(xpos,ypos,hsize,vsize,col,name);
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
					newRoundTriangulation(floor(random(100)),650,color(0));
				}
			}
		}
	}

	//a button to start playing selects the color
	class ButtonM4 extends ButtonM{

		ButtonM4(int xpos, int ypos, int hsize, int vsize, int col, String name) {
			super(xpos,ypos,hsize,vsize,col,name);
		}

		public void update(){
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
	class ButtonM5 extends ButtonM{

		ButtonM5(int xpos, int ypos, int hsize, int vsize, int col, String name) {
			super(xpos,ypos,hsize,vsize,col,name);
		}

		public void update(){
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
	class Tile extends ButtonM{
		int size;
		boolean dead;
		boolean last_click = false;

		Tile(int xpos, int ypos, int size, int col, String name) {
			super(xpos,ypos,size,size,col,name);
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

		public void update(){
			if (!dead){
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
						float dE = deltaE(col,reference.col);
						if (m != null) {
							col = color(0);
							dead = true;
							score += 50 + bonusTime*5;
							bonusTime = 10;
							bonusClock = true;
							lastSec= second();
						}
						else {
							score -= (10+dE + bonusTime*2);
							gNotificationManager.notify(1, gNotification);
							bonusTime = 0;
							bonusClock = false;
							/////////////////////// need for vibration
						}
					}
				}
			}
		}

	}//endclass


	/////////////////////////////////////////////////////////////////////////
	// Triangulation - Splash Icon
	void newRoundTriangulation(int seed, float dimension, int backColor) {
		  noiseSeed(seed);
		  randomSeed(seed);


		  ArrayList<PVector> vertices = new ArrayList<PVector>();
		  for (int i = 0; i < mNbVertices; i++) {
		    float angle = map (i, 0, mNbVertices/2, 0, TWO_PI);
		    float xpos = dimension/2 + random(-1, 1)* cos(angle)*dimension/2;
		    float ypos = dimension/2 + random(-1, 1)*sin(angle)*dimension/2;

		    vertices.add(new PVector(xpos, ypos));
		  }


		  for (int i = 0; i < mNbVertices/10; i++) {
		    float angle = map (i, 0, mNbVertices/10, 0, TWO_PI);
		    float xpos = dimension/2 +  cos(angle)*dimension/2;
		    float ypos = dimension/2 + sin(angle)*dimension/2;

		    vertices.add(new PVector(xpos, ypos));
		  }

		  triangulator = new CTriangulator(vertices);

		  ArrayList<Triangle> triangles = triangulator.getTriangles();

		  pg.beginDraw();
		  pg.background(backColor);
		  for (Triangle t : triangles) {
		    noiseSeed(seed);
		    noise += 0.05;
		    pg.beginShape(TRIANGLES);

		    int row_index = floor((map(noise(noise, 50, 20),0, 1, 0, colors.getRowCount())));
		    //row_index = int(random(colors.getRowCount()-1));
		    String newColor = "FF"+colors.getString(row_index, 2);
		    int c = color(unhex(newColor));
		    pg.stroke(c);
		    pg.fill(c);

		    pg.vertex(t.p1.x, t.p1.y);
		    pg.vertex(t.p2.x, t.p2.y);
		    pg.vertex(t.p3.x, t.p3.y);
		    pg.endShape(CLOSE);
		  }
		 pg.endDraw();
		}

	////////////////////////////////////////////////////////////////////
	// Splash rect triangulation
	void newSquareTriangulation(int seed,int iWidth, int iHeight, int backColor) {
		  noiseSeed(seed);
		  randomSeed(seed);


		  ArrayList<PVector> vertices = new ArrayList<PVector>();
		  for (int i = 0; i < mNbVertices; i++) {
		    vertices.add(new PVector(random(iWidth), random(iHeight)));
		  }

		  vertices.add(new PVector(0, 0));
		  vertices.add(new PVector(iWidth, 0));
		  vertices.add(new PVector(iWidth/2, 0));
		  vertices.add(new PVector(iWidth/2, iHeight));
		  vertices.add(new PVector(iWidth, iHeight));
		   vertices.add(new PVector(iWidth, iHeight/2));
		  vertices.add(new PVector(0, iHeight));
		  vertices.add(new PVector(0, iHeight/2));
		  triangulator = new CTriangulator(vertices);

		  triangulator = new CTriangulator(vertices);

		  ArrayList<Triangle> triangles = triangulator.getTriangles();

		  splash.beginDraw();
		  splash.background(backColor);
		  for (Triangle t : triangles) {
		    noiseSeed(seed);
		    noise += 0.05;
		    splash.beginShape(TRIANGLES);

		    int row_index = floor((map(noise(noise, 50, 20),0, 1, 0, colors.getRowCount())));
		    //row_index = int(random(colors.getRowCount()-1));
		    String newColor = "FF"+colors.getString(row_index, 2);
		    int c = color(unhex(newColor));
		    splash.stroke(c);
		    splash.fill(c);

		    splash.vertex(t.p1.x, t.p1.y);
		    splash.vertex(t.p2.x, t.p2.y);
		    splash.vertex(t.p3.x, t.p3.y);
		    splash.endShape(CLOSE);
		  }
		  splash.endDraw();
		}


	///////////////////////////////////////////////////////////////
	//CALCULATE DISTANCE BEETWEEN TWO COLORS
	public float deltaE(int col1, int col2) {
		float result = 0;

		float[] xyz1 = rgb2xyz(col1);
		float[] lab1 = xyz2lab(xyz1);

		float[] xyz2 = rgb2xyz(col2);
		float[] lab2 = xyz2lab(xyz2);

		float c1 = sqrt(lab1[1]*lab1[1]+lab1[2]*lab1[2]);
		float c2 = sqrt(lab2[1]*lab2[1]+lab2[2]*lab2[2]);
		float dc = c1-c2;
		float dl = lab1[0]-lab2[0];
		float da = lab1[1]-lab2[1];
		float db = lab1[2]-lab2[2];
		float dh = sqrt((da*da)+(db*db)-(dc*dc));
		float first = dl;
		double second = (float) dc/(1+0.045*c1);
		double third = dh/(1+0.015*c1);
		result = (sqrt((float) (first*first+second*second+third*third)));

		return result;
	}

	public float [] rgb2xyz(int rgb) {

		float[] result = new float[3];

		double red = red(rgb)/255;
		double green = green(rgb)/255;
		double blue = blue(rgb)/255;

		if (red>0.04045) {
			red = (red+0.055)/1.055;
			red = pow((float)red, (float)2.4);
		} else {
			red = red/12.92;
		}
		if (green>0.04045) {
			green = (green+0.055)/1.055;
			green = pow((float)green, (float)2.4);
		} else {
			green = green/12.92;
		}
		if (blue>0.04045) {
			blue = (blue+0.055)/1.055;
			blue = pow((float)blue, (float)2.4);
		} else {
			blue = blue/12.92;
		}

		blue *=100;
		red *=100;
		green *=100;

		result[0] =(float) (red * 0.4124 + green * 0.3576 + blue * 0.1805);
		result[1] = (float) (red * 0.2126 + green * 0.7152 + blue * 0.0722);
		result[2] = (float) (red * 0.0193 + green * 0.1192 + blue * 0.9505);

		return result;
	}

	public float [] xyz2lab(float[] xyz) {

		float[] result = new float[3];

		double x = xyz[0]/95.047;
		double y = xyz[1]/100;
		double z = xyz[2]/108.883;

		if (x>0.008856) {
			x = pow((float) x, (float) 0.333);
		} else {
			x = 7.787*x + 16/116;
		}
		if (y>0.008856) {
			y = pow((float) y, (float) 0.3333);
		} else {
			y = (7.787*y) + (16/116);
		}
		if (z>0.008856) {
			z = pow((float) z, (float) 0.333);
		} else {
			z = 7.787*z + 16/116;
		}

		result[0]= (float) (116*y -16);
		result[1]= (float) (500*(x-y));
		result[2]= (float) (200*(y-z));

		return result;
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
			text("Bonus points : " + bonusTime , gap + hsize*3/4 , vsize*2/4 + 250);
			fill(255, 0, 0);
			text("score : "+ score, gap +hsize*3/4,  vsize*3/4 +75, 400, 400  );

			for (int i=0; i<tiles.length; i++) {
				tiles[i].display();
				tiles[i].update();
			}
			check_grid();
			check_tiles();
		}

		public void check_grid() {
			int occurence = 0 ;
			for (int i = 0 ; i < tiles.length ; i++) {
				if (!tiles[i].dead) {
					if (reference.col == tiles[i].col){
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

		public void check_tiles(){
			int nb_dead = 0;
			for (int i = 0 ; i < tiles.length ; i++) {
				if (tiles[i].dead){
					nb_dead ++;
				}
			}
			if (nb_dead >= tiles.length){
				menu = 4;
				colors = currentTable;
				newRoundTriangulation(floor(random(100)),650,color(255));
			}
		}

	}

	public int sketchWidth() { return displayWidth; }
	public int sketchHeight() { return displayHeight; }
	public String sketchRenderer() {return P2D;}




}
