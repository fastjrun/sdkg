/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.codeg.common;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class PacketObject extends BaseCodeGenerableObject implements CodeGConstants {
    private String name;
    private boolean _new = true;
    private String parent;
    private boolean ref = false;
    private String remark ;

    private String _class;
    private Map<String, PacketObject> objects;
    private Map<String, PacketField> fields;
    private Map<String, PacketObject> lists;

    public PacketObject(String name, boolean _new, String _class) {
        this.name = name;
        this._new = _new;
        this._class = _class;
    }

    public PacketObject() {
    }

}
