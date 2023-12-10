package com.example.order.service;

import org.springframework.stereotype.Service;

@Service
public class StockService {

  public boolean isPossibleOrder(Long productId, int quantity) {
    return true;
  }

  public void decrease(Long productId, int quantity) {
    System.out.println("StockService.decreaseStock()");
  }

}
