package com.kaba4cow.net.udp;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.util.Iterator;
import java.util.Objects;

import com.kaba4cow.net.core.NetNode;
import com.kaba4cow.net.core.NetPeer;
import com.kaba4cow.net.core.NetPeerPacketReceiver;
import com.kaba4cow.net.core.NetState;
import com.kaba4cow.net.udp.UDPServer.UDPPeer;

/**
 * Represents a UDP server that listens for incoming packets, processes received data, and handles communication with connected
 * peers. The server listens on a specified address and handles incoming UDP packets by processing them through packet reception
 * callbacks.
 */
public abstract class UDPServer extends NetNode<DatagramChannel> implements NetPeerPacketReceiver<UDPPeer> {

	/**
	 * Constructs a UDPServer with the specified address and buffer size.
	 *
	 * @param address    the address to bind the server to
	 * @param bufferSize the size of the buffer to allocate for reading and writing data
	 * 
	 * @throws IOException if an I/O error occurs during server initialization
	 */
	public UDPServer(SocketAddress address, int bufferSize) throws IOException {
		super(address, bufferSize);
	}

	@Override
	protected DatagramChannel initializeChannel() throws IOException {
		DatagramChannel channel = DatagramChannel.open();
		channel.configureBlocking(false);
		channel.bind(getAddress());
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
				SocketAddress address = getChannel().receive(getBuffer());
				byte[] bytes = new byte[getBuffer().flip().remaining()];
				getBuffer().get(bytes);
				onPacketReceived(new UDPPeer(address), bytes);
			}
		}
	}

	@Override
	public String toString() {
		return String.format("UDPServer [address=%s, state=%s]", getAddress(), getState());
	}

	/**
	 * Represents a peer connected to the UDP server, providing methods to send data to the peer.
	 */
	public final class UDPPeer implements NetPeer {

		private final SocketAddress address;

		/**
		 * Constructs a UDPPeer with the specified address.
		 *
		 * @param address the address of the peer
		 */
		public UDPPeer(SocketAddress address) {
			this.address = address;
		}

		@Override
		public UDPPeer send(byte[] bytes) throws IOException {
			requireState(NetState.RUNNING);
			getChannel().send(ByteBuffer.wrap(bytes), address);
			return this;
		}

		@Override
		public SocketAddress getAddress() {
			return address;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getEnclosingInstance().hashCode();
			result = prime * result + Objects.hash(address);
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			UDPPeer other = (UDPPeer) obj;
			if (!getEnclosingInstance().equals(other.getEnclosingInstance()))
				return false;
			return Objects.equals(address, other.address);
		}

		private UDPServer getEnclosingInstance() {
			return UDPServer.this;
		}

		@Override
		public String toString() {
			return address.toString();
		}

	}

}
