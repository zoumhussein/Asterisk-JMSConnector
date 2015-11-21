package com.africasys.oss.asterisk.jmsconnector.jms;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
 /**
  * @author Zoumana TRAORE
  *
  */
@Component
public class MessagingProducer {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(MessagingProducer.class);
    private static final String JMS_CLIENT_ID = "asterisk-connector-producer";
	private ActiveMQConnectionFactory connectionFactory;
	private Connection connection;
    private Session session;
    private boolean isConnected; 

	@Value("${broker.url}")
    private String brokerURL;

	@Value("${broker.login}")
	private String brokerLogin;

	@Value("${broker.pwd}")
	private String brokerPassword;

	@Value("${broker.jms.enabled:false}")
	private boolean jmsEnabled;
	
	public MessagingProducer(){
	}
	
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
				session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
				isConnected = true;
				LOGGER.debug("Messaging Producer successully initialized");
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
	
	/**
	 * publish a message with no TTL
	 * 
	 * @param msg
	 * @param destinationName
	 * @param isQueue
	 * @return
	 */
	public boolean publishMessage(MapMessage msg, String destinationName, boolean isQueue){
		return publishMessage(msg, destinationName, isQueue, 0);
	}
	
	/**
	 * publish a message given a Time To Leave (TTL)
	 * 
	 * @param msg
	 * @param destinationName
	 * @param isQueue
	 * @param timeToLive
	 * @return
	 */
	public boolean publishMessage(MapMessage msg, String destinationName, boolean isQueue, long timeToLive){
		Destination destination = null;
		javax.jms.MessageProducer producer = null;
		boolean success = false;
		if(session != null){
			try {
				if(isQueue){
					destination = session.createQueue(destinationName);
				}else{
					destination = session.createTopic(destinationName);
				}
				producer = session.createProducer(destination);
				producer.setDeliveryMode(DeliveryMode.PERSISTENT);
				if(timeToLive > 0){
					producer.setTimeToLive(timeToLive);
				}
				producer.send(msg);
				success = true;
				LOGGER.debug("Message sent! {}", msg);
				
			} catch (JMSException e) {
				LOGGER.error(ExceptionUtils.getFullStackTrace(e));
			}finally{
				if(producer != null){
					try {
						producer.close();
					} catch (JMSException e) {
					}
				}
			}
		}
		return success;
	}
	
	
	public boolean isConnected() {
		return isConnected;
	}
	
	public Session getSession() {
		return session;
	}
 }