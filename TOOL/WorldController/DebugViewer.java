package TOOL.WorldController;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.GridLayout;
import java.util.HashMap;
import java.text.DecimalFormat;

public class DebugViewer extends JFrame {

    public static final int BOX_HEIGHT = 700;
    public static final int BOX_WIDTH = 300;
    public static final int LOC_PANEL_ENTRY_WIDTH = 400;
    public static final int LOC_PANEL_ENTRY_HEIGHT = 25;
    public static final int LOC_PANEL_WIDTH = BOX_WIDTH;
    public static final int LOC_PANEL_HEIGHT = BOX_HEIGHT/2;

    // string constants
    public static final String FRAME_STRING = "Frame:";
    public static final String OUT_OF_STRING = "of";
    public static final String
        KNOWN_POSITIONS_STRING = "-----KNOWN POSITIONS-----";
    public static final String
        MY_ESTIMATES_STRING = "-----MY LOC ESTIMATES-----";
    public static final String
        BALL_ESTIMATES_STRING = "-----BALL ESTIMATES-----";
    public static final String LANDMARKS_STRING = "-----LANDMARKS-----";
    public static final String MY_XYH_STRING = "X,Y,H:";
    public static final String UNCERT_XYH_STRING = "Uncert X,Y,H:";
    public static final String BALL_X_STRING = "Ball X:";
    public static final String BALL_Y_STRING = "Ball Y:";
    public static final String BALL_UNCERT_X_STRING = "Uncert X:";
    public static final String BALL_UNCERT_Y_STRING = "Y:";
    public static final String BALL_VEL_X_STRING = "Velocity X:";
    public static final String BALL_VEL_Y_STRING = "Y:";
    public static final String BALL_VEL_ABS_STRING = "Abs Velocity:";
    public static final String BALL_VEL_UNCERT_X_STRING = "Vel. Uncert X: ";
    public static final String BALL_VEL_UNCERT_Y_STRING = "Y: ";
    public static final String BALL_STRING = "Ball Dist/Bearing:";
    public static final String ODO_XYH_CHANGE_STRING = "Odo F,L,R:";
    public static final String ODO_X_CHANGE_STRING = "Odo F:";
    public static final String ODO_Y_CHANGE_STRING = "Odo L:";
    public static final String ODO_H_CHANGE_STRING = "Odo R:";
    public static final String
        LANDMARK_STUFF_STRING = "ID  :  Dist  /  Bearing";

    // landmark string array
    public final static String[] LANDMARKS = {"My Goal Left Post",
                                              "My Goal Right Post",
                                              "Opp Goal Left Post",
                                              "Opp Goal Right Post",
                                              "Left Beacon",
                                              "Right Beacon",
                                              "Ball",
                                              "My Corner Left L",
                                              "My Corner Right L",
                                              "My Goal Left T",
                                              "My Goal Right T",
                                              "My Goal Left L",
                                              "My Goal Right L",
                                              "Center Left T",
                                              "Center Right T",
                                              "Opp Corner Left L",
                                              "Opp Corner Right L",
                                              "Opp Goal Left T",
                                              "Opp Goal Right T",
                                              "Opp Goal Left L",
                                              "Opp Goal Right L"};

    // Here we assume right is yellow, left is blue
    // Left of a goal is left side looking through the back of the goal
    public final static String[] LANDMARK_TAG = {"Blue Goal Left Post",
                                                 "Blue Goal Right Post",
                                                 "Yellow Goal Left Post",
                                                 "Yellow Goal Right Post",
                                                 "Blue Corner Top L",
                                                 "Blue Corner Bottom L",
                                                 "Blue Goal Left T",
                                                 "Blue Goal Right T",
                                                 "Blue Goal Left L",
                                                 "Blue Goal Right L",
                                                 "Center Bottom T",
                                                 "Center Top T",
                                                 "Yellow Corner Bottom L",
                                                 "Yellow Corner Top L",
                                                 "Yellow Goal Left T",
                                                 "Yellow Goal Right T",
                                                 "Yellow Goal Left L",
                                                 "Yellow Goal Right L",
                                                 "Ball",
                                                 "Ambigious L",
                                                 "Ambigious T"};
    // Object IDs
    public static final int L_INNER_CORNER = 0;
    public static final int L_OUTER_CORNER = 1;
    public static final int T_CORNER = 2;
    public static final int CENTER_CIRCLE = 3;

    // FUZZY/CLEAR CORNER IDS start at = 4
    public static final int BLUE_GOAL_T = 4;
    public static final int YELLOW_GOAL_T = 5;
    public static final int BLUE_GOAL_RIGHT_L_OR_YELLOW_GOAL_LEFT_L = 6;
    public static final int BLUE_GOAL_LEFT_L_OR_YELLOW_GOAL_RIGHT_L = 7;
    public static final int BLUE_CORNER_TOP_L_OR_YELLOW_CORNER_BOTTOM_L = 8;
    public static final int BLUE_CORNER_BOTTOM_L_OR_YELLOW_CORNER_TOP_L = 9;
    public static final int CORNER_INNER_L = 10;
    public static final int GOAL_BOX_INNER_L = 11;

    // FUZZY/CLEAR CORNER IDS start at = 12
    public static final int BLUE_GOAL_OUTER_L = 12;
    public static final int YELLOW_GOAL_OUTER_L = 13;
    public static final int CENTER_T = 14;

    // SPECIFIC CORNER IDS start at = 15
    public static final int BLUE_CORNER_TOP_L = 15;
    public static final int BLUE_CORNER_BOTTOM_L = 16;
    public static final int BLUE_GOAL_LEFT_T = 17;
    public static final int BLUE_GOAL_RIGHT_T = 18;
    public static final int BLUE_GOAL_LEFT_L = 19;
    public static final int BLUE_GOAL_RIGHT_L = 20;
    public static final int CENTER_BOTTOM_T = 21;
    public static final int CENTER_TOP_T = 22;
    public static final int YELLOW_CORNER_BOTTOM_L = 23;
    public static final int YELLOW_CORNER_TOP_L = 24;
    public static final int YELLOW_GOAL_LEFT_T = 25;
    public static final int YELLOW_GOAL_RIGHT_T = 26;
    public static final int YELLOW_GOAL_LEFT_L = 27;
    public static final int YELLOW_GOAL_RIGHT_L = 28;
    public static final int CORNER_NO_IDEA_ID = 29;
    public static final int TOP_CC = 30;
    public static final int BOTTOM_CC = 31;

    // Field Objects
    public static final int BLUE_GOAL_TOP_POST = 40;
    public static final int BLUE_GOAL_BOTTOM_POST = 41;
    public static final int YELLOW_GOAL_BOTTOM_POST = 42;
    public static final int YELLOW_GOAL_TOP_POST = 43;
    public static final int BLUE_GOAL_POST = 44;
    public static final int YELLOW_GOAL_POST = 45;

    // Visual Cross
    public static final int BLUE_GOAL_CROSS = 80;
    public static final int YELLOW_GOAL_CROSS = 81;
    public static final int ABSTRACT_CROSS = 82;

    public int[] LANDMARK_X;
    public int[] LANDMARK_Y;

    public final static int BALL_ID = 99;

    // takes a Point and maps it to a string
    public HashMap <Point2D.Double,String> cornerMap;
    public HashMap <Integer, Point2D.Double> cornerIDMap;

    // Takes a numeric ID and returns an X,Y pair
    public HashMap <Integer, Point2D.Double> objectIDMap;
    public HashMap <Integer, String> objectIDStringMap;

    // frame labels
    private JLabel frameLabel1, frameLabel2;
    public JLabel frameNumber, frameTotal;
    private JPanel framePanel;

    // master label panel declarations
    private JLabel knownPositionLabel;
    private JPanel knownPositionPanel;
    private JLabel myEstimatesLabel;
    private JPanel myEstimatesPanel;
    private JLabel ballEstimatesLabel;
    private JPanel ballEstimatesPanel;
    private JLabel landmarksLabel;
    private JPanel landmarksPanel;
    private JLabel landmarkStuffLabel;
    private JPanel landmarkStuffPanel;

    // known values
    private JLabel knownXYHLabel;
    public JLabel knownX;
    public JLabel knownY;
    public JLabel knownH;
    private JPanel knownXYHPanel;
    public JLabel knownBallXLabel;
    public JLabel knownBallYLabel;
    public JLabel knownBallX;
    public JLabel knownBallY;
    private JPanel knownBallXYPanel;

    // my values
    private JLabel myXYHLabel;
    public JLabel myX;
    public JLabel myY;
    public JLabel myH;
    private JPanel myXYHPanel;

    // my uncertainty
    private JLabel myUncertXYHLabel;
    public JLabel myUncertX;
    public JLabel myUncertY;
    public JLabel myUncertH;
    private JPanel myUncertXYHPanel;

    // odometry values
    private JLabel odoXYHLabel;
    public JLabel odoX;
    public JLabel odoY;
    public JLabel odoH;
    private JPanel odoXYHPanel;

    // ball x/y estimates
    private JLabel ballXLabel;
    public JLabel ballX;
    private JLabel ballYLabel;
    public JLabel ballY;
    private JPanel ballXYPanel;

    // ball uncertainty estimates
    private JLabel ballUncertXLabel;
    public JLabel ballUncertX;
    private JLabel ballUncertYLabel;
    public JLabel ballUncertY;
    private JPanel ballUncertPanel;

    // ball velocity estimates
    private JLabel ballVelXLabel;
    public JLabel ballVelX;
    private JLabel ballVelYLabel;
    public JLabel ballVelY;
    private JPanel ballVelPanel;

    private JLabel ballVelAbsLabel;
    public JLabel ballVelAbs;
    private JPanel ballVelAbsPanel;

    // ball velocity estimates
    private JLabel ballVelUncertXLabel;
    public JLabel ballVelUncertX;
    private JLabel ballVelUncertYLabel;
    public JLabel ballVelUncertY;
    private JPanel ballVelUncertPanel;

    private WorldController wc;
    private Container window;
    private Component bottom_space;
    private JPanel locPanel;
    private Component landmark_components[];
    private int num_landmarks;

    // Format output for better display readability
    private DecimalFormat niceOutput;

    public DebugViewer(WorldController _wc, int x, int y) {
        super("Debug Viewer");

        // local copy of the WorldController instance
        wc = _wc;

        JFrame.setDefaultLookAndFeelDecorated(true);

        setLayout(new BoxLayout(this.getContentPane(), BoxLayout.PAGE_AXIS));

        window = getContentPane();
        num_landmarks = 0;
        landmark_components = new Component[50];

        cornerIDMap = new HashMap<Integer, Point2D.Double>();
        cornerMap = new HashMap<Point2D.Double,String>();
        objectIDMap = new HashMap<Integer, Point2D.Double>();
        objectIDStringMap = new HashMap<Integer, String>();

        // frame number

        frameLabel1 = new JLabel(FRAME_STRING, JLabel.CENTER);
        frameLabel2 = new JLabel(OUT_OF_STRING, JLabel.CENTER);
        frameNumber = new JLabel("1", JLabel.CENTER);
        frameTotal = new JLabel("1", JLabel.CENTER);
       	framePanel = new JPanel();
        framePanel.add(frameLabel1);
        framePanel.add(frameNumber);
        framePanel.add(frameLabel2);
        framePanel.add(frameTotal);

        // known stuff
        knownPositionLabel = new JLabel(KNOWN_POSITIONS_STRING, JLabel.CENTER);
        knownPositionPanel = new JPanel();
        knownPositionPanel.add(knownPositionLabel);
        knownPositionPanel.
            setPreferredSize(new Dimension(LOC_PANEL_ENTRY_WIDTH,
                                           LOC_PANEL_ENTRY_HEIGHT));

        // estimates label
        myEstimatesLabel = new JLabel(MY_ESTIMATES_STRING, JLabel.CENTER);
        myEstimatesPanel = new JPanel();
        myEstimatesPanel.add(myEstimatesLabel);
        myEstimatesPanel.
            setPreferredSize(new Dimension(LOC_PANEL_ENTRY_WIDTH,
                                           LOC_PANEL_ENTRY_HEIGHT));

        // ball estimates label
        ballEstimatesLabel = new JLabel(BALL_ESTIMATES_STRING, JLabel.CENTER);
        ballEstimatesPanel = new JPanel();
        ballEstimatesPanel.add(ballEstimatesLabel);
        ballEstimatesPanel.
            setPreferredSize(new Dimension(LOC_PANEL_ENTRY_WIDTH,
                                           LOC_PANEL_ENTRY_HEIGHT));

        // landmarks label
        landmarksLabel = new JLabel(LANDMARKS_STRING, JLabel.CENTER);
        landmarksPanel = new JPanel();
        landmarksPanel.add(landmarksLabel);
        landmarksPanel.
            setPreferredSize(new Dimension(LOC_PANEL_ENTRY_WIDTH,
                                           LOC_PANEL_ENTRY_HEIGHT));

        landmarkStuffLabel = new JLabel(LANDMARK_STUFF_STRING, JLabel.CENTER);
        landmarkStuffPanel = new JPanel();
        landmarkStuffPanel.add(landmarkStuffLabel);
        landmarkStuffPanel.
            setPreferredSize(new Dimension(LOC_PANEL_ENTRY_WIDTH,
                                           LOC_PANEL_ENTRY_HEIGHT));


        // my estimates
        myXYHLabel = new JLabel(MY_XYH_STRING, JLabel.CENTER);
        myX = new JLabel("0", JLabel.CENTER);
        myY = new JLabel("0", JLabel.CENTER);
        myH = new JLabel("0", JLabel.CENTER);
        myXYHPanel = new JPanel();
        myXYHPanel.add(myXYHLabel);
        myXYHPanel.add(myX);
        myXYHPanel.add(myY);
        myXYHPanel.add(myH);
        myXYHPanel.
            setPreferredSize(new Dimension(LOC_PANEL_ENTRY_WIDTH,
                                           LOC_PANEL_ENTRY_HEIGHT));

        // my uncertainty estimates
        myUncertXYHLabel = new JLabel(UNCERT_XYH_STRING, JLabel.CENTER);
        myUncertX = new JLabel("0", JLabel.CENTER);
        myUncertY = new JLabel("0", JLabel.CENTER);
        myUncertH = new JLabel("0", JLabel.CENTER);
        myUncertXYHPanel = new JPanel();
        myUncertXYHPanel.add(myUncertXYHLabel);
        myUncertXYHPanel.add(myUncertX);
        myUncertXYHPanel.add(myUncertY);
        myUncertXYHPanel.add(myUncertH);
        myUncertXYHPanel.
            setPreferredSize(new Dimension(LOC_PANEL_ENTRY_WIDTH,
                                           LOC_PANEL_ENTRY_HEIGHT));

        // odometry estimate initializations
        odoXYHLabel = new JLabel(ODO_XYH_CHANGE_STRING, JLabel.CENTER);
        odoX = new JLabel("0", JLabel.CENTER);
        odoY = new JLabel("0", JLabel.CENTER);
        odoH = new JLabel("0", JLabel.CENTER);
        odoXYHPanel = new JPanel();
        odoXYHPanel.add(odoXYHLabel);
        odoXYHPanel.add(odoX);
        odoXYHPanel.add(odoY);
        odoXYHPanel.add(odoH);

        ballXLabel = new JLabel(BALL_X_STRING, JLabel.CENTER);
        ballX = new JLabel("0", JLabel.CENTER);
        ballYLabel = new JLabel(BALL_Y_STRING, JLabel.CENTER);
        ballY = new JLabel("0", JLabel.CENTER);
        ballXYPanel = new JPanel();
        ballXYPanel.add(ballXLabel);
        ballXYPanel.add(ballX);
        ballXYPanel.add(ballYLabel);
        ballXYPanel.add(ballY);
        ballXYPanel.
            setPreferredSize(new Dimension(LOC_PANEL_ENTRY_WIDTH,
                                           LOC_PANEL_ENTRY_HEIGHT));

        ballUncertXLabel = new JLabel(BALL_UNCERT_X_STRING, JLabel.CENTER);
        ballUncertX = new JLabel("0", JLabel.CENTER);
        ballUncertYLabel = new JLabel(BALL_UNCERT_Y_STRING, JLabel.CENTER);
        ballUncertY = new JLabel("0", JLabel.CENTER);
        ballUncertPanel = new JPanel();
        ballUncertPanel.add(ballUncertXLabel);
        ballUncertPanel.add(ballUncertX);
        ballUncertPanel.add(ballUncertYLabel);
        ballUncertPanel.add(ballUncertY);
        ballUncertPanel.
            setPreferredSize(new Dimension(LOC_PANEL_ENTRY_WIDTH,
                                           LOC_PANEL_ENTRY_HEIGHT));

        ballVelXLabel = new JLabel(BALL_VEL_X_STRING, JLabel.CENTER);
        ballVelX = new JLabel("0", JLabel.CENTER);
        ballVelYLabel = new JLabel(BALL_VEL_Y_STRING, JLabel.CENTER);
        ballVelY = new JLabel("0", JLabel.CENTER);
        ballVelPanel = new JPanel();
        ballVelPanel.add(ballVelXLabel);
        ballVelPanel.add(ballVelX);
        ballVelPanel.add(ballVelYLabel);
        ballVelPanel.add(ballVelY);
        ballVelPanel.
            setPreferredSize(new Dimension(LOC_PANEL_ENTRY_WIDTH,
                                           LOC_PANEL_ENTRY_HEIGHT));

        // Add something to show absolute velocity
        ballVelAbsLabel = new JLabel(BALL_VEL_ABS_STRING, JLabel.CENTER);
        ballVelAbs = new JLabel("0", JLabel.CENTER);
        ballVelAbsPanel = new JPanel();
        ballVelAbsPanel.add(ballVelAbsLabel);
        ballVelAbsPanel.add(ballVelAbs);
        ballVelPanel.
            setPreferredSize(new Dimension(LOC_PANEL_ENTRY_WIDTH,
                                           LOC_PANEL_ENTRY_HEIGHT));

        ballVelUncertXLabel = new JLabel(BALL_VEL_UNCERT_X_STRING,
                                         JLabel.CENTER);
        ballVelUncertX = new JLabel("0", JLabel.CENTER);
        ballVelUncertYLabel = new JLabel(BALL_VEL_UNCERT_Y_STRING,
                                         JLabel.CENTER);
        ballVelUncertY = new JLabel("0", JLabel.CENTER);
        ballVelUncertPanel = new JPanel();
        ballVelUncertPanel.add(ballVelUncertXLabel);
        ballVelUncertPanel.add(ballVelUncertX);
        ballVelUncertPanel.add(ballVelUncertYLabel);
        ballVelUncertPanel.add(ballVelUncertY);
        ballVelUncertPanel.
            setPreferredSize(new Dimension(LOC_PANEL_ENTRY_WIDTH,
                                           LOC_PANEL_ENTRY_HEIGHT));


        // known positions
        knownXYHLabel = new JLabel(MY_XYH_STRING, JLabel.CENTER);
        knownX = new JLabel("0", JLabel.CENTER);
        knownY = new JLabel("0", JLabel.CENTER);
        knownH = new JLabel("0", JLabel.CENTER);
        knownXYHPanel = new JPanel();
        knownXYHPanel.add(knownXYHLabel);
        knownXYHPanel.add(knownX);
        knownXYHPanel.add(knownY);
        knownXYHPanel.add(knownH);
        knownXYHPanel.
            setPreferredSize(new Dimension(LOC_PANEL_ENTRY_WIDTH,
                                           LOC_PANEL_ENTRY_HEIGHT));
        knownBallX = new JLabel("0", JLabel.CENTER);
        knownBallY = new JLabel("0", JLabel.CENTER);
        knownBallXLabel = new JLabel(BALL_X_STRING, JLabel.CENTER);
        knownBallYLabel = new JLabel(BALL_Y_STRING, JLabel.CENTER);
        knownBallXYPanel = new JPanel();
        knownBallXYPanel.add(knownBallXLabel);
        knownBallXYPanel.add(knownBallX);
        knownBallXYPanel.add(knownBallYLabel);
        knownBallXYPanel.add(knownBallY);
        knownBallXYPanel.
            setPreferredSize(new Dimension(LOC_PANEL_ENTRY_WIDTH,
                                           LOC_PANEL_ENTRY_HEIGHT));

        // CALIBRATE BUTTONS LAYOUT

        locPanel = new JPanel();

        locPanel.add(framePanel);
        locPanel.add(knownPositionPanel);
        locPanel.add(knownXYHPanel);
        locPanel.add(knownBallXYPanel);
        locPanel.add(myEstimatesPanel);
        locPanel.add(myXYHPanel);
        locPanel.add(myUncertXYHPanel);
        locPanel.add(odoXYHPanel);
        locPanel.add(ballEstimatesPanel);
        locPanel.add(ballXYPanel);
        locPanel.add(ballUncertPanel);
        locPanel.add(ballVelPanel);
        locPanel.add(ballVelAbsPanel);
        locPanel.add(ballVelUncertPanel);
        locPanel.add(landmarksPanel);
        locPanel.add(landmarkStuffPanel);

        locPanel.setPreferredSize(new Dimension(LOC_PANEL_WIDTH,
                                                LOC_PANEL_HEIGHT));
        window.add(locPanel);

        setLocation(x,y);

        pack();
        setVisible(false);

        populateCornerMap();
        populateObjectIDMap();
        LANDMARK_X = new int[18];
        LANDMARK_X[0] = (int) wc.the_field.LANDMARK_LEFT_GOAL_TOP_POST_X;
        LANDMARK_X[1] = (int) wc.the_field.LANDMARK_LEFT_GOAL_BOTTOM_POST_X;
        LANDMARK_X[2] = (int) wc.the_field.LANDMARK_RIGHT_GOAL_TOP_POST_X;
        LANDMARK_X[3] = (int) wc.the_field.LANDMARK_RIGHT_GOAL_BOTTOM_POST_X;
        LANDMARK_X[4] = (int) wc.the_field.FIELD_WHITE_LEFT_SIDELINE_X;
        LANDMARK_X[5] = (int) wc.the_field.FIELD_WHITE_RIGHT_SIDELINE_X;

        LANDMARK_X[6] = (int) wc.the_field.FIELD_WHITE_LEFT_SIDELINE_X;
        LANDMARK_X[7] = (int) wc.the_field.FIELD_WHITE_LEFT_SIDELINE_X;
        LANDMARK_X[8] = (int) wc.the_field.FIELD_WHITE_LEFT_SIDELINE_X;
        LANDMARK_X[9] = (int) wc.the_field.FIELD_WHITE_LEFT_SIDELINE_X;

        LANDMARK_X[10] = (int) wc.the_field.FIELD_WHITE_LEFT_SIDELINE_X;
        LANDMARK_X[11] = (int) wc.the_field.FIELD_WHITE_RIGHT_SIDELINE_X;
        LANDMARK_X[12] = (int) wc.the_field.FIELD_WHITE_RIGHT_SIDELINE_X;
        LANDMARK_X[13] = (int) wc.the_field.FIELD_WHITE_LEFT_SIDELINE_X;

        LANDMARK_X[14] = (int) wc.the_field.FIELD_WHITE_RIGHT_SIDELINE_X;
        LANDMARK_X[15] = (int) wc.the_field.FIELD_WHITE_RIGHT_SIDELINE_X;
        LANDMARK_X[16] = (int) wc.the_field.FIELD_WHITE_RIGHT_SIDELINE_X;
        LANDMARK_X[17] = (int) wc.the_field.FIELD_WHITE_RIGHT_SIDELINE_X;

        LANDMARK_Y = new int[18];
        LANDMARK_Y[0] = (int) wc.the_field.LANDMARK_LEFT_GOAL_TOP_POST_Y;
        LANDMARK_Y[1] = (int) wc.the_field.LANDMARK_LEFT_GOAL_BOTTOM_POST_Y;
        LANDMARK_Y[2] = (int) wc.the_field.LANDMARK_RIGHT_GOAL_TOP_POST_Y;
        LANDMARK_Y[3] = (int) wc.the_field.LANDMARK_RIGHT_GOAL_BOTTOM_POST_Y;
        LANDMARK_Y[4] = (int) wc.the_field.FIELD_WHITE_BOTTOM_SIDELINE_Y;
        LANDMARK_Y[5] = (int) wc.the_field.FIELD_WHITE_BOTTOM_SIDELINE_Y;

        LANDMARK_Y[6] = (int) wc.the_field.FIELD_WHITE_BOTTOM_SIDELINE_Y;
        LANDMARK_Y[7] = (int) wc.the_field.FIELD_WHITE_BOTTOM_SIDELINE_Y;
        LANDMARK_Y[8] = (int) (wc.the_field.FIELD_WHITE_BOTTOM_SIDELINE_Y +
                               wc.the_field.GOAL_BOX_DEPTH);
        LANDMARK_Y[9] = (int) (wc.the_field.FIELD_WHITE_BOTTOM_SIDELINE_Y +
                               wc.the_field.GOAL_BOX_DEPTH);

        LANDMARK_Y[10] = (int) wc.the_field.MIDFIELD_Y;
        LANDMARK_Y[11] = (int) wc.the_field.MIDFIELD_Y;
        LANDMARK_Y[12] = (int) wc.the_field.FIELD_WHITE_TOP_SIDELINE_Y;
        LANDMARK_Y[13] = (int) wc.the_field.FIELD_WHITE_TOP_SIDELINE_Y;
        LANDMARK_Y[14] = (int) wc.the_field.FIELD_WHITE_TOP_SIDELINE_Y;
        LANDMARK_Y[15] = (int) wc.the_field.FIELD_WHITE_TOP_SIDELINE_Y;
        LANDMARK_Y[16] = (int) (wc.the_field.FIELD_WHITE_TOP_SIDELINE_Y -
                                wc.the_field.GOAL_BOX_DEPTH);
        LANDMARK_Y[17] = (int) (wc.the_field.FIELD_WHITE_TOP_SIDELINE_Y -
                                wc.the_field.GOAL_BOX_DEPTH);

        niceOutput = new DecimalFormat("####.##");
    }

    private void populateCornerMap() {
        // blue corner left l
        Point2D.Double blueCornerLeftL =
            new Point2D.Double(wc.the_field.FIELD_WHITE_LEFT_SIDELINE_X,
                               wc.the_field.FIELD_WHITE_BOTTOM_SIDELINE_Y);
        cornerMap.put(blueCornerLeftL, "Blue Corner Left L");
        //cornerIDMap
    }

    private void populateObjectIDMap()
    {
        // Abstract Corners
        Point2D.Double emptyPoint = new Point2D.Double(0.0,0.0);
        objectIDMap.put(new Integer(0), emptyPoint);
        objectIDStringMap.put(new Integer(0), "L Inner Corner");
        objectIDMap.put(new Integer(1), emptyPoint);
        objectIDStringMap.put(new Integer(1), "L Outer Corner");
        objectIDMap.put(new Integer(2), emptyPoint);
        objectIDStringMap.put(new Integer(2), "T Corner");
        objectIDMap.put(new Integer(3), emptyPoint);
        objectIDStringMap.put(new Integer(3), "Center Circle");
        objectIDMap.put(new Integer(4), emptyPoint);
        objectIDStringMap.put(new Integer(4), "Blue Goal T");
        objectIDMap.put(new Integer(5), emptyPoint);
        objectIDStringMap.put(new Integer(5), "Yellow Goal T");
        objectIDMap.put(new Integer(6), emptyPoint);
        objectIDStringMap.put(new Integer(6),
                              "Blue Goal Right L or Yellow Goal Left L");
        objectIDMap.put(new Integer(7), emptyPoint);
        objectIDStringMap.put(new Integer(7),
                              "BLUE GOAL LEFT L OR YELLOW GOAL RIGHT L");
        objectIDMap.put(new Integer(8), emptyPoint);
        objectIDStringMap.put(new Integer(8),
                              "BLUE CORNER LEFT L OR YELLOW CORNER LEFT L");
        objectIDMap.put(new Integer(9), emptyPoint);
        objectIDStringMap.put(new Integer(9),
                              "BLUE CORNER RIGHT L OR YELLOW CORNER RIGHT L");
        objectIDMap.put(new Integer(10), emptyPoint);
        objectIDStringMap.put(new Integer(10), "CORNER INNER L");
        objectIDMap.put(new Integer(11), emptyPoint);
        objectIDStringMap.put(new Integer(11), "GOAL BOX INNER L");
        objectIDMap.put(new Integer(12), emptyPoint);
        objectIDStringMap.put(new Integer(12), "BLUE GOAL OUTER L");
        objectIDMap.put(new Integer(13), emptyPoint);
        objectIDStringMap.put(new Integer(13), "YELLOW GOAL OUTER L");
        objectIDMap.put(new Integer(14), emptyPoint);
        objectIDStringMap.put(new Integer(14), "CENTER T");

        // Specific Corners
        Point2D.Double blueCornerTopL =
            new Point2D.Double(wc.the_field.FIELD_WHITE_LEFT_SIDELINE_X,
                               wc.the_field.FIELD_WHITE_TOP_SIDELINE_Y);
        objectIDMap.put(new Integer(15), blueCornerTopL);
        objectIDStringMap.put(new Integer(15), "Blue Corner Top L");
        Point2D.Double blueCornerBottomL =
            new Point2D.Double(wc.the_field.FIELD_WHITE_LEFT_SIDELINE_X,
                               wc.the_field.FIELD_WHITE_BOTTOM_SIDELINE_Y);
        objectIDMap.put(new Integer(16), blueCornerBottomL);
        objectIDStringMap.put(new Integer(16), "Blue Corner Bottom L");
        Point2D.Double blueGoalLeftT =
            new Point2D.Double(wc.the_field.FIELD_WHITE_LEFT_SIDELINE_X,
                               wc.the_field.CENTER_FIELD_Y +
                               wc.the_field.GOAL_BOX_WIDTH / 2.0);
        objectIDMap.put(new Integer(17), blueGoalLeftT);
        objectIDStringMap.put(new Integer(17), "Blue Goal Left T");
        Point2D.Double blueGoalRightT =
            new Point2D.Double(wc.the_field.FIELD_WHITE_LEFT_SIDELINE_X,
                               wc.the_field.CENTER_FIELD_Y -
                               wc.the_field.GOAL_BOX_WIDTH / 2.0);
        objectIDMap.put(new Integer(18), blueGoalRightT);
        objectIDStringMap.put(new Integer(18), "Blue Goal Right T");
        Point2D.Double blueGoalLeftL =
            new Point2D.Double(wc.the_field.FIELD_WHITE_LEFT_SIDELINE_X +
                               wc.the_field.GOAL_BOX_DEPTH,
                               wc.the_field.CENTER_FIELD_Y +
                               wc.the_field.GOAL_BOX_WIDTH / 2.0);
        objectIDMap.put(new Integer(19), blueGoalLeftL);
        objectIDStringMap.put(new Integer(19), "Blue Goal Left L");
        Point2D.Double blueGoalRightL =
            new Point2D.Double(wc.the_field.FIELD_WHITE_LEFT_SIDELINE_X +
                               wc.the_field.GOAL_BOX_DEPTH,
                               wc.the_field.CENTER_FIELD_Y -
                               wc.the_field.GOAL_BOX_WIDTH / 2.0);
        objectIDMap.put(new Integer(20), blueGoalRightL);
        objectIDStringMap.put(new Integer(20), "Blue Goal Right L");
        Point2D.Double centerBottomT =
            new Point2D.Double(wc.the_field.CENTER_FIELD_X,
                               wc.the_field.FIELD_WHITE_BOTTOM_SIDELINE_Y);
        objectIDMap.put(new Integer(21), centerBottomT);
        objectIDStringMap.put(new Integer(21), "Center Bottom T");
        Point2D.Double centerTopT =
            new Point2D.Double(wc.the_field.CENTER_FIELD_X,
                               wc.the_field.FIELD_WHITE_TOP_SIDELINE_Y);
        objectIDMap.put(new Integer(22), centerTopT);
        objectIDStringMap.put(new Integer(22), "Center Top T");
        Point2D.Double yellowCornerBottomL =
             new Point2D.Double(wc.the_field.FIELD_WHITE_RIGHT_SIDELINE_X,
                                wc.the_field.FIELD_WHITE_BOTTOM_SIDELINE_Y);
        objectIDMap.put(new Integer(23), yellowCornerBottomL);
        objectIDStringMap.put(new Integer(23), "Yellow Corner Bottom L");
        Point2D.Double yellowCornerTopL =
             new Point2D.Double(wc.the_field.FIELD_WHITE_RIGHT_SIDELINE_X,
                                wc.the_field.FIELD_WHITE_TOP_SIDELINE_Y);
        objectIDMap.put(new Integer(24), yellowCornerTopL);
        objectIDStringMap.put(new Integer(24), "Yellow Corner Top L");
        Point2D.Double yellowGoalLeftT =
            new Point2D.Double(wc.the_field.FIELD_WHITE_RIGHT_SIDELINE_X,
                               wc.the_field.CENTER_FIELD_Y -
                               wc.the_field.GOAL_BOX_WIDTH / 2.0);
        objectIDMap.put(new Integer(25), yellowGoalLeftT);
        objectIDStringMap.put(new Integer(25), "Yellow Goal Left T");
        Point2D.Double yellowGoalRightT =
            new Point2D.Double(wc.the_field.FIELD_WHITE_RIGHT_SIDELINE_X,
                               wc.the_field.CENTER_FIELD_Y +
                               wc.the_field.GOAL_BOX_WIDTH / 2.0);
        objectIDMap.put(new Integer(26), yellowGoalRightT);
        objectIDStringMap.put(new Integer(26), "Yellow Goal Right T");
        Point2D.Double yellowGoalLeftL =
            new Point2D.Double(wc.the_field.FIELD_WHITE_RIGHT_SIDELINE_X -
                               wc.the_field.GOAL_BOX_DEPTH,
                               wc.the_field.CENTER_FIELD_Y -
                               wc.the_field.GOAL_BOX_WIDTH / 2.0);
        objectIDMap.put(new Integer(27), yellowGoalLeftL);
        objectIDStringMap.put(new Integer(27), "Yellow Goal Left L");
        Point2D.Double yellowGoalRightL =
            new Point2D.Double(wc.the_field.FIELD_WHITE_RIGHT_SIDELINE_X -
                               wc.the_field.GOAL_BOX_DEPTH,
                               wc.the_field.CENTER_FIELD_Y +
                               wc.the_field.GOAL_BOX_WIDTH / 2.0);
        objectIDMap.put(new Integer(28), yellowGoalRightL);
        objectIDStringMap.put(new Integer(28), "Yellow Goal Right L");

        objectIDMap.put(new Integer(29), emptyPoint);
        objectIDStringMap.put(new Integer(29), "NO IDEA CORNER");

        Point2D.Double topCC =
            new Point2D.Double(wc.the_field.CENTER_FIELD_X,
                               wc.the_field.CENTER_FIELD_Y +
                               wc.the_field.CENTER_CIRCLE_RADIUS);
        objectIDMap.put(new Integer(30), topCC);
        objectIDStringMap.put(new Integer(30), "Yellow Goal Right L");

        Point2D.Double bottomCC =
            new Point2D.Double(wc.the_field.CENTER_FIELD_X,
                               wc.the_field.CENTER_FIELD_Y -
                               wc.the_field.CENTER_CIRCLE_RADIUS);
        objectIDMap.put(new Integer(31), bottomCC);
        objectIDStringMap.put(new Integer(31), "Yellow Goal Right L");

        // Field objects
        Point2D.Double bglpPT =
            new Point2D.Double(wc.the_field.LANDMARK_LEFT_GOAL_TOP_POST_X,
                               wc.the_field.LANDMARK_LEFT_GOAL_TOP_POST_Y);
        objectIDMap.put(new Integer(40), bglpPT);
        objectIDStringMap.put(new Integer(40), "Blue goal left post");
        Point2D.Double bgrpPT =
            new Point2D.Double(wc.the_field.LANDMARK_LEFT_GOAL_BOTTOM_POST_X,
                               wc.the_field.LANDMARK_LEFT_GOAL_BOTTOM_POST_Y);
        objectIDMap.put(new Integer(41), bgrpPT);
        objectIDStringMap.put(new Integer(41), "Blue goal right post");
        Point2D.Double ygrpPT =
            new Point2D.Double(wc.the_field.LANDMARK_RIGHT_GOAL_BOTTOM_POST_X,
                               wc.the_field.LANDMARK_RIGHT_GOAL_BOTTOM_POST_Y);
        objectIDMap.put(new Integer(42), ygrpPT);
        objectIDStringMap.put(new Integer(42), "Yellow goal left post");
        Point2D.Double yglpPT =
            new Point2D.Double(wc.the_field.LANDMARK_RIGHT_GOAL_TOP_POST_X,
                               wc.the_field.LANDMARK_RIGHT_GOAL_TOP_POST_Y);
        objectIDMap.put(new Integer(43), yglpPT);
        objectIDStringMap.put(new Integer(43), "Yellow goal right post");
        // Ambiguous field objects
        objectIDMap.put(new Integer(44), emptyPoint);
        objectIDStringMap.put(new Integer(44), "Blue Goal Post");
        objectIDMap.put(new Integer(45), emptyPoint);
        objectIDStringMap.put(new Integer(45), "Yellow Goal Post");

        Point2D.Double bg_cross =
            new Point2D.Double(wc.the_field.LEFT_SIDELINE_X + 180.0f - 5.0f,
                               wc.the_field.CENTER_FIELD_Y);
        Point2D.Double yg_cross =
            new Point2D.Double(wc.the_field.RIGHT_SIDELINE_X - 180.0f - 5.0f,
                               wc.the_field.CENTER_FIELD_Y);


        objectIDMap.put(new Integer(80), bg_cross);
        objectIDStringMap.put(new Integer(80), "Blue Goal Cross");
        objectIDMap.put(new Integer(81), yg_cross);
        objectIDStringMap.put(new Integer(81), "Yellow Goal Cross");
        objectIDMap.put(new Integer(82), emptyPoint);
        objectIDStringMap.put(new Integer(82), "Abstract Cross");
    }

    public Dimension getMinimumSize()
    { return new Dimension(BOX_WIDTH,BOX_HEIGHT); }

    public Dimension getPreferredSize()
    { return getMinimumSize(); }

    public int getNumLandmarks() {
        return num_landmarks;
    }

    public void addLandmark(int id, float dist, float bearing) {
        if ( !objectIDMap.containsKey(new Integer(id)) &&
             !cornerIDMap.containsKey(new Integer(id)) &&
             id != BALL_ID) {
            System.out.println("DebugViewer.java sawLandmark(): " +
                               "Saw Non-Existant Landmark: " + id +
                               " at line " + frameNumber.getText());
            return;
        }
        //System.out.println("addLandmark id: " + id +
        //		   " dist: " + dist +
        //		   " bearing: " + bearing);

        // label/panel declarations
        JLabel id_label, dist_label, bearing_label;
        JLabel colon_label, slash_label;
        JPanel panel;

        if (id == BALL_ID) {
            id_label = new JLabel("Ball", JLabel.CENTER);
        } else if (objectIDMap.containsKey(new Integer(id))) {
            id_label = new JLabel(objectIDStringMap.get(new Integer(id)),
                                  JLabel.CENTER);
        } else {
            // get id string from LANDMARKS array
            id_label = new JLabel(LANDMARKS[id], JLabel.CENTER);
        }

        // convert dist/bearing to strings, add to labels
        dist_label = new JLabel(niceOutput.format(dist), JLabel.CENTER);
        bearing_label = new JLabel(niceOutput.format(bearing), JLabel.CENTER);
        // make a colon and slash as JLabels
        colon_label = new JLabel(":", JLabel.CENTER);
        slash_label = new JLabel("/", JLabel.CENTER);

        // create and add to panel
        panel = new JPanel();
        panel.add(id_label);
        panel.add(colon_label);
        panel.add(dist_label);
        panel.add(slash_label);
        panel.add(bearing_label);
        panel.setAlignmentY(Component.TOP_ALIGNMENT);

        // add panel to window
        landmark_components[num_landmarks] = locPanel.add(panel);
        //window.validate();

        // incremenet landmarks on window
        num_landmarks++;
    }

    // removes all landmarks from debug viewer
    public void removeLandmarks() {

        //window.invalidate();
        // remove landmarks
        for (int i = 0; i < num_landmarks; i++) {
            locPanel.remove(landmark_components[i]);
        }
        locPanel.updateUI();
        //window.validate();
        num_landmarks = 0;
    }

    public boolean isDistinctLandmarkID(int ID)
    {
        return ( ID >= 15 && ID <= 43 && ID != 29 && ID != 82);
    }
}
