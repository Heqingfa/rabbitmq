package rabbitmq.rpc;

import java.io.IOException;
import java.util.Random;
import java.util.Scanner;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.Delivery;
import com.rabbitmq.client.AMQP.BasicProperties;

public class RPCServer {
	public static void main(String[] args) throws Exception {
		ConnectionFactory f = new ConnectionFactory();
		f.setHost("192.168.132.131");
		f.setPort(5672);
		f.setUsername("admin");
		f.setPassword("admin");
		
		Connection c = f.newConnection();
		Channel ch = c.createChannel();
		/*
		 * 定义队列 rpc_queue, 将从它接收请求信息
		 * 
		 * 参数:
		 * 1. queue, 对列名
		 * 2. durable, 持久化
		 * 3. exclusive, 排他
		 * 4. autoDelete, 自动删除
		 * 5. arguments, 其他参数属性
		 */
		ch.queueDeclare("rpc_queue",false,false,false,null);
		ch.queuePurge("rpc_queue");//清除队列中的内容
		
		ch.basicQos(1);//一次只接收一条消息
		
		
		//收到请求消息后的回调对象
		DeliverCallback deliverCallback = new DeliverCallback() {
			@Override
			public void handle(String consumerTag, Delivery message) throws IOException {
				//处理收到的数据(要求第几个斐波那契数)
				String msg = new String(message.getBody(), "UTF-8");
				int n = Integer.parseInt(msg);
				//求出第n个斐波那契数
				int r = fbnq(n);
				String response = String.valueOf(r);
				
				//设置发回响应的id, 与请求id一致, 这样客户端可以把该响应与它的请求进行对应
				BasicProperties replyProps = new BasicProperties.Builder()
						.correlationId(message.getProperties().getCorrelationId())
						.build();
				/*
				 * 发送响应消息
				 * 1. 默认交换机
				 * 2. 由客户端指定的,用来传递响应消息的队列名
				 * 3. 参数(关联id)
				 * 4. 发回的响应消息
				 */
				ch.basicPublish("",message.getProperties().getReplyTo(), replyProps, response.getBytes("UTF-8"));
				//发送确认消息
				ch.basicAck(message.getEnvelope().getDeliveryTag(), false);
			}
		};
		
		//
		CancelCallback cancelCallback = new CancelCallback() {
			@Override
			public void handle(String consumerTag) throws IOException {
			}
		};
		
		//消费者开始接收消息, 等待从 rpc_queue接收请求消息, 不自动确认
		ch.basicConsume("rpc_queue", false, deliverCallback, cancelCallback);
	}

	protected static int fbnq(int n) {
		if(n == 1 || n == 2) return 1;
		
		return fbnq(n-1)+fbnq(n-2);
	}
}
