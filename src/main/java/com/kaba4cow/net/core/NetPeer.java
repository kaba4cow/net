package com.kaba4cow.net.core;

import java.io.IOException;
import java.net.SocketAddress;

/**
 * Represents a network peer capable of sending packets.
 */
public interface NetPeer {

	/**
	 * Sends the given packet to the peer.
	 * 
	 * @param packet the packet to be sent
	 * 
	 * @return a reference to this object
	 * 
	 * @throws IOException if an I/O error occurs while sending packet
	 */
	NetPeer send(NetPacket packet) throws IOException;

	/**
	 * Gets the address of the peer.
	 * 
	 * @return the {@link SocketAddress} of the peer
	 */
	SocketAddress getAddress();

}
