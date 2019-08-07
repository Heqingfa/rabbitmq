package rabbitmq.routing;

import java.util.Random;
import java.util.Scanner;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class Test1 {
	public static void main(String[] args) throws Exception {
		String[] a = {"warning", "info", "error"};
		
		ConnectionFactory f = new ConnectionFactory();
		f.setHost("192.168.132.131");
		f.setPort(5672);
		f.setUsername("admin");
		f.setPassword("admin");
		
		Connection c = f.newConnection();
		Channel ch = c.createChannel();
		
		//参数1: 交换机名
		//参数2: 交换机类型
		ch.exchangeDeclare("direct_logs", BuiltinExchangeType.DIRECT);
		
		while (true) {
			System.out.print("输入消息: ");
			String msg = new Scanner(System.in).nextLine();
			if ("exit".equals(msg)) {
				break;
			}
			
			//随机产生日志级别
			String level = a[new Random().nextInt(a.length)];
			
			//参数1: 交换机名
			//参数2: routingKey, 路由键,这里我们用日志级别,如"error","info","warning"
			//参数3: 其他配置属性
			//参数4: 发布的消息数据 
			ch.basicPublish("direct_logs", level, null, msg.getBytes());
			System.out.println("消息已发送: "+level+" - "+msg);
			
		}

		c.close();
	}
}
