package me.newsong.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * Created by SinjinSong on 2017/3/25.
 * 将一张图片发送到服务器上
 */

public class BlockingIOTCPClient {
    private SocketChannel clientChannel;
    private ByteBuffer buf;

    public BlockingIOTCPClient() {
        try {
            clientChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 9000));
        } catch (IOException e) {
            e.printStackTrace();
        }

        buf = ByteBuffer.allocate(1024);

    }

    public void send(String fileName) {
        try {
            FileChannel fileChannel = FileChannel.open(Paths.get(fileName), StandardOpenOption.READ);
            while (fileChannel.read(buf) != -1) {
                buf.flip();
                clientChannel.write(buf);
                buf.clear();
            }
            System.out.println("客户端已发送文件" + fileName);
            fileChannel.close();
            //注意，这种情况下服务器仍在等待，因为不知道客户端是否传输完毕，客户端也处于阻塞状态，等待服务器的响应。
            //解决方法是客户端调用shutdownOutput方法，告诉服务器此次传输数据完毕，这样服务器才会停止阻塞，继续执行下面的代码。
            //另一种方式是使用非阻塞IO
            clientChannel.shutdownOutput();
            
            //发送完需接收服务器发送的已收到的回应
            //注意发送的字节未必正好是1024的整数倍，所以要保存一下读取到的字节数
            int len;
            System.out.println("客户端开始接收响应信息:");
            while ((len = clientChannel.read(buf)) != -1) {
                buf.flip();
                System.out.print(new String(buf.array(), 0, len));
                buf.clear();
            }
            clientChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        BlockingIOTCPClient client = new BlockingIOTCPClient();
        client.send("E:/1.jpeg");
    }
}
