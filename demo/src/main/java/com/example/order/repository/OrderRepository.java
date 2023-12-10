package com.example.order.repository;

import com.example.order.entity.Order;

public interface OrderRepository {

  void save(Order order);

  Order findById(Long id);

}
