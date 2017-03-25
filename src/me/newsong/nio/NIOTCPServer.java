package me.newsong.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Iterator;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by SinjinSong on 2017/3/25.
 */
public class NIOTCPServer {
    private ServerSocketChannel serverSocketChannel;
    private final String FILE_PATH = "E:/uploads/";
    private AtomicInteger i;
    private final String RESPONSE_MSG = "服务器接收数据成功";
    private Selector selector;
    private ExecutorService acceptPool;
    private ExecutorService readPool;

    public NIOTCPServer() {
        try {
            serverSocketChannel = ServerSocketChannel.open();
            //切换为非阻塞模式
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.bind(new InetSocketAddress(9000));
            //获得选择器
            selector = Selector.open();
            //将channel注册到selector上
            //第二个参数是选择键，用于说明selector监控channel的状态
            //可能的取值：SelectionKey.OP_READ OP_WRITE OP_CONNECT OP_ACCEPT

            //监控的是channel的接收状态
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            acceptPool = new ThreadPoolExecutor(50, 100, 1000, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<>());
            readPool = new ThreadPoolExecutor(50, 100, 1000, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<>());
            i = new AtomicInteger(0);
            System.out.println("服务器启动");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void receive() {
        try {
            //如果有一个及以上的客户端的数据准备就绪
            while (selector.select() > 0) {
                //获取当前选择器中所有注册的监听事件
                for (Iterator<SelectionKey> it = selector.selectedKeys().iterator(); it.hasNext(); ) {
                    SelectionKey key = it.next();
                    //如果"接收"事件已就绪
                    if (key.isAcceptable()) {
                        //交由接收事件的处理器处理
                        acceptPool.submit(new ReceiveEventHander());
                    } else if (key.isReadable()) {
                        //如果"读取"事件已就绪
                        //交由读取事件的处理器处理
                        readPool.submit(new ReadEventHandler((SocketChannel) key.channel()));
                    }
                    //处理完毕后，需要取消当前的选择键
                    it.remove();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    class ReceiveEventHander implements Runnable {

        public ReceiveEventHander() {
        }

        @Override
        public void run() {
            SocketChannel client = null;
            try {
                client = serverSocketChannel.accept();
                // 接收的客户端也要切换为非阻塞模式
                client.configureBlocking(false);
                // 监控客户端的读操作是否就绪
                client.register(selector, SelectionKey.OP_READ);
                System.out.println("服务器连接客户端:" + client.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    class ReadEventHandler implements Runnable {
        private ByteBuffer buf;
        private SocketChannel client;

        public ReadEventHandler(SocketChannel client) {
            this.client = client;
            buf = ByteBuffer.allocate(1024);
        }

        @Override
        public void run() {

            FileChannel fileChannel = null;
            try {
                int index = 0;
                synchronized (client) {
                    while (client.read(buf) != -1) {
                        if (fileChannel == null) {
                            index = i.getAndIncrement();
                            fileChannel = FileChannel.open(Paths.get(FILE_PATH, index + ".jpeg"), StandardOpenOption.WRITE, StandardOpenOption.CREATE);
                        }
                        buf.flip();
                        fileChannel.write(buf);
                        buf.clear();
                    }
                }
                if (fileChannel != null) {
                    fileChannel.close();
                    System.out.println("服务器写来自客户端" + client + " 文件" + index + " 完毕");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public static void main(String[] args) {
        NIOTCPServer server = new NIOTCPServer();
        server.receive();
    }
}
