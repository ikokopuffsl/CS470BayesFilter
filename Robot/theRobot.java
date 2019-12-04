
import javax.swing.*;
import java.awt.event.*;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.lang.*;
import javax.swing.JComponent;
import javax.swing.JFrame;

//import sun.tools.tree.Vset;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import java.util.Scanner;
import java.net.*;


// This class draws the probability map and value iteration map that you create to the window
// You need only call updateProbs() and updateValues() from your theRobot class to update these maps
class mySmartMap extends JComponent implements KeyListener {
    public static final int NORTH = 0;
    public static final int SOUTH = 1;
    public static final int EAST = 2;
    public static final int WEST = 3;
    public static final int STAY = 4;

    int currentKey;

    int winWidth, winHeight;
    double sqrWdth, sqrHght;
    Color gris = new Color(170,170,170);
    Color myWhite = new Color(220, 220, 220);
    World mundo;
    
    int gameStatus;

    double[][] probs;
    double[][] vals;
    
    public mySmartMap(int w, int h, World wld) {
        mundo = wld;
        probs = new double[mundo.width][mundo.height];
        vals = new double[mundo.width][mundo.height];
        winWidth = w;
        winHeight = h;
        
        sqrWdth = (double)w / mundo.width;
        sqrHght = (double)h / mundo.height;
        currentKey = -1;
        
        addKeyListener(this);
        
        gameStatus = 0;
    }
    
    public void addNotify() {
        super.addNotify();
        requestFocus();
    }
    
    public void setWin() {
        gameStatus = 1;
        repaint();
    }
    
    public void setLoss() {
        gameStatus = 2;
        repaint();
    }
    
    public void updateProbs(double[][] _probs) {
        for (int y = 0; y < mundo.height; y++) {
            for (int x = 0; x < mundo.width; x++) {
                probs[x][y] = _probs[x][y];
            }
        }
        
        repaint();
    }
    
    public void updateValues(double[][] _vals) {
        for (int y = 0; y < mundo.height; y++) {
            for (int x = 0; x < mundo.width; x++) {
                vals[x][y] = _vals[x][y];
            }
        }
        
        repaint();
    }

    public void paint(Graphics g) {
        paintProbs(g);
        //paintValues(g);
    }

    public void paintProbs(Graphics g) {
        double maxProbs = 0.0;
        int mx = 0, my = 0;
        for (int y = 0; y < mundo.height; y++) {
            for (int x = 0; x < mundo.width; x++) {
                if (probs[x][y] > maxProbs) {
                    maxProbs = probs[x][y];
                    mx = x;
                    my = y;
                }
                if (mundo.grid[x][y] == 1) {
                    g.setColor(Color.black);
                    g.fillRect((int)(x * sqrWdth), (int)(y * sqrHght), (int)sqrWdth, (int)sqrHght);
                }
                else if (mundo.grid[x][y] == 0) {
                    //g.setColor(myWhite);
                    
                    int col = (int)(255 * Math.sqrt(probs[x][y]));
                    if (col > 255)
                        col = 255;
                    g.setColor(new Color(255-col, 255-col, 255));
                    g.fillRect((int)(x * sqrWdth), (int)(y * sqrHght), (int)sqrWdth, (int)sqrHght);
                }
                else if (mundo.grid[x][y] == 2) {
                    g.setColor(Color.red);
                    g.fillRect((int)(x * sqrWdth), (int)(y * sqrHght), (int)sqrWdth, (int)sqrHght);
                }
                else if (mundo.grid[x][y] == 3) {
                    g.setColor(Color.green);
                    g.fillRect((int)(x * sqrWdth), (int)(y * sqrHght), (int)sqrWdth, (int)sqrHght);
                }
            
            }
            if (y != 0) {
                g.setColor(gris);
                g.drawLine(0, (int)(y * sqrHght), (int)winWidth, (int)(y * sqrHght));
            }
        }
        for (int x = 0; x < mundo.width; x++) {
                g.setColor(gris);
                g.drawLine((int)(x * sqrWdth), 0, (int)(x * sqrWdth), (int)winHeight);
        }
        
        //System.out.println("repaint maxProb: " + maxProbs + "; " + mx + ", " + my);
        
        g.setColor(Color.green);
        g.drawOval((int)(mx * sqrWdth)+1, (int)(my * sqrHght)+1, (int)(sqrWdth-1.4), (int)(sqrHght-1.4));
        
        if (gameStatus == 1) {
            g.setColor(Color.green);
            g.drawString("You Won!", 8, 25);
        }
        else if (gameStatus == 2) {
            g.setColor(Color.red);
            g.drawString("You're a Loser!", 8, 25);
        }
    }
    
    public void paintValues(Graphics g) {
        double maxVal = -99999, minVal = 99999;
        int mx = 0, my = 0;
        
        for (int y = 0; y < mundo.height; y++) {
            for (int x = 0; x < mundo.width; x++) {
                if (mundo.grid[x][y] != 0)
                    continue;
                
                if (vals[x][y] > maxVal)
                    maxVal = vals[x][y];
                if (vals[x][y] < minVal)
                    minVal = vals[x][y];
            }
        }
        if (minVal == maxVal) {
            maxVal = minVal+1;
        }

        int offset = winWidth+20;
        for (int y = 0; y < mundo.height; y++) {
            for (int x = 0; x < mundo.width; x++) {
                if (mundo.grid[x][y] == 1) {
                    g.setColor(Color.black);
                    g.fillRect((int)(x * sqrWdth)+offset, (int)(y * sqrHght), (int)sqrWdth, (int)sqrHght);
                }
                else if (mundo.grid[x][y] == 0) {
                    //g.setColor(myWhite);
                    
                    //int col = (int)(255 * Math.sqrt((vals[x][y]-minVal)/(maxVal-minVal)));
                    int col = (int)(255 * (vals[x][y]-minVal)/(maxVal-minVal));
                    if (col > 255)
                        col = 255;
                    g.setColor(new Color(255-col, 255-col, 255));
                    g.fillRect((int)(x * sqrWdth)+offset, (int)(y * sqrHght), (int)sqrWdth, (int)sqrHght);
                }
                else if (mundo.grid[x][y] == 2) {
                    g.setColor(Color.red);
                    g.fillRect((int)(x * sqrWdth)+offset, (int)(y * sqrHght), (int)sqrWdth, (int)sqrHght);
                }
                else if (mundo.grid[x][y] == 3) {
                    g.setColor(Color.green);
                    g.fillRect((int)(x * sqrWdth)+offset, (int)(y * sqrHght), (int)sqrWdth, (int)sqrHght);
                }
            
            }
            if (y != 0) {
                g.setColor(gris);
                g.drawLine(offset, (int)(y * sqrHght), (int)winWidth+offset, (int)(y * sqrHght));
            }
        }
        for (int x = 0; x < mundo.width; x++) {
                g.setColor(gris);
                g.drawLine((int)(x * sqrWdth)+offset, 0, (int)(x * sqrWdth)+offset, (int)winHeight);
        }
    }

    
    public void keyPressed(KeyEvent e) {
        //System.out.println("keyPressed");
    }
    public void keyReleased(KeyEvent e) {
        //System.out.println("keyReleased");
    }
    public void keyTyped(KeyEvent e) {
        char key = e.getKeyChar();
        //System.out.println(key);
        
        switch (key) {
            case 'i':
                currentKey = NORTH;
                break;
            case ',':
                currentKey = SOUTH;
                break;
            case 'j':
                currentKey = WEST;
                break;
            case 'l':
                currentKey = EAST;
                break;
            case 'k':
                currentKey = STAY;
                break;
        }
    }
}

// This is the main class that you will add to in order to complete the lab
public class theRobot extends JFrame {
    // Mapping of actions to integers
    public static final int NORTH = 0;
    public static final int SOUTH = 1;
    public static final int EAST = 2;
    public static final int WEST = 3;
    public static final int STAY = 4;
    public static final int GOAL_REWARD = 500;
    public static final int STAIRS_REWARD = -500;

    Color bkgroundColor = new Color(230,230,230);
    
    static mySmartMap myMaps; // instance of the class that draw everything to the GUI
    String mundoName;
    
    World mundo; // mundo contains all the information about the world.  See World.java
    double moveProb, sensorAccuracy, nonMoveProb;  // stores probabilies that the robot moves in the intended direction
                                      // and the probability that a sonar reading is correct, respectively
    
    // variables to communicate with the Server via sockets
    public Socket s;
	public BufferedReader sin;
	public PrintWriter sout;
    
    // variables to store information entered through the command-line about the current scenario
    boolean isManual = false; // determines whether you (manual) or the AI (automatic) controls the robots movements
    boolean knownPosition = false;
    int startX = -1, startY = -1;
    int decisionDelay = 250;
    
    // store your probability map (for position of the robot in this array
    double[][] probs;
    //Util matrix
    double[][] utils;
    
    public theRobot(String _manual, int _decisionDelay) {
        // initialize variables as specified from the command-line
        if (_manual.equals("automatic"))
            isManual = false;
        else
            isManual = true;
        decisionDelay = _decisionDelay;
        
        // get a connection to the server and get initial information about the world
        initClient();
    
        // Read in the world
        mundo = new World(mundoName);
        
        // set up the GUI that displays the information you compute
        int width = 500;
        int height = 500;
        int bar = 20;
        setSize(width,height+bar);
        getContentPane().setBackground(bkgroundColor);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(0, 0, width, height+bar);
        myMaps = new mySmartMap(width, height, mundo);
        getContentPane().add(myMaps);
        
        setVisible(true);
        setTitle("Probability and Value Maps");
        initUtils();
        doStuff(); // Function to have the robot move about its world until it gets to its goal or falls in a stairwell
    }

    // this function establishes a connection with the server and learns
    //   1 -- which world it is in
    //   2 -- it's transition model (specified by moveProb)
    //   3 -- it's sensor model (specified by sensorAccuracy)
    //   4 -- whether it's initial position is known.  if known, its position is stored in (startX, startY)
    public void initClient() {
        int portNumber = 3333;
        String host = "localhost";
        
        try {
			s = new Socket(host, portNumber);
            sout = new PrintWriter(s.getOutputStream(), true);
			sin = new BufferedReader(new InputStreamReader(s.getInputStream()));
            
            mundoName = sin.readLine();
            moveProb = Double.parseDouble(sin.readLine());
            nonMoveProb = ((1-moveProb)/3);
            sensorAccuracy = Double.parseDouble(sin.readLine());
            System.out.println("Need to open the mundo: " + mundoName);
            System.out.println("moveProb: " + moveProb);
            System.out.println("sensorAccuracy: " + sensorAccuracy);
            
            // find out of the robots position is know
            String _known = sin.readLine();
            if (_known.equals("known")) {
                knownPosition = true;
                startX = Integer.parseInt(sin.readLine());
                startY = Integer.parseInt(sin.readLine());
                System.out.println("Robot's initial position is known: " + startX + ", " + startY);
            }
            else {
                System.out.println("Robot's initial position is unknown");
            }
        } catch (IOException e) {
            System.err.println("Caught IOException: " + e.getMessage());
        }
    }

    // function that gets human-specified actions
    // 'i' specifies the movement up
    // ',' specifies the movement down
    // 'l' specifies the movement right
    // 'j' specifies the movement left
    // 'k' specifies the movement stay
    int getHumanAction() {
        System.out.println("Reading the action selected by the user");
        while (myMaps.currentKey < 0) {
            try {
                Thread.sleep(50);
            }
            catch(InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
        int a = myMaps.currentKey;
        myMaps.currentKey = -1;
        
        System.out.println("Action: " + a);
        
        return a;
    }

    // Create the utils array and fill it
    public void initUtils(){
        double[][] reward_matrix = new double[mundo.width][mundo.height];
        utils = new double[mundo.width][mundo.height];
        for (int y = 0; y < mundo.height; y++) {
            for (int x = 0; x < mundo.width; x++) {
                utils[x][y] = 0.0;
                reward_matrix[x][y] = 0.0;
                if (mundo.grid[x][y] != 1){
                    if (mundo.grid[x][y] == 3){
                        reward_matrix[x][y] = GOAL_REWARD;
                    }
                    else if(mundo.grid[x][y] == 2){
                        reward_matrix[x][y] = STAIRS_REWARD;
                    }
                }
            }
        }
        prettyPrint(utils, "Utility graph Start");
        prettyPrint(reward_matrix, "Reward matrix");
        stabilizeUtils(reward_matrix);
        prettyPrint(utils, "Utility graph");
    }

    public void stabilizeUtils(double[][] reward_matrix){
        boolean keepGoing = true;
        double difference = Double.MAX_VALUE;
        double inner_dif;
        double beliefInFuture = 0.999;
        while (keepGoing){
            inner_dif = Double.MIN_VALUE;
            for (int y = 0; y < mundo.height; y++) {
                for (int x = 0; x < mundo.width; x++) {
                    if(mundo.grid[x][y] != 1){
                        double old_util = utils[x][y];
                        if (reward_matrix[x][y] != 0){
                            beliefInFuture = 0;
                        }
                        else{
                            beliefInFuture = 0.9;
                        }
                        utils[x][y] = reward_matrix[x][y] + beliefInFuture * (getMaxUtilState(x, y));
                        // Finding the greatest different between the old/updated value in utils
                        if (Math.abs(old_util - utils[x][y]) > inner_dif){
                            inner_dif = Math.abs(old_util - utils[x][y]);
                        }
                        
                    }
                }
            }
            //Check if we have stablized the reward matrix
            if (Math.abs(difference - inner_dif) < 0.01){
                keepGoing = false;
            }
            if(inner_dif < difference){
                difference = inner_dif;
            }
        }
    }
    // public static final int NORTH = 0;
    // public static final int SOUTH = 1;
    // public static final int EAST = 2;
    // public static final int WEST = 3;
    public double getMaxUtilState(int x, int y){
        ArrayList maxList = new ArrayList();
        for (int i = 0; i < 4; i++) {
            ArrayList utilsList = new ArrayList<>();
            if(mundo.grid[x][y-1] != 1){ // up 0
                utilsList.add((i == 0 ? moveProb : nonMoveProb) * utils[x][y-1]);
            }
            if (mundo.grid[x][y+1] != 1){ // down 1
                utilsList.add((i == 1 ? moveProb : nonMoveProb) * utils[x][y+1]);
            }
            if (mundo.grid[x+1][y] != 1){ // right 2
                utilsList.add((i == 2 ? moveProb : nonMoveProb) * utils[x+1][y]);
            }
            if (mundo.grid[x-1][y] != 1){ // left 3
                utilsList.add((i == 3 ? moveProb : nonMoveProb) * utils[x-1][y]);
            }
            Collections.sort(utilsList);
            maxList.add(utilsList.get(utilsList.size() - 1));
        }
        Collections.sort(maxList);
        double myMax = (double)maxList.get(maxList.size() - 1);
        return myMax;
    }

    // initializes the probabilities of where the AI is
    void initializeProbabilities() {
        probs = new double[mundo.width][mundo.height];
        // if the robot's initial position is known, reflect that in the probability map
        if (knownPosition) {
            for (int y = 0; y < mundo.height; y++) {
                for (int x = 0; x < mundo.width; x++) {
                    if ((x == startX) && (y == startY))
                        probs[x][y] = 1.0;
                    else
                        probs[x][y] = 0.0;
                }
            }
        }
        else {  // otherwise, set up a uniform prior over all the positions in the world that are open spaces
            int count = 0;
            
            for (int y = 0; y < mundo.height; y++) {
                for (int x = 0; x < mundo.width; x++) {
                    if (mundo.grid[x][y] == 0)
                        count++;
                }
            }
            
            for (int y = 0; y < mundo.height; y++) {
                for (int x = 0; x < mundo.width; x++) {
                    if (mundo.grid[x][y] == 0)
                        probs[x][y] = 1.0 / count;
                    else
                        probs[x][y] = 0;
                }
            }
        }
        myMaps.updateProbs(probs);
    }
    //For example, the sonar string 1001, specifies that the sonars found a wall in the North and West directions, but not in the South and East directions
    double findProbOfSonar(int x, int y,String sonars){
        String actual = "";
        if (mundo.grid[x][y-1] == 1) {actual += "1";}//up
        else {actual += "0";}
        if (mundo.grid[x][y+1] == 1) {actual += "1";}//down
        else {actual += "0";}
        if (mundo.grid[x+1][y] == 1) {actual += "1";}//right
        else {actual += "0";}
        if (mundo.grid[x-1][y] == 1) {actual += "1";}//left
        else {actual += "0";}
        //System.out.println("Reported vs Correct sonar: " + sonars + " " + actual);
        if (actual.equals(sonars)){
		    //System.out.println("Correct sensors");
		    return sensorAccuracy;
        }
        else{
            return (1-sensorAccuracy)/(mundo.width * mundo.height);
        }
    }
    // public static final int NORTH = 0;
    // public static final int SOUTH = 1;
    // public static final int EAST = 2;
    // public static final int WEST = 3;
    // probs:
    // [1][1] [1][2]
    // [2][1] [2][2]
    //mundo:
    //[1][1] [1][2]
    //[2][1] [2][2]
    void ProbCalc(int x, int y, int action, double[][] probsCopy){
        // The probability left over from a square in a cross being a wall
        double extraProb = 0.0;
        // The probability that we do not move in a direction that is not the move dir
        double nonMoveProb = ((1-moveProb)/3);
        //Check to see if the square above [x][y] is empty
        //System.out.println("\nprobsCopy[x][y]: [" + x + "] [" + y + "]");
        if(mundo.grid[x][y-1] == 0){ // up 0
            // if we are going up use action_prob, else use nonMovement
            //System.out.println(probsCopy[x-1][y]);
            probsCopy[x][y-1] += (action == 0 ? moveProb : nonMoveProb) * probs[x][y];
            //System.out.println(probsCopy[x-1][y]);
            //prettyPrint(probsCopy, "probsCopy");
        }
        else{ // if the space above us is not clear
            // store nonMove if we are not moving up, else store moveProb
            extraProb += (action != 0 ? nonMoveProb : moveProb); // store relevant prob
            //System.out.println("extraProb action 0: " + extraProb);
        }

        if (mundo.grid[x][y+1] == 0){ // down 1
            probsCopy[x][y+1] += (action == 1 ? moveProb : nonMoveProb) * probs[x][y];
        }
        else{
            extraProb += (action != 1 ? nonMoveProb : moveProb);
            //System.out.println("extraProb action 1: " + extraProb);
        }

        if (mundo.grid[x+1][y] == 0){ // right 2
            probsCopy[x+1][y] += (action == 2 ? moveProb : nonMoveProb) * probs[x][y];
        }
        else{
            extraProb += (action != 2 ? nonMoveProb : moveProb);
            //System.out.println("extraProb action 2: " + extraProb);
        }

        if (mundo.grid[x-1][y] == 0){ // left 3
            probsCopy[x-1][y] += (action == 3 ? moveProb : nonMoveProb) * probs[x][y];
        }
        else{
            extraProb += (action != 3 ? nonMoveProb : moveProb);
            //System.out.println("extraProb action 3: " + extraProb);
        }
        
        //System.out.println("extraProb: " + extraProb + " Prior: " + probs[x][y]);
        probsCopy[x][y] += extraProb * probs[x][y]; 
        //prettyPrint(probsCopy, "probsCopy2");
    }

    void updateProbs(double[][] probsCopy){
        for(int i = 0; i < probsCopy.length; i++){
            for (int j = 0; j < probsCopy.length; j++){
                probs[i][j] = probsCopy[i][j];
            }
        }
    }

    //       To do this, you should update the 2D-array "probs"
    // Note: sonars is a bit string with four characters, specifying the sonar reading in the direction of North, South, East, and West
    //       For example, the sonar string 1001, specifies that the sonars found a wall in the North and West directions, but not in the South and East directions
    void updateProbabilities(int action, String sonars) {
        // your code
        //copy probs
        double[][] probsCopy = new double[probs.length][probs.length];
        //prettyPrint(probs, "probs1");
        //prettyPrint(probsCopy, "probsCopy");
        for (int y = 0; y < mundo.height; y++) {
            for (int x = 0; x < mundo.width; x++) {
                if (mundo.grid[x][y] == 0){
                    ProbCalc(x, y, action, probsCopy);
                }
            }
        }
        updateProbs(probsCopy);
        //probsCopy = new double[probs.length][probs.length];
        //prettyPrint(probs, "probs2");
        //Add in sensor data
        double normalization = 0.0;
        for (int y = 0; y < mundo.height; y++) {
            for (int x = 0; x < mundo.width; x++) {
                if (mundo.grid[x][y] == 0){
                    double sonarProb = findProbOfSonar(x,y,sonars);
                    //System.out.println("SonarProb: " + sonarProb);
                    normalization += sonarProb * probs[x][y];
                    //System.out.println("spot: " + probs[x][y]);
                    probs[x][y] = sonarProb * probs[x][y]; // used to be just probs
                    //prettyPrint(probsCopy, "probsCopy");
                }
            }
        }
        //Add in normalization constant
        for (int y = 0; y < mundo.height; y++) {
            for (int x = 0; x < mundo.width; x++) {
                probs[x][y] = (1/normalization) * probs[x][y]; // used to just be equal to probs
            }
        }
        //updateProbs(probsCopy);
        //prettyPrint(probs, "probs3");
        myMaps.updateProbs(probs); // call this function after updating your probabilities so that the
                                   //  new probabilities will show up in the probability map on the GUI
    }                  //  new probabilities will show up in the probability map on the GUI


     // Gets the action that has garned the greatest utility overall
     int getAction(double[] move_array){
        double max = Double.MIN_VALUE;
        int index = -1;

        for(int i = 0; i < move_array.length; i++){
            if(max < move_array[i]){
                max = move_array[i];
                index = i;
            }
        }
        System.out.println("Move: " + index);
        return index;
    }

    // This is the function you'd need to write to make the robot move using your AI;
    // in this function we define how to choose an action. Basically you must see which direction garners the greatest utility
    // while factoring in the probability you are at squares allowing you to move in those directions
    // NORTH = 0;
    // SOUTH = 1;
    // EAST = 2;
    // WEST = 3;
    // STAY = 4
    int automaticAction() {
        // array holding each of the values for moving in a given direction
        double [] move_array = new double[4];
        //prettyPrint(probs, "Probs_action");
        // for every move I can make
        for (int i = 0; i < 4; i++) {
            double move_value = 0;
            // for every state in the matrix
            for (int y = 0; y < mundo.height; y++) {
                for (int x = 0; x < mundo.width; x++) {
                    double extraProb = 0.0;
                    // only look at the spaces you can move to
                    if(mundo.grid[x][y] != 1){
                            // add up the prob ill go to a square times the utility of that square times the prob in a position to get there
                        if(mundo.grid[x][y-1] !=1 ){
                            move_value += ((i == 0 ? moveProb : nonMoveProb) * utils[x][y-1]) * probs[x][y];
                        }
                        if(mundo.grid[x][y+1] !=1 ){
                            move_value += ((i == 1 ? moveProb : nonMoveProb) * utils[x][y+1]) * probs[x][y];
                        }
                        if(mundo.grid[x+1][y] !=1 ){
                            move_value += ((i == 2 ? moveProb : nonMoveProb) * utils[x+1][y]) * probs[x][y];
                        }
                        if(mundo.grid[x-1][y] !=1 ){
                            move_value += ((i == 3 ? moveProb : nonMoveProb) * utils[x-1][y]) * probs[x][y];
                        }
                        // prob im going to be staying put
                        //System.out.println(extraProb + " " + probs[x][y] + " " + utils[x][y]);
                        //move_value += extraProb * probs[x][y] * utils[x][y]; 
                    }
                }
            }
            move_array[i] = move_value;
        }
        // for (int i = 0; i < move_array.length; i++) {
        //     System.out.print(move_array[i] + " ");
        // }
        // System.out.println();
        return getAction(move_array);

    }

    void doStuff() {
        int action;
        
        //valueIteration();  // TODO: function you will write in Part II of the lab
        initializeProbabilities();  // Initializes the location (probability) map
        
        while (true) {
            try {
                if (isManual)
                    action = getHumanAction();  // get the action selected by the user (from the keyboard)
                else
                    action = automaticAction(); // TODO: get the action selected by your AI;
                                                // you'll need to write this function for part III
                
                sout.println(action); // send the action to the Server
                
                // get sonar readings after the robot moves
                String sonars = sin.readLine();
                //System.out.println("Sonars: " + sonars);
            
                updateProbabilities(action, sonars); // TODO: this function should update the probabilities of where the AI thinks it is
                
                if (sonars.length() > 4) {  // check to see if the robot has reached its goal or fallen down stairs
                    if (sonars.charAt(4) == 'w') {
                        System.out.println("I won!");
                        myMaps.setWin();
                        break;
                    }
                    else if (sonars.charAt(4) == 'l') {
                        System.out.println("I lost!");
                        myMaps.setLoss();
                        break;
                    }
                }
                else {
                    // here, you'll want to update the position probabilities
                    // since you know that the result of the move as that the robot
                    // was not at the goal or in a stairwell
                }
                Thread.sleep(decisionDelay);  // delay that is useful to see what is happening when the AI selects actions
                                              // decisionDelay is specified by the send command-line argument, which is given in milliseconds
            }
            catch (IOException e) {
                System.out.println(e);
            }
            catch(InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
    }
    
    public void prettyPrint(double[][] matrix, String functionName) {
        System.out.println(functionName);
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix.length; j++) {
                System.out.printf("%.5f ",matrix[i][j]);
            }
            System.out.println();
        }
    }
    public void prettyPrintInt(int[][] matrix, String functionName) {
        System.out.println(functionName);
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix.length; j++) {
                System.out.printf("%d ",matrix[i][j]);
            }
            System.out.println();
        }
    }
    // java theRobot [manual/automatic] [delay]
    public static void main(String[] args) {
        theRobot robot = new theRobot(args[0], Integer.parseInt(args[1]));  // starts up the robot
    }
}