package com.acromere.zerra.event;

import com.acromere.zerra.javafx.Fx;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import lombok.CustomLog;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeoutException;

/**
 * A {@link EventHandler} that captures the JavaFX events it receives and allows
 * a caller to wait for an event of a given type.
 *
 * <p>Events are captured in a queue as they are handled. Callers can then wait
 * for an event of a specific type, or for the next event, up to a configurable
 * timeout. This is primarily used in tests that need to observe and
 * synchronize on JavaFX events.</p>
 *
 * @param <T> The type of event handled
 */
@CustomLog
public class FxEventWatcher <T extends Event> implements EventHandler<T> {

	/** The default wait timeout, in milliseconds. */
	private static final long DEFAULT_WAIT_TIMEOUT = 2500;

	/** The captured events, in the order they were captured. */
	private final Queue<T> events = new ConcurrentLinkedQueue<>();

	/** The wait timeout, in milliseconds. */
	@Getter
	private final long timeout;

	/** Whether a message is printed to standard output when an event is captured. */
	@Getter
	@Setter
	private boolean printEventCapture;

	/**
	 * Create an event watcher with the default wait timeout.
	 */
	public FxEventWatcher() {
		this( DEFAULT_WAIT_TIMEOUT );
	}

	/**
	 * Create an event watcher with the specified wait timeout.
	 *
	 * @param timeout The wait timeout, in milliseconds
	 */
	public FxEventWatcher( long timeout ) {
		this.timeout = timeout;
	}

	/**
	 * Capture an event by adding it to the queue of captured events and waking
	 * any threads waiting for an event.
	 *
	 * @param event The event to capture
	 */
	@Override
	public synchronized void handle( T event ) {
		if( printEventCapture ) System.out.println( "Captured FX event: type=" + event.getEventType() );
		events.offer( event );
		notifyAll();
	}

	/**
	 * Get a snapshot of the captured events.
	 *
	 * @return A new list containing the captured events, in the order they were
	 *     captured
	 */
	public synchronized List<T> getEvents() {
		return new ArrayList<>( events );
	}

	/**
	 * Wait for an event of a specific type to occur, then wait for the JavaFX
	 * event loop to process any pending events.
	 *
	 * @param type The event type to wait for
	 * @throws InterruptedException If the wait is interrupted
	 * @throws TimeoutException If no matching event occurs before the timeout
	 */
	public void waitForEvent( EventType<? extends T> type ) throws InterruptedException, TimeoutException {
		waitForEvent( type, timeout );
		Fx.waitFor( timeout );
	}

	/**
	 * Wait for the next event of a specific type to occur, using the configured
	 * timeout.
	 *
	 * @param type The event type to wait for
	 * @throws InterruptedException If the wait is interrupted
	 * @throws TimeoutException If no matching event occurs before the timeout
	 */
	@SuppressWarnings( "unused" )
	public synchronized void waitForNextEvent( EventType<T> type ) throws InterruptedException, TimeoutException {
		waitForNextEvent( type, timeout );
	}

	/**
	 * Wait for an event of a specific type to occur. If the event has already
	 * occurred, this method will return immediately. If the event has not already
	 * occurred, then this method waits until the next event occurs, or the
	 * specified timeout, whichever comes first.
	 *
	 * @param type The event type to wait for
	 * @param timeout How long, in milliseconds, to wait for the event
	 * @throws InterruptedException If the timeout is exceeded
	 */
	public synchronized void waitForEvent( EventType<? extends T> type, long timeout ) throws InterruptedException, TimeoutException {
		if( timeout <= 0 ) return;

		String eventTypeName = type.getSuperType() + "." + type.getName();
		long expiration = System.currentTimeMillis() + timeout;
		boolean expired = expiration < System.currentTimeMillis();

		T event = null;
		while( !expired && (event = findNext( type )) == null ) {
			wait( Math.max( 0, expiration - System.currentTimeMillis() ) );

			// Wait if the expiration is still greater than the current time
			expired = expiration < System.currentTimeMillis();
		}

		if( expired ) throw new TimeoutException( "Timeout waiting for event " + eventTypeName );

		if( event.getClass().getSimpleName().equals( "ToolEvent")) System.out.println( "Received event=" + event );
	}

	/**
	 * Remove and return the next captured event of the specified type.
	 *
	 * @param type The event type to look for
	 * @return The next captured event of the type, or null if none is captured
	 */
	private synchronized T findNext( EventType<? extends T> type ) {
		T event;
		while( (event = events.poll()) != null ) {
			if( event.getEventType() == type ) return event;
		}
		return null;
	}

	/**
	 * Wait for the next event of a specific type to occur. This method always waits until the next event occurs, or the specified timeout, whichever comes first.
	 *
	 * @param type The event type to wait for
	 * @param timeout How long, in milliseconds, to wait for the event
	 * @throws InterruptedException If the timeout is exceeded
	 */
	@SuppressWarnings( "SameParameterValue" )
	private synchronized void waitForNextEvent( EventType<T> type, long timeout ) throws InterruptedException, TimeoutException {
		events.clear();
		waitForEvent( type, timeout );
	}

}
