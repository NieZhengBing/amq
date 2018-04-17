package com.nzb.activemq.activemq;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

public class Sender {

	public static void main(String... args) throws JMSException {
		// 1 创建ConnectionFactory, 需要用户名， 密码，以及链接地址
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("nzb",
				"nzb", "tcp://0.0.0.0:61616");

		// 2 通过ConnectionFactory, 创建connection, 并开启connection
		Connection connection = connectionFactory.createConnection();
		connection.start();
		// 3通过Connection,创建session, transacted 是否启用事物，acknowledgeMode 签收模式
//		Session session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);
//		使用client签收的方式
		Session session = connection.createSession(true, Session.CLIENT_ACKNOWLEDGE);
		// 4 通过session,创建destination, p2p(点对点)模型中destination称为Queue,
		// Topic(发布订阅)模式中destination称为topic
		Destination destination = session.createQueue("queue1");
		// 5通过session创建消息的发送(producer)或接收对象(receiver)
//		MessageProducer messageProducer = session.createProducer(destination);
		MessageProducer messageProducer = session.createProducer(null);
		
		
		// 6通过messageProducer对象,设置消息是否持久化，以及持久化到1 文件， 2 KahaDB,LiveDB, 
		// JDBC(mysql或者其他的数据库) 4 Memory(内存，不属于持久化范畴)
//		messageProducer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

		// 创建数据，发送到MQ上面去
		// TextMessage message = session.createTextMessage("hello world");
		for (int i = 0; i < 5; i++) {
			TextMessage textMessage = session.createTextMessage();
			textMessage.setText("我是消息内容, id为" + i);
            System.out.println("我是生产者，生产的消息是: " + textMessage.getText());
//			messageProducer.send(textMessage);
//            第一个参数 目的地
//          第二个参数 
//          第三个参数 持久化
//          第四个参数 优先级(0-9, 0-4表示普通 5-9 表示加急消息)
//          第五个参数 表示消息在MQ上面保存的时间
            messageProducer.send(destination, textMessage, DeliveryMode.NON_PERSISTENT, i, 1000 * 60 * 2);
		}
//		使用事务提交
		session.commit();
		if (connection != null) {
			connection.close();
		}
	}
}
