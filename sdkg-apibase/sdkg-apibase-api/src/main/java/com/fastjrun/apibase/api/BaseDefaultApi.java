/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.apibase.api;

import com.fastjrun.apibase.dto.DefaultResponse;

public interface BaseDefaultApi extends BaseApi {
    @Override
    DefaultResponse status();
}
