package com.acromere.zerra.event;

import com.acromere.event.Event;
import com.acromere.event.EventType;
import com.acromere.util.JavaUtil;
import lombok.Getter;

/**
 * An {@link Event} that wraps a {@link javafx.event.Event}.
 *
 * <p>The wrapper adopts the source of the wrapped JavaFX event and the
 * {@link Event#ANY} event type, and exposes the wrapped event through a getter.
 * This allows a JavaFX event to be published through the Acromere event
 * system.</p>
 */
@Getter
public class FxEventWrapper extends Event {

	/** The wrapped JavaFX event. */
	private final javafx.event.Event fxEvent;

	/**
	 * Wrap a JavaFX event.
	 *
	 * @param fxEvent The JavaFX event to wrap
	 */
	public FxEventWrapper( javafx.event.Event fxEvent ) {
		super( fxEvent.getSource(), Event.ANY );
		this.fxEvent = fxEvent;
	}

	/**
	 * Return the event type, narrowed to the type parameter of this wrapper.
	 *
	 * @return The event type, cast to the wrapper type
	 */
	@SuppressWarnings( "unchecked" )
	@Override
	public EventType<? extends FxEventWrapper> getEventType() {
		return (EventType<? extends FxEventWrapper>)super.getEventType();
	}

	/**
	 * Return a string representation of this wrapper, prefixed by the class name
	 * and followed by the wrapped event.
	 *
	 * @return A string representation of this wrapper
	 */
	@Override
	public String toString() {
		return JavaUtil.getClassName( this ) + ": " + fxEvent;
	}

}
