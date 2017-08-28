package nio03;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Pipe;

import org.junit.Test;

public class TestPipe {


	@Test
	public void test01() throws IOException{
		//��ȡ�ܵ�
		Pipe pipe = Pipe.open();
		
		ByteBuffer buf = ByteBuffer.allocate(1024);
		buf.put("ͨ������ܵ���������".getBytes());
		//2.��������������д��ܵ�
		Pipe.SinkChannel sinkChannel = pipe.sink();
		buf.flip();
		sinkChannel.write(buf);
		
		//3.��ȡ�������е�����
		Pipe.SourceChannel sourceChannel = pipe.source();
		buf.flip();
		int len = sourceChannel.read(buf);
		System.out.println(new String(buf.array(),0,len));
		
		sourceChannel.close();
		sinkChannel.close();
		
	}
}
