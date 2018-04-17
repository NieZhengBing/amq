package com.nzb.activemq.activemq;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

public class Receiver {
	
	public static void main(String... args) throws JMSException {
		// 1 创建ConnectionFactory, 需要用户名， 密码，以及链接地址
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("nzb",
				"nzb", "tcp://0.0.0.0:61616");

		// 2 通过ConnectionFactory, 创建connection, 并开启connection
		Connection connection = connectionFactory.createConnection();
		connection.start();
		// 3通过Connection,创建session, transacted 是否启用事物，acknowledgeMode 签收模式
//		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
		// 4 通过session,创建destination, p2p(点对点)模型中destination称为Queue,
		// Topic(发布订阅)模式中destination称为topic
		Destination destination = session.createQueue("queue1");
		// 5通过session创建消息的发送(producer)或接收对象(receiver)
		MessageConsumer messageConsumer = session.createConsumer(destination);

		while(true) {
			TextMessage msg = (TextMessage) messageConsumer.receive();
			if (msg == null) {
				break;
			}
//			手工去签收消息,另起一线程(tcp)去通知我们的MQ服务，确认签收
			msg.acknowledge();
			System.out.println("消息: " + msg.toString());
			System.out.println("接收到的内容: " + msg.getText());
		}
		if (connection != null) {
			connection.close();
		}
	}

}
