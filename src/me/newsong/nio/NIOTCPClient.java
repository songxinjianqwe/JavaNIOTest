package me.newsong.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.*;

/**
 * Created by SinjinSong on 2017/3/25.
 * 将一张图片发送到服务器上
 */

public class NIOTCPClient {
    private SocketChannel clientChannel;
    private ByteBuffer buf;

    public NIOTCPClient() {
        try {
            clientChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 9000));
            //设置客户端为非阻塞模式
            clientChannel.configureBlocking(false);
            buf = ByteBuffer.allocate(1024);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
            clientChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ExecutorService pool = new ThreadPoolExecutor(50, 100, 1000, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<>());
        Instant begin = Instant.now();
        for (int i = 0; i < 200; i++) {
            pool.submit(() -> {
                NIOTCPClient client = new NIOTCPClient();
                client.send("E:/1.jpeg");
            });
        }
        pool.shutdown();
        Instant end = Instant.now();
        System.out.println(Duration.between(begin,end));
    }
}
