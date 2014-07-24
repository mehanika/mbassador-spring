package org.mbassy.spring;

import net.engio.mbassy.bus.common.PubSubSupport;
import net.engio.mbassy.listener.Listener;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.Assert;

/**
 * BeanPostProcessor that registers all classes annotated with {@link net.engio.mbassy.listener.Listener}
 * with the MessageBus attached to the post processor.  Override {@link #isMessageListener(Object, String)}
 * if you want to implement your own listener conditions.
 */
public class ListenerBeanPostProcessor implements BeanPostProcessor
{
	private PubSubSupport messageBus;

	public ListenerBeanPostProcessor() {
	}

	public ListenerBeanPostProcessor( PubSubSupport messageBus ) {
		setMessageBus( messageBus );
	}

	public void setMessageBus( PubSubSupport messageBus ) {
		Assert.notNull( messageBus );
		this.messageBus = messageBus;
	}

	@Override
	public Object postProcessBeforeInitialization( Object bean, String beanName ) throws BeansException {
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization( Object bean, String beanName ) throws BeansException {
		if ( isMessageListener( bean, beanName ) ) {
			messageBus.subscribe( bean );
		}
		return bean;
	}

	protected boolean isMessageListener( Object bean, String beanName ) {
		return bean != null && AnnotationUtils.findAnnotation( AopUtils.getTargetClass( bean ),
		                                                       Listener.class ) != null;
	}
}
