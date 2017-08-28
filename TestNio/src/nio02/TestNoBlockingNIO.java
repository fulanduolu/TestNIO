package nio02;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.Iterator;

import org.junit.Test;

/**
 * 非阻塞式NIO
 * * 1.通道（Channel）：负责连接
 * 		java.nio.channels.Channel	
 * 			|--SelecctableChannel
 * 				|--SocketChannel
 * 				|--ServerSocketChannel
 * 				|--DatagramChannel
 * 
 * 				|--Pipe.SinkChannel
 * 				|--Pipe.SourceChannel
 * 2.缓冲区（Buffer）：负责数据的存取
 * 3.选择器（Selector）：是SelectableChannel的多路复用器。用于监控SelectableChannel的IO状况
 * 
 * @author fulan
 */
public class TestNoBlockingNIO {
	//客户端
	@Test
	public void client() throws IOException{
		//1.获取通道
		SocketChannel sChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1",9898));
		
		//2.切换成非阻塞模式
		sChannel.configureBlocking(false);
		
		//3.分配指定区域的缓冲区
		ByteBuffer buf = ByteBuffer.allocate(1024);
		
		//4.发送数据给服务端
		buf.put(new Date().toString().getBytes());
		buf.flip();
		sChannel.write(buf);
		buf.clear();
		sChannel.close();
	}
	//服务端
	@Test
	public void server() throws IOException{
		//1.获取的通道
		ServerSocketChannel ssChannel = ServerSocketChannel.open();
		//2.切换为非阻塞模式
		ssChannel.configureBlocking(false);
		//3.绑定连接
		ssChannel.bind(new InetSocketAddress(9898));
		
		//4.获取选择器
		Selector selector = Selector.open();
		//5.将通道注册到选择器上,并且选择键制定了“监听事件”（此处监听接收事件）
		ssChannel.register(selector, SelectionKey.OP_ACCEPT);
		//6.轮询式的获取选择器上的已经“准备就绪”的事件。
		while(selector.select()>0){
			//7.获取当前选择器中所有注册的“选择键(已就绪的监听事件)”
			Iterator<SelectionKey> it = selector.selectedKeys().iterator();
			//8.迭代获取准备就绪的事件
			while(it.hasNext()){
				SelectionKey sk = it.next();
				//9.判断具体是什么事件准备就绪
				if(sk.isAcceptable()){
					//10.若为接收就绪了,获取客户端的连接
					SocketChannel sChannel = ssChannel.accept();
					//11.继续把客户端的通道切换成非阻塞模式
					sChannel.configureBlocking(false);
					//12.将该通道注册到选择器上
					sChannel.register(selector, SelectionKey.OP_READ);
					
				}else if(sk.isReadable()){
					//13.获取当前选择器上的“读就绪状态的通道”
					SocketChannel sChannel =(SocketChannel) sk.channel();
					
					//14.读取数据
					ByteBuffer buf=ByteBuffer.allocate(1024);
					
					int len = 0;
					while((len=sChannel.read(buf))!=-1){
						buf.flip();
						System.out.println(new String(buf.array() , 0 , len));
						buf.clear();
					}
					
				}
				//15.取消选择键SelectKey
				it.remove();
			}
		}
		
		
	}
}
