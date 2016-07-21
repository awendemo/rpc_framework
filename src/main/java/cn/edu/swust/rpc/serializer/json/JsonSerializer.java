package cn.edu.swust.rpc.serializer.json;

import cn.edu.swust.rpc.serializer.Serializer;
import com.alibaba.fastjson.JSON;

import java.io.*;

/**
 * Created by Administrator on 2016/7/21.
 */
public class JsonSerializer implements Serializer {
    @Override
    public void init() {

    }

    @Override
    public void register(Class<?> type) {

    }

    @Override
    public <T> T toObject(byte[] bytes, Class<T> type) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }

        try {
            String data = new String(bytes, "ISO-8859-1");
            return JSON.parseObject(data, type);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public byte[] toByteArray(Object obj) {
        if (obj == null) {
            return null;
        }

        try {
            String data = JSON.toJSONString(obj);
            return data.getBytes("ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public String getSerializeType() {
        return "Json";
    }
}

