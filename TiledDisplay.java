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

/* This is the class for 27" multi-touch screen
 * We will however be only using single touch to interact with it
 */
public class TiledDisplay extends JFrame {

	private int w = 3840, h = 2400;  // Simple dimensions for testing
	private int tileX4W = 410, tileX4H = 410;  // Testing 4x4 tile dimensions

	private JFrame frame = null;
	private Container  root = null;  // Root container

	// Interface
	private JPanel tiledPanel = null;  	// We should only need one panel for tiled display
	
	// Images
	//private ImageIcon whiteImg = null;
	//private ImageIcon blackImg = null;
	//private ImageIcon startImg = null;       // test background image for 4x4 tiles
	public ImageIcon tiled_init = null;      // start screen
	public ImageIcon tiled_ks_broken = null; // kirby smith broken screen
	public ImageIcon tiled_nab = null;
	public ImageIcon tiled_sab = null;
	public ImageIcon tiled_ss = null;
	public ImageIcon tiled_ks_fixed = null;
	public ImageIcon tiled_ks_NM = null;
	public ImageIcon tiled_ks_NP = null;
	private JLabel tiledLabel = null;   
	private JLabel label2 = null;       // label for screen 2
	private JLabel label3 = null;       // label for screen 3
	private JLabel label4 = null;       // label for screen 4
	private ImageIcon currentImg = null;
	private ImageIcon screen4Img = null; // image for screen 4



	// OSC Message components
	// The port to listen in
	private int receiverPort = 8000;
	OSCPortIn receiver = null;

	public static void main(String[] args) throws java.net.SocketException { 
		new TiledDisplay(); 
	}
 
	 public TiledDisplay() throws java.net.SocketException {
		  super("Project 3 by Phu Nguyen, Jacob Marzloff, and Joshua Medernach");
		  buildDisplay();
		  setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		  // Set up the OSC reciever
		  setUpOSC();
	 }
  
	public void buildDisplay() {
		this.root = getContentPane();
		
		//touchScreenImg = new ImageIcon("/tiledDisplayTestWhite.png").getImage();
		// this.whiteImg = new ImageIcon("tiledDisplayTestWhite.png");
		// this.blackImg = new ImageIcon("tiledDisplayTestBlack.png");
		// this.startImg = new ImageIcon("tiledDisplayTest.png");
		// this.screen4Img = new ImageIcon("tiledDisplayTestWhite.png");
		
		// Images for 2x2 tile display
		this.tiled_init = new ImageIcon("tiled_init.jpg");
		this.tiled_ks_broken = new ImageIcon("tiled_ks_broken.jpg");
		this.tiled_ks_fixed = new ImageIcon("tiled_ks_fixed.jpg");
		this.tiled_nab = new ImageIcon("tiled_nab.jpg");
		this.tiled_sab = new ImageIcon("tiled_sab.jpg");
		this.tiled_ss = new ImageIcon("tiled_ss.jpg");
		this.tiled_ks_NM = new ImageIcon("tiled_ks_NM.jpg");
		this.tiled_ks_NP = new ImageIcon("tiled_ks_NP.jpg");
		
		this.tiledPanel = new JPanel();
		this.tiledPanel.setLayout(new FlowLayout());
		this.tiledPanel.setPreferredSize(new Dimension(w,h));
		
		this.tiledPanel.setBackground(Color.black);
		this.tiledLabel = new JLabel(this.tiled_init);
		
		this.tiledPanel.add(this.tiledLabel);
		
		this.setSize(new Dimension(w,h));
		
		this.root.add(tiledPanel);
		this.pack();
		this.setLocation(0, -1200); 
		this.setVisible(true);
	}

	// public void flipImages() {
		// if (this.currentImg == this.whiteImg) {
			// this.currentImg = this.blackImg;
			// this.label.setIcon(this.currentImg);
		// } else {
			// this.currentImg = this.whiteImg;
			// label.setIcon(this.currentImg);
		// }
	// }
	
	// public void flipImagesScreen4() {
		// if (this.screen4Img == this.whiteImg) {
			// this.screen4Img = this.blackImg;
			// this.label4.setIcon(this.screen4Img);
		// } else {
			// this.screen4Img = this.whiteImg;
			// label4.setIcon(this.screen4Img);
		// }
	// }
	
	// Change tile display screen by the lot number
	public void changeImage(int lotNumber) {
		if (lotNumber == 0) {
			this.tiledLabel.setIcon(this.tiled_init);      // Initial screen
		}
		else if (lotNumber == 1) {
			this.tiledLabel.setIcon(this.tiled_ks_broken); // Kirby Smith broken screen
		}
		else if (lotNumber == 2) {
			this.tiledLabel.setIcon(this.tiled_nab);       // North Alex Box screen
		}
		else if (lotNumber == 3) {
			this.tiledLabel.setIcon(this.tiled_sab);       // South Alex Box screen
		}
		else if (lotNumber == 4) {
			this.tiledLabel.setIcon(this.tiled_ss);        // South Stadium screen
		}
		else if (lotNumber == 501) {
			this.tiledLabel.setIcon(this.tiled_ks_fixed);  // Kirby Smith fixed screen
		}
		else if (lotNumber == 700) {
			this.tiledLabel.setIcon(this.tiled_ks_NM);     // Kirby Smith notify maintenance screen
		}
		else if (lotNumber == 800) {
			this.tiledLabel.setIcon(this.tiled_ks_NP);     // Kirby Smith notify patrons screen
		}
		else
			System.out.println("Invalid lotNumber in changeImage()");
	}

	public void setUpOSC() throws java.net.SocketException {
		this.receiver = new OSCPortIn(receiverPort);

		// We define a message handler to process messages
    	// This library calls its message handlers OSCListener
    	// since they "listen" for an event to happen and process
    	// it once they "hear" one.
    	// OSCListener handler1 = new OSCListener() {
      		// public void acceptMessage(java.util.Date time, OSCMessage message) {
        		// TODO: Put your code to process a message in here
        		// System.out.println("Handler1 called with address " + message.getAddress());
        
        		// flipImages();
      		// }
    	// };
    	
    	// OSCListener handler4 = new OSCListener() {
      		// public void acceptMessage(java.util.Date time, OSCMessage message) {
        		// TODO: Put your code to process a message in here
        		// System.out.println("Handler4 called with address " + message.getAddress());
        
        		// flipImagesScreen4();
      		// }
    	// };
		
		// Initial screen message Handle
		OSCListener initialHandler = new OSCListener() {
      		public void acceptMessage(java.util.Date time, OSCMessage message) {
        		System.out.println("initialHandler called with address " + message.getAddress());
        
        		changeImage(0);
      		}
    	};
		
    	// Kirby Smith broken Handle
		OSCListener ksHandler = new OSCListener() {
      		public void acceptMessage(java.util.Date time, OSCMessage message) {
        		System.out.println("ksHandler called with address " + message.getAddress());
        
        		changeImage(1);
      		}
    	};
    	
    	// North Alex Box Handle
    	OSCListener naHandler = new OSCListener() {
      		public void acceptMessage(java.util.Date time, OSCMessage message) {
        		System.out.println("naHandler called with address " + message.getAddress());
        
        		changeImage(2);
      		}
    	};
    	
    	// South Alex Box Handle
    	OSCListener saHandler = new OSCListener() {
      		public void acceptMessage(java.util.Date time, OSCMessage message) {
        		System.out.println("saHandler called with address " + message.getAddress());
        
        		changeImage(3);
      		}
    	};
    	
    	// South Stadium Handle
    	OSCListener ssHandler = new OSCListener() {
      		public void acceptMessage(java.util.Date time, OSCMessage message) {
        		System.out.println("ssHandler called with address " + message.getAddress());
        
        		changeImage(4);
      		}
    	};
    
    	// Kirby Smith fix Handle
    	OSCListener ksfHandler = new OSCListener() {
      		public void acceptMessage(java.util.Date time, OSCMessage message) {
        		System.out.println("ksfHandler called with address " + message.getAddress());
        
        		changeImage(501);
      		}
    	};
    	
    	// Kirby Smith Notify Maintenance Handle
    	OSCListener ksNMHandler = new OSCListener() {
      		public void acceptMessage(java.util.Date time, OSCMessage message) {
        		System.out.println("ksNMHandler called with address " + message.getAddress());
        
        		changeImage(700);
      		}
    	};
    	
    	// Kirby Smith Notify Patron Handle
    	OSCListener ksNPHandler = new OSCListener() {
      		public void acceptMessage(java.util.Date time, OSCMessage message) {
        		System.out.println("ksNPHandler called with address " + message.getAddress());
        
        		changeImage(800);
      		}
    	};
    	// I want handler1 to be called on addresses /a and /b and
    	// handler2 to be called on /c
    	// this.receiver.addListener("/a", handler1);
    	// this.receiver.addListener("/d", handler4);
		this.receiver.addListener("/initial", initialHandler);   // Starting message handle
		this.receiver.addListener("/kirbysmith", ksHandler);     // Kirby Smith broken message handle
		this.receiver.addListener("/northalex", naHandler);      // North Alex Box message handle
		this.receiver.addListener("/southalex", saHandler);      // South Alex Box message handle
		this.receiver.addListener("/southstad", ssHandler);      // South Stadium message handle
		this.receiver.addListener("/kirbysmithfix", ksfHandler); // Kirby Smith fix message handle
		this.receiver.addListener("/kirbysmithNM", ksNMHandler); // Kirby Smith notify maintenance message handle
		this.receiver.addListener("/kirbysmithNP", ksNPHandler); // Kirby Smith notify patrons message handle
    
    	System.out.println("Server is listening on port " + receiverPort + "...");
    	this.receiver.startListening();
	}
}