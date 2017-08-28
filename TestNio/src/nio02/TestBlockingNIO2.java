package nio02;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import org.junit.Test;

/**
 * 阻塞式NIO，如果不shutdown，服务端并不知道客户端发送信息已经结束
 * 会一直处于等待状态。所以程序就不会运行结束。
 * @author fulan
 *
 */
public class TestBlockingNIO2 {

	@Test
	public void Client() throws IOException{
		SocketChannel sChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1",9898));
		FileChannel inChannel = FileChannel.open(Paths.get("1.txt"),StandardOpenOption.READ);
		ByteBuffer buf = ByteBuffer.allocate(1024);
		while(inChannel.read(buf)!=-1){
			buf.flip();
			sChannel.write(buf);
			buf.clear();
		}
		
		//因为现在直接是阻塞模式了，所以会程序运行不完
		sChannel.shutdownOutput();
		
		//接收服务端的反馈
		int len=0;
		while((len=sChannel.read(buf))!=-1){
				buf.flip();
				System.out.println(new String(buf.array(),0,len));
				buf.clear();
		}	
		
		inChannel.close();
		sChannel.close();
	}
	
	
	@Test
	public void Server() throws IOException{
		ServerSocketChannel ssChannel = ServerSocketChannel.open();
		FileChannel outChannel = FileChannel.open(Paths.get("3.txt"), StandardOpenOption.WRITE,StandardOpenOption.READ,StandardOpenOption.CREATE);
		ssChannel.bind(new InetSocketAddress(9898));
		ByteBuffer buf = ByteBuffer.allocate(1024);
		SocketChannel sChannel = ssChannel.accept();
		while(sChannel.read(buf)!=-1){
			buf.flip();
			outChannel.write(buf);
			buf.clear();
		}
		
		String str="接收到数据了";
		buf.put(str.getBytes());
		buf.flip();
		sChannel.write(buf);
		
		sChannel.close();
		outChannel.close();
		ssChannel.close();
		
	}
}
