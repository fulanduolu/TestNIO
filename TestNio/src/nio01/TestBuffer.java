package nio01;

import java.nio.ByteBuffer;

import org.junit.Test;

/**
 * 一。缓冲区(Buffer)：在NIO中负责数据的存取。缓冲本质就是数组。
 * 用于存储不同数据类型的数据
 * 根据数据类型的不同（*Boolean除外），提供了相应类型的缓冲区：
 * ByteBuffer,CharBuffer,ShortBuffer,IntBuffer,LongBuffer,FloatBuffer,DoubleBuffer
 * 
 * 上述缓冲区的管理方式几乎一致，都是通过allocate（）获取缓冲区
 * 
 * 
 * 二。缓冲区存取数据的核心方法：
 * put()：存入数据到缓冲区
 * get()：获取缓冲区的数据
 * 
 * 三。缓冲区的四个核心属性：
 * capacity：容量,表示缓冲区中最大存储数据的容量。一旦声明不能改变
 * limit：限界，表示缓冲区中可以操纵数据的大小。（limit 后面的数据我们不能进行读写操作）
 * position：位置。表示缓冲区中正在操纵数据的位置。
 * 
 * mark:用于记录当前position的位置。可以通过reset()恢复到mark的位置。
 * 0<=mark<=position<=limit<=capacity
 * 
 * 四。直接缓冲区与非直接缓冲区。
 * 非直接缓冲区：通过allocate()方法分配缓冲区，将缓冲区建立在JVM的内存中。
 * 直接缓冲区：通过allocateDirect()方法分配直接缓冲区，将缓冲区建立在物理内存中。（可以提高效率）
 * 
 * 
 * @author fulanduolu
 *
 */
public class TestBuffer {

	@Test
	public void test1(){
		//1.通过allocate方法分配一个指定大小的缓冲区
		ByteBuffer buf=ByteBuffer.allocate(1024);
		
		System.out.println("==========调用allocate方法之后");
		//查看当前缓冲区的位置
		System.out.println(buf.position());
		//查看缓冲区的限界
		System.out.println(buf.limit());
		//查看缓冲区的最大存储数量
		System.out.println(buf.capacity());
		
		//利用put()方法，存入数据到缓冲区中区。
		String str="fulan";
		buf.put(str.getBytes());
		System.out.println(buf.position());
		System.out.println(buf.limit());
		System.out.println(buf.capacity());
		
		//开启读数据模式
		buf.flip();
		System.out.println("=======读数据");
		System.out.println(buf.position());
		System.out.println(buf.limit());
		System.out.println(buf.capacity());
		
		//利用get方法读取缓冲区的数据
		byte[] bst=new byte[buf.limit()];
		buf.get(bst);
		System.out.println(new String(bst,0,bst.length));
		
		//读取完数据
		System.out.println("=======读取完数据");
		System.out.println(buf.position());
		System.out.println(buf.limit());
		System.out.println(buf.capacity());
		
		
		//rewind()会将position置0，可以重复读取数据。
		buf.rewind();
		System.out.println("=======rewind()");
		System.out.println(buf.position());
		System.out.println(buf.limit());
		System.out.println(buf.capacity());
	
		//clear():清空缓冲区,但是缓冲区里面的数据还是在的。只不过这些数据，处于"被遗忘"的状态
		buf.clear();
		System.out.println(buf.position());
		System.out.println(buf.limit());
		System.out.println(buf.capacity());
		
		System.out.println((char)buf.get());
	}
	
	@Test
	public void test2(){
		String str="leimi";
		ByteBuffer buf=ByteBuffer.allocate(1024);
		buf.put(str.getBytes());
		buf.flip();
		byte[] bst=new byte[buf.limit()];
		buf.get(bst,0,2);
		System.out.println(new String(bst,0,2));
		
		System.out.println(buf.position());
		
		//mark():标记
		buf.mark();
		buf.get(bst,2,2);
		System.out.println(new String(bst,2,2));
		System.out.println(buf.position());
		//恢复到标记的位置
		buf.reset();
		System.out.println(buf.position());
		
		System.out.println(buf.hasRemaining());
		//判断缓冲区中是否还有剩余的数据，如果有
		//获取缓冲区中可以操纵的数量。	
		if(buf.hasRemaining()){
			System.out.println(buf.remaining());
		}
	}
	
	@Test
	public void test3(){
		//分配直接缓冲区
		ByteBuffer buf=ByteBuffer.allocateDirect(1024);
		//判断是不是直接缓冲区
		System.out.println(buf.isDirect());
		
	}
}
