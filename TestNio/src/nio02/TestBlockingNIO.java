package nio02;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import org.junit.Test;

/**
 * 一。使用NIO完成网络通信的三个核心
 * 1.通道（Channel）：负责连接
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
 *
 */
public class TestBlockingNIO {

	
	/**
	 * 客户端
	 * @throws Exception 
	 */
	@Test
	public void client() throws Exception{
		//1.获取通道
		SocketChannel sChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1",9898));
		
		FileChannel inChannel = FileChannel.open(Paths.get("1.jpg"), StandardOpenOption.READ);
		//2.分配指定大小的缓冲区
		ByteBuffer buf = ByteBuffer.allocate(1024);
		
		//3.读取本地文件，并发送到服务端去
		while(inChannel.read(buf)!=-1){
			//切换缓冲区为读模式
			buf.flip();
			sChannel.write(buf);
			buf.clear();
		}
		
		//4.关闭通道
		inChannel.close();
		sChannel.close();
	}
	
	
	/**
	 * 服务端
	 * @throws Exception 
	 */
	@Test
	public void server() throws Exception{
		//1.获取通道
		ServerSocketChannel ssChannel = ServerSocketChannel.open();
		
		FileChannel outChannel = FileChannel.open(Paths.get("6.jpg"), StandardOpenOption.READ,StandardOpenOption.WRITE,StandardOpenOption.CREATE);
		//2.绑定连接端口号
		ssChannel.bind(new InetSocketAddress(9898));
		
		//3.获取客户端的连接的通道
		SocketChannel sChannel = ssChannel.accept();
		
		//读取客户端发送的数据，并保存到本地
		ByteBuffer buf = ByteBuffer.allocate(1024);	 
		
		while(sChannel.read(buf)!=-1){
			buf.flip();
			outChannel.write(buf);
			buf.clear();
		}
		outChannel.close();
		ssChannel.close();
		sChannel.close();
	}
}
