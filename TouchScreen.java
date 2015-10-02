/* Project 3 by Phu Nguyen, Jacob Marzloff, and Joshua Medernach 
 * 4-16-15
 * CSC 4243-001
 * Dr. Ullmer
 */
 
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;

import com.illposed.osc.*;
import java.net.*;
import javax.swing.border.LineBorder;

/* This is the class for 27" multi-touch screen
 * We will however be only using single touch to interact with it
 */
public class TouchScreen extends JFrame {

	private int w = 1920, h = 1080;  // Simple dimensions for testing

	private JFrame frame = null;
	private Container  root = null;  // Root container

	// Interface
	private JPanel  interfacePanel = null;  	// We should only need one panel for touch screen
	private JPanel  interfaceSidePanel = null;  // Panel for buttons
	private JButton notifyMaintenance = null;	// Send message to maintenance team
	private JButton notifyPatrons = null;       // Send message to patrons
	private JLabel  mapTitle = null;            // Map description
	
	// TouchScreen Images
	// Initializing as global variables so Handler class (at bottom) can access loaded images
	public JLabel touchScreenImg = null;													// Touch Screen Label
	public ImageIcon ts_initial = new ImageIcon("touchscreen_init.jpg");					// Initial touch screen image
	public ImageIcon ts_ks_selected = new ImageIcon("touchscreen_ks_selected.jpg");			// Touch screen with Kirby Smith selected
	public ImageIcon ts_ks_selectedfix = new ImageIcon("touchscreen_ks_selectedfix.jpg");   // Touch screen with Kirby Smith fixed selected
	public ImageIcon ts_nab_selected = new ImageIcon("touchscreen_nab_selected.jpg");		// Touch screen with North Alex Box selected
	public ImageIcon ts_sab_selected = new ImageIcon("touchscreen_sab_selected.jpg");		// Touch screen with South Alex Box selected
	public ImageIcon ts_ss_selected = new ImageIcon("touchscreen_ss_selected.jpg");			// Touch screen with South Stadium selected

	// Real code below
	private JButton closeButton = null;			// May not be required if we detect click positions instead
	private JButton openButton = null;			// May not be required if we detect click positions instead

	// OSC Message components
	private int remotePort = 8000; // The port the server is listening on we are sending to

	// we need two IP addresses and two senders, one for 2x2 and one for project 2 2x1
	private InetAddress twoByOneIP = InetAddress.getByName("IP ADDRESS OF 2x1 MACHINE");		// In future, we will hard code ip address via = InetAddress.getByName("192.168.1.147");
	private OSCPortOut twoByOneSender = null;

	private InetAddress twoByTwoIP = InetAddress.getByName("IP ADDRESS OF 2x2 MACHINE");
	private OSCPortOut twoByTwoSender = null;
	
	// status of Kirby Smith garage, false = close, true = open
	public boolean lotStatus = false;
	
	// start at lotNumber 0 for initial screen
	public int lotNumber = 0;

	public static void main(String[] args) throws UnknownHostException, SocketException, java.io.IOException, InterruptedException { 
		new TouchScreen(); 
	}
 
	 public TouchScreen() throws UnknownHostException, SocketException, java.io.IOException, InterruptedException {
		  super("Project 3 by Phu Nguyen, Jacob Marzloff, and Joshua Medernach");
		  buildUI();
		  setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		  setUpOSC();
	 }
	 
	 /*
	  * Send close and open messages to 2x1 display (project 2)
	  */
	 public void sendMessageToClose() throws UnknownHostException, SocketException, java.io.IOException, InterruptedException {
		 // The address to send our message to
		 String addressClose = "/close";

		 // Use the address to form an OSCMessage
		 OSCMessage messageClose = new OSCMessage(addressClose);

		 // Send each message
		 System.out.printf("Sending messageClose to %s:%s at %s\n", this.twoByOneIP, this.remotePort, messageClose.getAddress());
		 twoByOneSender.send(messageClose);
	 }

	 public void sendMessageToOpen() throws UnknownHostException, SocketException, java.io.IOException, InterruptedException {
		 // The address to send our message to
		 String addressOpen = "/open";

		 // Use the address to form an OSCMessage
		 OSCMessage messageOpen = new OSCMessage(addressOpen);

		 // Send each message
		 System.out.printf("Sending messageOpen to %s:%s at %s\n", this.twoByOneIP, this.remotePort, messageOpen.getAddress());
		 twoByOneSender.send(messageOpen);
		 
		 this.sendMessageToTiled(1);
	 }
	 
	 public void setUpOSC() throws UnknownHostException, SocketException, java.io.IOException, InterruptedException {
			// The IP Address of the server (or listener) we would like to send to
	    	// For testing we will use the `getLocalHost` function to send to our
	    	// machine, but for using with different physical devices, use something
	    	// like the following commented out line.
			
			// Jacob: Tested this out on the two laptops at my house and had success!
			//			We'll need to get the static IP address from the 2x2 display and hardcode it in the line below
			//			Seems like everything should work fine on that front.
	    	// this.remoteIP  = InetAddress.getByName("192.168.1.147");

	    	// This is for 2x1 display (project 2)
	    	// this.twoByOneIP = InetAddress.getLocalHost();
	    
	    	// Bring the IP Address and port together to form our OSC Sender for 2x1 display
	    	this.twoByOneSender = new OSCPortOut(this.twoByOneIP, this.remotePort);

	    	// Next, this is for 2x2 display
	    	// this.twoByTwoIP = InetAddress.getLocalHost();
	    
	    	// Bring the IP Address and port together to form our OSC Sender for 2x2 display
	    	this.twoByTwoSender = new OSCPortOut(this.twoByTwoIP, this.remotePort);
	 }
	 
		
	 // Notify Maintenance Button Action
	 Action actionNotifyMaintenance = new AbstractAction() {
		 public void actionPerformed(ActionEvent ae) {
			 
			// address to send notify maintenance message to screen
			String address = "/kirbysmithNM";
		    
			try {
		    	sendNoticeToTiled(address);
			}
			catch (Exception error) {
				System.out.println("Failed to send message to address " + address);
			}			
		 }
	 };
		
	 // Notify Patrons Button Action
	 Action actionNotifyPatrons = new AbstractAction() {
		 public void actionPerformed(ActionEvent ae) {
			 
			// address to send notify patrons message to screen
			String address = "/kirbysmithNP";
			
		    try {
		    	sendNoticeToTiled(address);
			}
			catch (Exception error) {
				System.out.println("Failed to send message to address " + address);
			}	
		 }
	 };
	
	 // Close Button Action
	 Action actionCloseButton = new AbstractAction() {
		 public void actionPerformed(ActionEvent ae) {
			 lotStatus = false;
			 try {
				 // send message to close 2x1 screen
				 sendMessageToClose();
			 }
			 catch (Exception error) {
				 System.out.println("Failed to send message to close garage");
			 }
		 }
	 };
	 
	 // Open Button Action
	 Action actionOpenButton = new AbstractAction() {
		 public void actionPerformed(ActionEvent ae) {
			 lotStatus = true;
			 try {
				 //send message to resume 2x1 screen
				 sendMessageToOpen();
				 
				 //update map with fixed Kirby Smith status
				 changeImage(1);
			 }
			 catch (Exception error) {
				 System.out.println("Failed to send message to open garage");
			 }		
		 }
	 };

    // Touch Screen Interface
	public void buildUI() throws UnknownHostException {
		this.root = getContentPane();
		
		this.root.setBackground(Color.black);                      // frame background color
		touchScreenImg = new JLabel(ts_initial);                   // Start with initial touch screen image
		notifyMaintenance = new JButton(actionNotifyMaintenance);  // notify maintenance button
		notifyMaintenance.setText("Notify Maintenance");           // set maintenance button text
		notifyPatrons = new JButton(actionNotifyPatrons);          // notify patrons button
		notifyPatrons.setText("Notify Patrons");                   // set notify patrons button text
		closeButton = new JButton(actionCloseButton);              // close lot button
		closeButton.setText("Close Lot");                          // set close lot button text
		openButton = new JButton(actionOpenButton);                // open lot button
		openButton.setText("Open Lot");                            // set open lot button text
		
		// set layout of frame
		this.setLayout(new FlowLayout());
		
		// Create main panel of screen for map and side panel of screen for buttons
		this.interfacePanel = new JPanel();
		this.interfaceSidePanel = new JPanel();
		
		// Set garage title size and font
		this.mapTitle = new JLabel("Select an Area", JLabel.CENTER);
		this.mapTitle.setForeground(Color.white);
		this.mapTitle.setPreferredSize(new Dimension(410,110));
		this.mapTitle.setFont(mapTitle.getFont().deriveFont(42.0f));
		this.mapTitle.setAlignmentX(CENTER_ALIGNMENT);
		
		// Set layout of main panel
		this.interfacePanel.setLayout(new BoxLayout(this.interfacePanel, BoxLayout.Y_AXIS));
		this.interfacePanel.setPreferredSize(new Dimension(w,h));
		this.interfacePanel.add(touchScreenImg);
		this.interfacePanel.setPreferredSize(new Dimension(1450, 1080));
		
		// Set layout of side panel
		this.interfaceSidePanel.add(mapTitle);
		this.interfaceSidePanel.add(Box.createRigidArea(new Dimension(200,200))); // add spacing in between buttons
		
		// Add notify maintenance button to side panel
		this.interfaceSidePanel.add(notifyMaintenance);
		this.interfaceSidePanel.add(Box.createRigidArea(new Dimension(0,100))); // add spacing in between buttons
		notifyMaintenance.setPreferredSize(new Dimension(320,80));
		notifyMaintenance.setBackground(Color.black);
		notifyMaintenance.setBorder(new LineBorder(Color.white, 2));
		notifyMaintenance.setFont(notifyMaintenance.getFont().deriveFont(30.0f));
		notifyMaintenance.setForeground(Color.white);
		
		// Add notify patrons button to side panel
		this.interfaceSidePanel.add(notifyPatrons);
		this.interfaceSidePanel.add(Box.createRigidArea(new Dimension(0,100))); // add spacing in between buttons
		notifyPatrons.setPreferredSize(new Dimension(320, 80));
		notifyPatrons.setBackground(Color.black);
		notifyPatrons.setBorder(new LineBorder(Color.white, 2));
		notifyPatrons.setFont(notifyPatrons.getFont().deriveFont(30.0f));
		notifyPatrons.setForeground(Color.white);
		
		// Add close lot button to side panel
		this.interfaceSidePanel.add(closeButton);
		this.interfaceSidePanel.add(Box.createRigidArea(new Dimension(0,100))); // add spacing in between buttons
		closeButton.setPreferredSize(new Dimension(320, 80));
		closeButton.setBackground(Color.black);
		closeButton.setBorder(new LineBorder(Color.white, 2));
		closeButton.setFont(closeButton.getFont().deriveFont(30.0f));
		closeButton.setForeground(Color.white);
		
		// Add open lot button to side panel
		this.interfaceSidePanel.add(openButton);
		this.interfaceSidePanel.add(Box.createRigidArea(new Dimension(0,100))); // add spacing in between buttons
		openButton.setPreferredSize(new Dimension(320, 80));
		openButton.setBackground(Color.black);
		openButton.setBorder(new LineBorder(Color.white, 2));
		openButton.setFont(openButton.getFont().deriveFont(30.0f));
		openButton.setForeground(Color.white);
		
		// Align buttons to center of panel
		notifyMaintenance.setAlignmentX(CENTER_ALIGNMENT);
		notifyPatrons.setAlignmentX(CENTER_ALIGNMENT);
		closeButton.setAlignmentX(CENTER_ALIGNMENT);
		openButton.setAlignmentX(CENTER_ALIGNMENT);
		
		// Set size and background color of panels
		this.interfaceSidePanel.setPreferredSize(new Dimension(440, 1080));
		this.interfacePanel.setBackground(Color.black);
		this.interfaceSidePanel.setBackground(Color.black);
		
		// Create event handler and mouse listener
		Handlerclass handler = new Handlerclass();
		touchScreenImg.addMouseListener(handler);
		touchScreenImg.addMouseMotionListener(handler);
		
		// Set dimension of frame
		this.setSize(new Dimension(w,h));
		
		this.root.add(interfacePanel);
		this.root.add(interfaceSidePanel);
		this.pack();
		this.setVisible(true);		
	}
	
	// Send notify message to tile display
	public void sendNoticeToTiled(String address) throws UnknownHostException, SocketException, java.io.IOException, InterruptedException {
    	// Use the address to form an OSCMessage
    	OSCMessage message = new OSCMessage(address);

    	// Send each message
    	System.out.printf("Sending message to %s:%s at %s\n", this.twoByTwoIP, this.remotePort, message.getAddress());
    	twoByTwoSender.send(message);
	}
	
	// Send garage selection to tile display
	public void sendMessageToTiled(int lotNumber) throws UnknownHostException, SocketException, java.io.IOException, InterruptedException {
		// The address to send our message to
    	String address = "";

		if (lotNumber == 0) {
			address = "/initial";            // Initial starting screen
		}
		else if (lotNumber == 1) {
			// check status of garage
			if(lotStatus == false) {
				address = "/kirbysmith";     // Kirby Smith screen, broken areas
			}
			else {
				address = "/kirbysmithfix";  // Kirby Smith screen functional
			}
		}
		else if (lotNumber == 2) {
			address = "/northalex";          // North Alex Box screen
		}
		else if (lotNumber == 3) {
			address = "/southalex";          // South Alex Box screen
		}
		else if (lotNumber == 4) {
			address = "/southstad";          // South Stadium screen
		}
		else 
			System.out.println("Invalid lotNumber in sendMessageToTiled()");
    	// Use the address to form an OSCMessage
    	OSCMessage message = new OSCMessage(address);

    	// Send each message
    	System.out.printf("Sending message to %s:%s at %s\n", this.twoByTwoIP, this.remotePort, message.getAddress());
    	twoByTwoSender.send(message);
	}
	
	// Mouse event handler
	private class Handlerclass implements MouseListener, MouseMotionListener {
		@Override
		public void mouseDragged(MouseEvent arg0) {
			// TODO Auto-generated method stub			
		}

		@Override
		public void mouseMoved(MouseEvent arg0) {
			// TODO Auto-generated method stub			
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			// TODO Auto-generated method stub			
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub
		}

		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub
		}

		@Override
		public void mousePressed(MouseEvent e) {
			int x = 0;    // create place to save x-coordinate 
			int y = 0;    // create place to save y-coordinate
			x = e.getX(); // get current mouse x-coordinate
			y = e.getY(); // get current mouse y-coordinate
			
			// check bounds for Kirby Smith Lot
			if(x > 793 && x < 893 && y > 252 && y < 352) {
			   // Call method for switching image
			   System.out.println("Pressed Kirby Smith Lot");
			   mapTitle.setText("Kirby Smith");
			   lotNumber = 1;
			}
			// check bounds for North Alex Box Lot
			else if(x > 410 && x < 510 && y > 294 && y < 394) {
			   // Call method for switching image
			   System.out.println("Pressed North Alex Box Lot");
			   mapTitle.setText("North Alex Box");
			   lotNumber = 2;
			}
			// check bounds for South Alex Box Lot
			else if(x > 410 && x < 510 && y > 430 && y < 530) {
			   // Call method for switching image
			   System.out.println("Pressed South Alex Box Lot");
			   mapTitle.setText("South Alex Box");
			   lotNumber = 3;
			}
			// check bounds for South Stadium Lot
			else if(x > 489 && x < 589 && y > 663 && y < 763) {
			   // Call method for switching image
			   System.out.println("Pressed South Stadium Lot");
			   mapTitle.setText("South Stadium");
			   lotNumber = 4;
			}
			else;   //Non select able area 
			
			changeImage(lotNumber);
			try {
				sendMessageToTiled(lotNumber);
			}
			catch (Exception error) {
				System.out.println("Failed to send message to address " + lotNumber);
			}
		}
		@Override
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}
	}
	
	// Change button size when selected
	public void changeImage(int lotNumber) {
		if (lotNumber == 1) {
			if(lotStatus == false)
				touchScreenImg.setIcon(ts_ks_selected);
			else
				touchScreenImg.setIcon(ts_ks_selectedfix);
		}
		else if (lotNumber == 2) {
			touchScreenImg.setIcon(ts_nab_selected);
		}
		else if (lotNumber == 3) {
			touchScreenImg.setIcon(ts_sab_selected);
		}
		else if (lotNumber == 4) {
			touchScreenImg.setIcon(ts_ss_selected);
		}
		else
			System.out.println("Error with changeImage(). Incorrect lotNumber");
	}
	
}
