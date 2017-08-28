package nio01;

import java.nio.ByteBuffer;

import org.junit.Test;

/**
 * һ��������(Buffer)����NIO�и������ݵĴ�ȡ�����屾�ʾ������顣
 * ���ڴ洢��ͬ�������͵�����
 * �����������͵Ĳ�ͬ��*Boolean���⣩���ṩ����Ӧ���͵Ļ�������
 * ByteBuffer,CharBuffer,ShortBuffer,IntBuffer,LongBuffer,FloatBuffer,DoubleBuffer
 * 
 * �����������Ĺ���ʽ����һ�£�����ͨ��allocate������ȡ������
 * 
 * 
 * ������������ȡ���ݵĺ��ķ�����
 * put()���������ݵ�������
 * get()����ȡ������������
 * 
 * �������������ĸ��������ԣ�
 * capacity������,��ʾ�����������洢���ݵ�������һ���������ܸı�
 * limit���޽磬��ʾ�������п��Բ������ݵĴ�С����limit ������������ǲ��ܽ��ж�д������
 * position��λ�á���ʾ�����������ڲ������ݵ�λ�á�
 * 
 * mark:���ڼ�¼��ǰposition��λ�á�����ͨ��reset()�ָ���mark��λ�á�
 * 0<=mark<=position<=limit<=capacity
 * 
 * �ġ�ֱ�ӻ��������ֱ�ӻ�������
 * ��ֱ�ӻ�������ͨ��allocate()�������仺��������������������JVM���ڴ��С�
 * ֱ�ӻ�������ͨ��allocateDirect()��������ֱ�ӻ��������������������������ڴ��С����������Ч�ʣ�
 * 
 * 
 * @author fulanduolu
 *
 */
public class TestBuffer {

	@Test
	public void test1(){
		//1.ͨ��allocate��������һ��ָ����С�Ļ�����
		ByteBuffer buf=ByteBuffer.allocate(1024);
		
		System.out.println("==========����allocate����֮��");
		//�鿴��ǰ��������λ��
		System.out.println(buf.position());
		//�鿴���������޽�
		System.out.println(buf.limit());
		//�鿴�����������洢����
		System.out.println(buf.capacity());
		
		//����put()�������������ݵ�������������
		String str="fulan";
		buf.put(str.getBytes());
		System.out.println(buf.position());
		System.out.println(buf.limit());
		System.out.println(buf.capacity());
		
		//����������ģʽ
		buf.flip();
		System.out.println("=======������");
		System.out.println(buf.position());
		System.out.println(buf.limit());
		System.out.println(buf.capacity());
		
		//����get������ȡ������������
		byte[] bst=new byte[buf.limit()];
		buf.get(bst);
		System.out.println(new String(bst,0,bst.length));
		
		//��ȡ������
		System.out.println("=======��ȡ������");
		System.out.println(buf.position());
		System.out.println(buf.limit());
		System.out.println(buf.capacity());
		
		
		//rewind()�Ὣposition��0�������ظ���ȡ���ݡ�
		buf.rewind();
		System.out.println("=======rewind()");
		System.out.println(buf.position());
		System.out.println(buf.limit());
		System.out.println(buf.capacity());
	
		//clear():��ջ�����,���ǻ�������������ݻ����ڵġ�ֻ������Щ���ݣ�����"������"��״̬
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
		
		//mark():���
		buf.mark();
		buf.get(bst,2,2);
		System.out.println(new String(bst,2,2));
		System.out.println(buf.position());
		//�ָ�����ǵ�λ��
		buf.reset();
		System.out.println(buf.position());
		
		System.out.println(buf.hasRemaining());
		//�жϻ��������Ƿ���ʣ������ݣ������
		//��ȡ�������п��Բ��ݵ�������	
		if(buf.hasRemaining()){
			System.out.println(buf.remaining());
		}
	}
	
	@Test
	public void test3(){
		//����ֱ�ӻ�����
		ByteBuffer buf=ByteBuffer.allocateDirect(1024);
		//�ж��ǲ���ֱ�ӻ�����
		System.out.println(buf.isDirect());
		
	}
}
