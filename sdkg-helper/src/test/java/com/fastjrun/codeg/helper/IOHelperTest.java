package com.fastjrun.codeg.helper;

import java.io.File;

import org.junit.Test;

import com.fastjrun.codeg.helper.IOHelper;

public class IOHelperTest {

    @Test
    public void testDeleteDir() {
        boolean res=IOHelper.deleteDir("src/test/data");
        System.out.println(res);
    }
    


    @Test
    public void testDelete() {
        File file=new File("src/test/data/org/fastjrun/demospring4");
        boolean res=file.delete();
        System.out.println(res);
    }

}
