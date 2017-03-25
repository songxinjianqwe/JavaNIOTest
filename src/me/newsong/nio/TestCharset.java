package me.newsong.nio;

import org.junit.Test;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.Arrays;
import java.util.concurrent.Callable;

/**
 * Created by SinjinSong on 2017/3/25.
 */
public class TestCharset {
    @Test
    public void test1() throws CharacterCodingException {
        Charset charset = Charset.forName("GBK");
        //编码器，可以将字符串转为字节数组
        CharsetEncoder encoder = charset.newEncoder();
        //解码器，可以将字节数组转为字符串
        CharsetDecoder decoder = charset.newDecoder();
        
        //编码和解码使用同一编码不会产生乱码，如果是不同编码，那么会产生乱码
        CharBuffer charBuffer = CharBuffer.allocate(1000);
        charBuffer.put("呵呵呵哒");
        //转为读取模式
        charBuffer.flip();
        
        //编码
        ByteBuffer byteBuffer = encoder.encode(charBuffer);
        System.out.println(Arrays.toString(byteBuffer.array()));
        //[-70, -57, -70, -57, -70, -57, -33, -43]
        //解码
        CharBuffer charBuffer2 = decoder.decode(byteBuffer);
        System.out.println(charBuffer2.toString());
        //呵呵呵哒
    }
}
