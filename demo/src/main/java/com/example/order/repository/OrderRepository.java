package com.example.order.repository;

import com.example.order.entity.Order;
import org.springframework.stereotype.Repository;

@Repository
public class OrderRepository {

  public void save(Order order) {
    System.out.println("OrderRepository.save()");
  }

  public Order findById(Long id) {
    System.out.println("OrderRepository.findById()");
    return new Order(id);
  }

}
