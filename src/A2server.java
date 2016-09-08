import java.io.*;
import java.net.*;
import java.util.Calendar;

public class A2server {
	public static void main(String[] args) {

		String getPort;
		int portNumber;
		byte[] ReceiveData;
		byte[] sendData;
		boolean listeningForConnection = true;
		DatagramPacket serverReceive = null;
		DatagramPacket serverSend = null;
		InetAddress clientAddress = null;
		int clientPort = 0;
		Boolean handshake = false;
		Boolean messageSent = false;
		int start = 0;
		int end = 16;
		int sequenceNumber = 0;
		Boolean firstTime = true;
		int seqNumberReceived = 0;
		int i = 0;

		if (args.length != 1) {
			System.err.println("Wrong arguments! Use as 'java server <port number>'," + "where <port number> is a valid integer over 1024!");
			System.exit(1);
		}

		getPort = args[0];
		portNumber = Integer.parseInt(getPort);


		if ((portNumber < 1024) || (portNumber > 65535)) {																														
			System.err.println("Invalid Argument: Please enter a port number greater than 1024, less then 65535");
			System.exit(1);
		}

		ReceiveData = new byte[17];
		sendData = new byte[17];
		DatagramSocket serverSocket = null;
		try {
			serverSocket = new DatagramSocket(portNumber);
		} catch (SocketException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		boolean synReceived = false;
		boolean requestReceived = false;

		boolean ackReceived = false;
		boolean dataSent = false;
		boolean finAckReceived = false;
		stringObj stringObj1 = new stringObj();
		
		System.out.println("Server Running...");

		while (listeningForConnection) {
			if (!handshake) {
				if (!synReceived) {
					ReceiveData = new byte[17];
					serverReceive = new DatagramPacket(ReceiveData, ReceiveData.length);
					try {
						serverSocket.receive(serverReceive);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					ReceiveData = new byte[17];
					ReceiveData = serverReceive.getData();
					String serverMsg = new String(serverReceive.getData());
					serverMsg = serverMsg.trim();
					if (serverMsg.equals(stringObj1.getSyn())) {
						clientAddress = serverReceive.getAddress();
						clientPort = serverReceive.getPort();
						synReceived = true;
						System.out.println("Syn recieved.");
					}
				}
				if (synReceived) {
					String sendSynAck = stringObj1.getSynAck();
					sendData = new byte[17];
					sendData = sendSynAck.getBytes();
					serverSend = new DatagramPacket(sendData, sendData.length, clientAddress, clientPort);
					try {
						serverSocket.send(serverSend);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if (requestReceived) {
						System.out.println("Synack sent.");

					}
				}
				if (!requestReceived && synReceived) {
					serverReceive = new DatagramPacket(ReceiveData, ReceiveData.length);
					try {
						serverSocket.receive(serverReceive);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					ReceiveData = new byte[17];
					ReceiveData = serverReceive.getData();
					String serverMsg = new String(serverReceive.getData());
					serverMsg = serverMsg.trim();

					if (serverMsg.equals(stringObj1.getRequest())) {
						clientAddress = serverReceive.getAddress();
						clientPort = serverReceive.getPort();
						requestReceived = true;
						System.out.println("Request recieved.");
					}
				}
				if (requestReceived && synReceived) {
					handshake = true;
				}
			}
			while (handshake) {
				Calendar cal = Calendar.getInstance();
				int day = cal.get(Calendar.DAY_OF_WEEK);

				dataObj dataObj1 = new dataObj();
				String dataMsg = dataObj1.getMOTD(day);

				int dataLength = dataMsg.length();
				dataLength = dataLength / 16;

				DatagramPacket dataPacketArray[] = new DatagramPacket[dataLength];
				if (!dataSent) {
					if (!messageSent) {
						while (i < dataLength) {
							if (i == (dataLength - 1)) {
								messageSent = true;
							}
							String sendMsg = "";
							if (firstTime) {
								sendMsg = "" + sequenceNumber;
								firstTime = false;
							} else {
								if (sequenceNumber == 0) {
									sequenceNumber = 1;
								} else if (sequenceNumber == 1) {
									sequenceNumber = 0;
								}
								sendMsg = "" + sequenceNumber;
							}
							String newDataMsg = dataMsg.substring(start, end);
							sendMsg = sendMsg + newDataMsg;
							start = end;
							end = end + 16;

							sendData = new byte[17];
							sendData = sendMsg.getBytes();
							System.out.println(sendMsg);
							dataPacketArray[i] = new DatagramPacket(sendData, sendData.length, clientAddress, clientPort);
							try {
								serverSocket.send(dataPacketArray[i]);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							ackReceived = false;

							while (!ackReceived) {
								if (ackReceived) {
									dataSent = true;
									System.out.println("Data sent.");
								}
								ReceiveData = new byte[17];
								serverReceive = new DatagramPacket(ReceiveData, ReceiveData.length);
								try {
									serverSocket.receive(serverReceive);
								} catch (IOException e) {
									e.printStackTrace();
								}
								ReceiveData = serverReceive.getData();
								String serverMsg = new String(serverReceive.getData());
								String getSeqNumber = "" + serverMsg.charAt(0);
								System.out.println(getSeqNumber);
								Boolean tempT = true;
								try {
									seqNumberReceived = Integer.parseInt(getSeqNumber);
								} catch (NumberFormatException e) {
									tempT = false;

								}
								if (tempT) {
									System.out.println("Server Receiving: " + serverMsg);
									if (seqNumberReceived == sequenceNumber) {
										i++;
										serverMsg = serverMsg.trim();
										serverMsg = serverMsg.substring(1);
										if (serverMsg.equals("ACK")) {
											ackReceived = true;
											System.out.println("Sequence number: " + sequenceNumber);
											System.out.println("Ack received.");
											if (!messageSent) {
												dataSent = true;
											}
										}
									} 
									else {
										System.out.println("sequece number recieved: " + seqNumberReceived);
										System.out.println("Sequence numbers do not match up");
										System.out.println("Resending Data");
										try {
											serverSocket.send(dataPacketArray[i]);
										} catch (IOException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
									}
								}
							}
						}
					}
				}
				if (dataSent) {
					String finMsg = stringObj1.getFin();
					if (sequenceNumber == 0) {
						sequenceNumber = 1;
					} else if (sequenceNumber == 1) {
						sequenceNumber = 0;
					}
					String sendMsg = "" + sequenceNumber;
					sendMsg = sendMsg + finMsg;
					sendData = new byte[17];
					sendData = sendMsg.getBytes();
					serverSend = new DatagramPacket(sendData, sendData.length, clientAddress, clientPort);
					try {
						serverSocket.send(serverSend);
					} catch (IOException e) {
						e.printStackTrace();
					}
					if (finAckReceived) {
						System.out.println("Fin sent.");
					}
				}
				if (!finAckReceived) {
					ReceiveData = new byte[17];
					serverReceive = new DatagramPacket(ReceiveData, ReceiveData.length);
					try {
						serverSocket.receive(serverReceive);
					} catch (IOException e) {
						e.printStackTrace();
					}
					ReceiveData = serverReceive.getData();
					String serverMsg = new String(ReceiveData);
					serverMsg = serverMsg.trim();
					String getSeqNumber = "" + serverMsg.charAt(0);
					try {
						seqNumberReceived = Integer.parseInt(getSeqNumber);
					} catch (NumberFormatException e) {

					}
					serverMsg = serverMsg.trim();
					serverMsg = serverMsg.substring(1);
					System.out.println("Server sending:" + serverMsg);
					if (serverMsg.equals("ACK")) {
						listeningForConnection = false;
						serverSocket.close();
						System.out.println("Finack recieved.");
						System.exit(0);
					} else {
					System.out.println("Invalid sequence number.");
					System.out.println("Sending 'FIN' packet again.");
					try {
						serverSocket.send(serverSend);
					} catch (IOException e) {
						e.printStackTrace();
					}
					}
				}
			}
		}
	}
}