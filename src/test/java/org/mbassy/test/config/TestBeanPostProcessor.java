package org.mbassy.test.config;

import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.bus.config.BusConfiguration;
import net.engio.mbassy.listener.Listener;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mbassy.spring.ListenerBeanPostProcessor;
import org.mbassy.spring.SpringSubscriptionManagerProvider;
import org.mbassy.test.listeners.MessageTrackingListener;
import org.mbassy.test.messages.ListenerTrackingMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestBeanPostProcessor.PostProcessingConfig.class)
public class TestBeanPostProcessor
{
	@Autowired
	private MBassador<ListenerTrackingMessage> messageBus;

	@Autowired
	private MessageTrackingListener messageTrackingListener;

	@Test
	public void listenerIsAutoRegistered() {
		ListenerTrackingMessage message = new ListenerTrackingMessage();
		messageTrackingListener.addExpectedMessage( message );

		messageBus.publish( message );
		messageTrackingListener.validate();
	}

	@Listener
	protected static class AnnotatedMessageTrackingListener extends MessageTrackingListener
	{
	}

	@Configuration
	protected static class PostProcessingConfig
	{
		@Bean
		public AnnotatedMessageTrackingListener messageTrackingListener() {
			return new AnnotatedMessageTrackingListener();
		}

		@Bean
		public MBassador<ListenerTrackingMessage> messageBus() {
			BusConfiguration busConfiguration = BusConfiguration.Default();
			busConfiguration.setSubscriptionManagerProvider( new SpringSubscriptionManagerProvider() );

			return new MBassador<ListenerTrackingMessage>( busConfiguration );
		}

		@Bean
		public ListenerBeanPostProcessor listenerBeanPostProcessor() {
			return new ListenerBeanPostProcessor( messageBus() );
		}
	}
}
