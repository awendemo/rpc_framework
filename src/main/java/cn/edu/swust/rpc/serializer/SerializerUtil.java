package cn.edu.swust.rpc.serializer;

import cn.edu.swust.rpc.serializer.hessian.HessianSerializer;
import cn.edu.swust.rpc.serializer.jdk.JdkSerializer;
import cn.edu.swust.rpc.serializer.json.JsonSerializer;
import cn.edu.swust.rpc.serializer.kryo.KryoSerializer;
import cn.edu.swust.rpc.serializer.protostuff.ProtostuffSerializer;

public class SerializerUtil {
    public static Serializer getSerializer(String serializeType) {
        if (serializeType == null | serializeType.isEmpty()) {
            return null;
        }

        Serializer serializer;

        if ("Hession".equalsIgnoreCase(serializeType)) {
            serializer = new HessianSerializer();
        } else if ("Jdk".equalsIgnoreCase(serializeType)) {
            serializer = new JdkSerializer();
        } else if ("Json".equalsIgnoreCase(serializeType)) {
            serializer = new JsonSerializer();
        } else if ("Kryo".equalsIgnoreCase(serializeType)) {
            serializer = new KryoSerializer();
        } else if ("Protostuff".equalsIgnoreCase(serializeType)) {
            serializer = new ProtostuffSerializer();
        } else {
            return null;
        }

        return serializer;
    }
}
