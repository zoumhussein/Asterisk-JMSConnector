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
	private MapMessage mapMessage;
	
	public AsteriskEvent(Session session) {
	}
	
	public AsteriskEvent(String payload, Session session, String callerId, String callee, Date callDate) throws JMSException{
		if(session != null){
			mapMessage = session.createMapMessage();
			mapMessage.setJMSType(this.getClass().getName());
		}else{
			throw new JMSException("session is null");
		}
		setParameter("payload", payload);
		setParameter("callerId", callerId);
		setParameter("callee", callee);
		SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
		setParameter("callDate", dateFormat.format(callDate));
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
		setParameter("callerId", value);
	}

	public void setPayload(String value) throws JMSException{
		setParameter("payload", value);
	}
	
	/**
	 * 
	 * @param value
	 * @return
	 * @throws JMSException
	 */
	public String getCallerId(String value) throws JMSException{
		return getValueForParamer("callerId");
	}
	
	
	
}
