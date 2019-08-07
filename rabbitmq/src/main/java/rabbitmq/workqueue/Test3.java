package rabbitmq.workqueue;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.Delivery;

public class Test3 {
	public static void main(String[] args) throws Exception {
		//连接工厂
		ConnectionFactory f = new ConnectionFactory();
		f.setHost("192.168.64.140");
		f.setUsername("admin");
		f.setPassword("admin");
		//建立连接
		Connection c = f.newConnection();
		//建立信道
		Channel ch = c.createChannel();
		//声明队列
		ch.queueDeclare("helloworld",false,false,false,null);
		System.out.println("等待接收数据");
		
		//收到消息后用来处理消息的回调对象
		DeliverCallback callback = new DeliverCallback() {
			@Override
			public void handle(String consumerTag, Delivery message) throws IOException {
				String msg = new String(message.getBody(), "UTF-8");
				System.out.println("收到: "+msg);
				for (int i = 0; i < msg.length(); i++) {
					if (msg.charAt(i)=='.') {
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
						}
					}
				}
				System.out.println("处理结束");
				//发送回执
				ch.basicAck(message.getEnvelope().getDeliveryTag(), false);
			}
		};
		
		//消费者取消时的回调对象
		CancelCallback cancel = new CancelCallback() {
			@Override
			public void handle(String consumerTag) throws IOException {
			}
		};
		
		//autoAck设置为false,则需要手动确认发送回执
		ch.basicConsume("helloworld", false, callback, cancel);
	}
}