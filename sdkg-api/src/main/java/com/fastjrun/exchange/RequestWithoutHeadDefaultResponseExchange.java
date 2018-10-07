package com.fastjrun.exchange;

import com.fastjrun.dto.DefaultListResponse;
import com.fastjrun.dto.DefaultResponse;

public class RequestWithoutHeadDefaultResponseExchange<T, V>
        extends BaseRequestWithoutHeadExchange<V, DefaultResponse<V>, DefaultListResponse<V>> {
}
