package me.newsong.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * Created by SinjinSong on 2017/3/25.
 */
public class BlockingIOTCPServer {
    private ServerSocketChannel serverSocketChannel;
    private final String FILE_PATH = "E:/uploads/";
    private int i = 0;
    private ByteBuffer buf;
    private final String RESPONSE_MSG = "服务器接收数据成功";
    public BlockingIOTCPServer() {
        try {
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.bind(new InetSocketAddress(9000));
            buf = ByteBuffer.allocate(1024);
            System.out.println("服务器启动");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void receive() {
        while (true) {
            try {
                //接受客户端发送的文件
                SocketChannel client = serverSocketChannel.accept();
                System.out.println("服务器连接客户端:"+client.toString());
                FileChannel fileChannel = FileChannel.open(Paths.get(FILE_PATH, i + ".jpeg"), StandardOpenOption.WRITE, StandardOpenOption.CREATE);
                while (client.read(buf) != -1) {
                    buf.flip();
                    fileChannel.write(buf);
                    buf.clear();
                }
                System.out.println("服务器写文件完毕");
                i++;
                //将响应信息发送给客户端
                buf.put(RESPONSE_MSG.getBytes());
                buf.flip();
                client.write(buf);
                System.out.println("服务器发送响应信息完毕");
                fileChannel.close();
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        BlockingIOTCPServer server = new BlockingIOTCPServer();
        server.receive();
    }
}
