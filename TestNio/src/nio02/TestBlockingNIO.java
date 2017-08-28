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
 * һ��ʹ��NIO�������ͨ�ŵ���������
 * 1.ͨ����Channel������������
 * 		java.nio.channels.Channel	
 * 			|--SelecctableChannel
 * 				|--SocketChannel
 * 				|--ServerSocketChannel
 * 				|--DatagramChannel
 * 
 * 				|--Pipe.SinkChannel
 * 				|--Pipe.SourceChannel
 * 2.��������Buffer�����������ݵĴ�ȡ
 * 3.ѡ������Selector������SelectableChannel�Ķ�·�����������ڼ��SelectableChannel��IO״��
 * 
 * @author fulan
 *
 */
public class TestBlockingNIO {

	
	/**
	 * �ͻ���
	 * @throws Exception 
	 */
	@Test
	public void client() throws Exception{
		//1.��ȡͨ��
		SocketChannel sChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1",9898));
		
		FileChannel inChannel = FileChannel.open(Paths.get("1.jpg"), StandardOpenOption.READ);
		//2.����ָ����С�Ļ�����
		ByteBuffer buf = ByteBuffer.allocate(1024);
		
		//3.��ȡ�����ļ��������͵������ȥ
		while(inChannel.read(buf)!=-1){
			//�л�������Ϊ��ģʽ
			buf.flip();
			sChannel.write(buf);
			buf.clear();
		}
		
		//4.�ر�ͨ��
		inChannel.close();
		sChannel.close();
	}
	
	
	/**
	 * �����
	 * @throws Exception 
	 */
	@Test
	public void server() throws Exception{
		//1.��ȡͨ��
		ServerSocketChannel ssChannel = ServerSocketChannel.open();
		
		FileChannel outChannel = FileChannel.open(Paths.get("6.jpg"), StandardOpenOption.READ,StandardOpenOption.WRITE,StandardOpenOption.CREATE);
		//2.�����Ӷ˿ں�
		ssChannel.bind(new InetSocketAddress(9898));
		
		//3.��ȡ�ͻ��˵����ӵ�ͨ��
		SocketChannel sChannel = ssChannel.accept();
		
		//��ȡ�ͻ��˷��͵����ݣ������浽����
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
