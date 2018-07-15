package com.fastjrun.packet;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

public class BaseApiRequest<V extends BaseBody> extends BasePacket<BaseApiRequestHead, V> {
}
