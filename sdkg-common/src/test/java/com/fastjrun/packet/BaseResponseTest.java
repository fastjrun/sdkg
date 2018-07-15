package com.fastjrun.packet;

import org.junit.Test;

public class BaseResponseTest {

    @Test
    public void testToString() {
        BaseDefaultResponse<EmptyResponseBody> response = new BaseDefaultResponse<EmptyResponseBody>();
        BaseDefaultResponseHead responseHead = new BaseDefaultResponseHead();
        responseHead.setCode("0000");
        responseHead.setMsg("ok");
        response.setHead(responseHead);
        System.out.println(response);
    }

}
