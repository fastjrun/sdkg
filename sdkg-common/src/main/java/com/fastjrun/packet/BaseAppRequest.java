package com.fastjrun.packet;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

public class BaseAppRequest<V extends BaseBody> extends BasePacket<BaseAppRequestHead, V> {
}
