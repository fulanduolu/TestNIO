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
 * ������ʽNIO
 * * 1.ͨ����Channel������������
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
 */
public class TestNoBlockingNIO {
	//�ͻ���
	@Test
	public void client() throws IOException{
		//1.��ȡͨ��
		SocketChannel sChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1",9898));
		
		//2.�л��ɷ�����ģʽ
		sChannel.configureBlocking(false);
		
		//3.����ָ������Ļ�����
		ByteBuffer buf = ByteBuffer.allocate(1024);
		
		//4.�������ݸ������
		buf.put(new Date().toString().getBytes());
		buf.flip();
		sChannel.write(buf);
		buf.clear();
		sChannel.close();
	}
	//�����
	@Test
	public void server() throws IOException{
		//1.��ȡ��ͨ��
		ServerSocketChannel ssChannel = ServerSocketChannel.open();
		//2.�л�Ϊ������ģʽ
		ssChannel.configureBlocking(false);
		//3.������
		ssChannel.bind(new InetSocketAddress(9898));
		
		//4.��ȡѡ����
		Selector selector = Selector.open();
		//5.��ͨ��ע�ᵽѡ������,����ѡ����ƶ��ˡ������¼������˴����������¼���
		ssChannel.register(selector, SelectionKey.OP_ACCEPT);
		//6.��ѯʽ�Ļ�ȡѡ�����ϵ��Ѿ���׼�����������¼���
		while(selector.select()>0){
			//7.��ȡ��ǰѡ����������ע��ġ�ѡ���(�Ѿ����ļ����¼�)��
			Iterator<SelectionKey> it = selector.selectedKeys().iterator();
			//8.������ȡ׼���������¼�
			while(it.hasNext()){
				SelectionKey sk = it.next();
				//9.�жϾ�����ʲô�¼�׼������
				if(sk.isAcceptable()){
					//10.��Ϊ���վ�����,��ȡ�ͻ��˵�����
					SocketChannel sChannel = ssChannel.accept();
					//11.�����ѿͻ��˵�ͨ���л��ɷ�����ģʽ
					sChannel.configureBlocking(false);
					//12.����ͨ��ע�ᵽѡ������
					sChannel.register(selector, SelectionKey.OP_READ);
					
				}else if(sk.isReadable()){
					//13.��ȡ��ǰѡ�����ϵġ�������״̬��ͨ����
					SocketChannel sChannel =(SocketChannel) sk.channel();
					
					//14.��ȡ����
					ByteBuffer buf=ByteBuffer.allocate(1024);
					
					int len = 0;
					while((len=sChannel.read(buf))!=-1){
						buf.flip();
						System.out.println(new String(buf.array() , 0 , len));
						buf.clear();
					}
					
				}
				//15.ȡ��ѡ���SelectKey
				it.remove();
			}
		}
		
		
	}
}