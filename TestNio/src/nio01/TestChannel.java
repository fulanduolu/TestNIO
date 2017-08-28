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
 * 一。通道(Channel)：用于源节点与目标节点的连接。在Java NIO中负责缓冲区中数据的传输。Channel本身是
 * 不存储任何输的。所以Channel需要配合缓冲区来完成传输与操作。
 * 
 * 二。通道的主要实现类
 *  java.nio.Channels.Channel接口：
 *  	FileChannel
 *  	SocketChannel
 *  	ServerSocketChannel
 *  	DatagramChannel
 * 三。获取通道
 * 1.Java针对支持通道的类提供了getChannel()方法
 * 		本地io操作：
 * 		FileInputStream/FileOutputStream
 * 		RandomAccessFile
 * 		网络io操作：
 * 		Socket
 * 		ServerSocket
 * 		DatagramSocket
 * 2.在JDK 1.7中的NIO.2针对各个通道提供了一个静态方法open()
 * 3.在JDK 1.7中的NIO.2的Files 工具类的newByteChannel()方法
 * 
 * 四。通道之间的数据传输
 * transferFrom()
 * transferTo()
 * @author admin
 * 
 * 五。分散(Scatter)于聚集(Gather)
 * 分散读取(Scatter Reads)：将通道中的数据分散到多个缓冲区中
 * 聚集写入(Gathering Writes):将多个缓冲区中的数据聚集到通道中去
 *
 *
 * 六。字符集：Charset
 * 编码：字符串-》字节数组
 * 解码：字节数组-》字符串
 */
public class TestChannel {

	/*
	 * 完成通道的复制(非直接缓冲区)
	 * */
	@Test
	public void test1() throws Exception{
		FileInputStream in = new FileInputStream("1.jpg");
		FileOutputStream out = new FileOutputStream("2.jpg");
		
		//1.获取通道
		FileChannel inChannel = in.getChannel();
		FileChannel outChannel = out.getChannel();
		
		//2.分配指定大小的缓冲区
		ByteBuffer buf = ByteBuffer.allocate(1024);
		
		//3.将通道的数据读入缓冲区
		while(inChannel.read(buf)!=-1){
				buf.flip();             //将缓冲区的写数据模式切换为读数据模式
				//4.将缓冲区中的数据写入到通道中
				outChannel.write(buf);
				buf.clear();    //清空缓冲区				
		}
		
		outChannel.close();
		inChannel.close();
		in.close();
		out.close();
	}
	
	/**
	 * 使用直接缓冲区完成通道的复制(内存映射文件)
	 * @throws IOException 
	 */
	@Test
	public void test02() throws IOException{
		FileChannel inChannel = FileChannel.open(Paths.get("1.jpg"), StandardOpenOption.READ);
		FileChannel outChannel = FileChannel.open(Paths.get("3.jpg"), StandardOpenOption.WRITE,StandardOpenOption.READ,StandardOpenOption.CREATE_NEW);   //CREATE_NEW存在就报错，不存在就创建
		
		//内存映射文件。 （直接缓冲区模式只有ByteBuffer支持）
		MappedByteBuffer inMappedBuf = inChannel.map(MapMode.READ_ONLY, 0, inChannel.size());
		MappedByteBuffer outMappedBuf = outChannel.map(MapMode.READ_WRITE, 0, inChannel.size());
		
		//因为在物理内存中，所以只需要对缓冲区进行数据读写操作
		byte[] dst = new byte[inMappedBuf.limit()];
		inMappedBuf.get(dst);
		outMappedBuf.put(dst);
		
		inChannel.close();
		outChannel.close();
	}
	
	
	/**
	 * 通道之间的数据传输(通道之间通信也是用的直接缓冲区的方式来执行)
	 * @throws IOException 
	 */
	@Test
	public void test3() throws IOException{
		FileChannel inChannel = FileChannel.open(Paths.get("1.jpg"), StandardOpenOption.READ);
		FileChannel outChannel = FileChannel.open(Paths.get("4.jpg"), StandardOpenOption.WRITE,StandardOpenOption.READ,StandardOpenOption.CREATE_NEW);
		
		//从inChannel通道到outChannel
		inChannel.transferTo(0, inChannel.size(), outChannel);
		inChannel.close();
		outChannel.close();
	}

	//分散与聚集
	@Test
	public void test4() throws IOException{
		RandomAccessFile raf = new RandomAccessFile("1.txt.txt","rw");
		
		//1.获取通道
		FileChannel channel1 = raf.getChannel();
		
		//2.分配指定大小的缓冲区
		ByteBuffer buf1 = ByteBuffer.allocate(10);
		ByteBuffer buf2 = ByteBuffer.allocate(1024);

		//3.分散读取
		ByteBuffer[] bufs = {buf1,buf2};
		channel1.read(bufs);
		
		//4.将所有缓冲区从写模式换成读模式
		for(ByteBuffer b:bufs){
			b.flip();
		}
		
		System.out.println(new String(bufs[0].array(),0,bufs[0].limit()));
		System.out.println("===================================");
		System.out.println(new String(bufs[1].array(),0,bufs[1].limit()));
		
		//4.聚集写入
		RandomAccessFile raf2 = new RandomAccessFile("2.txt","rw");
		FileChannel channel2 = raf2.getChannel();
		
		channel2.write(bufs);
		
		channel2.close();
		channel1.close();
	}
	
	/**
	 * 字符集
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
	 * 字符集
	 * @throws CharacterCodingException 
	 */
	@Test
	public void test6() throws IOException{
		Charset cs1 = Charset.forName("GBK");
		//获取编码器与解码器
		CharsetEncoder ce = cs1.newEncoder();   //得到编码器
		CharsetDecoder cd = cs1.newDecoder();   //得到解码器
		
		CharBuffer cbuf = CharBuffer.allocate(1024);
		cbuf.put("芙兰朵露吖！");
		cbuf.flip();
		
		//编码
		ByteBuffer bBuf = ce.encode(cbuf);	
		for(int i= 0;i < 12;i++){
			System.out.println(bBuf.get());
		}
		//进行解码
		bBuf.flip();
		CharBuffer buf = cd.decode(bBuf);
		System.out.println(buf.toString());

	}
}
