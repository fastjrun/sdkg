package com.fastjrun.eladmin.vo;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

public class PageResult<T> implements Serializable {
  public static final int PAGE_SIZE_DEFAULT = 20;
  private int totalElements;
  private int pageSize;
  private int totalPage;

  @JsonProperty("currentPage")
  private int currPage;

  private List<T> content;

  public PageResult() {}

  public PageResult(List<T> content, int totalElements, int pageSize, int currPage) {
    this.content = content;
    this.totalElements = totalElements;
    this.pageSize = pageSize;
    this.currPage = currPage;
    this.totalPage = (int) Math.ceil((double) totalElements / (double) pageSize);
  }


  public int getPageSize() {
    return this.pageSize;
  }

  public void setPageSize(int pageSize) {
    this.pageSize = pageSize;
  }

  public int getTotalPage() {
    return this.totalPage;
  }

  public void setTotalPage(int totalPage) {
    this.totalPage = totalPage;
  }

  public int getCurrPage() {
    return this.currPage;
  }

  public void setCurrPage(int currPage) {
    this.currPage = currPage;
  }

  public int getTotalElements() {
    return totalElements;
  }

  public void setTotalElements(int totalElements) {
    this.totalElements = totalElements;
  }

  public List<T> getContent() {
    return content;
  }

  public void setContent(List<T> content) {
    this.content = content;
  }
}
