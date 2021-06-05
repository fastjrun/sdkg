/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.codeg.common;

public class PacketField {
  /** 支持字段名称中含"-"或“_”字符 */
  private String name;

  private String fieldName;

  private String nameAlias;

  private String length;

  private String remark;

  private boolean canBeNull;

  /** 字段数据类型 String:List */
  private String datatype;

  /* pathVariable需要用到顺序 */
  private int index;

  private String getter;

  private String setter;

  public String getFieldName() {
    return fieldName;
  }

  public void setFieldName(String fieldName) {
    this.fieldName = fieldName;
  }

  public String getGetter() {
    return getter;
  }

  public void setGetter(String getter) {
    this.getter = getter;
  }

  public String getSetter() {
    return setter;
  }

  public void setSetter(String setter) {
    this.setter = setter;
  }

  public int getIndex() {
    return index;
  }

  public void setIndex(int index) {
    this.index = index;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getLength() {
    return length;
  }

  public void setLength(String length) {
    this.length = length;
  }

  public String getRemark() {
    return remark;
  }

  public void setRemark(String remark) {
    this.remark = remark;
  }

  public boolean isCanBeNull() {
    return canBeNull;
  }

  public void setCanBeNull(boolean canBeNull) {
    this.canBeNull = canBeNull;
  }

  public String getDatatype() {
    return datatype;
  }

  public void setDatatype(String datatype) {
    this.datatype = datatype;
  }

  public String getNameAlias() {
    return nameAlias;
  }

  public void setNameAlias(String nameAlias) {
    this.nameAlias = nameAlias;
  }

  @Override
  public String toString() {
    return "RestField [name="
        + name
        + ", fieldName="
        + fieldName
        + ", nameAlias="
        + nameAlias
        + ", length="
        + length
        + ", remark="
        + remark
        + ", canBeNull="
        + canBeNull
        + ", datatype="
        + datatype
        + "]";
  }
}
