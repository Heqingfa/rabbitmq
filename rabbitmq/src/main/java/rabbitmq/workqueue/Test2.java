package rabbitmq.workqueue;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.Delivery;

public class Test2 {
	public static void main(String[] args) throws Exception {
		ConnectionFactory f = new ConnectionFactory();
		f.setHost("192.168.132.131");
		f.setUsername("admin");
		f.setPassword("admin");
		Connection c = f.newConnection();
		Channel ch = c.createChannel();
		ch.queueDeclare("helloworld",false,false,false,null);
		System.out.println("等待接收数据");
		
		//收到消息后用来处理消息的回调对象
		DeliverCallback callback = new DeliverCallback() {
			@Override
			public void handle(String consumerTag, Delivery message) throws IOException {
				String msg = new String(message.getBody(), "UTF-8");
				System.out.println("收到: "+msg);

				//遍历字符串中的字符,每个点使进程暂停一秒
				for (int i = 0; i < msg.length(); i++) {
					if (msg.charAt(i)=='.') {
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
						}
					}
				}
				System.out.println("处理结束");
			}
		};
		
		//消费者取消时的回调对象
		CancelCallback cancel = new CancelCallback() {
			@Override
			public void handle(String consumerTag) throws IOException {
			}
		};
		
		ch.basicConsume("helloworld", true, callback, cancel);
	}
}