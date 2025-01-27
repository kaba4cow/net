package com.kaba4cow.net.core;

import java.io.IOException;

/**
 * A listener interface for handling packets from a specific network peer.
 * 
 * @param <Peer> the type of {@link NetPeer} being handled
 */
public interface NetPeerPacketHandler<Peer extends NetPeer> {

	/**
	 * Called when a packet is sent from a specific peer.
	 * 
	 * @param peer   the peer to which the packet was sent
	 * @param packet the sent packet
	 * 
	 * @throws IOException if an I/O error occurs while processing the packet
	 */

	void onPacketSent(Peer peer, NetPacket packet) throws IOException;

	/**
	 * Called when a packet is received from a specific peer.
	 * 
	 * @param peer   the peer from which the packet was received
	 * @param packet the received packet
	 * 
	 * @throws IOException if an I/O error occurs while processing the packet
	 */
	void onPacketReceived(Peer peer, NetPacket packet) throws IOException;

}
