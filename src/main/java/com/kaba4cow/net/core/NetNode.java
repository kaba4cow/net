package com.kaba4cow.net.core;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.Selector;
import java.nio.channels.spi.AbstractSelectableChannel;

/**
 * Represents a network node that manages a network connection using a selectable channel. This class provides mechanisms to
 * start, stop, and handle the state of the connection.
 *
 * @param <Channel> the type of the selectable channel
 */
public abstract class NetNode<Channel extends AbstractSelectableChannel> {

	private final SocketAddress address;
	private final Selector selector;
	private final Channel channel;

	private final ByteBuffer buffer;

	private NetState state;

	/**
	 * Constructs a new NetNode with the specified address and buffer size.
	 * 
	 * @param address    the address of the network node
	 * @param bufferSize the size of the buffer to allocate for data transmission
	 * 
	 * @throws IOException if an I/O error occurs during channel or selector initialization
	 */
	public NetNode(SocketAddress address, int bufferSize) throws IOException {
		this.address = address;
		this.selector = Selector.open();
		this.channel = initializeChannel();
		this.buffer = ByteBuffer.allocate(bufferSize);
		this.state = NetState.NONE;
	}

	/**
	 * Called when the node has started and is ready for communication. Subclasses should implement this method to handle any
	 * necessary setup after starting.
	 * 
	 * @throws IOException if an error occurs during the start process
	 */
	protected abstract void onStarted() throws IOException;

	/**
	 * Called when the node is in the process of closing. Subclasses should implement this method to handle any necessary
	 * cleanup before closing.
	 * 
	 * @throws IOException if an error occurs during the closing process
	 */
	protected abstract void onClosing() throws IOException;

	/**
	 * Called when the node has completely closed. Subclasses should implement this method to handle any necessary cleanup after
	 * closure.
	 * 
	 * @throws IOException if an error occurs during the closure process
	 */
	protected abstract void onClosed() throws IOException;

	/**
	 * Called when an exception occurs during the operation of the node. Subclasses should implement this method to handle
	 * exceptions and perform necessary error handling.
	 * 
	 * @param exception the exception that occurred
	 * 
	 * @throws IOException if an error occurs while handling the exception
	 */
	protected abstract void onError(Exception exception) throws IOException;

	/**
	 * Initializes the channel for this node. Subclasses should implement this method to create the appropriate channel for
	 * communication.
	 * 
	 * @return the initialized channel
	 * 
	 * @throws IOException if an error occurs while initializing the channel
	 */
	protected abstract Channel initializeChannel() throws IOException;

	/**
	 * Updates the node’s state and handles I/O operations. Subclasses should implement this method to process data after each
	 * selector select event.
	 * 
	 * @throws IOException if an error occurs during the update process
	 */
	protected abstract void update() throws IOException;

	/**
	 * Starts the node, initiating the communication and event loop. The node will continuously process events until it is
	 * stopped or an error occurs.
	 * 
	 * @throws IOException if an error occurs while starting the node or during event processing
	 */
	public void start() throws IOException {
		if (state != NetState.CLOSING && state != NetState.CLOSED) {
			state = NetState.RUNNING;
			onStarted();
			while (state == NetState.RUNNING)
				try {
					selector.select();
					if (state != NetState.RUNNING)
						break;
					update();
				} catch (Exception exception) {
					onError(exception);
					break;
				}
			state = NetState.CLOSING;
			onClosing();
			channel.close();
			selector.close();
			state = NetState.CLOSED;
			onClosed();
		}
	}

	/**
	 * Closes the node, stopping any ongoing operations and closing the channel. The node will transition to the CLOSING state.
	 */
	public void close() {
		if (state == NetState.RUNNING) {
			state = NetState.CLOSING;
			selector.wakeup();
		}
	}

	/**
	 * Gets the address of the node.
	 * 
	 * @return the address of the node
	 */
	public SocketAddress getAddress() {
		return address;
	}

	/**
	 * Gets the selector associated with this node.
	 * 
	 * @return the selector for managing I/O events
	 */
	protected Selector getSelector() {
		return selector;
	}

	/**
	 * Gets the channel associated with this node.
	 * 
	 * @return the channel used for communication
	 */
	protected Channel getChannel() {
		return channel;
	}

	/**
	 * Gets the buffer used for data transmission.
	 * 
	 * @return the buffer used for storing data for transmission
	 */
	protected ByteBuffer getBuffer() {
		return buffer;
	}

	/**
	 * Gets the current state of the node.
	 * 
	 * @return the current state of the node
	 */
	public NetState getState() {
		return state;
	}

	/**
	 * Ensures that the node is in the specified state. If the node is not in the specified state, an IllegalStateException is
	 * thrown.
	 * 
	 * @param state the required state of the node
	 * 
	 * @throws IllegalStateException if the node is not in the specified state
	 */
	public void requireState(NetState state) {
		if (this.state != state)
			throw new IllegalStateException(String.format("%s != %s", this.state, state));
	}

}
