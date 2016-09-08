import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import javax.swing.*;

public class guiClient implements ActionListener{

  JFrame frame;
  JPanel mainPanel, p1Main, p1, buttonPanel, p2, p3, buttonPanel2;;
  JLabel t1, t2, t3;
  static JTextField tf;
  JTextArea ta1, ta2;
  ButtonGroup group1;
  Box verticalBox;
  Boolean ackFin = false;
  JButton testMode,getMOTD, sendSyn, sendRequest, sendAckZero, sendAckOne;
  static String[] argz;
  CardLayout cdLayout = new CardLayout();
  
  byte[] sendData = new byte[17];
	byte[] ReceiveData = new byte[17];
	static DatagramSocket clientSocket = null;
	DatagramPacket clientSend = null;
	DatagramPacket clientReceive = null;
	static String getPort;
	static int portNumber;
	static InetAddress serverAddress = null;
	Boolean handshake = false;
	Boolean loopY = true;
	Boolean dataReceived = false;
	Boolean finSent = false;
	Boolean checkClose = false;
	String serverMsg = "";
	int seqNumberReceived = 0;
	boolean synSent = false;
	boolean synAckReceived = false;
	boolean requestSent = false;
	stringObj stringObj1 = new stringObj();
	String finalMsg = "";  

  public guiClient() {
    frame = new JFrame();
    frame.setLayout(new BorderLayout());
    
    mainPanel = new JPanel();
    mainPanel.setLayout(cdLayout);
    
    //1st panel
    p1Main = new JPanel();
    p1Main.setLayout(new BorderLayout());
    p1= new JPanel();
    
    t1 = new JLabel("<html><p>CPS-706: Assignment </p>"+
    				"<p>Section 4</p>"
    				+ "<p>Made by:  Jennahan Pathmanathan, 500521237</p>"
    				+ "<p>          Paveethan Ramaneeswaran, 500570079</p>"
    				+ "<p>          Jenis Jesuratnam, 500508892</p>"
    				+ "</html>", SwingConstants.CENTER);
    p1.add(t1);
    
    buttonPanel = new JPanel(new GridLayout(2,1));
    
    testMode = new JButton ("Test Mode");
    testMode.setActionCommand("testMode");
    testMode.addActionListener(this);
    
    getMOTD = new JButton("Get MOTD");
    getMOTD.setActionCommand("getMOTD");
    getMOTD.addActionListener(this);
    
    buttonPanel.add(getMOTD);
    buttonPanel.add(testMode);
    
    p1Main.add(p1, BorderLayout.CENTER);
    p1Main.add(buttonPanel, BorderLayout.SOUTH);
    mainPanel.add("1", p1Main);
    
    frame.add(mainPanel);
    
    //p2
    p2 = new JPanel();
    p2.setLayout(new BorderLayout());
    
    t2 = new JLabel ("Get Message Of the Day");
    p2.add(t2, BorderLayout.NORTH);
    
    tf = new JTextField(25);
    tf.setEditable(false);
    tf.setText("Waiting for Message");
    p2.add(tf, BorderLayout.CENTER);
    
    ta1 = new JTextArea(25,25);
    ta1.setEditable(false);
    p2.add(ta1, BorderLayout.SOUTH);
    
    mainPanel.add("2", p2);
    
    //p3
    p3 = new JPanel();
    p3.setLayout(new BorderLayout());
    
    buttonPanel2 = new JPanel(new GridLayout(4,1));
    
    sendSyn = new JButton("Send SYN");
    sendSyn.setActionCommand("sendSyn");
    sendSyn.addActionListener(this);
    
    sendRequest = new JButton("Send REQUEST");
    sendRequest.setActionCommand("sendRequest");
    sendRequest.addActionListener(this);
    
    sendAckZero = new JButton("Send ACK0");
    sendAckZero.setActionCommand("sendAckZero");
    sendAckZero.addActionListener(this);
    
    sendAckOne = new JButton("Send ACK1");
    sendAckOne.setActionCommand("sendAckOne");
    sendAckOne.addActionListener(this);
    
    buttonPanel2.add(sendSyn);
    buttonPanel2.add(sendRequest);
    buttonPanel2.add(sendAckZero);
    buttonPanel2.add(sendAckOne);
    
    p3.add(buttonPanel2, BorderLayout.CENTER);
    
    ta2 = new JTextArea(15,15);
    JScrollPane scroll = new JScrollPane (ta2, 
    		   JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
    
    
    p3.add(scroll, BorderLayout.SOUTH);
    
    mainPanel.add("3", p3);
    
    frame.setSize(550, 500); 

    frame.addWindowListener(new WindowAdapter() {
        public void windowClosing(WindowEvent evt) {
            System.exit(0);
        }
    });

    frame.setVisible(true);
    
  }
  public static void main(String[] args) {
	  if (args.length != 2) {
			System.err.println("Wrong arguments! Use as 'java server <port number> <hostName>'," + "where <port number> is a valid integer over 1024!");
			System.exit(1);
		}

		getPort = args[0];
		portNumber = Integer.parseInt(getPort);

		if ((portNumber < 1024) || (portNumber > 65535)) {
			System.err.println("Invalid Argument: Please enter a port number greater than 1024, less then 65535");
			System.exit(1);
		}

		try {
			serverAddress = InetAddress.getByName(args[1]);
		} catch (UnknownHostException e) {
			System.out.println("Unable to resolve host: " + e);
			System.exit(1);
		}
		try {
			clientSocket = new DatagramSocket();
		} catch (SocketException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
      new guiClient();
    }
  public void actionPerformed(ActionEvent ez) {
	  if (ez.getActionCommand ().equals ("getMOTD")) {
		  doCompleteMOTD();
		  cdLayout.show(mainPanel, "2");
	  }
	  if (ez.getActionCommand ().equals ("testMode")) {
		  cdLayout.show(mainPanel, "3");
	  }
	  if (ez.getActionCommand().equals ("sendSyn")) {
		  String sendSyn = stringObj1.getSyn();
			sendData = new byte[17];
			sendData = sendSyn.getBytes();
			clientSend = new DatagramPacket(sendData, sendData.length, serverAddress, portNumber);
			try {						
				clientSocket.send(clientSend);
				ta2.append("Syn sent.\n");
			} catch (IOException e) {
				e.printStackTrace();
			}
			getReceive();
	  }
	  if (ez.getActionCommand().equals ("sendRequest")) {
		  String sendSyn = stringObj1.getRequest();
			sendData = new byte[17];
			sendData = sendSyn.getBytes();
			clientSend = new DatagramPacket(sendData, sendData.length, serverAddress, portNumber);
			try {						
				clientSocket.send(clientSend);
				ta2.append("Request sent.\n");
			} catch (IOException e) {
				e.printStackTrace();
			}
			getReceive();
	  }
	  if (ez.getActionCommand().equals ("sendAckZero")) {
		  String sendSyn = "0ACK";
			sendData = new byte[17];
			sendData = sendSyn.getBytes();
			clientSend = new DatagramPacket(sendData, sendData.length, serverAddress, portNumber);
			try {						
				clientSocket.send(clientSend);
				ta2.append("ACK 0 sent.\n");
			} catch (IOException e) {
			}
			if (ackFin){
				sendAckZero.setEnabled(false);	
				JOptionPane.showMessageDialog(frame, "Server closed down after successful transmission. MOTD is: "+finalMsg);
			}
			else {
				getReceive();
			}
	  }
	  if (ez.getActionCommand().equals ("sendAckOne")) {
		  String sendSyn = "1ACK";
			sendData = new byte[17];
			sendData = sendSyn.getBytes();
			clientSend = new DatagramPacket(sendData, sendData.length, serverAddress, portNumber);
			try {						
				clientSocket.send(clientSend);
				ta2.append("ACK 1 sent.\n");
			} catch (IOException e) {
			}
			getReceive();
	  }
  }
  
  public void getReceive(){
	  ReceiveData = new byte[17];
	  clientReceive = new DatagramPacket(ReceiveData, ReceiveData.length);
		try {
			clientSocket.setSoTimeout(1000);
			try {
				clientSocket.receive(clientReceive);
				ReceiveData = new byte[17];
				ReceiveData = clientReceive.getData();
				serverMsg = new String(clientReceive.getData());
				serverMsg = serverMsg.trim();
				if (serverMsg.length() != 0){
					if(serverMsg.equals(stringObj1.checkFin())){
						sendSyn.setEnabled(false);
						sendRequest.setEnabled(false);
						sendAckZero.setEnabled(true);
						sendAckOne.setEnabled(false);
						ackFin = true;
						JOptionPane.showMessageDialog(frame, "Must acknolwedge fin!");
					}
					if (serverMsg.length() == 17) {
						dataReceived = true;
						String tempTxt = serverMsg.substring(1);
						finalMsg = finalMsg + tempTxt; 
					}
					ta2.append("Client recieved: "+serverMsg+"\n");
				}
			} catch (IOException e) {
			}
		} catch (SocketException e) {
		}
  }
  
  public void doCompleteMOTD(){
		while (loopY) {
			if (!handshake) {
				if (!synSent) {
					String sendSyn = stringObj1.getSyn();
					sendData = new byte[17];
					sendData = sendSyn.getBytes();
					clientSend = new DatagramPacket(sendData, sendData.length, serverAddress, portNumber);
					try {						
						clientSocket.send(clientSend);
						ta1.append("Syn sent.\n");
					} catch (IOException e) {
						e.printStackTrace();
					}
					if (synAckReceived) {
						synSent = true;
					}
				}
				if (!synAckReceived) {
					clientReceive = new DatagramPacket(ReceiveData, ReceiveData.length);
					try {
						clientSocket.receive(clientReceive);
					} catch (IOException e) {
						e.printStackTrace();
					}
					ReceiveData = new byte[17];
					ReceiveData = clientReceive.getData();
					serverMsg = new String(clientReceive.getData());
					serverMsg = serverMsg.trim();
					if (serverMsg.equals(stringObj1.getSynAck())) {
						synAckReceived = true;
						synSent = true;
						ta1.append("Synack recieved.\n");
					}
				}
				if (synAckReceived) {
					String sendRequst = stringObj1.getRequest();
					sendData = new byte[17];
					sendData = sendRequst.getBytes();
					clientSend = new DatagramPacket(sendData, sendData.length, serverAddress, portNumber);
					try {
						clientSocket.send(clientSend);
						ta1.append("Request sent.\n");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					requestSent = true;
				}
				if (requestSent) {
					handshake = true;
				}
			}
			while (handshake) {
				if ((!dataReceived) && (!finSent)) {
					ReceiveData = new byte[17];
					clientReceive = new DatagramPacket(ReceiveData, ReceiveData.length);
					try {
						clientSocket.receive(clientReceive);
					} catch (IOException e) {
						e.printStackTrace();
					}
					ReceiveData = clientReceive.getData();
					serverMsg = new String(clientReceive.getData());
					serverMsg = serverMsg.trim();
					String getSeqNumber = "" + serverMsg.charAt(0);
					seqNumberReceived = Integer.parseInt(getSeqNumber);

					ta1.append("Server sent: "+serverMsg.substring(1)+".\n");
					if (serverMsg.length() == 17) {
						dataReceived = true;
						String tempTxt = serverMsg.substring(1);
						finalMsg = finalMsg + tempTxt; 
					}
					serverMsg = serverMsg.substring(1);
					if (serverMsg.equals("FIN")) {
						dataReceived = false;
						finSent = true;
					}
				}
				if ((dataReceived) && (!finSent)) {
					ta1.append("Sequence Number " + seqNumberReceived+" received.\n");
					String sendMsg = "" + seqNumberReceived;

					sendData = new byte[17];
					String sendAck = "ACK";
					sendMsg = sendMsg + sendAck;
					sendData = sendMsg.getBytes();
					clientSend = new DatagramPacket(sendData, sendData.length, serverAddress, portNumber);
					try {
						clientSocket.send(clientSend);
						ta1.append("ACK with Sequence Number of " + seqNumberReceived+" sent.\n");
						
					} catch (IOException e) {
						e.printStackTrace();
					}
					dataReceived = false;
				}
				if (finSent) {
					String sendMsg = "" + seqNumberReceived;

					sendData = new byte[17];
					String sendAck = "0ACK";
					sendMsg = sendMsg + sendAck;
					sendData = sendAck.getBytes();
					clientSend = new DatagramPacket(sendData, sendData.length, serverAddress, portNumber);
					try {
						clientSocket.send(clientSend);
						ta1.append("ACK to FIN sent.\n");
					} catch (IOException e) {
						e.printStackTrace();
					}
					dataReceived = false;
					checkClose = clientSocket.isConnected();
				}
				if ((finSent) && (!checkClose)) {
					clientSocket.close();
					handshake = false;
					loopY = false;
					tf.setText("Today's message is "+finalMsg);
				}
			}
		}
	}
}