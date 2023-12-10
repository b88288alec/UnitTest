package com.example.order.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

  private Long id;
  private String name;
  private String description;

  public Product(Long id) {
    this.id = id;
  }

}
