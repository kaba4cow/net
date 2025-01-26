package com.kaba4cow.net.core;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

/**
 * Represents a network peer capable of sending data.
 */
public interface NetPeer {

	/**
	 * Sends the given byte array to the peer.
	 * 
	 * @param bytes the data to be sent
	 * 
	 * @return a reference to this object
	 * 
	 * @throws IOException if an I/O error occurs while sending data
	 */
	NetPeer send(byte[] bytes) throws IOException;

	/**
	 * Sends the data from the provided ByteBuffer to the peer.
	 * 
	 * @param buffer the buffer containing the data to be sent
	 * 
	 * @return a reference to this object
	 * 
	 * @throws IOException if an I/O error occurs while sending data
	 */
	default NetPeer send(ByteBuffer buffer) throws IOException {
		byte[] bytes = new byte[buffer.remaining()];
		buffer.get(bytes);
		return send(bytes);
	}

	/**
	 * Sends the given string to the peer, using the provided charset.
	 * 
	 * @param string  the string to be sent
	 * @param charset the charset to use for encoding the string
	 * 
	 * @return a reference to this object
	 * 
	 * @throws IOException if an I/O error occurs while sending data
	 */
	default NetPeer send(String string, Charset charset) throws IOException {
		return send(string.getBytes(charset));
	}

	/**
	 * Sends the given string to the peer, using the platform's default charset.
	 * 
	 * @param string the string to be sent
	 * 
	 * @return a reference to this object
	 * 
	 * @throws IOException if an I/O error occurs while sending data
	 */
	default NetPeer send(String string) throws IOException {
		return send(string.getBytes());
	}

	/**
	 * Gets the address of the peer.
	 * 
	 * @return the {@link SocketAddress} of the peer
	 */
	SocketAddress getAddress();

}
