package com.fastjrun.api;

import com.fastjrun.packet.BaseResponse;
import com.fastjrun.packet.BaseResponseHead;
import com.fastjrun.packet.EmptyBody;

public interface BaseApi<T extends BaseResponseHead> {
    BaseResponse<T, EmptyBody> status();
}
