# Asterisk-JMSConnector
Spring-Boot project that connects to an Asterisk IPBX through AMI protocol and publishes events on an ActiveMQ broker JMS topic


#What for? 
This project is a component of a global enterprise message driven architecture applications coupling telephony and IT.
This standalone app connects to distributed asterisks cluster (1 app instance running localy on each Asterisk server) to listen to event that it broadcast to ActiveMQ topic. 
Back there, there are others apps (web...) subscribing on this topic that handle the event and take actions: web popup alert to a user for incoming call, updates in CRM, update in billing... 
