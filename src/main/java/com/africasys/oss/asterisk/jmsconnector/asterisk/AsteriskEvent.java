/**
 * 
 */
package com.africasys.oss.asterisk.jmsconnector.asterisk;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Session;

/**
 * @author Zoumana TRAORE
 *
 */
public class AsteriskEvent {
	
	private static final String DATE_FORMAT = "yyyyMMdd-HHmmss";
	private static final String PAYLOAD = "payload";
	private static final String CALLER_ID = "callerId";
	private static final String CALLEE = "callee";
	private static final String CALL_DATE = "callDate";
	private static final String EVENT_TYPE = "eventType";
	private static final String CONTEXT = "context";
	private static final String EXTENSION = "extension";
		
	private MapMessage mapMessage;
	
	public AsteriskEvent(Session session) throws JMSException {
		mapMessage = session.createMapMessage();
	}
	
	public AsteriskEvent(String payload, Session session, String callerId, String callee, Date callDate) throws JMSException{
		if(session != null){
			mapMessage = session.createMapMessage();
			mapMessage.setJMSType(this.getClass().getName());
		}else{
			throw new JMSException("session is null");
		}
		setParameter(PAYLOAD, payload);
		setParameter(CALLER_ID, callerId);
		setParameter(CALLEE, callee);
		SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
		setParameter(CALL_DATE, dateFormat.format(callDate));
		setParameter(EVENT_TYPE, "");
		setParameter(CONTEXT, "");
		setParameter(EXTENSION, "");

	}
	
	public MapMessage getMapMessage() {
		return mapMessage;
	}
	
	public void setParameter(String key, String value) throws JMSException{
		mapMessage.setObjectProperty(key, value);
	}
	
	public String getValueForParamer(String key) throws JMSException{
		return (String) mapMessage.getObjectProperty(key);
	}
	
	/**
	 * 
	 * @param value
	 * @throws JMSException
	 */
	public void setCallerId(String value) throws JMSException{
		setParameter(CALLER_ID, value);
	}

	public void setPayload(String value) throws JMSException{
		setParameter(PAYLOAD, value);
	}
	
	/**
	 * 
	 * @param value
	 * @return
	 * @throws JMSException
	 */
	public String getCallerId() throws JMSException{
		return getValueForParamer(CALLER_ID);
	}

	public String getCallee() throws JMSException{
		return getValueForParamer(CALLEE);
	}
	
	public String getPayload() throws JMSException{
		return getValueForParamer(PAYLOAD);
	}
	
	public String getExtension() throws JMSException{
		return getValueForParamer(EXTENSION);
	}
	
	public String getContext() throws JMSException{
		return getValueForParamer(CONTEXT);
	}
	
	public void setEventTpe(String value) throws JMSException{
		setParameter(EVENT_TYPE, value);
	}
	
	public static AsteriskEvent objectBuilder(MapMessage mapMsg) throws JMSException{
		return new AsteriskEvent(mapMsg.getString(PAYLOAD), null, mapMsg.getString(CALLER_ID), mapMsg.getString(CALLEE), null);
	}
	
	
	
}
