/* Project 3 by Phu Nguyen, Jacob Marzloff, and Joshua Medernach 
 * 4-22-15
 * CSC 4243-001
 * Dr. Ullmer
 */

 // Move to left 540px and up 3840px

/*
* This class was formerly project 2, extended for project 3
*/
 
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import com.illposed.osc.*;
import java.net.SocketException;

public class ParkingScreen extends JFrame {

	private int w = 2114, h = 5724;  // Width and height of root container and JPanel
							 // It's not exact 4k + 1080p dimensions
							 // This is to adjust for the pixels that the window takes up by default
							 // The top of a window in Windows 7 is 30px, the sides are 8px each
							 // Decreasing the size of the images will (hopefully) make up for this

	private JFrame frame = null;
	private Container  root = null;  // Root container

	// Entry Tab
	private JPanel  entryImagePanel = null;  		// Entry Panel that contains enterTopImage and enterBottomImage
	private JLabel  enterTopImage = null;  			// Staging label for top enter image
	private JLabel	enterBottomImage = null;		// Staging label for bottom enter image
	private ImageIcon  enterTop = null;    			// Top image for enter screen
	private ImageIcon  enterBottomInit = null;    	// Initial bottom image for enter screen
	private ImageIcon	enterBottomPersonal = null; // Personal bottom image for enter screen

	// Exit Tab
	private JPanel  exitImagePanel = null;  		// Entry Panel that contains exitTopImage and exitBottomImage
	private JLabel  exitTopImage = null;  			// Staging label for top exit image
	private JLabel	exitBottomImage = null;			// Staging label for bottom exit image
	private ImageIcon  exitTopInit = null;   		// Initial top image with traffic and no tabs
	private ImageIcon  exitTopTraffic = null;   	// Top image with traffic map and tabs
	private ImageIcon  exitTopHome = null;    		// Top image with home map and tabs
	private ImageIcon  exitTopGrocery = null;   	// Top image with grocery map and tabs
	private ImageIcon  exitBottomInit = null;    	// Initial bottom exit image
	private ImageIcon  exitBottomInfo = null;		// Bottom image with personal info tab open
	private ImageIcon  exitBottomCar = null;		// Bottom image with car info tab open
	private ImageIcon  exitBottomTakeout = null;	// Bottom image with takeout info tab open

	// Closed mode
	private JPanel closedImagePanel = null;			// Closed Panel that contains closedTopImage and closedBottomImage
	private JLabel closedTopImage = null;			// Staging label for closed top image
	private JLabel closedBottomImage = null;        // Staging label for closed bottom image
	private ImageIcon closedTop = null;             // Image with a closure message and red background
	private ImageIcon closedBottom = null;          // Blank, red image

	private boolean isClosed = false;				// Is this application closed to use?




	public static void main(String[] args) { 
		new ParkingScreen(); 
	}
 
	 public ParkingScreen() {
		  super("Project 3 by Phu Nguyen, Jacob Marzloff, and Joshua Medernach");
		  buildEntryUI();
		  startOSC();
		  setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	 }
  
	public void buildEntryUI () {
		// frame = new JFrame("Test test test");
		root = getContentPane();

		enterTop = new ImageIcon("enter_top.jpg");
		enterBottomInit = new ImageIcon("enter_bottom_initial.jpg");
		enterBottomPersonal = new ImageIcon("enter_bottom_personal.jpg");

		enterTopImage = new JLabel(enterTop);
		enterBottomImage = new JLabel(enterBottomInit);
		
		// Setting left alignment so bottom image is pushed to far left side of screen
		// May need to change to CENTER_ALIGNMENT depending on how the screens are setup in Coates
		enterTopImage.setAlignmentX(Component.CENTER_ALIGNMENT);
		enterBottomImage.setAlignmentX(Component.CENTER_ALIGNMENT);

		entryImagePanel = new JPanel();
		entryImagePanel.setLayout(new BoxLayout(entryImagePanel, BoxLayout.Y_AXIS));
		entryImagePanel.setPreferredSize(new Dimension(w,h));

		// Anything not covered by the images will be black.
		entryImagePanel.setOpaque(true);
		entryImagePanel.setBackground(Color.black);
		
		entryImagePanel.add(enterTopImage);
		entryImagePanel.add(enterBottomImage);
		entryImagePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		// entryImagePanel.setAlignmentY(Component.CENTER_ALIGNMENT);
		root.add(entryImagePanel);
		pack();
		Dimension size = this.getPreferredSize();
		this.setLocation(-540, -3840); 
		setVisible(true);
	}

	public void buildExitUI () {
		root.removeAll();

		exitTopInit = new ImageIcon("exit_top_initial.jpg");
		exitTopTraffic = new ImageIcon("exit_top_traffic.jpg");
		exitTopHome = new ImageIcon("exit_top_home.jpg");
		exitTopGrocery = new ImageIcon("exit_top_grocery.jpg");
		exitBottomInit = new ImageIcon("exit_bottom_initial.jpg");
		exitBottomInfo = new ImageIcon("exit_bottom_info.jpg");
		exitBottomCar = new ImageIcon("exit_bottom_car.jpg");
		exitBottomTakeout = new ImageIcon("exit_bottom_takeout.jpg");

		exitTopImage = new JLabel(exitTopInit);
		exitBottomImage = new JLabel(exitBottomInit);
		
		// Setting left alignment so bottom image is pushed to far left side of screen
		// May need to change to CENTER_ALIGNMENT depending on how the screens are setup in Coates
		exitTopImage.setAlignmentX(Component.CENTER_ALIGNMENT);
		exitBottomImage.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		exitImagePanel = new JPanel();
		exitImagePanel.setLayout(new BoxLayout(exitImagePanel, BoxLayout.Y_AXIS));
		exitImagePanel.setPreferredSize(new Dimension(w,h));
		
		// Anything not covered by the images will be black.
		exitImagePanel.setOpaque(true);
		exitImagePanel.setBackground(Color.black);

		exitImagePanel.add(exitTopImage);
		exitImagePanel.add(exitBottomImage);

		root.add(exitImagePanel);
		pack();
		setVisible(true);
	}

	public void buildCloseUI () {
		root.removeAll();

		closedTop = new ImageIcon("closed_top.jpg");
		closedBottom = new ImageIcon("closed_bottom.jpg");

		closedTopImage = new JLabel(closedTop);
		closedBottomImage = new JLabel(closedBottom);
		
		// Setting left alignment so bottom image is pushed to far left side of screen
		// May need to change to CENTER_ALIGNMENT depending on how the screens are setup in Coates
		closedTopImage.setAlignmentX(Component.CENTER_ALIGNMENT);
		closedBottomImage.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		closedImagePanel = new JPanel();
		closedImagePanel.setLayout(new BoxLayout(closedImagePanel, BoxLayout.Y_AXIS));
		closedImagePanel.setPreferredSize(new Dimension(w,h));
		
		// Anything not covered by the images will be red.
		closedImagePanel.setOpaque(true);
		closedImagePanel.setBackground(Color.black);

		closedImagePanel.add(closedTopImage);
		closedImagePanel.add(closedBottomImage);

		root.add(closedImagePanel);
		pack();
		setVisible(true);
	}

	public void startOSC() {
		int receiverPort = 8000;
		try {
			OSCPortIn receiver = new OSCPortIn(receiverPort);

			/////////////////////////////
			//// Entry Mode Handlers ////
			/////////////////////////////
			OSCListener entryModeHandler = new OSCListener() {
				public void acceptMessage(java.util.Date time, OSCMessage message) {
					if (!isClosed) {
						 buildEntryUI();
						 System.out.println("Entry Mode tab is opened");
					}
				}
			};
			OSCListener entryFaderHandler = new OSCListener() {
				public void acceptMessage(java.util.Date time, OSCMessage message) {
					Object[] values = message.getArguments();
					System.out.println("entryFader value: " + (Float) values[0]);
					if (!isClosed) {
						if ((Float) values[0] > 80) {
							enterBottomImage.setIcon(enterBottomPersonal);
							System.out.println("Entry successful");
						}
					}
				}
			};
			////////////////////////////
			//// Exit Mode Handlers ////
			////////////////////////////
			OSCListener exitModeHandler = new OSCListener() {
				public void acceptMessage(java.util.Date time, OSCMessage message) {
					if (!isClosed) {
					 	buildExitUI();
						System.out.println("Exit Mode tab is opened");
					}
				}
			};
			
			OSCListener exitFaderHandler = new OSCListener() {
				public void acceptMessage(java.util.Date time, OSCMessage message) {
					Object[] values = message.getArguments();
					System.out.println("exitFader value: " + (Float) values[0]);
					if (!isClosed) {
						if ((Float) values[0] > 80) {
							exitTopImage.setIcon(exitTopTraffic);
							exitBottomImage.setIcon(exitBottomInfo);
							System.out.println("Exit successful");
						}
					}
				}
			};

			OSCListener trafficPushHandler = new OSCListener() {
				public void acceptMessage(java.util.Date time, OSCMessage message) {
					if (!isClosed) {
					 	exitTopImage.setIcon(exitTopTraffic);
					 	System.out.println("Traffic map is opened");
					}
				}
			};

			OSCListener homePushHandler = new OSCListener() {
				public void acceptMessage(java.util.Date time, OSCMessage message) {
					if (!isClosed) {
						exitTopImage.setIcon(exitTopHome);
						System.out.println("Route home map is opened");
					}
				}
			};

			OSCListener storePushHandler = new OSCListener() {
				public void acceptMessage(java.util.Date time, OSCMessage message) {
					if (!isClosed) {
						exitTopImage.setIcon(exitTopGrocery);
						System.out.println("Route to store map is opened");
					}
				}
			};

			OSCListener personalPushHandler = new OSCListener() {
				public void acceptMessage(java.util.Date time, OSCMessage message) {
					if (!isClosed) {
						exitBottomImage.setIcon(exitBottomInfo);
						System.out.println("Personal info is opened");
					}
				}
			};
			
			OSCListener carPushHandler = new OSCListener() {
				public void acceptMessage(java.util.Date time, OSCMessage message) {
					if (!isClosed) {
						exitBottomImage.setIcon(exitBottomCar);
						System.out.println("Car info is opened");
					}
				}
			};
			
			OSCListener takeoutPushHandler = new OSCListener() {
				public void acceptMessage(java.util.Date time, OSCMessage message) {
					if (!isClosed) {
						exitBottomImage.setIcon(exitBottomTakeout);
						System.out.println("Takeout selection is opened");
					}
				}
			};

			OSCListener closeHandler = new OSCListener() {
				public void acceptMessage(java.util.Date time, OSCMessage message) {
					buildCloseUI();
					isClosed = true;
					System.out.println("Closed down!");
				}
			};

			OSCListener openHandler = new OSCListener() {
				public void acceptMessage(java.util.Date time, OSCMessage message) {
					buildEntryUI();
					isClosed = false;
					System.out.println("Opened up!");
				}
			};

			// Entry Mode listeners
			receiver.addListener("/1", entryModeHandler);  // This is a listener for tab opening
			receiver.addListener("/1/entryFader", entryFaderHandler);
			// Exit Mode listeners
			receiver.addListener("/2", exitModeHandler);  // This is a listener for tab opening
			receiver.addListener("/2/exitFader", exitFaderHandler);
			receiver.addListener("/3/traffic", trafficPushHandler);
			receiver.addListener("/3/home", homePushHandler);
			receiver.addListener("/3/store", storePushHandler);
			receiver.addListener("/3/personal", personalPushHandler);
			receiver.addListener("/3/car", carPushHandler);
			receiver.addListener("/3/takeout", takeoutPushHandler);

			// We want unique addresses for setting the closure mode for this application
			// So they don't conflict with the tiled display addresses
			// Therefore:
			// to close, use this address: "/close"
			// to open, use this address: "/open"
			receiver.addListener("/close", closeHandler);
			receiver.addListener("/open", openHandler);


			System.out.println("Server is listening on port " + receiverPort + "...");
			receiver.startListening();
		}
		catch (SocketException e) {
			System.out.println("SocketException error in startOSC()");
			System.err.println(e);
		}    
	}
}
