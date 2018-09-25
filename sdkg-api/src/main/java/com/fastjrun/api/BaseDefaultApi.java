package com.fastjrun.api;

import com.fastjrun.dto.DefaultResponse;

public interface BaseDefaultApi extends BaseApi {
    DefaultResponse status();
}