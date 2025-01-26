# Net Library

A lightweight, flexible Java network library providing abstractions for UDP and TCP client-server communication using Java NIO channels.

## Features

- Support for both UDP and TCP network communication
- Non-blocking I/O using Java NIO Selector
- State management for network connections
- Extensible abstract base classes for clients and servers
- Packet reception callbacks
- Peer management for TCP connections

## Package Structure

- `com.kaba4cow.net.core`: Core interfaces and classes
- `com.kaba4cow.net.udp`: UDP client and server implementations
- `com.kaba4cow.net.tcp`: TCP client and server implementations

## Key Components

### Core Interfaces

- `NetPeer`: Represents a network peer capable of sending data
- `NetPacketReceiver`: Handles packet reception events
- `NetPeerPacketReceiver`: Handles peer-specific packet reception

### Network States

The library tracks network connection states:
- `NONE`: Initial state
- `RUNNING`: Active connection
- `CLOSING`: Closing in progress
- `CLOSED`: Connection terminated

## Usage

### TCP

Server:

```java
public class ExampleTCPServer extends TCPServer {

	public ExampleTCPServer(SocketAddress address) throws IOException {
		super(address, 1024);
	}

	@Override
	protected void onOpen(TCPPeer connection) throws IOException {
		System.out.println("Peer open");
	}

	@Override
	protected void onClosing(TCPPeer connection) throws IOException {
		System.out.println("Peer closing...");
	}

	@Override
	protected void onClosed(TCPPeer connection) throws IOException {
		System.out.println("Peer closed");
	}

	@Override
	protected void onStarted() throws IOException {
		System.out.println("Server started");
	}

	@Override
	protected void onClosing() throws IOException {
		System.out.println("Server closing...");
		for (TCPPeer peer : getPeers())
			peer.close();
	}

	@Override
	protected void onClosed() throws IOException {
		System.out.println("Server closed");
	}

	@Override
	protected void onError(Exception exception) throws IOException {
		exception.printStackTrace();
	}

	@Override
	public void onPacketReceived(TCPPeer peer, byte[] bytes) throws IOException {
		System.out.printf("Received packet %s from %s\n", new String(bytes), peer);
		peer.send("Hello Client!");
	}

	public static void main(String[] args) throws IOException {
		new ExampleTCPServer(new InetSocketAddress(42069)).start();
	}

}
```

Client:

```java
public class ExampleTCPClient extends TCPClient {

	public ExampleTCPClient(SocketAddress address) throws IOException {
		super(address, 1024);
	}

	@Override
	protected void onStarted() throws IOException {
		System.out.println("Client started");
	}

	@Override
	protected void onConnected() throws IOException {
		System.out.println("Client connected");
		send("Hello Server!");
	}

	@Override
	protected void onClosing() throws IOException {
		System.out.println("Client closing...");
	}

	@Override
	protected void onClosed() throws IOException {
		System.out.println("Client closed");
	}

	@Override
	protected void onError(Exception exception) throws IOException {
		exception.printStackTrace();
	}

	@Override
	public void onPacketReceived(byte[] bytes) throws IOException {
		System.out.printf("Received packet %s\n", new String(bytes));
	}

	public static void main(String[] args) throws IOException {
		new ExampleTCPClient(new InetSocketAddress("localhost", 42069)).start();
	}

}
```

### UDP

Server:

```java
public class ExampleUDPServer extends UDPServer {

	public ExampleUDPServer(SocketAddress address) throws IOException {
		super(address, 1024);
	}

	@Override
	protected void onStarted() throws IOException {
		System.out.println("Server started");
	}

	@Override
	protected void onClosing() throws IOException {
		System.out.println("Server closing...");
	}

	@Override
	protected void onClosed() throws IOException {
		System.out.println("Server closed");
	}

	@Override
	protected void onError(Exception exception) throws IOException {
		exception.printStackTrace();
	}

	@Override
	public void onPacketReceived(UDPPeer peer, byte[] bytes) throws IOException {
		System.out.printf("Received packet %s from %s\n", new String(bytes), peer);
		peer.send("Hello Client!");
	}

	public static void main(String[] args) throws IOException {
		new ExampleUDPServer(new InetSocketAddress(42069)).start();
	}

}
```

Client:

```java
public class ExampleUDPClient extends UDPClient {

	public ExampleUDPClient(SocketAddress address) throws IOException {
		super(address, 1024);
	}

	@Override
	protected void onStarted() throws IOException {
		System.out.println("Client started");
		send("Hello Server!");
	}

	@Override
	protected void onClosing() throws IOException {
		System.out.println("Client closing...");
	}

	@Override
	protected void onClosed() throws IOException {
		System.out.println("Client closed");
	}

	@Override
	protected void onError(Exception exception) throws IOException {
		exception.printStackTrace();
	}

	@Override
	public void onPacketReceived(byte[] bytes) throws IOException {
		System.out.printf("Received packet %s\n", new String(bytes));
	}

	public static void main(String[] args) throws IOException {
		new ExampleUDPClient(new InetSocketAddress("localhost", 42069)).start();
	}

}
```