package com.fastjrun.example.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

public class PageResult<T> implements Serializable {
  public static final int PAGE_SIZE_DEFAULT = 20;
  private int total;
  private int pageSize;
  private int totalPage;

  @JsonProperty("currentPage")
  private int currPage;

  private List<T> rows;

  public PageResult() {}

  public PageResult(List<T> rows, int total, int pageSize, int currPage) {
    this.rows = rows;
    this.total = total;
    this.pageSize = pageSize;
    this.currPage = currPage;
    this.totalPage = (int) Math.ceil((double) total / (double) pageSize);
  }

  public int getTotal() {
    return this.total;
  }

  public void setTotal(int total) {
    this.total = total;
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

  public List<T> getRows() {
    return this.rows;
  }

  public void setRows(List<T> rows) {
    this.rows = rows;
  }
}
