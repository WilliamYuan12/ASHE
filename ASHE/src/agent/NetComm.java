package agent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.*;

class NetComm {
	
	NetComm(String[] args) throws Exception {
		sckt = new Socket(args[0], Integer.parseInt(args[1]));
		bs = new BufferedReader(new InputStreamReader(sckt.getInputStream()));
		ps = new PrintStream(sckt.getOutputStream());
		send("VERSION:2.0.0");
	}
	
	String receive() throws IOException {
		String msg = null;
		do 
			msg = bs.readLine();
		while (msg != null && (msg.charAt(0) == '#' || msg.charAt(0) == ';'));
		return msg;
	}
	
	void send(String msg) {
		ps.print(msg + "\r\n");
	}
	
	void close() throws Exception {
		ps.close();
		bs.close();
		sckt.close();
	}
	
	boolean connected() {
		return sckt.isConnected();
	}
	
	private Socket sckt;
	private BufferedReader bs;
	private PrintStream ps;
}
