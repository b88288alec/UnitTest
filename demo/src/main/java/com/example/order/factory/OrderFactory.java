package com.example.order.factory;

import com.example.order.entity.Order;
import com.example.order.entity.Product;
import com.example.order.exception.OutOfStockException;
import com.example.order.repository.OrderRepository;
import com.example.order.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderFactory {

  private final OrderRepository orderRepository;
  private final StockService stockService;

  public Order reconstruction(Long orderId) {
    return orderRepository.findById(orderId);
  }

  public Order create(Product product, int quantity) {
    // 檢查產品是否還有庫存
    boolean isPossibleOrder = stockService.isPossibleOrder(product.getId(), quantity);
    if (isPossibleOrder) {
      // 訂單成立
      Order order = new Order(product.getId(), quantity);
      orderRepository.save(order);
      // 扣除庫存數量
      stockService.decrease(product.getId(), quantity);
      return order;
    } else {
      // 沒庫存拋錯誤
      throw new OutOfStockException("The product id " + product.getId() + " is out of stock");
    }
  }
}
