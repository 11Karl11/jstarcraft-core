package com.jstarcraft.core.codec.thrift.converter;

import java.lang.reflect.Type;

import org.apache.thrift.protocol.TProtocol;

import com.jstarcraft.core.codec.specification.ClassDefinition;
import com.jstarcraft.core.codec.thrift.ThriftReader;
import com.jstarcraft.core.codec.thrift.ThriftWriter;

/**
 * 协议转换器
 * 
 * <pre>
 * 参考ProtocolBuffer协议与ASF3协议
 * </pre>
 * 
 * @author Birdy
 *
 * @param <T>
 */
public abstract class ProtocolConverter<T> {

    protected TProtocol protocol;

    public TProtocol getProtocol() {
        return this.protocol;
    }

    public void setProtocol(TProtocol protocol) {
        this.protocol = protocol;
    }

    /**
     * 从指定上下文读取内容
     * 
     * @param context
     * @param type
     * @param definition
     * @throws Exception
     * @return
     */
    abstract public T readValueFrom(ThriftReader context, Type type, ClassDefinition definition) throws Exception;

    /**
     * 将指定内容写到上下文
     * 
     * @param context
     * @param value
     * @throws Exception
     */
    abstract public void writeValueTo(ThriftWriter context, Type type, ClassDefinition definition, T value) throws Exception;

    /** 1111 0000(类型掩码) */
    public static final byte TYPE_MASK = (byte) 0xF0;

    /** 0000 1111(标记掩码) */
    public static final byte MARK_MASK = (byte) 0x0F;

    /**
     * 通过指定字节数据获取类型码
     *
     * @param data
     * @return
     */
    public static byte getType(byte data) {
        byte code = (byte) (data & TYPE_MASK);
        return code;
    }

    /**
     * 通过指定字节数据获取标记码
     *
     * @param data
     * @return
     */
    public static byte getMark(byte data) {
        byte mark = (byte) (data & MARK_MASK);
        return mark;
    }
}