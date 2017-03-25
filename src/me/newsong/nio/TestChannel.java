package me.newsong.nio;

import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * Created by SinjinSong on 2017/3/25.
 */

public class TestChannel {
    //Channel本身不存储数据，需要配合缓冲区进行传输
    //FileChannel、SocketChannel、ServerSocketChannel(TCP)、DatagramChannel(UDP)
    //获取通道：
    //1、getChannel方法
    //1)本地IO：FileInputStream、FileOutputStream、RandomAccessFile：
    //2)网络IO：Socket、ServerSocket、DatagramSocket

    //2、NIO2针对各个通道提供了静态方法open
    //3、Files.newByteChannel

    @Test
    public void test1() throws IOException {
        FileInputStream fis = new FileInputStream(TestChannel.class.getClassLoader().getResource("1.jpeg").getFile());
        FileOutputStream fos = new FileOutputStream(TestChannel.class.getClassLoader().getResource("2.jpeg").getFile());
        //获取通道
        FileChannel inChannel = fis.getChannel();
        FileChannel outChannel = fos.getChannel();

        //分配缓冲区（非直接缓冲区）
        ByteBuffer buf = ByteBuffer.allocate(1024);
//        ByteBuffer buf = ByteBuffer.allocateDirect(1024);

        //将通道中的数据存入缓冲区，然后将缓冲区中的数据存入另一通道
        while (inChannel.read(buf) != -1) {
            //将缓冲区由写入模式切换为读取模式
            buf.flip();
            outChannel.write(buf);
            buf.clear();
        }
        inChannel.close();
        outChannel.close();
        fis.close();
        fos.close();
    }

    @Test
    public void test2() throws IOException {
        //获取通道
        //Paths的get方法可以传递可变参数，把路径拼出来
        FileChannel inChannel = FileChannel.open(Paths.get("E:", "/nio", "/1.jpeg"), StandardOpenOption.READ);
        //CREATE模式是文件不存在就创建，存在就覆盖
        //CREATE_NEW模式是文件不存在就创建，存在就抛异常
        //在这里加一个读模式是因为下面的MapMode是读写模式
        FileChannel outChannel = FileChannel.open(Paths.get("2.jpeg"), StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE);

        //分配缓冲区，等同于allocateDirect，是直接缓冲区，又称内存映射文件
        //直接缓冲区只支持byte数据类型
        //分配直接缓冲区之后，就不再需要Channel了，可以只使用ByteBuffer来读写；直接对缓冲区进行数据读写
        MappedByteBuffer inMappedBuf = inChannel.map(FileChannel.MapMode.READ_ONLY, 0, inChannel.size());
        MappedByteBuffer outMappedBuf = outChannel.map(FileChannel.MapMode.READ_WRITE, 0, inChannel.size());
//        ByteBuffer buf = ByteBuffer.allocateDirect(1024);

        //不需要通道了
        byte[] bytes = new byte[inMappedBuf.limit()];
        inMappedBuf.get(bytes);
        outMappedBuf.put(bytes);
        inChannel.close();
        outChannel.close();
    }

    @Test
    public void test3() throws IOException {
        FileChannel inChannel = FileChannel.open(Paths.get("E:", "/nio", "/1.jpeg"), StandardOpenOption.READ);
        FileChannel outChannel = FileChannel.open(Paths.get("2.jpeg"), StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE);
        //直接在通道之间进行数据传输，使用的也是直接缓冲区
        inChannel.transferTo(0,inChannel.size(),outChannel);
        inChannel.close();
        outChannel.close();
    }
    
    @Test
    public void test4() throws IOException {
        //文件大小是<1k
        FileChannel inChannel = FileChannel.open(Paths.get("E:/1.txt"),StandardOpenOption.READ);
        FileChannel outChannel = FileChannel.open(Paths.get("E:/2.txt"),StandardOpenOption.WRITE,StandardOpenOption.CREATE);
        
        ByteBuffer buf1 = ByteBuffer.allocate(100);
        ByteBuffer buf2 = ByteBuffer.allocate(1024);
        ByteBuffer [] bufs = {buf1,buf2};
        inChannel.read(bufs);
        for(ByteBuffer buf: bufs){
            buf.flip();
            System.out.println(buf.limit());
            //100
            //806
        }
        System.out.println("BUF1:");
        System.out.println(new String(buf1.array(),0,buf1.limit(),"GBK"));
        System.out.println("BUF2:");
        System.out.println(new String(buf2.array(),0,buf2.limit(),"GBK"));
        outChannel.write(bufs);
    }
}
