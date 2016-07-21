package cn.edu.swust.rpc.serializer.json;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/7/21.
 */
public class JsonSerializerTest {
    @Test
    public void test() {
        Map<String, String> value = new HashMap<>();

        value.put("1", "a");
        value.put("2", "b");

        JsonSerializer jsonSerializer = new JsonSerializer();

        byte[] data = jsonSerializer.toByteArray(value);
        System.out.println(data);

        Map<String, String> value_ret = jsonSerializer.toObject(data, Map.class);
        System.out.println(value_ret);
    }
}