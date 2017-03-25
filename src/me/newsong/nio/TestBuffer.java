package me.newsong.nio;

import org.junit.Test;

import java.nio.ByteBuffer;

/**
 * Created by SinjinSong on 2017/3/25.
 */
public class TestBuffer {
    @Test
    public void test1(){
        //分配了1000个字节的缓冲区
        ByteBuffer buf = ByteBuffer.allocate(1000);
        System.out.println(buf.capacity());//1000
        System.out.println(buf.limit());//1000
        System.out.println(buf.position());//0
        
        //写数据模式
        //put方法存入数据到缓冲区
        String str = "abcde";
        buf.put(str.getBytes());
        System.out.println(buf.position());//5
        
        //flip方法可以切换至读数据模式
        buf.flip();
        System.out.println(buf.limit());//5  变为写模式的position
        System.out.println(buf.position());//0
        //至多可以读取5个字节的数据
        byte[] dest = new byte[buf.limit()];
        buf.get(dest);
        System.out.println(new String(dest));
        
        //取数据后的变化
        System.out.println(buf.limit());//5
        System.out.println(buf.position());//5
        
        //rewind是将position重新放到开头，可以重复读取数据
        buf.rewind();
        System.out.println(buf.position());//0
        
        //清空缓冲区，注意里面的数据并没有被清空，而是延迟删除，数据处于一种被遗忘的状态。
        buf.clear();
        System.out.println(buf.limit());//1000
        System.out.println(buf.position());//0

        System.out.println((char)buf.get());//a 
        // 仍可以读取，但无法获取有效数据的长度
        
       
        
    }
    @Test
    public void test2(){
        ByteBuffer buf = ByteBuffer.allocate(1000);
        //mark 标记：记录当前的position位置
        //reset 是回到之前mark的标记处
        String str = "abcde";
        buf.put(str.getBytes());
        buf.flip();
        byte [] bytes = new byte[buf.limit()];
        //读前2个字节
        buf.get(bytes,0,2);
        System.out.println(new String(bytes, 0, 2));//ab

        System.out.println(buf.position());//2
        buf.mark();
        //读剩下3个字节
        buf.get(bytes,2,3);
        System.out.println(new String(bytes,2,3));//cde
        System.out.println(buf.position());//5
        buf.reset();
        System.out.println(buf.position());//2
        
        //判断缓冲区是否还有剩余数据
        if(buf.hasRemaining()){
            //获取缓冲区中的剩余数据字节数，是limit-position
            System.out.println(buf.remaining());//3
        }
    }
    
    @Test
    public void test3(){
        ByteBuffer buf = ByteBuffer.allocateDirect(1000);
        System.out.println(buf.isDirect());   
    }
}
