package com.fastjrun.helper;

import org.testng.annotations.Test;

import java.util.Date;

public class MockHelperTest {
    @Test
    public void testGeDate(){
        Date date=MockHelper.geDate();
        System.out.println(date);


    }
}
