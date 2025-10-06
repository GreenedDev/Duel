package net.multylands.duels.utils.storage;

import java.io.*;
import java.util.Arrays;

public class SerializationUtil {
    public static String deserialize(byte[] data) {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(data);
             DataInputStream dis = new DataInputStream(bis)) {

            return Arrays.toString(dis.readAllBytes());
        } catch (IOException ex) {
            throw new RuntimeException("Failed to deserialize duel element", ex);
        }
    }

    public static byte[] serialize(String element) {

        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             DataOutputStream dos = new DataOutputStream(bos)) {
            dos.writeBytes(element);

            return bos.toByteArray();
        } catch (IOException ex) {
            throw new RuntimeException("Failed to serialize duel element", ex);
        }
    }
}
