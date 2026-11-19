package com.acromere.zerra.event;

import com.acromere.event.EventHub;
import javafx.event.EventDispatchChain;
import lombok.Getter;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * An {@link EventHub} that dispatches {@link javafx.event.Event} events to
 * registered handlers.
 *
 * <p>Handlers are registered by JavaFX event type and are invoked for an event
 * of a matching type or any of its super types. If this hub has a parent, the
 * parent receives the event after the handlers of this hub have been invoked.</p>
 */
public class FxEventHub extends EventHub {

	/** The registered handlers keyed by JavaFX event type. */
	private final Map<javafx.event.EventType<? extends javafx.event.Event>, Collection<FxEventHandlerWrapper<?>>> fxHandlers;

	/**
	 * Create an event hub with no parent.
	 */
	public FxEventHub() {
		this.fxHandlers = new ConcurrentHashMap<>();
	}

	/**
	 * Set the parent event hub and return this hub for chaining.
	 *
	 * @param parent The parent event hub
	 * @return This hub
	 */
	public FxEventHub parent( EventHub parent ) {
		super.parent( parent );
		return this;
	}

	/**
	 * Dispatch a JavaFX event to all handlers of the event type and of all of its
	 * super types, then to the parent hub, if any.
	 *
	 * @param event The event to dispatch
	 * @return This hub
	 */
	public FxEventHub dispatch( javafx.event.Event event ) {
		// While the type of the incoming event is known, the parent event types
		// used later in the method are not well-known. They could be of any event
		// type, and therefore this variable needs to allow any event type.
		javafx.event.EventType<?> type = event.getEventType();

		// Go through all the handlers of the event type and all handlers of all
		// the parent event types, passing the event to each handler.
		while( type != null ) {
			fxHandlers.computeIfPresent( type, ( t, handlers ) -> {
				handlers.forEach( handler -> handler.dispatchEvent( event, null ) );
				return handlers;
			} );
			type = type.getSuperType();
		}

		// If there is a parent event hub, pass the event to it
		EventHub parent = super.getParent();
		if( parent instanceof FxEventHub ) ((FxEventHub)parent).dispatch( event );

		return this;
	}

	/**
	 * Register a handler for an event type and return this hub for chaining.
	 *
	 * @param type The event type to register the handler for
	 * @param handler The handler to register
	 * @return This hub
	 */
	public <T extends javafx.event.Event> FxEventHub register( javafx.event.EventType<? super T> type, javafx.event.EventHandler<? super T> handler ) {
		fxHandlers.computeIfAbsent( type, k -> new HashSet<>() ).add( new FxEventHandlerWrapper<>( handler ) );
		return this;
	}

	/**
	 * Unregister a handler for an event type and return this hub for chaining.
	 *
	 * @param type The event type to unregister the handler for
	 * @param handler The handler to unregister
	 * @return This hub
	 */
	public <T extends javafx.event.Event> FxEventHub unregister( javafx.event.EventType<? super T> type, javafx.event.EventHandler<? super T> handler ) {
		fxHandlers.computeIfPresent( type, ( t, c ) -> {
			c.removeIf( w -> w.getHandler() == handler );
			return c.isEmpty() ? null : c;
		} );
		return this;
	}

	/**
	 * A wrapper that adapts a JavaFX {@link javafx.event.EventHandler} to the
	 * {@link javafx.event.EventDispatcher} interface used by this hub.
	 *
	 * @param <T> The type of event handled
	 */
	@Getter
	private static class FxEventHandlerWrapper<T extends javafx.event.Event> implements javafx.event.EventDispatcher, javafx.event.EventHandler<T> {

		/** The wrapped handler. */
		private final javafx.event.EventHandler<T> handler;

		/**
		 * Wrap a JavaFX event handler.
		 *
		 * @param handler The handler to wrap
		 */
		public FxEventHandlerWrapper( javafx.event.EventHandler<T> handler ) {
			this.handler = handler;
		}

		@Override
		public void handle( T event ) {
			handler.handle( event );
		}

		@Override
		@SuppressWarnings( "unchecked" )
		public javafx.event.Event dispatchEvent( javafx.event.Event event, EventDispatchChain tail ) {
			handle( (T)event );
			return event;
		}

	}

}
