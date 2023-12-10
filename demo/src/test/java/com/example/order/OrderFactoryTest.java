package com.example.order;

import com.example.demo.DemoApplication;
import com.example.order.entity.Order;
import com.example.order.entity.Product;
import com.example.order.exception.OutOfStockException;
import com.example.order.factory.OrderFactory;
import com.example.order.repository.OrderRepository;
import com.example.order.service.StockService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = DemoApplication.class)
class OrderFactoryTest {

  @Autowired
  private OrderFactory orderFactory;
  @MockBean
  private OrderRepository orderRepository;
  @MockBean
  private StockService stockService;

  @Test
  @DisplayName("下新訂單(有庫存)")
  void createHasStock() {
    this.mock_isPossibleOrder(true);

    Product product = new Product(20L);
    Order order = orderFactory.create(product, 10);

    // 驗證真的有存到資料庫
    Mockito.verify(orderRepository, Mockito.times(1)).save(order);
    // 驗證真的有減少庫存量
    Mockito.verify(stockService, Mockito.times(1)).decrease(product.getId(), 10);
  }

  @Test
  @DisplayName("下新訂單(沒庫存)")
  void createOutOfStock() {
    this.mock_isPossibleOrder(false);

    Product product = new Product(20L);
    try {
      Order order = orderFactory.create(product, 10);
      Assertions.fail("An error should be thrown");
    } catch (OutOfStockException e) {
         /* 注意：catch 裡不能放 Exception，因為程式預期會為拋出的錯只有 OutOfStockException，
         如果拋出了非 OutOfStockException 的例外都屬非預期的行為，應該要讓測試失敗 */
      Assertions.assertThat(e).hasMessageContaining("out of stock");
    }
  }

  private void mock_isPossibleOrder(boolean isPossibleOrder) {
    Mockito.when(stockService.isPossibleOrder(Mockito.anyLong(), Mockito.anyInt()))
        .thenReturn(isPossibleOrder);
  }

}
