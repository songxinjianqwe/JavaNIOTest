package me.newsong.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.Date;
import java.util.Scanner;

/**
 * Created by SinjinSong on 2017/3/25.
 */
public class NIOUDPClient {
    /**
     * 客户端使用send方法发送数据
     */
    private DatagramChannel client;
    
    public NIOUDPClient() {
        try {
            client = DatagramChannel.open();
            //设置为非阻塞IO
            client.configureBlocking(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void start() {
        ByteBuffer buf = ByteBuffer.allocate(1024);
        Scanner scanner = new Scanner(System.in);
        try {
            //一次不能发送超过1024个字节的数据，否则会抛出异常
            while (scanner.hasNext()) {
                buf.put((new Date() + "  :"+scanner.next()).getBytes());
                buf.flip();
                client.send(buf, new InetSocketAddress("127.0.0.1", 9000));
                buf.clear();
            }
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        NIOUDPClient client = new NIOUDPClient();
        client.start();
    }
}
