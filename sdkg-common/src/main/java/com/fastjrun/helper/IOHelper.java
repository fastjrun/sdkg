package com.fastjrun.helper;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class IOHelper {
    


    public static byte[] read(InputStream inputStream) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int num = inputStream.read(buffer);
        while (num != -1) {
            baos.write(buffer, 0, num);
            num = inputStream.read(buffer);
        }
        baos.flush();
        return baos.toByteArray();
    }

}
