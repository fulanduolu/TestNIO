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
 * ģ��ʹ��NIO�ķ�����ģʽ��һ��Ⱥ��
 * @author admin
 *
 */
public class TestNoBlockingNIO1 {

	/**
	 * �ͻ���
	 * @throws IOException
	 */
	@Test
	public void client() throws IOException{
		SocketChannel sChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1",9898));
		sChannel.configureBlocking(false);
		//����һ��������
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
	 * ������
	 * @throws IOException 
	 */
	@Test
	public void server() throws IOException{
		ServerSocketChannel ssChannel = ServerSocketChannel.open();
		//����������óɷ�����ģʽ
		ssChannel.configureBlocking(false);
		ByteBuffer buf = ByteBuffer.allocate(1024);
		ssChannel.bind(new InetSocketAddress(9898));
		//��ȡһ��ѡ����
		Selector selector = Selector.open();
		//���������¼�
		ssChannel.register(selector, SelectionKey.OP_ACCEPT);
		//ʹ��while������������
		while(selector.select()>0){
			 Iterator<SelectionKey> it = selector.selectedKeys().iterator();
			 while(it.hasNext()){
				 SelectionKey sk = it.next();
				 //��ʱȡ�����ļ����жϸü���ʲô״̬ 
				 if(sk.isAcceptable()){
					 //����׼�������ļ�
					 SocketChannel sChannel = ssChannel.accept();
					 sChannel.configureBlocking(false);
					 sChannel.register(selector, SelectionKey.OP_READ);	
				 }else if(sk.isReadable()){
					 //�����ݾ����ļ�
					 SocketChannel sChannel = (SocketChannel)sk.channel();
					 sChannel.configureBlocking(false);
					 int len=0;
					 while((len=sChannel.read(buf))>0){
						 //���ж�����
						 buf.flip();
						 System.out.println(new String(buf.array(),0,len));
						 buf.clear();
					 }
					 
				 }
				 //ȡ��ѡ���
				 it.remove();
			 }
			
		}
		
	}
}
