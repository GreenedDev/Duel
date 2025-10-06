package net.multylands.duels.utils.storage;

import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;


public class DuelElementDataType implements PersistentDataType<byte[], String> {
    @Override
    public Class<byte[]> getPrimitiveType() {
        return byte[].class;
    }

    @Override
    public Class<String> getComplexType() {
        return String.class;
    }

    @Override
    public byte[] toPrimitive(String element, PersistentDataAdapterContext context) {
        return SerializationUtil.serialize(element);
    }

    @Override
    public String fromPrimitive(byte[] primitive, PersistentDataAdapterContext context) {
        return SerializationUtil.deserialize(primitive);
    }
}