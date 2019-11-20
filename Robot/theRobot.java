
import javax.swing.*;
import java.awt.event.*;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.lang.*;
import javax.swing.JComponent;
import javax.swing.JFrame;
import java.io.*;
import java.util.Random;
import java.util.Scanner;
import java.net.*;
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;


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
        System.out.println("Update Probs");
        for (int i = 0; i < mundo.height; i++) {
            for (int j = 0; j < mundo.width; j++) {
                System.out.printf("%.5f ",_probs[i][j]);
            }
            System.out.println();
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

    /*
    * Added the look up tables for the probabilities
    */
    double[][] left;
    double[][] right;
    double[][] up;
    double[][] down;
    int[][] state_id;
    Map<Integer, int[]> state_coord;
    Map<int[], Integer> coord_to_id;

    Color bkgroundColor = new Color(230,230,230);

    static mySmartMap myMaps; // instance of the class that draw everything to the GUI
    String mundoName;

    World mundo; // mundo contains all the information about the world.  See World.java
    double moveProb, sensorAccuracy;  // stores probabilies that the robot moves in the intended direction
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

    // store your computed value of being in each state (x, y)
    double[][] Vs;


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

        /*
        * Create the look up tables for the probabilities of each state for each direction
        */
        int state_count = 0;

        state_coord = new HashMap<Integer,int[]>();
        coord_to_id = new HashMap<int[], Integer>();



        // State table. Recreate the mundo world with the integer to represent the name of the state
        state_id = new int[mundo.height][mundo.width];
        // Find out how many States we have
        for (int y = 0; y < mundo.height; y++) {
            for (int x = 0; x < mundo.width; x++) {
                if (mundo.grid[x][y] == 0) {
                    // Assigning IDs to each state from top left to bottem right
                    state_id[x][y] = state_count;
                    int[] coord = new int[2];
                    coord[0] = x;
                    coord[1] = y;
                    state_coord.put(state_count, coord);

                    state_count++;

                }
                else {
                    state_id[x][y] = -1;
                }
            }
        }
        System.out.println("State ID to Coord");
        for (Map.Entry<Integer, int[]> entry : state_coord.entrySet()) {
            System.out.println(entry.getKey() + ":" + entry.getValue()[0] + " " + entry.getValue()[1]);
        }


        double robot_move_assurance = moveProb;

        left = new double[state_count][state_count];
        right = new double[state_count][state_count];
        up = new double[state_count][state_count];
        down = new double[state_count][state_count];

        // Each row is a state and the row array represent the probability we will go to the state i given we are at the current state (row index)
        // Create the 4 look up tables
        for (int t = 0; t < 4; t++) {                               // The four tables
            int probability_index = 0;
            for(int y = 0; y < mundo.height; y++) {
                for (int x = 0; x < mundo.width; x++) {
                    if (mundo.grid[x][y] == 0) {                    // If it is a possible state
                        double[] temp_probability = new double[state_count];
                        double non_robot_move = (1 - robot_move_assurance) / 3;
                        if (t == 0) { // Tells us Robot Direction - UP
                            // Edge Cases Included
                            if (x == 0 || state_id[x-1][y] == -1) { // Wall - UP
                                temp_probability[state_id[x][y]] += robot_move_assurance; // Add the probability of robot to current spot

                            }
                            else {
                                temp_probability[state_id[x-1][y]] += robot_move_assurance;
                            }
                            if (y == (state_count - 1) || state_id[x][y+1] == -1) { // Wall- RIGHT
                                temp_probability[state_id[x][y]] += non_robot_move;
                            }
                            else {
                                temp_probability[state_id[x][y+1]] += non_robot_move;
                            }
                            if ( x == (state_count - 1) || state_id[x+1][y] == -1) { // Wall - DOWN
                                temp_probability[state_id[x][y]] += non_robot_move;
                            }
                            else {
                                temp_probability[state_id[x+1][y]] += non_robot_move;
                            }
                            if (y == 0 || state_id[x][y-1] == -1) { // Wall - LEFT
                                temp_probability[state_id[x][y]] += non_robot_move;
                            }
                            else {
                                temp_probability[state_id[x][y-1]] += non_robot_move;
                            }
                        }
                        if (t == 1) { // Tells us Robot Direction - RIGHT
                            if (x == 0 || state_id[x-1][y] == -1) { // Wall - UP
                                temp_probability[state_id[x][y]] += non_robot_move;
                            }
                            else {
                                temp_probability[state_id[x-1][y]] += non_robot_move;
                            }
                            if (y == (state_count - 1) || state_id[x][y+1] == -1) { // Wall- RIGHT
                                temp_probability[state_id[x][y]] += robot_move_assurance;
                            }
                            else {
                                temp_probability[state_id[x][y+1]] += robot_move_assurance;
                            }
                            if (x == (state_count - 1) || state_id[x+1][y] == -1) { // Wall - DOWN
                                temp_probability[state_id[x][y]] += non_robot_move;
                            }
                            else {
                                temp_probability[state_id[x+1][y]] += non_robot_move;
                            }
                            if (y == 0 || state_id[x][y-1] == -1) { // Wall - LEFT
                                temp_probability[state_id[x][y]] += non_robot_move;
                            }
                            else {
                                temp_probability[state_id[x][y-1]] += non_robot_move;
                            }
                        }
                        if (t == 2) { // Tells us Robot Direction - DOWN
                            if (x == 0 || state_id[x-1][y] == -1) { // Wall - UP
                                temp_probability[state_id[x][y]] += non_robot_move;
                            }
                            else {
                                temp_probability[state_id[x-1][y]] += non_robot_move;
                            }
                            if (y == (state_count - 1) || state_id[x][y+1] == -1) { // Wall- RIGHT
                                temp_probability[state_id[x][y]] += non_robot_move;
                            }
                            else {
                                temp_probability[state_id[x][y+1]] += non_robot_move;
                            }
                            if (y == (state_count - 1) || state_id[x+1][y] == -1) { // Wall - DOWN
                                temp_probability[state_id[x][y]] += robot_move_assurance;
                            }
                            else {
                                temp_probability[state_id[x+1][y]] += robot_move_assurance;
                            }
                            if (y == 0 || state_id[x][y-1] == -1) { // Wall - LEFT
                                temp_probability[state_id[x][y]] += non_robot_move;
                            }
                            else {
                                temp_probability[state_id[x][y-1]] += non_robot_move;
                            }
                        }
                        if (t == 3) { // Tells us Robot Direction - LEFT
                            if (x == 0 || state_id[x-1][y] == -1) { // Wall - UP
                                temp_probability[state_id[x][y]] += non_robot_move;
                            }
                            else {
                                temp_probability[state_id[x-1][y]] += non_robot_move;
                            }
                            if (y == (state_count - 1) || state_id[x][y+1] == -1) { // Wall- RIGHT
                                temp_probability[state_id[x][y]] += non_robot_move;
                            }
                            else {
                                temp_probability[state_id[x][y+1]] += non_robot_move;
                            }
                            if (x == (state_count - 1) || state_id[x+1][y] == -1) { // Wall - DOWN
                                temp_probability[state_id[x][y]] += non_robot_move;
                            }
                            else {
                                temp_probability[state_id[x+1][y]] += non_robot_move;
                            }
                            if (y == 0 || state_id[x][y-1] == -1) { // Wall - LEFT
                                temp_probability[state_id[x][y]] += robot_move_assurance;
                            }
                            else {
                                temp_probability[state_id[x][y-1]] += robot_move_assurance;
                            }
                        }

                        // Fill in each table, but need to fix so it is not based on y, it should be based on count
                        if (t == 0) {
                            // System.out.println("New Update on UP");
                            for (int i = 0; i < state_count; i++) {
                                // System.out.print(temp_probability[i] + " ");
                                up[i][probability_index] = temp_probability[i];
                            }
                            // System.out.println();
                        }
                        if (t == 1) {
                            // System.out.println("New Update on RIGHT");
                            for (int i = 0; i < state_count; i++) {
                                // System.out.print(temp_probability[i] + " ");
                                right[i][probability_index] = temp_probability[i];
                            }
                            // System.out.println();
                        }
                        if (t == 2) {
                            // System.out.println("New Update on DOWN");
                            for (int i = 0; i < state_count; i++) {
                                // System.out.print(temp_probability[i] + " ");
                                down[i][probability_index] = temp_probability[i];
                            }
                            // System.out.println();
                        }
                        if (t == 3) {
                            for (int i = 0; i < state_count; i++) {
                                left[i][probability_index] = temp_probability[i];
                            }
                            // System.out.println();
                        }
                        probability_index++;
                    }

                }
            }
        }

        prettyPrint(up, "Constructor - UP");
        prettyPrint(right, "Constructor - RIGHT");
        prettyPrint(down, "Constructor - DOWN");
        prettyPrint(left, "Constructor - LEFT");


        /*
        * End of Addition
        */



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

        System.out.println("Action: " + a + " ");

        return a;
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

        prettyPrint(probs, "Initialize Probabilities");


        myMaps.updateProbs(probs);
    }
    double findProbOfSonar(int y, int x,String sonars){
        String actual = "";
        if (mundo.grid[y-1][x] == 0) {actual += "1";}
        else {actual += "0";}
        if (mundo.grid[y][x-1] == 0) {actual += "1";}
        else {actual += "0";}
        if (mundo.grid[y+1][x] == 0) {actual += "1";}
        else {actual += "0";}
        if (mundo.grid[y][x+1] == 0) {actual += "1";}
        else {actual += "0";}
        if (actual.equals(sonars)){
		System.out.println("Correct sensors");
		return sensorAccuracy;
	    
        }
        else{
            return (1-sensorAccuracy);
        }
    }
    // TODO: update the probabilities of where the AI thinks it is based on the action selected and the new sonar readings
    //       To do this, you should update the 2D-array "probs"
    // Note: sonars is a bit string with four characters, specifying the sonar reading in the direction of North, South, East, and West
    //       For example, the sonar string 1001, specifies that the sonars found a wall in the North and West directions, but not in the South and East directions
    void updateProbabilities(int action, String sonars) {
        // your code

        Map<Integer, double[][]> actionMap = new HashMap<Integer, double[][]>();
        actionMap.put(0,up);
        actionMap.put(1,down);
        actionMap.put(2,right);
        actionMap.put(3,left);

        for (int y = 0; y < mundo.height; y++) {
            for (int x = 0; x < mundo.width; x++) {
                if (mundo.grid[x][y] == 0){
                    System.out.println("Valid State");
                    int id = state_id[x][y];
                    System.out.println("ID of the State We Are Calcuating From: " + id);
                    for (int j = 0; j < state_id.length; j++) {
                        double action_prob = actionMap.get(action)[j][id];
                        double temp_sum = 0;
                        System.out.println("Column Value For S" + id + " at Ps" + j + " : " + action_prob);
                        // if (j == id) {
                        //     System.out.println("J == ID");
                        //     int cross_coordinate_X = state_coord.get(j)[0];
                        //     int cross_coordinate_Y= state_coord.get(j)[1];
                        //     temp_sum = action_prob * probs[x][y];
                        //     probs[cross_coordinate_Y][cross_coordinate_X] = temp_sum;
                        // }
                        if (action_prob != 0) {
                            System.out.println("Map Coordinates of One of the Crosses : " + state_coord.get(j)[0] + " " + state_coord.get(j)[1]);
                            int cross_coordinate_X = state_coord.get(j)[0];
                            int cross_coordinate_Y= state_coord.get(j)[1];
                            temp_sum = action_prob * probs[x][y];
                            System.out.println("Prior Prob of X Y: " + x + " " + y + " Value: " + probs[x][y]);
                            System.out.println("New Probability Addition: " + temp_sum);
                            probs[cross_coordinate_Y][cross_coordinate_X] += temp_sum;
     
                        }     	
		            }
                }
            }
        }
        // Add in sensor data
        // double normalization = 0.0;
        // for (int y = 0; y < mundo.height; y++) {
        //     for (int x = 0; x < mundo.width; x++) {
        //         if (mundo.grid[x][y] == 0){
        //             double sonarProb = findProbOfSonar(x,y,sonars);
        //             normalization += sonarProb * probs[x][y];
        //             probs[x][y] = sonarProb * probs[x][y];
        //         }
        //     }
        // }
        // Add in normalization constant
        // for (int y = 0; y < mundo.height; y++) {
        //     for (int x = 0; x < mundo.width; x++) {
        //         probs[x][y] = (1/normalization) * probs[x][y];
        //     }
        // }

        prettyPrint(probs, "Update Probabilities");

        myMaps.updateProbs(probs); // call this function after updating your probabilities so that the
                                   //  new probabilities will show up in the probability map on the GUI
    }

    // This is the function you'd need to write to make the robot move using your AI;
    // You do NOT need to write this function for this lab; it can remain as is
    int automaticAction() {

        return STAY;  // default action for now
    }

    void doStuff() {

        int action;

        //valueIteration();  // TODO: function you will write in Part II of the lab
        initializeProbabilities();  // Initializes the location (probability) map

        while (true) {
            try {
                if (isManual) {
                    action = getHumanAction();  // get the action selected by the user (from the keyboard)
                    System.out.println(action);
                }
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



// This is the original

// import javax.swing.*;
// import java.awt.event.*;
// import java.awt.Color;
// import java.awt.Font;
// import java.awt.Graphics;
// import java.lang.*;
// import javax.swing.JComponent;
// import javax.swing.JFrame;
// import java.io.*;
// import java.util.Random;
// import java.util.Scanner;
// import java.net.*;
// import java.util.Map;
// import java.util.HashMap;
// import java.util.Arrays;


// // This class draws the probability map and value iteration map that you create to the window
// // You need only call updateProbs() and updateValues() from your theRobot class to update these maps
// class mySmartMap extends JComponent implements KeyListener {
//     public static final int NORTH = 0;
//     public static final int SOUTH = 1;
//     public static final int EAST = 2;
//     public static final int WEST = 3;
//     public static final int STAY = 4;

//     int currentKey;

//     int winWidth, winHeight;
//     double sqrWdth, sqrHght;
//     Color gris = new Color(170,170,170);
//     Color myWhite = new Color(220, 220, 220);
//     World mundo;

//     int gameStatus;

//     double[][] probs;
//     double[][] vals;

//     public mySmartMap(int w, int h, World wld) {
//         mundo = wld;
//         probs = new double[mundo.width][mundo.height];
//         vals = new double[mundo.width][mundo.height];
//         winWidth = w;
//         winHeight = h;

//         sqrWdth = (double)w / mundo.width;
//         sqrHght = (double)h / mundo.height;
//         currentKey = -1;

//         addKeyListener(this);

//         gameStatus = 0;
//     }

//     public void addNotify() {
//         super.addNotify();
//         requestFocus();
//     }

//     public void setWin() {
//         gameStatus = 1;
//         repaint();
//     }

//     public void setLoss() {
//         gameStatus = 2;
//         repaint();
//     }

//     public void updateProbs(double[][] _probs) {
//         for (int y = 0; y < mundo.height; y++) {
//             for (int x = 0; x < mundo.width; x++) {
//                 probs[x][y] = _probs[x][y];
//             }
//         }
//         System.out.println("Update Probs");
//         for (int i = 0; i < mundo.height; i++) {
//             for (int j = 0; j < mundo.width; j++) {
//                 System.out.printf("%.5f ",_probs[i][j]);
//             }
//             System.out.println();
//         }

//         repaint();
//     }

//     public void updateValues(double[][] _vals) {
//         for (int y = 0; y < mundo.height; y++) {
//             for (int x = 0; x < mundo.width; x++) {
//                 vals[x][y] = _vals[x][y];
//             }
//         }

//         repaint();
//     }

//     public void paint(Graphics g) {
//         paintProbs(g);
//         //paintValues(g);
//     }

//     public void paintProbs(Graphics g) {
//         double maxProbs = 0.0;
//         int mx = 0, my = 0;
//         for (int y = 0; y < mundo.height; y++) {
//             for (int x = 0; x < mundo.width; x++) {
//                 if (probs[x][y] > maxProbs) {
//                     maxProbs = probs[x][y];
//                     mx = x;
//                     my = y;
//                 }
//                 if (mundo.grid[x][y] == 1) {
//                     g.setColor(Color.black);
//                     g.fillRect((int)(x * sqrWdth), (int)(y * sqrHght), (int)sqrWdth, (int)sqrHght);
//                 }
//                 else if (mundo.grid[x][y] == 0) {
//                     //g.setColor(myWhite);

//                     int col = (int)(255 * Math.sqrt(probs[x][y]));
//                     if (col > 255)
//                         col = 255;
//                     g.setColor(new Color(255-col, 255-col, 255));
//                     g.fillRect((int)(x * sqrWdth), (int)(y * sqrHght), (int)sqrWdth, (int)sqrHght);
//                 }
//                 else if (mundo.grid[x][y] == 2) {
//                     g.setColor(Color.red);
//                     g.fillRect((int)(x * sqrWdth), (int)(y * sqrHght), (int)sqrWdth, (int)sqrHght);
//                 }
//                 else if (mundo.grid[x][y] == 3) {
//                     g.setColor(Color.green);
//                     g.fillRect((int)(x * sqrWdth), (int)(y * sqrHght), (int)sqrWdth, (int)sqrHght);
//                 }

//             }
//             if (y != 0) {
//                 g.setColor(gris);
//                 g.drawLine(0, (int)(y * sqrHght), (int)winWidth, (int)(y * sqrHght));
//             }
//         }
//         for (int x = 0; x < mundo.width; x++) {
//                 g.setColor(gris);
//                 g.drawLine((int)(x * sqrWdth), 0, (int)(x * sqrWdth), (int)winHeight);
//         }

//         //System.out.println("repaint maxProb: " + maxProbs + "; " + mx + ", " + my);

//         g.setColor(Color.green);
//         g.drawOval((int)(mx * sqrWdth)+1, (int)(my * sqrHght)+1, (int)(sqrWdth-1.4), (int)(sqrHght-1.4));

//         if (gameStatus == 1) {
//             g.setColor(Color.green);
//             g.drawString("You Won!", 8, 25);
//         }
//         else if (gameStatus == 2) {
//             g.setColor(Color.red);
//             g.drawString("You're a Loser!", 8, 25);
//         }
//     }

//     public void paintValues(Graphics g) {
//         double maxVal = -99999, minVal = 99999;
//         int mx = 0, my = 0;

//         for (int y = 0; y < mundo.height; y++) {
//             for (int x = 0; x < mundo.width; x++) {
//                 if (mundo.grid[x][y] != 0)
//                     continue;

//                 if (vals[x][y] > maxVal)
//                     maxVal = vals[x][y];
//                 if (vals[x][y] < minVal)
//                     minVal = vals[x][y];
//             }
//         }
//         if (minVal == maxVal) {
//             maxVal = minVal+1;
//         }

//         int offset = winWidth+20;
//         for (int y = 0; y < mundo.height; y++) {
//             for (int x = 0; x < mundo.width; x++) {
//                 if (mundo.grid[x][y] == 1) {
//                     g.setColor(Color.black);
//                     g.fillRect((int)(x * sqrWdth)+offset, (int)(y * sqrHght), (int)sqrWdth, (int)sqrHght);
//                 }
//                 else if (mundo.grid[x][y] == 0) {
//                     //g.setColor(myWhite);

//                     //int col = (int)(255 * Math.sqrt((vals[x][y]-minVal)/(maxVal-minVal)));
//                     int col = (int)(255 * (vals[x][y]-minVal)/(maxVal-minVal));
//                     if (col > 255)
//                         col = 255;
//                     g.setColor(new Color(255-col, 255-col, 255));
//                     g.fillRect((int)(x * sqrWdth)+offset, (int)(y * sqrHght), (int)sqrWdth, (int)sqrHght);
//                 }
//                 else if (mundo.grid[x][y] == 2) {
//                     g.setColor(Color.red);
//                     g.fillRect((int)(x * sqrWdth)+offset, (int)(y * sqrHght), (int)sqrWdth, (int)sqrHght);
//                 }
//                 else if (mundo.grid[x][y] == 3) {
//                     g.setColor(Color.green);
//                     g.fillRect((int)(x * sqrWdth)+offset, (int)(y * sqrHght), (int)sqrWdth, (int)sqrHght);
//                 }

//             }
//             if (y != 0) {
//                 g.setColor(gris);
//                 g.drawLine(offset, (int)(y * sqrHght), (int)winWidth+offset, (int)(y * sqrHght));
//             }
//         }
//         for (int x = 0; x < mundo.width; x++) {
//                 g.setColor(gris);
//                 g.drawLine((int)(x * sqrWdth)+offset, 0, (int)(x * sqrWdth)+offset, (int)winHeight);
//         }
//     }


//     public void keyPressed(KeyEvent e) {
//         //System.out.println("keyPressed");
//     }
//     public void keyReleased(KeyEvent e) {
//         //System.out.println("keyReleased");
//     }
//     public void keyTyped(KeyEvent e) {
//         char key = e.getKeyChar();
//         //System.out.println(key);

//         switch (key) {
//             case 'i':
//                 currentKey = NORTH;
//                 break;
//             case ',':
//                 currentKey = SOUTH;
//                 break;
//             case 'j':
//                 currentKey = WEST;
//                 break;
//             case 'l':
//                 currentKey = EAST;
//                 break;
//             case 'k':
//                 currentKey = STAY;
//                 break;
//         }
//     }
// }


// // This is the main class that you will add to in order to complete the lab
// public class theRobot extends JFrame {
//     // Mapping of actions to integers
//     public static final int NORTH = 0;
//     public static final int SOUTH = 1;
//     public static final int EAST = 2;
//     public static final int WEST = 3;
//     public static final int STAY = 4;

//     /*
//     * Added the look up tables for the probabilities
//     */
//     double[][] left;
//     double[][] right;
//     double[][] up;
//     double[][] down;

//     Color bkgroundColor = new Color(230,230,230);

//     static mySmartMap myMaps; // instance of the class that draw everything to the GUI
//     String mundoName;

//     World mundo; // mundo contains all the information about the world.  See World.java
//     double moveProb, sensorAccuracy;  // stores probabilies that the robot moves in the intended direction
//                                       // and the probability that a sonar reading is correct, respectively

//     // variables to communicate with the Server via sockets
//     public Socket s;
// 	public BufferedReader sin;
// 	public PrintWriter sout;

//     // variables to store information entered through the command-line about the current scenario
//     boolean isManual = false; // determines whether you (manual) or the AI (automatic) controls the robots movements
//     boolean knownPosition = false;
//     int startX = -1, startY = -1;
//     int decisionDelay = 250;

//     // store your probability map (for position of the robot in this array
//     double[][] probs;

//     // store your computed value of being in each state (x, y)
//     double[][] Vs;


//     public theRobot(String _manual, int _decisionDelay) {
//         // initialize variables as specified from the command-line
//         if (_manual.equals("automatic"))
//             isManual = false;
//         else
//             isManual = true;
//         decisionDelay = _decisionDelay;

//         // get a connection to the server and get initial information about the world
//         initClient();

//         // Read in the world
//         mundo = new World(mundoName);

//         /*
//         * Create the look up tables for the probabilities of each state for each direction
//         */
//         int state_count = 0;

//         // State table. Recreate the mundo world with the integer to represent the name of the state
//         int[][] state_id = new int[mundo.height][mundo.width];
//         // Find out how many States we have
//         for (int y = 0; y < mundo.height; y++) {
//             for (int x = 0; x < mundo.width; x++) {
//                 if (mundo.grid[y][x] == 0) {
//                     // Assigning IDs to each state from top left to bottem right
//                     state_id[y][x] = state_count;
//                     state_count++;
//                 }
//                 else {
//                     state_id[y][x] = -1;
//                 }
//             }
//         }

//         double robot_move_assurance = moveProb;

//         left = new double[state_count][state_count];
//         right = new double[state_count][state_count];
//         up = new double[state_count][state_count];
//         down = new double[state_count][state_count];

//         // Each row is a state and the row array represent the probability we will go to the state i given we are at the current state (row index)
//         // Create the 4 look up tables
//         for (int t = 0; t < 4; t++) {                               // The four tables
//             int probability_index = 0;
//             for(int y = 0; y < mundo.height; y++) {
//                 for (int x = 0; x < mundo.width; x++) {
//                     if (mundo.grid[y][x] == 0) {                    // If it is a possible state
//                         double[] temp_probability = new double[state_count];
//                         double non_robot_move = (1 - robot_move_assurance) / 3;
//                         if (t == 0) { // Tells us Robot Direction - UP
//                             // Edge Cases Included
//                             if (y == 0 || state_id[y-1][x] == -1) { // Wall - UP
//                                 temp_probability[state_id[y][x]] += robot_move_assurance; // Add the probability of robot to current spot

//                             }
//                             else {
//                                 temp_probability[state_id[y-1][x]] += robot_move_assurance;
//                             }
//                             if (x == (state_count - 1) || state_id[y][x+1] == -1) { // Wall- RIGHT
//                                 temp_probability[state_id[y][x]] += non_robot_move;
//                             }
//                             else {
//                                 temp_probability[state_id[y][x+1]] += non_robot_move;
//                             }
//                             if ( y == (state_count - 1) || state_id[y+1][x] == -1) { // Wall - DOWN
//                                 temp_probability[state_id[y][x]] += non_robot_move;
//                             }
//                             else {
//                                 temp_probability[state_id[y+1][x]] += non_robot_move;
//                             }
//                             if (x == 0 || state_id[y][x-1] == -1) { // Wall - LEFT
//                                 temp_probability[state_id[y][x]] += non_robot_move;
//                             }
//                             else {
//                                 temp_probability[state_id[y][x-1]] += non_robot_move;
//                             }
//                         }
//                         if (t == 1) { // Tells us Robot Direction - RIGHT
//                             if (y == 0 || state_id[y-1][x] == -1) { // Wall - UP
//                                 temp_probability[state_id[y][x]] += non_robot_move;
//                             }
//                             else {
//                                 temp_probability[state_id[y-1][x]] += non_robot_move;
//                             }
//                             if (x == (state_count - 1) || state_id[y][x+1] == -1) { // Wall- RIGHT
//                                 temp_probability[state_id[y][x]] += robot_move_assurance;
//                             }
//                             else {
//                                 temp_probability[state_id[y][x+1]] += robot_move_assurance;
//                             }
//                             if (y == (state_count - 1) || state_id[y+1][x] == -1) { // Wall - DOWN
//                                 temp_probability[state_id[y][x]] += non_robot_move;
//                             }
//                             else {
//                                 temp_probability[state_id[y+1][x]] += non_robot_move;
//                             }
//                             if (x == 0 || state_id[y][x-1] == -1) { // Wall - LEFT
//                                 temp_probability[state_id[y][x]] += non_robot_move;
//                             }
//                             else {
//                                 temp_probability[state_id[y][x-1]] += non_robot_move;
//                             }
//                         }
//                         if (t == 2) { // Tells us Robot Direction - DOWN
//                             if (y == 0 || state_id[y-1][x] == -1) { // Wall - UP
//                                 temp_probability[state_id[y][x]] += non_robot_move;
//                             }
//                             else {
//                                 temp_probability[state_id[y-1][x]] += non_robot_move;
//                             }
//                             if (x == (state_count - 1) || state_id[y][x+1] == -1) { // Wall- RIGHT
//                                 temp_probability[state_id[y][x]] += non_robot_move;
//                             }
//                             else {
//                                 temp_probability[state_id[y][x+1]] += non_robot_move;
//                             }
//                             if (x == (state_count - 1) || state_id[y+1][x] == -1) { // Wall - DOWN
//                                 temp_probability[state_id[y][x]] += robot_move_assurance;
//                             }
//                             else {
//                                 temp_probability[state_id[y+1][x]] += robot_move_assurance;
//                             }
//                             if (x == 0 || state_id[y][x-1] == -1) { // Wall - LEFT
//                                 temp_probability[state_id[y][x]] += non_robot_move;
//                             }
//                             else {
//                                 temp_probability[state_id[y][x-1]] += non_robot_move;
//                             }
//                         }
//                         if (t == 3) { // Tells us Robot Direction - LEFT
//                             if (y == 0 || state_id[y-1][x] == -1) { // Wall - UP
//                                 temp_probability[state_id[y][x]] += non_robot_move;
//                             }
//                             else {
//                                 temp_probability[state_id[y-1][x]] += non_robot_move;
//                             }
//                             if (x == (state_count - 1) || state_id[y][x+1] == -1) { // Wall- RIGHT
//                                 temp_probability[state_id[y][x]] += non_robot_move;
//                             }
//                             else {
//                                 temp_probability[state_id[y][x+1]] += non_robot_move;
//                             }
//                             if (y == (state_count - 1) || state_id[y+1][x] == -1) { // Wall - DOWN
//                                 temp_probability[state_id[y][x]] += non_robot_move;
//                             }
//                             else {
//                                 temp_probability[state_id[y+1][x]] += non_robot_move;
//                             }
//                             if (x == 0 || state_id[y][x-1] == -1) { // Wall - LEFT
//                                 temp_probability[state_id[y][x]] += robot_move_assurance;
//                             }
//                             else {
//                                 temp_probability[state_id[y][x-1]] += robot_move_assurance;
//                             }
//                         }

//                         // Fill in each table, but need to fix so it is not based on y, it should be based on count
//                         if (t == 0) {
//                             // System.out.println("New Update on UP");
//                             for (int i = 0; i < state_count; i++) {
//                                 // System.out.print(temp_probability[i] + " ");
//                                 up[probability_index][i] = temp_probability[i];
//                             }
//                             // System.out.println();
//                         }
//                         if (t == 1) {
//                             // System.out.println("New Update on RIGHT");
//                             for (int i = 0; i < state_count; i++) {
//                                 // System.out.print(temp_probability[i] + " ");
//                                 right[probability_index][i] = temp_probability[i];
//                             }
//                             // System.out.println();
//                         }
//                         if (t == 2) {
//                             // System.out.println("New Update on DOWN");
//                             for (int i = 0; i < state_count; i++) {
//                                 // System.out.print(temp_probability[i] + " ");
//                                 down[probability_index][i] = temp_probability[i];
//                             }
//                             // System.out.println();
//                         }
//                         if (t == 3) {
//                             for (int i = 0; i < state_count; i++) {
//                                 left[probability_index][i] = temp_probability[i];
//                             }
//                             // System.out.println();
//                         }
//                         probability_index++;
//                     }

//                 }
//             }
//         }

//         prettyPrint(up, "Constructor - UP");
//         prettyPrint(right, "Constructor - RIGHT");
//         prettyPrint(down, "Constructor - DOWN");
//         prettyPrint(left, "Constructor - LEFT");


//         /*
//         * End of Addition
//         */



//         // set up the GUI that displays the information you compute
//         int width = 500;
//         int height = 500;
//         int bar = 20;
//         setSize(width,height+bar);
//         getContentPane().setBackground(bkgroundColor);
//         setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//         setBounds(0, 0, width, height+bar);
//         myMaps = new mySmartMap(width, height, mundo);
//         getContentPane().add(myMaps);

//         setVisible(true);
//         setTitle("Probability and Value Maps");

//         doStuff(); // Function to have the robot move about its world until it gets to its goal or falls in a stairwell
//     }

//     // this function establishes a connection with the server and learns
//     //   1 -- which world it is in
//     //   2 -- it's transition model (specified by moveProb)
//     //   3 -- it's sensor model (specified by sensorAccuracy)
//     //   4 -- whether it's initial position is known.  if known, its position is stored in (startX, startY)
//     public void initClient() {
//         int portNumber = 3333;
//         String host = "localhost";

//         try {
// 			s = new Socket(host, portNumber);
//             sout = new PrintWriter(s.getOutputStream(), true);
// 			sin = new BufferedReader(new InputStreamReader(s.getInputStream()));

//             mundoName = sin.readLine();
//             moveProb = Double.parseDouble(sin.readLine());
//             sensorAccuracy = Double.parseDouble(sin.readLine());
//             System.out.println("Need to open the mundo: " + mundoName);
//             System.out.println("moveProb: " + moveProb);
//             System.out.println("sensorAccuracy: " + sensorAccuracy);

//             // find out of the robots position is know
//             String _known = sin.readLine();
//             if (_known.equals("known")) {
//                 knownPosition = true;
//                 startX = Integer.parseInt(sin.readLine());
//                 startY = Integer.parseInt(sin.readLine());
//                 System.out.println("Robot's initial position is known: " + startX + ", " + startY);
//             }
//             else {
//                 System.out.println("Robot's initial position is unknown");
//             }
//         } catch (IOException e) {
//             System.err.println("Caught IOException: " + e.getMessage());
//         }
//     }

//     // function that gets human-specified actions
//     // 'i' specifies the movement up
//     // ',' specifies the movement down
//     // 'l' specifies the movement right
//     // 'j' specifies the movement left
//     // 'k' specifies the movement stay
//     int getHumanAction() {
//         System.out.println("Reading the action selected by the user");
//         while (myMaps.currentKey < 0) {
//             try {
//                 Thread.sleep(50);
//             }
//             catch(InterruptedException ex) {
//                 Thread.currentThread().interrupt();
//             }
//         }
//         int a = myMaps.currentKey;
//         myMaps.currentKey = -1;

//         System.out.println("Action: " + a);

//         return a;
//     }

//     // initializes the probabilities of where the AI is
//     void initializeProbabilities() {
//         probs = new double[mundo.width][mundo.height];
//         // if the robot's initial position is known, reflect that in the probability map
//         if (knownPosition) {
//             for (int y = 0; y < mundo.height; y++) {
//                 for (int x = 0; x < mundo.width; x++) {
//                     if ((x == startX) && (y == startY))
//                         probs[x][y] = 1.0;
//                     else
//                         probs[x][y] = 0.0;
//                 }
//             }
//         }
//         else {  // otherwise, set up a uniform prior over all the positions in the world that are open spaces
//             int count = 0;

//             for (int y = 0; y < mundo.height; y++) {
//                 for (int x = 0; x < mundo.width; x++) {
//                     if (mundo.grid[x][y] == 0)
//                         count++;
//                 }
//             }

//             for (int y = 0; y < mundo.height; y++) {
//                 for (int x = 0; x < mundo.width; x++) {
//                     if (mundo.grid[x][y] == 0)
//                         probs[x][y] = 1.0 / count;
//                     else
//                         probs[x][y] = 0;
//                 }
//             }
//         }

//         prettyPrint(probs, "Initialize Probabilities");


//         myMaps.updateProbs(probs);
//     }
//     double findProbOfSonar(int y, int x,String sonars){
//         String actual = "";
//         if (mundo.grid[y-1][x] == -1) {actual += "1";}
//         else {actual += "0";}
//         if (mundo.grid[y][x-1] == -1) {actual += "1";}
//         else {actual += "0";}
//         if (mundo.grid[y+1][x] == -1) {actual += "1";}
//         else {actual += "0";}
//         if (mundo.grid[y][x+1] == -1) {actual += "1";}
//         else {actual += "0";}
//         if (actual.equals(sonars)){
//             return sensorAccuracy;
//         }
//         else{
//             return (1-sensorAccuracy);
//         }
//     }
//     // TODO: update the probabilities of where the AI thinks it is based on the action selected and the new sonar readings
//     //       To do this, you should update the 2D-array "probs"
//     // Note: sonars is a bit string with four characters, specifying the sonar reading in the direction of North, South, East, and West
//     //       For example, the sonar string 1001, specifies that the sonars found a wall in the North and West directions, but not in the South and East directions
//     void updateProbabilities(int action, String sonars) {
//         // your code
//         // System.out.println(Arrays.deepToString(left));
//         // System.out.println(Arrays.deepToString(right));
//         // System.out.println(Arrays.deepToString(up));
//         // System.out.println(Arrays.deepToString(down));


//         // Map<Integer, double[][]> actionMap = new HashMap<Integer, double[][]>();
//         // actionMap.put(0,up);
//         // actionMap.put(1,down);
//         // actionMap.put(2,right);
//         // actionMap.put(3,left);

//         // for (int y = 0; y < mundo.height; y++) {
//         //     for (int x = 0; x < mundo.width; x++) {
//         //         if (mundo.grid[y][x] == 0){
//         //             probs[y][x] = actionMap.get(action)[y][x] * probs[y][x];
//         //         }
//         //     }
//         // }
//         // // Add in sensor data
//         // double normalization = 0.0;
//         // for (int y = 0; y < mundo.height; y++) {
//         //     for (int x = 0; x < mundo.width; x++) {
//         //         if (mundo.grid[y][x] == 0){
//         //             double sonarProb = findProbOfSonar(y,x,sonars);
//         //             normalization += sonarProb * probs[y][x];
//         //             probs[y][x] = sonarProb * probs[y][x];
//         //         }
//         //     }
//         // }
//         // // Add in normalization constant
//         // for (int y = 0; y < mundo.height; y++) {
//         //     for (int x = 0; x < mundo.width; x++) {
//         //         probs[y][x] = (1/normalization) * probs[y][x];
//         //     }
//         // }

//         prettyPrint(probs, "Update Probabilities");

//         myMaps.updateProbs(probs); // call this function after updating your probabilities so that the
//                                    //  new probabilities will show up in the probability map on the GUI
//     }

//     // This is the function you'd need to write to make the robot move using your AI;
//     // You do NOT need to write this function for this lab; it can remain as is
//     int automaticAction() {

//         return STAY;  // default action for now
//     }

//     void doStuff() {

//         int action;

//         //valueIteration();  // TODO: function you will write in Part II of the lab
//         initializeProbabilities();  // Initializes the location (probability) map

//         while (true) {
//             try {
//                 if (isManual)
//                     action = getHumanAction();  // get the action selected by the user (from the keyboard)
//                 else
//                     action = automaticAction(); // TODO: get the action selected by your AI;
//                                                 // you'll need to write this function for part III

//                 sout.println(action); // send the action to the Server

//                 // get sonar readings after the robot moves
//                 String sonars = sin.readLine();
//                 //System.out.println("Sonars: " + sonars);

//                 updateProbabilities(action, sonars); // TODO: this function should update the probabilities of where the AI thinks it is

//                 if (sonars.length() > 4) {  // check to see if the robot has reached its goal or fallen down stairs
//                     if (sonars.charAt(4) == 'w') {
//                         System.out.println("I won!");
//                         myMaps.setWin();
//                         break;
//                     }
//                     else if (sonars.charAt(4) == 'l') {
//                         System.out.println("I lost!");
//                         myMaps.setLoss();
//                         break;
//                     }
//                 }
//                 else {
//                     // here, you'll want to update the position probabilities
//                     // since you know that the result of the move as that the robot
//                     // was not at the goal or in a stairwell
//                 }
//                 Thread.sleep(decisionDelay);  // delay that is useful to see what is happening when the AI selects actions
//                                               // decisionDelay is specified by the send command-line argument, which is given in milliseconds
//             }
//             catch (IOException e) {
//                 System.out.println(e);
//             }
//             catch(InterruptedException ex) {
//                 Thread.currentThread().interrupt();
//             }
//         }
//     }


//     public void prettyPrint(double[][] matrix, String functionName) {
//         System.out.println(functionName);
//         for (int i = 0; i < matrix.length; i++) {
//             for (int j = 0; j < matrix.length; j++) {
//                 System.out.printf("%.5f ",matrix[i][j]);
//             }
//             System.out.println();
//         }
//     }
//     // java theRobot [manual/automatic] [delay]
//     public static void main(String[] args) {
//         theRobot robot = new theRobot(args[0], Integer.parseInt(args[1]));  // starts up the robot
//     }
// }
