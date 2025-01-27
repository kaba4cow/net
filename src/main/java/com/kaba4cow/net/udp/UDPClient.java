package com.kaba4cow.net.udp;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.util.Iterator;

import com.kaba4cow.net.core.NetNode;
import com.kaba4cow.net.core.NetPacket;
import com.kaba4cow.net.core.NetPacketHandler;
import com.kaba4cow.net.core.NetPeer;
import com.kaba4cow.net.core.NetState;

/**
 * Represents a UDP client that connects to a server, sends data, and processes received packets. The client sends packets to a
 * specified server and processes responses from the server through packet reception callbacks.
 */
public abstract class UDPClient extends NetNode<DatagramChannel> implements NetPeer, NetPacketHandler {

	/**
	 * Constructs a UDPClient with the specified address and buffer size.
	 *
	 * @param address    the address of the server to connect to
	 * @param bufferSize the size of the buffer to allocate for reading and writing data
	 * 
	 * @throws IOException if an I/O error occurs during client initialization
	 */
	public UDPClient(SocketAddress address, int bufferSize) throws IOException {
		super(address, bufferSize);
	}

	@Override
	public UDPClient send(NetPacket packet) throws IOException {
		requireState(NetState.RUNNING);
		getChannel().send(packet.asByteBuffer(), getAddress());
		onPacketSent(packet);
		return this;
	}

	@Override
	protected DatagramChannel initializeChannel() throws IOException {
		DatagramChannel channel = DatagramChannel.open();
		channel.configureBlocking(false);
		channel.register(getSelector(), SelectionKey.OP_READ);
		return channel;
	}

	@Override
	protected void update() throws IOException {
		Iterator<SelectionKey> keys = getSelector().selectedKeys().iterator();
		while (keys.hasNext()) {
			SelectionKey key = keys.next();
			keys.remove();
			if (key.isReadable()) {
				getBuffer().clear();
				getChannel().receive(getBuffer());
				byte[] bytes = new byte[getBuffer().flip().remaining()];
				getBuffer().get(bytes);
				onPacketReceived(NetPacket.fromByteArray(bytes));
			}
		}
	}

	@Override
	public String toString() {
		return String.format("UDPClient [address=%s]", getAddress());
	}

}
