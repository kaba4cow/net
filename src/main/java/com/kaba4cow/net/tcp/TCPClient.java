package com.kaba4cow.net.tcp;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import com.kaba4cow.net.core.NetNode;
import com.kaba4cow.net.core.NetPacket;
import com.kaba4cow.net.core.NetPacketHandler;
import com.kaba4cow.net.core.NetPeer;
import com.kaba4cow.net.core.NetState;

/**
 * Represents a TCP client that connects to a TCP server, sends data, and handles incoming packets. The client can initiate a
 * connection to a server, manage its state, and process data received from the server.
 */
public abstract class TCPClient extends NetNode<SocketChannel> implements NetPeer, NetPacketHandler {

	/**
	 * Constructs a TCPClient with the specified address and buffer size.
	 *
	 * @param address    the address of the server to connect to
	 * @param bufferSize the size of the buffer to allocate for reading and writing data
	 * 
	 * @throws IOException if an I/O error occurs while initializing the client
	 */
	public TCPClient(SocketAddress address, int bufferSize) throws IOException {
		super(address, bufferSize);
	}

	/**
	 * Called when the client successfully connects to the server. Subclasses should implement this method to handle actions
	 * after the connection is established.
	 *
	 * @throws IOException if an error occurs while handling the connection
	 */
	protected abstract void onConnected() throws IOException;

	@Override
	public TCPClient send(NetPacket packet) throws IOException {
		requireState(NetState.RUNNING);
		getChannel().write(packet.asByteBuffer());
		onPacketSent(packet);
		return this;
	}

	@Override
	protected SocketChannel initializeChannel() throws IOException {
		SocketChannel channel = SocketChannel.open();
		channel.configureBlocking(false);
		channel.connect(getAddress());
		channel.register(getSelector(), SelectionKey.OP_CONNECT);
		return channel;
	}

	@Override
	protected void update() throws IOException {
		for (SelectionKey key : getSelector().selectedKeys())
			if (key.isConnectable()) {
				if (getChannel().finishConnect()) {
					getChannel().register(getSelector(), SelectionKey.OP_READ);
					onConnected();
				}
			} else if (key.isReadable())
				try {
					getBuffer().clear();
					int read = getChannel().read(getBuffer());
					if (read == -1) {
						close();
						return;
					} else {
						byte[] bytes = new byte[getBuffer().flip().remaining()];
						getBuffer().get(bytes);
						onPacketReceived(NetPacket.fromByteArray(bytes));
					}
				} catch (IOException exception) {
					close();
				} catch (Exception exception) {
					onError(exception);
				}
		getSelector().selectedKeys().clear();
	}

	@Override
	public String toString() {
		return String.format("TCPClient [address=%s, state=%s]", getAddress(), getState());
	}

}
