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
 * ����ʽNIO�������shutdown������˲���֪���ͻ��˷�����Ϣ�Ѿ�����
 * ��һֱ���ڵȴ�״̬�����Գ���Ͳ������н�����
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
		
		//��Ϊ����ֱ��������ģʽ�ˣ����Ի�������в���
		sChannel.shutdownOutput();
		
		//���շ���˵ķ���
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
		
		String str="���յ�������";
		buf.put(str.getBytes());
		buf.flip();
		sChannel.write(buf);
		
		sChannel.close();
		outChannel.close();
		ssChannel.close();
		
	}
}
