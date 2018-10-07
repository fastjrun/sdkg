package com.fastjrun.exchange;

import com.fastjrun.dto.BasePacket;
import com.fastjrun.dto.DefaultListResponse;
import com.fastjrun.dto.DefaultResponse;

public abstract class DefaultResponseExchange<U extends BasePacket<?, ?>, V>
        extends BaseExchange<U, DefaultResponse<V>, DefaultListResponse<V>> {
}
