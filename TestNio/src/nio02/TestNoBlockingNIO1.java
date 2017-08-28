package nio02;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

import org.junit.Test;
/**
 * 模拟使用NIO的非阻塞模式做一个群聊
 * @author admin
 *
 */
public class TestNoBlockingNIO1 {

	/**
	 * 客户端
	 * @throws IOException
	 */
	@Test
	public void client() throws IOException{
		SocketChannel sChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1",9898));
		sChannel.configureBlocking(false);
		//声明一个缓冲区
		ByteBuffer buf = ByteBuffer.allocate(1024);
		Scanner scan = new Scanner(System.in);
		while(scan.hasNext()){
			String str = scan.next();
			buf.put(str.getBytes());
			buf.flip();
			sChannel.write(buf);
			buf.clear();
		}
		sChannel.close();
	}
	
	/**
	 * 服务器
	 * @throws IOException 
	 */
	@Test
	public void server() throws IOException{
		ServerSocketChannel ssChannel = ServerSocketChannel.open();
		//将服务端设置成非阻塞模式
		ssChannel.configureBlocking(false);
		ByteBuffer buf = ByteBuffer.allocate(1024);
		ssChannel.bind(new InetSocketAddress(9898));
		//获取一个选择器
		Selector selector = Selector.open();
		//监听连接事件
		ssChannel.register(selector, SelectionKey.OP_ACCEPT);
		//使用while是现象多个连接
		while(selector.select()>0){
			 Iterator<SelectionKey> it = selector.selectedKeys().iterator();
			 while(it.hasNext()){
				 SelectionKey sk = it.next();
				 //此时取出来的键，判断该键是什么状态 
				 if(sk.isAcceptable()){
					 //连接准备就绪的键
					 SocketChannel sChannel = ssChannel.accept();
					 sChannel.configureBlocking(false);
					 sChannel.register(selector, SelectionKey.OP_READ);	
				 }else if(sk.isReadable()){
					 //读数据就绪的键
					 SocketChannel sChannel = (SocketChannel)sk.channel();
					 sChannel.configureBlocking(false);
					 int len=0;
					 while((len=sChannel.read(buf))>0){
						 //进行读数据
						 buf.flip();
						 System.out.println(new String(buf.array(),0,len));
						 buf.clear();
					 }
					 
				 }
				 //取消选择键
				 it.remove();
			 }
			
		}
		
	}
}
