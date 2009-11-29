
// This file is part of TOOL, a robotics interaction and development
// package created by the Northern Bites RoboCup team of Bowdoin College
// in Brunswick, Maine.
//
// TOOL is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// TOOL is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with TOOL.  If not, see <http://www.gnu.org/licenses/>.

package TOOL.Net;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.Insets;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import javax.swing.JComboBox;

import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import TOOL.TOOL;
import TOOL.Calibrate.VisionState;
import TOOL.Data.DataTypes;
import TOOL.Data.Frame;
import TOOL.Data.DataTypes.DataType;
import TOOL.Image.ImagePanel;
import TOOL.Image.ProcessedImage;
import TOOL.Image.TOOLImage;
import TOOL.Image.ThresholdedImageOverlay;
import TOOL.Net.DataRequest;

import TOOL.TOOLModule;
import TOOL.TOOLException;

public class RobotViewModule extends TOOLModule implements PopupMenuListener {

    private NetworkModule net;

    private JPanel displayPanel;
    private Map<JButton, DataType> typeMap;
    private Map<JMenuItem, RemoteRobot> robotMap;
    private JButton robotButton;
    private JPopupMenu robotMenu;

    private ImagePanel imagePanel;

    private RemoteRobot selectedRobot;

    private Thread streamingThread;
    private DataType streamType = DataTypes.DataType.THRESH;
    private boolean isStreaming = false;
    private boolean isSavingStream = false;
    private boolean isDrawingObjects = false;

    private Object streamOptionsLock;
    private Object drawObjectsLock;

    private String saveFramePath = null;

    private TOOL tool;

    private static final long FRAME_LENGTH_MILLIS = 40;

    private static final int SIDEBAR_WIDTH = 300;

    public RobotViewModule(TOOL t, NetworkModule net_mod) {
        super(t);

        tool = t;

        net = net_mod;
        displayPanel = new JPanel();
        typeMap = new HashMap<JButton, DataType>();
        robotMap = new HashMap<JMenuItem, RemoteRobot>();
        imagePanel = new ImagePanel();

        streamOptionsLock = new Object();

        selectedRobot = null;

        createStreamingThread();
        streamingThread.start();

        initLayout();
    }

    public String getDisplayName() {
        return "Robot";
    }

    public Component getDisplayComponent() {
        return displayPanel;
    }

    private void initLayout() {
        displayPanel.setLayout(new GridBagLayout());
        GridBagConstraints mainc = new GridBagConstraints();
        displayPanel.setBackground(new Color(123,123,0));

        JPanel subPanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;

        // Background for debugging GUI
        // subPanel.setBackground(new Color(0,0,255));
        
        c.gridwidth = 1;
        c.gridx = 0; c.gridy = 0;
        c.weightx = 1;
        c.anchor = GridBagConstraints.PAGE_START;
        robotButton = new JButton("Robot");
        robotButton.addActionListener(this);
        subPanel.add(robotButton, c);

        robotMenu = new JPopupMenu();
        robotMenu.addPopupMenuListener(this);
        subPanel.add(robotMenu);
        //subPanel.add(Box.createRigidArea(new Dimension(10, 10)));

        //createUpdateButtons(subPanel);
        c.gridy++;
        subPanel.add(new StreamOptionsPanel(this), c);

        c.gridy++;
        c.weighty = 1;
        subPanel.add(new SaveOptionsPanel(this), c);
        
        mainc.gridx = 0; mainc.gridy = 0;
        //mainc.anchor = GridBagConstraints.WEST;
        mainc.fill = GridBagConstraints.VERTICAL;
        mainc.weighty = 1;
        displayPanel.add(subPanel, mainc);

        subPanel = new JPanel();
        //subPanel.setLayout(new BoxLayout(subPanel, BoxLayout.PAGE_AXIS));
        //subPanel.add(Box.createHorizontalGlue());
        subPanel.add(imagePanel);

        mainc.gridx = 1; mainc.gridy = 0;
        mainc.weightx = 1;
        mainc.fill = GridBagConstraints.BOTH;
        displayPanel.add(subPanel, mainc);
    }

    private void createUpdateButtons(JPanel subPanel){
        subPanel.add(new JLabel("Update:"));
        subPanel.add(Box.createRigidArea(new Dimension(30, 10)));

        for (DataType t : DataTypes.types()) {
            JButton b = new JButton(DataTypes.title(t));
            typeMap.put(b, t);
            b.addActionListener(this);
            subPanel.add(b);
        }
    }

    // private void createStreamingButtons(JPanel panel){
    //     JPanel subPanel = new JPanel();
    //     subPanel.setLayout(new BoxLayout(subPanel, BoxLayout.PAGE_AXIS));
    //     panel.add(subPanel);

    //     subPanel.add(new JLabel("Streaming:"));


    //     String streamingTypes[] = {DataTypes.title(DataTypes.DataType.THRESH),
    //                                DataTypes.title(DataTypes.DataType.IMAGE)};
    //     JComboBox streamComboBox = new JComboBox(streamingTypes);

    //     Dimension maxDim = new Dimension(200,40);
    //     Dimension prefDim = new Dimension(200,40);
    //     streamComboBox.setAlignmentX(Component.LEFT_ALIGNMENT);
    //     streamComboBox.setMaximumSize(maxDim);
    //     streamComboBox.setPreferredSize(prefDim);

    //     streamComboBox.addActionListener( new ActionListener() {
    //             public void actionPerformed(ActionEvent e){
    //                 JComboBox box = (JComboBox)e.getSource();
    //                 if (box.getSelectedItem() ==
    //                     DataTypes.title(DataTypes.DataType.THRESH)){
    //                     streamType = DataTypes.DataType.THRESH;
    //                 }
    //                 else if (box.getSelectedItem() ==
    //                          DataTypes.title(DataTypes.DataType.IMAGE)){
    //                 streamType = DataTypes.DataType.IMAGE;
    //                 }
    //             }
    //         });

    //     subPanel.add(streamComboBox);

    //     startStopButton = new JButton("Start");
    //     startStopButton.addActionListener(new ActionListener() {
    //             public void actionPerformed(ActionEvent e){
    //                 if (selectedRobot == null)
    //                     return;

    //                 // Assuming we are using proper directory structure,
    //                 // set the default save folder to
    //                 // $ROBOCUP/man/frames/stream.

    //                 if (saveFramePath == null) {
    //                     if (tool.CONSOLE.pathExists("../man/frames/stream")) {
    //                         saveFramePath = tool.CONSOLE.formatPath("../man/frames/stream");
    //                     } else {
    //                         saveFramePath = tool.CONSOLE.promptDirOpen("Save Destination",
    //                                                                    "../man/frames");
    //                     }
    //                 }

    //                 if (saveFramePath == null)
    //                     return;

    //                 isStreaming = !isStreaming;

    //                 if (isStreaming){
    //                     startStopButton.setText("Stop");
    //                 } else {
    //                     startStopButton.setText("Start");
    //                 }

    //             }
    //         });
    //     subPanel.add(startStopButton);

    //     JButton frameDestButton = new JButton("Set destination");
    //     frameDestButton.addActionListener(new ActionListener() {
    //             public void actionPerformed(ActionEvent e){
    //                 saveFramePath = tool.CONSOLE.promptDirOpen("Save Destination",
    //                                                            saveFramePath);
    //             }
    //         });
    //     subPanel.add(frameDestButton);

    //     saveStreamBox = new JCheckBox("Save stream");
    //     saveStreamBox.addItemListener( new ItemListener() {
    //             public void itemStateChanged(ItemEvent e) {

    //                 if (e.getStateChange() == ItemEvent.SELECTED){
    //                     isSavingStream = true;
    //                 } else if (e.getStateChange() == ItemEvent.DESELECTED){
    //                     isSavingStream = false;
    //                 }

    //             }
    //         });
    //     subPanel.add(saveStreamBox);

    //     drawObjectsBox = new JCheckBox("Draw objects");
    //     drawObjectsBox.addItemListener( new ItemListener() {
    //             public void itemStateChanged(ItemEvent e) {

    //                 synchronized(drawObjectsLock) {
    //                     if (e.getStateChange() == ItemEvent.SELECTED) {
    //                         isDrawingObjects = true;
    //                     } else if (e.getStateChange() == ItemEvent.DESELECTED) {
    //                         isDrawingObjects = false;
    //                     }
    //                 }

    //             }
    //         });
    //     subPanel.add(drawObjectsBox);

    //     saveOptionBallBox = new JCheckBox("Ball");
    //     saveOptionBallBox.addItemListener( new ItemListener() {
    //             public void itemStateChanged(ItemEvent e) {
    //                 if (e.getStateChange() == ItemEvent.SELECTED) {
    //                 } else if (e.getStateChange() == ItemEvent.DESELECTED) {
    //                 }
    //             }
    //         });
    // }

    // Pretty tremendous hack for streaming images from Nao, probably could
    // and should be more elegant. Oh well.
    private void createStreamingThread() {

        streamingThread = new Thread(new Runnable() {
                public void run() {
                    int numFramesStreamed = 0;

                    long startTime = 0;
                    long timeSpent = 0;

                    VisionState visionState = null;

                    // Stream options lock not edited by other threads..
                    boolean _isDrawingObjects = false;
                    boolean _isStreaming = false;
                    boolean _isSavingStream = false;

                    boolean _saveBall = false;
                    boolean _saveAll = true;

                    try {
                        while (true){
                            startTime = System.currentTimeMillis();

                            synchronized(streamOptionsLock) {
                                _isDrawingObjects = isDrawingObjects;
                                _isStreaming = isStreaming;
                                _isSavingStream = isSavingStream;
                            }
                            if (!_isStreaming){
                                Thread.sleep(1500);
                                continue;
                            }

                            TOOLImage img = null;
                            ThresholdedImageOverlay threshOverlay = null;
                            Frame f = new Frame();

                            if (streamType == DataTypes.DataType.THRESH) {
                                if (_isSavingStream) {
                                    f = selectedRobot.get(numFramesStreamed);
                                    selectedRobot.fillNewFrame(f);
                                    selectedRobot.load(numFramesStreamed);
                                    selectedRobot.store(numFramesStreamed, f,
                                                        saveFramePath);
                                    numFramesStreamed ++;
                                    if (f != null) {
                                        if (visionState == null) {
                                            visionState = new VisionState(f,
                                                                          tool.getColorTable());
                                            visionState.update();
                                            img = visionState.getThreshImage();
                                        } else {
                                            visionState.newFrame(f, tool.getColorTable());
                                            visionState.update();
                                            img = visionState.getThreshImage();
                                        }
                                    }
                                } else if (_isDrawingObjects) {
                                    // If we're not saving the stream, but are
                                    // drawing objects, then we will need a
                                    // vision state anyway (HACK)
                                    selectedRobot.fillNewFrame(f);
                                    if (f != null) {
                                        if (visionState == null) {
                                            visionState = new VisionState(f,
                                                                          tool.getColorTable());
                                        } else {
                                            visionState.newFrame(f, tool.getColorTable());
                                        }
                                        visionState.update();
                                        img = visionState.getThreshImage();
                                    }
                                } else {
                                    img = selectedRobot.retrieveThresh();
                                }

                                if (_isDrawingObjects) {
                                    imagePanel.setOverlayImage(visionState.getThreshOverlay());
                                } else {
                                    imagePanel.setOverlayImage(null);
                                }
                            } else if (streamType == DataTypes.DataType.IMAGE) {
                                if (_isSavingStream) {
                                    // If we're saving the stream, get all of
                                    // the data we need and save the frame
                                    selectedRobot.fillNewFrame(f);
                                    f = selectedRobot.get(numFramesStreamed);
                                    selectedRobot.fillNewFrame(f);
                                    selectedRobot.load(numFramesStreamed);
                                    selectedRobot.store(numFramesStreamed, f,
                                                        saveFramePath);
                                    numFramesStreamed ++;
                                    img = f.image();
                                } else if (_isDrawingObjects) {
                                    // If we're not saving the stream, but are
                                    // drawing objects, then we will need a
                                    // frame anyway (HACK)
                                    selectedRobot.fillNewFrame(f);
                                    img = f.image();
                                } else {
                                    // If we're doing neither, just get an image
                                    // from the robot
                                    img = selectedRobot.retrieveImage();
                                }

                                if (_isDrawingObjects) {
                                    if (f != null) {
                                        if (visionState == null) {
                                            visionState = new VisionState(f,
                                                                          tool.getColorTable());
                                        } else {
                                            visionState.newFrame(f, tool.getColorTable());
                                        }
                                        visionState.update();
                                        imagePanel.setOverlayImage(visionState.getThreshOverlay());
                                    }
                                } else {
                                    imagePanel.setOverlayImage(null);
                                }
                            }

                            if (img != null) {
                                imagePanel.updateImage(img);
                            }

                            timeSpent = System.currentTimeMillis() - startTime;
                            if (timeSpent < FRAME_LENGTH_MILLIS){
                                Thread.sleep(FRAME_LENGTH_MILLIS - timeSpent);
                            }
                        }
                    } catch (InterruptedException e){
                    } catch (TOOLException e) {}

                }
            });
    }

    public void retrieveType(DataType t) {
        if (selectedRobot == null)
            return;

        TOOLImage i;
        switch (t) {
        case IMAGE:
            TOOL.CONSOLE.message("Requesting a raw image");
            i = selectedRobot.retrieveImage();
            if (i != null)
                imagePanel.updateImage(i);
            break;
        case THRESH:
            TOOL.CONSOLE.message("Requesting a thresholded image");
            i = selectedRobot.retrieveThresh();
            if (i != null)
                imagePanel.updateImage(i);
            break;
        }
    }

    //
    // ActionListener contract
    //

    public void actionPerformed(ActionEvent e) {

        if (e.getSource() instanceof JButton) {
            JButton b = (JButton)e.getSource();
            if (b == robotButton) {
                // show popup menu for selecting robots
                robotMenu.show(b, b.getWidth(), 0);
            }else {
                if (!typeMap.containsKey(b))
                    // error?
                    return;
                // retrieve the type of data selected
                retrieveType(typeMap.get(b));
            }
        }else if (e.getSource() instanceof JMenuItem) {
            JMenuItem m = (JMenuItem)e.getSource();
            if (!robotMap.containsKey(m))
                // error?
                return;
            // select the given robot for future data retrievals
            selectedRobot = robotMap.get(m);
            robotButton.setText(selectedRobot.name());
            robotMenu.setVisible(false);
        }
    }

    //
    // PopupMenuListener contract
    //

    public void popupMenuCanceled(PopupMenuEvent e) {
    }

    public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
        robotMenu.removeAll();
    }

    public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
        boolean none = true;
        robotMap.clear();
        for (RemoteRobot robot : net.getRobots()) {
            none = false;
            JMenuItem b = new JMenuItem(robot.name());
            robotMap.put(b, robot);
            b.addActionListener(this);
            robotMenu.add(b);
        }

        if (none)
            robotMenu.add("None available");
    }

    private class StreamOptionsPanel extends JPanel {
        private RobotViewModule parent;

        private JButton startStopButton, streamButton;
        private JCheckBox saveStreamBox;
        private JCheckBox drawObjectsBox;
        private JComboBox streamComboBox;

        public StreamOptionsPanel(RobotViewModule parentModule) {
            super(new GridBagLayout());
            parent = parentModule;

            GridBagConstraints c = new GridBagConstraints();
            c.fill = GridBagConstraints.HORIZONTAL;

            // Background to debug gui
            //this.setBackground(new Color(0,255,0));

            //this.setMaximumSize(new Dimension(SIDEBAR_WIDTH,300));
            this.setBorder(BorderFactory.createTitledBorder("Streaming"));

            c.gridwidth = 1;
            c.gridx = 0; c.gridy = 0;
            c.weightx = 1;
            String streamingTypes[] = {DataTypes.title(DataTypes.DataType.THRESH),
                                       DataTypes.title(DataTypes.DataType.IMAGE)};
            streamComboBox = new JComboBox(streamingTypes);

            Dimension maxDim = new Dimension(200,40);
            Dimension prefDim = new Dimension(200,40);
            streamComboBox.setAlignmentX(Component.LEFT_ALIGNMENT);
            streamComboBox.addActionListener( new ActionListener() {
                    public void actionPerformed(ActionEvent e){
                        JComboBox box = (JComboBox)e.getSource();
                        if (box.getSelectedItem() ==
                            DataTypes.title(DataTypes.DataType.THRESH)){
                            streamType = DataTypes.DataType.THRESH;
                        }
                        else if (box.getSelectedItem() ==
                                 DataTypes.title(DataTypes.DataType.IMAGE)){
                            streamType = DataTypes.DataType.IMAGE;
                        }
                    }
                });
            this.add(streamComboBox, c);

            startStopButton = new JButton("Start");
            startStopButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e){
                        if (selectedRobot == null)
                            return;

                        // Assuming we are using proper directory structure,
                        // set the default save folder to
                        // $ROBOCUP/man/frames/stream.

                        if (saveFramePath == null) {
                            if (tool.CONSOLE.pathExists("../man/frames/stream")) {
                                saveFramePath = tool.CONSOLE.formatPath("../man/frames/stream");
                            } else {
                                saveFramePath = tool.CONSOLE.promptDirOpen("Save Destination",
                                                                           "../man/frames");
                            }
                        }

                        if (saveFramePath == null)
                            return;

                        isStreaming = !isStreaming;

                        if (isStreaming){
                            startStopButton.setText("Stop Streaming");
                        } else {
                            startStopButton.setText("Start Streaming");
                        }

                    }
                });
            c.gridy++;
            this.add(startStopButton, c);

            JButton frameDestButton = new JButton("Set destination");
            frameDestButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e){
                        saveFramePath = tool.CONSOLE.promptDirOpen("Save Destination",
                                                                   saveFramePath);
                    }
                });
            c.gridy++;
            this.add(frameDestButton, c);

            drawObjectsBox = new JCheckBox("Draw Objects");
            drawObjectsBox.addItemListener( new ItemListener() {
                    public void itemStateChanged(ItemEvent e) {

                        synchronized(drawObjectsLock) {
                            if (e.getStateChange() == ItemEvent.SELECTED) {
                                parent.isDrawingObjects = true;
                            } else if (e.getStateChange() == ItemEvent.DESELECTED) {
                                parent.isDrawingObjects = false;
                            }
                        }

                    }
                });
            c.gridy++;
            this.add(drawObjectsBox, c);
    
        }
    }
    
    private class SaveOptionsPanel extends JPanel {
        private RobotViewModule parent;

        private JCheckBox saveFramesBox;

        private JCheckBox saveAllBox;
        private JCheckBox saveBallBox;
        private JCheckBox saveCrossBox;
        private JCheckBox saveYGPBox;
        private JCheckBox saveBGPBox;
        private JCheckBox saveLineBox;

        public SaveOptionsPanel(RobotViewModule parentModule) {
            super(new GridBagLayout());
            parent = parentModule;

            GridBagConstraints c = new GridBagConstraints();
            c.fill = GridBagConstraints.HORIZONTAL;

            // Background for debugging GUI
            // this.setBackground(new Color(255,0,0)); 

            //this.setMaximumSize(new Dimension(SIDEBAR_WIDTH,300));
            this.setBorder(BorderFactory.createTitledBorder("Saving"));

            c.gridwidth = 2;
            c.weightx = 1;
            c.gridx = 0; c.gridy = 0;

            // Add the save frames checkbox - if unchecked, save no frames
            saveFramesBox = new JCheckBox("Save Frames");
            saveFramesBox.addItemListener( new ItemListener() {
                    public void itemStateChanged(ItemEvent e) {

                        if (e.getStateChange() == ItemEvent.SELECTED){
                            parent.isSavingStream = true;
                        } else if (e.getStateChange() == ItemEvent.DESELECTED){
                            parent.isSavingStream = false;
                        }

                    }
                });
            this.add(saveFramesBox, c);

            // Add the save frames label
            c.gridx = 0; c.gridy++;
            c.insets = new Insets(10,0,0,0);
            this.add(new JLabel("Frames to Save:"), c);
            c.insets = new Insets(0,0,0,0);

            // Add the save all box - if checked, save every frame
            c.gridx = 0; c.gridy++;
            saveAllBox = new JCheckBox("All", true);
            this.add(saveAllBox, c);

            // Add the object specific checkboxes - if checked, save
            // frames with the specific object in the frame
            c.gridwidth = 1;
            saveBallBox = new JCheckBox("Ball");
            c.gridx = 0; c.gridy++;
            this.add(saveBallBox, c);
            saveCrossBox = new JCheckBox("Cross");
            c.gridx = 1;
            this.add(saveCrossBox, c);
            saveYGPBox = new JCheckBox("YGP");
            c.gridx = 0; c.gridy++;
            this.add(saveYGPBox, c);
            saveBGPBox = new JCheckBox("BGP");
            c.gridx = 1;
            this.add(saveBGPBox, c);
        }
    }
}
