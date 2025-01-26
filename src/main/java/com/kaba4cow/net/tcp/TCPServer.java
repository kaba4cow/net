package com.kaba4cow.net.tcp;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

import com.kaba4cow.net.core.NetNode;
import com.kaba4cow.net.core.NetPeer;
import com.kaba4cow.net.core.NetPeerPacketReceiver;
import com.kaba4cow.net.core.NetState;
import com.kaba4cow.net.tcp.TCPServer.TCPPeer;

/**
 * Represents a TCP server that listens for incoming connections from clients, manages active client connections, and handles
 * packet reception. The server accepts new connections, reads data, and provides methods to send data to connected peers.
 */
public abstract class TCPServer extends NetNode<ServerSocketChannel> implements NetPeerPacketReceiver<TCPPeer> {

	private final Set<TCPPeer> peers;

	/**
	 * Constructs a TCPServer with the specified address and buffer size.
	 *
	 * @param address    the address to bind the server
	 * @param bufferSize the size of the buffer to allocate for reading and writing data
	 * 
	 * @throws IOException if an I/O error occurs while initializing the server
	 */
	public TCPServer(SocketAddress address, int bufferSize) throws IOException {
		super(address, bufferSize);
		this.peers = new HashSet<>();
	}

	/**
	 * Called when a new peer (client) opens a connection to the server. Subclasses should implement this method to handle the
	 * opening of a peer connection.
	 *
	 * @param peer the newly connected peer
	 * 
	 * @throws IOException if an error occurs during the connection handling
	 */
	protected abstract void onOpen(TCPPeer peer) throws IOException;

	/**
	 * Called when a peer is closing its connection. Subclasses should implement this method to handle the closing of a peer
	 * connection.
	 *
	 * @param peer the peer whose connection is being closed
	 * 
	 * @throws IOException if an error occurs during the closing process
	 */
	protected abstract void onClosing(TCPPeer peer) throws IOException;

	/**
	 * Called when a peer has completely closed its connection. Subclasses should implement this method to handle any final
	 * cleanup after a peer closes.
	 *
	 * @param peer the peer whose connection is fully closed
	 * 
	 * @throws IOException if an error occurs during the closing process
	 */
	protected abstract void onClosed(TCPPeer peer) throws IOException;

	@Override
	protected ServerSocketChannel initializeChannel() throws IOException {
		ServerSocketChannel channel = ServerSocketChannel.open();
		channel.bind(getAddress());
		channel.configureBlocking(false);
		channel.register(getSelector(), SelectionKey.OP_ACCEPT);
		return channel;
	}

	@Override
	protected void update() throws IOException {
		Iterator<SelectionKey> keys = getSelector().selectedKeys().iterator();
		while (keys.hasNext()) {
			SelectionKey key = keys.next();
			TCPPeer peer = (TCPPeer) key.attachment();
			keys.remove();
			try {
				if (key.isAcceptable()) {
					SocketChannel channel = getChannel().accept();
					if (Objects.nonNull(channel)) {
						channel.configureBlocking(false);
						peer = new TCPPeer(channel);
						channel.register(getSelector(), SelectionKey.OP_READ, peer);
						peers.add(peer);
					}
				} else if (key.isReadable()) {
					getBuffer().clear();
					if (peer.channel.read(getBuffer()) == -1) {
						key.cancel();
						peer.close();
						break;
					} else {
						byte[] bytes = new byte[getBuffer().flip().remaining()];
						getBuffer().get(bytes);
						onPacketReceived(peer, bytes);
					}
				}
			} catch (IOException exception) {
				key.cancel();
				if (Objects.nonNull(peer))
					peer.close();
			}
		}
	}

	/**
	 * Returns an unmodifiable set of all connected peers.
	 *
	 * @return the set of peers connected to the server
	 */
	public Set<TCPPeer> getPeers() {
		return Collections.unmodifiableSet(peers);
	}

	@Override
	public String toString() {
		return String.format("TCPServer [address=%s, state=%s, peers=%s]", getAddress(), getState(), peers);
	}

	/**
	 * Represents a single connected peer (client) of the TCP server.
	 */
	public final class TCPPeer implements NetPeer {

		private final SocketChannel channel;
		private final SocketAddress address;

		private TCPPeer(SocketChannel channel) throws IOException {
			this.channel = channel;
			this.address = channel.getRemoteAddress();
			onOpen(this);
		}

		@Override
		public TCPPeer send(byte[] bytes) throws IOException {
			requireState(NetState.RUNNING);
			channel.write(ByteBuffer.wrap(bytes));
			return this;
		}

		/**
		 * Closes the peer connection and removes it from the list of peers.
		 *
		 * @throws IOException if an error occurs while closing the connection
		 */
		public void close() throws IOException {
			onClosing(this);
			channel.close();
			peers.remove(this);
			onClosed(this);
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
			result = prime * result + Objects.hash(address, channel);
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
			TCPPeer other = (TCPPeer) obj;
			if (!getEnclosingInstance().equals(other.getEnclosingInstance()))
				return false;
			return Objects.equals(address, other.address) && Objects.equals(channel, other.channel);
		}

		private TCPServer getEnclosingInstance() {
			return TCPServer.this;
		}

		@Override
		public String toString() {
			return address.toString();
		}

	}

}
