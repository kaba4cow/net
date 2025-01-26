package com.kaba4cow.net.core;

import java.io.IOException;

/**
 * A listener interface for receiving network packets.
 */
public interface NetPacketReceiver {

	/**
	 * Called when a packet is received.
	 * 
	 * @param bytes the received data
	 * 
	 * @throws IOException if an I/O error occurs while processing the packet
	 */
	void onPacketReceived(byte[] bytes) throws IOException;

}
