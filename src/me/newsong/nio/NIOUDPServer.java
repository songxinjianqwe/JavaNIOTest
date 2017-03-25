package me.newsong.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * Created by SinjinSong on 2017/3/25.
 */
public class NIOUDPServer {
    
    /**
     * 服务器使用receive方法接收数据
     */
    private DatagramChannel server;
    private Selector selector;
    public NIOUDPServer() {
        try {
            server = DatagramChannel.open();
            //设置为非阻塞IO
            server.configureBlocking(false);
            server.bind(new InetSocketAddress(9000));
            selector = Selector.open();
            server.register(selector, SelectionKey.OP_READ);
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    
    public void receive(){
        try {
            while(selector.select() > 0){
                ByteBuffer buf = ByteBuffer.allocate(1024);
                 for (Iterator<SelectionKey> it = selector.selectedKeys().iterator(); it.hasNext(); ) {
                    SelectionKey key = it.next();
                    if(key.isReadable()){
                        System.out.println("客户端已连接");
                        server.receive(buf);
                        buf.flip();
                        System.out.println(new String(buf.array(),0,buf.limit()));
                    }
                    it.remove();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        NIOUDPServer server = new NIOUDPServer();
        server.receive();
    }
}
