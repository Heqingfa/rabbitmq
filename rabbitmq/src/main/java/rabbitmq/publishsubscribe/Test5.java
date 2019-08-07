package rabbitmq.publishsubscribe;

import java.util.Scanner;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class Test5 {
	public static void main(String[] args) throws Exception {
		ConnectionFactory f = new ConnectionFactory();
		f.setHost("192.168.132.131");
		f.setPort(5672);
		f.setUsername("admin");
		f.setPassword("admin");
		
		Connection c = f.newConnection();
		Channel ch = c.createChannel();
		
		//定义名字为logs的交换机,交换机类型为fanout
		//这一步是必须的，因为禁止发布到不存在的交换。
		ch.exchangeDeclare("logs", "fanout");
		
		while (true) {
			System.out.print("输入消息: ");
			String msg = new Scanner(System.in).nextLine();
			if ("exit".equals(msg)) {
				break;
			}
			
			//第一个参数,向指定的交换机发送消息
			//第二个参数,不指定队列,由消费者向交换机绑定队列
			//如果还没有队列绑定到交换器，消息就会丢失，
			//但这对我们来说没有问题;即使没有消费者接收，我们也可以安全地丢弃这些信息。
			ch.basicPublish("logs", "", null, msg.getBytes("UTF-8"));
			System.out.println("消息已发送: "+msg);
		}

		c.close();
	}
}