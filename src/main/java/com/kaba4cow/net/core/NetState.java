package com.kaba4cow.net.core;

/**
 * Represents the possible states of a network connection in a {@link NetNode}. These states define the lifecycle of a
 * connection, including its initialization, active communication, closing, and closure.
 */
public enum NetState {

	/**
	 * Indicates that the network connection is in an undefined or initial state.
	 */
	NONE,

	/**
	 * Indicates that the network connection is currently active and running.
	 */
	RUNNING,

	/**
	 * Indicates that the network connection is in the process of closing.
	 */
	CLOSING,

	/**
	 * Indicates that the network connection has been closed.
	 */
	CLOSED;
}