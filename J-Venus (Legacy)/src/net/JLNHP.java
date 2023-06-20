package net;

/**
 * JavaDOS local network handshake protocol
 *
 */
public class JLNHP {
	String targetIP = "";
	public JLNHP(String targetIP) {
		this.targetIP = targetIP;
	}
	
	public String getTargetIP() {
		return targetIP;
	}
}
