package nio01;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.junit.Test;

/**
 * һ��ͨ��(Channel)������Դ�ڵ���Ŀ��ڵ�����ӡ���Java NIO�и��𻺳��������ݵĴ��䡣Channel������
 * ���洢�κ���ġ�����Channel��Ҫ��ϻ���������ɴ����������
 * 
 * ����ͨ������Ҫʵ����
 *  java.nio.Channels.Channel�ӿڣ�
 *  	FileChannel
 *  	SocketChannel
 *  	ServerSocketChannel
 *  	DatagramChannel
 * ������ȡͨ��
 * 1.Java���֧��ͨ�������ṩ��getChannel()����
 * 		����io������
 * 		FileInputStream/FileOutputStream
 * 		RandomAccessFile
 * 		����io������
 * 		Socket
 * 		ServerSocket
 * 		DatagramSocket
 * 2.��JDK 1.7�е�NIO.2��Ը���ͨ���ṩ��һ����̬����open()
 * 3.��JDK 1.7�е�NIO.2��Files �������newByteChannel()����
 * 
 * �ġ�ͨ��֮������ݴ���
 * transferFrom()
 * transferTo()
 * @author admin
 * 
 * �塣��ɢ(Scatter)�ھۼ�(Gather)
 * ��ɢ��ȡ(Scatter Reads)����ͨ���е����ݷ�ɢ�������������
 * �ۼ�д��(Gathering Writes):������������е����ݾۼ���ͨ����ȥ
 *
 *
 * �����ַ�����Charset
 * ���룺�ַ���-���ֽ�����
 * ���룺�ֽ�����-���ַ���
 */
public class TestChannel {

	/*
	 * ���ͨ���ĸ���(��ֱ�ӻ�����)
	 * */
	@Test
	public void test1() throws Exception{
		FileInputStream in = new FileInputStream("1.jpg");
		FileOutputStream out = new FileOutputStream("2.jpg");
		
		//1.��ȡͨ��
		FileChannel inChannel = in.getChannel();
		FileChannel outChannel = out.getChannel();
		
		//2.����ָ����С�Ļ�����
		ByteBuffer buf = ByteBuffer.allocate(1024);
		
		//3.��ͨ�������ݶ��뻺����
		while(inChannel.read(buf)!=-1){
				buf.flip();             //����������д����ģʽ�л�Ϊ������ģʽ
				//4.���������е�����д�뵽ͨ����
				outChannel.write(buf);
				buf.clear();    //��ջ�����				
		}
		
		outChannel.close();
		inChannel.close();
		in.close();
		out.close();
	}
	
	/**
	 * ʹ��ֱ�ӻ��������ͨ���ĸ���(�ڴ�ӳ���ļ�)
	 * @throws IOException 
	 */
	@Test
	public void test02() throws IOException{
		FileChannel inChannel = FileChannel.open(Paths.get("1.jpg"), StandardOpenOption.READ);
		FileChannel outChannel = FileChannel.open(Paths.get("3.jpg"), StandardOpenOption.WRITE,StandardOpenOption.READ,StandardOpenOption.CREATE_NEW);   //CREATE_NEW���ھͱ��������ھʹ���
		
		//�ڴ�ӳ���ļ��� ��ֱ�ӻ�����ģʽֻ��ByteBuffer֧�֣�
		MappedByteBuffer inMappedBuf = inChannel.map(MapMode.READ_ONLY, 0, inChannel.size());
		MappedByteBuffer outMappedBuf = outChannel.map(MapMode.READ_WRITE, 0, inChannel.size());
		
		//��Ϊ�������ڴ��У�����ֻ��Ҫ�Ի������������ݶ�д����
		byte[] dst = new byte[inMappedBuf.limit()];
		inMappedBuf.get(dst);
		outMappedBuf.put(dst);
		
		inChannel.close();
		outChannel.close();
	}
	
	
	/**
	 * ͨ��֮������ݴ���(ͨ��֮��ͨ��Ҳ���õ�ֱ�ӻ������ķ�ʽ��ִ��)
	 * @throws IOException 
	 */
	@Test
	public void test3() throws IOException{
		FileChannel inChannel = FileChannel.open(Paths.get("1.jpg"), StandardOpenOption.READ);
		FileChannel outChannel = FileChannel.open(Paths.get("4.jpg"), StandardOpenOption.WRITE,StandardOpenOption.READ,StandardOpenOption.CREATE_NEW);
		
		//��inChannelͨ����outChannel
		inChannel.transferTo(0, inChannel.size(), outChannel);
		inChannel.close();
		outChannel.close();
	}

	//��ɢ��ۼ�
	@Test
	public void test4() throws IOException{
		RandomAccessFile raf = new RandomAccessFile("1.txt.txt","rw");
		
		//1.��ȡͨ��
		FileChannel channel1 = raf.getChannel();
		
		//2.����ָ����С�Ļ�����
		ByteBuffer buf1 = ByteBuffer.allocate(10);
		ByteBuffer buf2 = ByteBuffer.allocate(1024);

		//3.��ɢ��ȡ
		ByteBuffer[] bufs = {buf1,buf2};
		channel1.read(bufs);
		
		//4.�����л�������дģʽ���ɶ�ģʽ
		for(ByteBuffer b:bufs){
			b.flip();
		}
		
		System.out.println(new String(bufs[0].array(),0,bufs[0].limit()));
		System.out.println("===================================");
		System.out.println(new String(bufs[1].array(),0,bufs[1].limit()));
		
		//4.�ۼ�д��
		RandomAccessFile raf2 = new RandomAccessFile("2.txt","rw");
		FileChannel channel2 = raf2.getChannel();
		
		channel2.write(bufs);
		
		channel2.close();
		channel1.close();
	}
	
	/**
	 * �ַ���
	 */
	@Test
	public void test5(){
		 Map<String,Charset> map=Charset.availableCharsets();
		 Set<Entry<String,Charset>> set = map.entrySet();
		 for(Entry<String,Charset> entry:set){
			 System.out.println(entry.getKey()+"--->"+entry.getValue());
		 }
	}
	
	/**
	 * �ַ���
	 * @throws CharacterCodingException 
	 */
	@Test
	public void test6() throws IOException{
		Charset cs1 = Charset.forName("GBK");
		//��ȡ�������������
		CharsetEncoder ce = cs1.newEncoder();   //�õ�������
		CharsetDecoder cd = cs1.newDecoder();   //�õ�������
		
		CharBuffer cbuf = CharBuffer.allocate(1024);
		cbuf.put("ܽ����¶߹��");
		cbuf.flip();
		
		//����
		ByteBuffer bBuf = ce.encode(cbuf);	
		for(int i= 0;i < 12;i++){
			System.out.println(bBuf.get());
		}
		//���н���
		bBuf.flip();
		CharBuffer buf = cd.decode(bBuf);
		System.out.println(buf.toString());

	}
}
