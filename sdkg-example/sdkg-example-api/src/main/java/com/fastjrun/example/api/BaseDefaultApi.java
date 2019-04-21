/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.example.api;

import com.fastjrun.example.dto.DefaultResponse;

public interface BaseDefaultApi extends BaseApi {
    DefaultResponse status();
}