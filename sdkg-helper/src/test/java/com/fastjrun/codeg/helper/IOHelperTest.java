package com.fastjrun.codeg.helper;

import org.junit.Test;

import java.io.File;

public class IOHelperTest {

    @Test
    public void testDeleteDir() {
        boolean res = IOHelper.deleteDir("src/test/data");
        System.out.println(res);
    }


    @Test
    public void testDelete() {
        File file = new File("src/test/data/org/fastjrun/demospring4");
        boolean res = file.delete();
        System.out.println(res);
    }
}
