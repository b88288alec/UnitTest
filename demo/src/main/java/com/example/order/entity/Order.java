package com.example.order.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {

  private Long id;
  private String name;
  private String description;
  private int quantity;
  private Long productId;

  public Order(Long id) {
    this.id = id;
  }

  public Order(Long productId, int quantity) {
    this.productId = productId;
    this.quantity = quantity;
  }
}
