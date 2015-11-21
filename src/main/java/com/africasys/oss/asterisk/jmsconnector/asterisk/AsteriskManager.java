package com.africasys.oss.asterisk.jmsconnector.asterisk;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.asteriskjava.manager.AuthenticationFailedException;
import org.asteriskjava.manager.ManagerConnection;
import org.asteriskjava.manager.ManagerConnectionFactory;
import org.asteriskjava.manager.ManagerEventListener;
import org.asteriskjava.manager.PingThread;
import org.asteriskjava.manager.TimeoutException;
import org.asteriskjava.manager.event.AgentLoginEvent;
import org.asteriskjava.manager.event.AgentLogoffEvent;
import org.asteriskjava.manager.event.BridgeEvent;
import org.asteriskjava.manager.event.ConnectEvent;
import org.asteriskjava.manager.event.DialEvent;
import org.asteriskjava.manager.event.DisconnectEvent;
import org.asteriskjava.manager.event.ExtensionStatusEvent;
import org.asteriskjava.manager.event.HoldEvent;
import org.asteriskjava.manager.event.ManagerEvent;
import org.asteriskjava.manager.event.NewAccountCodeEvent;
import org.asteriskjava.manager.event.PeerStatusEvent;
import org.asteriskjava.manager.event.PriEventEvent;
import org.asteriskjava.manager.event.QueueEntryEvent;
import org.asteriskjava.manager.event.QueueMemberAddedEvent;
import org.asteriskjava.manager.event.QueueMemberEvent;
import org.asteriskjava.manager.event.QueueMemberRemovedEvent;
import org.asteriskjava.manager.event.QueueMemberStatusEvent;
import org.asteriskjava.manager.event.RegistryEvent;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.africasys.oss.asterisk.jmsconnector.jms.MessagingConsumer;
import com.africasys.oss.asterisk.jmsconnector.jms.MessagingProducer;


/**
 * @author Zoumana TRAORE
 *
 */
@Component
public class AsteriskManager implements ManagerEventListener{

	private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(AsteriskManager.class);

	@Value("${ami.username}")
	private String asteriskUsername;
	
	@Value("${ami.password}")
	private String asteriskPassword;
	
	@Value("${ami.server}")
	private String asteriskHost;
	
	@Value("${cluster.backend.topic}")
	private String backendTopic;
	
	@Value("${cluster.asterisk.queue}")
	private String asteriskQueue;
	
	@Autowired
	private PingThread pingThread;

	@Autowired
	private MessagingConsumer consumer;
	
	@Autowired
	private MessagingProducer producer;

	private ManagerConnection managerConnection;

    public AsteriskManager(){
    }

    @PostConstruct
    public void connect() throws InterruptedException{

    	int retry = 10;
    	boolean connected = false;
    	while(retry >0 && !connected){
    		try{
            
    			ManagerConnectionFactory factory = new ManagerConnectionFactory(asteriskHost, asteriskUsername, asteriskPassword);
            	this.managerConnection = factory.createManagerConnection();
            	managerConnection.addEventListener(this);
            	pingThread.addConnection(managerConnection);
            	managerConnection.login();
    			connected = true;
    		}catch (IllegalStateException | IOException
						| AuthenticationFailedException | TimeoutException e) {
    				LOGGER.warn("connect error {}", ExceptionUtils.getFullStackTrace(e));
    				retry --;
			}
    		
		   Thread.sleep(20*1000);
		}
    	
    	if(!connected){
			LOGGER.error("App is not connected to it target asterisk after retries!");
    	}else{
    		while(true){
			   Thread.sleep(10*1000);
    		}
    	}
    }

    @PreDestroy
    public void shutdown(){
		if(managerConnection != null){
			try {
				managerConnection.logoff();
			} catch (Exception e) {
				LOGGER.warn("shutdown() error {}",e);
			}
		}
    }

	@Override
	public void onManagerEvent(ManagerEvent event) {
		LOGGER.debug("New event received {}", event);

		boolean shoudBePublished = false;
		
		//TODO varsetevent hangupevent newextenevent newchannelevent newstateevent
		
		if(event instanceof AgentLoginEvent ){
			shoudBePublished = true;
		}else if(event instanceof AgentLogoffEvent ){
			shoudBePublished = true;
		}else if(event instanceof DialEvent ){
			shoudBePublished = true;
		}else if(event instanceof BridgeEvent ){
			shoudBePublished = true;
		}else if(event instanceof ConnectEvent ){
			shoudBePublished = true;
		}else if(event instanceof DisconnectEvent ){
			shoudBePublished = true;
		}else if(event instanceof ExtensionStatusEvent ){
			shoudBePublished = true;
		}else if(event instanceof HoldEvent ){
			shoudBePublished = true;
		}else if(event instanceof NewAccountCodeEvent ){
			shoudBePublished = true;
		}else if(event instanceof PriEventEvent ){
			shoudBePublished = true;
		}else if(event instanceof QueueEntryEvent ){
			shoudBePublished = true;
		}else if(event instanceof QueueMemberEvent ){
			shoudBePublished = true;
		}else if(event instanceof QueueMemberAddedEvent ){
			shoudBePublished = true;
		}else if(event instanceof QueueMemberRemovedEvent ){
			shoudBePublished = true;
		}else if(event instanceof QueueMemberStatusEvent ){
			shoudBePublished = true;
		}else if(event instanceof RegistryEvent ){
			shoudBePublished = true;
		}else if(event instanceof PeerStatusEvent ){
			shoudBePublished = true;
		}
		
		if(shoudBePublished && producer.getSession() != null){
			try {
			AsteriskEvent asteriskEvent = new AsteriskEvent(producer.getSession());
			asteriskEvent.setPayload(event.toString());
			producer.publishMessage(asteriskEvent.getMapMessage(), backendTopic, false, 10*60*1000);
			} catch (Exception e) {
				LOGGER.error(ExceptionUtils.getFullStackTrace(e));
			}
		}
	}
}
