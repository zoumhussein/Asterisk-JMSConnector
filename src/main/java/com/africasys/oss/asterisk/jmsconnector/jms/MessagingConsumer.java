package com.africasys.oss.asterisk.jmsconnector.jms;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
 /**
  * 
  * @author Zoumana TRAORE
  * This <b>IS NOT Singleton Spring bean </b>
  */
@Component
@Scope("prototype") //1 listener instance per user thread
public class MessagingConsumer implements MessageListener {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(MessagingConsumer.class);
    private static final String JMS_CLIENT_ID = "asterisk-connector";
	private ActiveMQConnectionFactory connectionFactory;
	private Connection connection;
    private Session session;
    
	@Value("${broker.url}")
    private String brokerURL;

	@Value("${broker.login}")
	private String brokerLogin;

	@Value("${broker.pwd}")
	private String brokerPassword;

	@Value("${broker.jms.enabled:false}")
	private boolean jmsEnabled;
	
	@Value("${cluster.asterisk.queue}")
	private String asteriskQueue;
	
	@PostConstruct
    public void init() {
		
		if(jmsEnabled){
			try {
				//insecure mode
				if(brokerLogin == null || brokerPassword == null){
					connectionFactory = new ActiveMQConnectionFactory(brokerURL);
				}else{
					connectionFactory = new ActiveMQConnectionFactory(brokerLogin, brokerPassword, brokerURL);
				}
				connectionFactory.setClientIDPrefix(JMS_CLIENT_ID);
				connection = connectionFactory.createConnection();
				connection.start();
				session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
				LOGGER.debug("Messaging Receiver successully initialized");

				createReceiver(asteriskQueue, true); //scalable design: each builder instance will subscribe to a queue in order to allow only 1 to take that action
				
			} catch (JMSException e) {
				LOGGER.error(ExceptionUtils.getFullStackTrace(e));
			}
		}
    }
	
	@PreDestroy
    public void closeAll() {
		if(session != null){
			try {
				session.close();
			} catch (JMSException e) {
				e.printStackTrace();
			}
		}
		
		if(connection != null){
			try {
				connection.close();
			} catch (JMSException e) {
				e.printStackTrace();
			}
		}
    }
	
	public MessageConsumer createReceiver(String destinationName, boolean isQueue){
		Destination destination = null;
		MessageConsumer consumer = null;
		if(session != null){
			try {
				if(isQueue){
					destination = session.createQueue(destinationName);
				}else{
					destination = session.createTopic(destinationName);
				}
				consumer = session.createConsumer(destination);
				consumer.setMessageListener(this);
				LOGGER.debug("Receiver created!");
			} catch (JMSException e) {
				LOGGER.error(ExceptionUtils.getFullStackTrace(e));
			}
		}
		return consumer;
	}

	@Override
	public void onMessage(Message message) {
		try {
			message.acknowledge();
			LOGGER.debug("Message received of type {} {}", message.getClass(), message);
			
			if(message instanceof MapMessage){
				LOGGER.debug("Map Message received: {}", message);

				//TODO implement business logic here 
			}
			
		} catch (JMSException e) {
			LOGGER.error(ExceptionUtils.getFullStackTrace(e));
		}
	}
 
 }