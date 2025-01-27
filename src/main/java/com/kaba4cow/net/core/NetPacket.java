package com.kaba4cow.net.core;

import java.io.ByteArrayInputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.function.Function;

/**
 * Represents a network packet that encapsulates raw byte data for transmission.
 */
public class NetPacket {

	private final byte[] bytes;

	private NetPacket(byte[] bytes) {
		this.bytes = bytes;
	}

	/**
	 * Creates a {@link NetPacket} from a byte array.
	 *
	 * @param bytes the byte array containing the packet data
	 * 
	 * @return a new {@link NetPacket} instance containing the bytes
	 */
	public static NetPacket fromByteArray(byte[] bytes) {
		return new NetPacket(bytes);
	}

	/**
	 * Creates a {@link NetPacket} from a string using the default charset.
	 *
	 * @param string the string to convert into a packet
	 * 
	 * @return a new {@link NetPacket} instance containing the string bytes
	 */
	public static NetPacket fromString(String string) {
		return fromByteArray(string.getBytes());
	}

	/**
	 * Creates a {@link NetPacket} from a string using a specified charset.
	 *
	 * @param string  the string to convert into a packet
	 * @param charset the charset to use for encoding the string
	 * 
	 * @return a new {@link NetPacket} instance containing the encoded string bytes
	 */
	public static NetPacket fromString(String string, Charset charset) {
		return fromByteArray(string.getBytes(charset));
	}

	/**
	 * Transforms this packet using a provided mapping function.
	 *
	 * @param <T>    the type of the result
	 * @param mapper the function to transform the packet
	 * 
	 * @return the result of applying the function to this packet
	 */
	public <T> T map(Function<NetPacket, T> mapper) {
		return mapper.apply(this);
	}

	/**
	 * Returns the raw byte array of this packet.
	 *
	 * @return the packet byte array
	 */
	public byte[] asByteArray() {
		return bytes;
	}

	/**
	 * Wraps the packet byte array in a {@link ByteBuffer}.
	 *
	 * @return a {@link ByteBuffer} containing the packet data
	 */
	public ByteBuffer asByteBuffer() {
		return ByteBuffer.wrap(bytes);
	}

	/**
	 * Returns a {@link ByteArrayInputStream} that allows reading the packet data as a stream.
	 *
	 * @return a new {@link ByteArrayInputStream} containing the packet data
	 */
	public ByteArrayInputStream asInputStream() {
		return new ByteArrayInputStream(bytes);
	}

	/**
	 * Converts the packet byte array to a string using the default charset.
	 *
	 * @return the packet data as a string
	 */
	public String asString() {
		return new String(bytes);
	}

	/**
	 * Converts the packet byte array to a string using a specified charset.
	 *
	 * @param charset the charset to use for decoding
	 * 
	 * @return the packet data as a string
	 */
	public String asString(Charset charset) {
		return new String(bytes, charset);
	}

	/**
	 * Returns the length of the packet data in bytes.
	 *
	 * @return the number of bytes in the packet
	 */
	public int length() {
		return bytes.length;
	}

	@Override
	public String toString() {
		return "NetPacket ".concat(Arrays.toString(bytes));
	}

}
