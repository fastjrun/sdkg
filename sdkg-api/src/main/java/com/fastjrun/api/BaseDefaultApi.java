package com.fastjrun.api;

import com.fastjrun.packet.BaseResponse;
import com.fastjrun.packet.DefaultResponse;
import com.fastjrun.packet.DefaultResponseHead;
import com.fastjrun.packet.EmptyBody;

public interface BaseDefaultApi extends BaseApi {
    DefaultResponse<EmptyBody> status();
}
