package com.kaba4cow.net.core;

import java.io.IOException;

/**
 * A listener interface for handling network packets.
 */
public interface NetPacketHandler {

	/**
	 * Called when a packet is sent.
	 * 
	 * @param packet the sent packet
	 * 
	 * @throws IOException if an I/O error occurs while processing the packet
	 */
	void onPacketSent(NetPacket packet) throws IOException;

	/**
	 * Called when a packet is received.
	 * 
	 * @param packet the received packet
	 * 
	 * @throws IOException if an I/O error occurs while processing the packet
	 */
	void onPacketReceived(NetPacket packet) throws IOException;

}
