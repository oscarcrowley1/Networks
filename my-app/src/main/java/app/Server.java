package app;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class Server extends Node {
	static final int DEFAULT_PORT = 50001; // Assigning port number

	static final int HEADER_LENGTH = 2; // giving header
	static final int TYPE_POS = 0;

	static final byte TYPE_STRING = 1;
	static final int LENGTH_POS = 1;

	static final byte TYPE_ACK = 2;

	Terminal terminal;

	Server(Terminal terminal, int port) {
		try {
			this.terminal = terminal;
			socket = new DatagramSocket(port);
			listener.go();
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}
	}

	/* Assuming the data sent is a string i.e if its a username and password */

	public synchronized void onReceipt(DatagramPacket packet) {
		try {
			String content;
			byte[] data;
			byte[] buffer;

			data = packet.getData();
			switch (data[TYPE_POS]) {
			case TYPE_STRING:
				buffer = new byte[data[LENGTH_POS]];
				System.arraycopy(data, HEADER_LENGTH, buffer, 0, buffer.length);
				content = new String(buffer);
				terminal.println("|" + content + "|");
				terminal.println("Length: " + content.length());

				data = new byte[HEADER_LENGTH];
				data[TYPE_POS] = TYPE_ACK;

				DatagramPacket response;
				response = new DatagramPacket(data, data.length);
				response.setSocketAddress(packet.getSocketAddress());
				socket.send(response);
				break;
			default:
				terminal.println("Unexpected packet" + packet.toString());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public synchronized void start() throws Exception {
		terminal.println("Waiting for contact");
		this.wait();
	}

	public static void main(String[] args) {
		try {
			Terminal terminal = new Terminal("Server");
			(new Server(terminal, DEFAULT_PORT)).start();
			terminal.println("Program completed");
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}
	}
}