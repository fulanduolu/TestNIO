package nio03;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Pipe;

import org.junit.Test;

public class TestPipe {


	@Test
	public void test01() throws IOException{
		//获取管道
		Pipe pipe = Pipe.open();
		
		ByteBuffer buf = ByteBuffer.allocate(1024);
		buf.put("通过单向管道发送数据".getBytes());
		//2.将缓冲区的数据写入管道
		Pipe.SinkChannel sinkChannel = pipe.sink();
		buf.flip();
		sinkChannel.write(buf);
		
		//3.读取缓冲区中的数据
		Pipe.SourceChannel sourceChannel = pipe.source();
		buf.flip();
		int len = sourceChannel.read(buf);
		System.out.println(new String(buf.array(),0,len));
		
		sourceChannel.close();
		sinkChannel.close();
		
	}
}
