package com.fastjrun.packet;

import org.junit.Test;

public class BaseResponseTest {

    @Test
    public void testToString() {
        DefaultResponse<EmptyResponseBody> response = new DefaultResponse<EmptyResponseBody>();
        DefaultResponseHead responseHead = new DefaultResponseHead();
        responseHead.setCode("0000");
        responseHead.setMsg("ok");
        response.setHead(responseHead);
        System.out.println(response);
    }

}
