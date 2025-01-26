package com.kaba4cow.net.core;

import java.io.IOException;

/**
 * A listener interface for receiving packets from a specific network peer.
 * 
 * @param <Peer> the type of {@link NetPeer} being handled
 */
public interface NetPeerPacketReceiver<Peer extends NetPeer> {

	/**
	 * Called when a packet is received from a specific peer.
	 * 
	 * @param peer  the peer from which the packet was received
	 * @param bytes the received data
	 * 
	 * @throws IOException if an I/O error occurs while processing the packet
	 */
	void onPacketReceived(Peer peer, byte[] bytes) throws IOException;

}
