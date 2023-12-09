# 處理依賴

前面討論過程式依賴關係會直接影響測試的難度，但要完全杜絕依賴關係又不現實，因此我們必須仰賴工具來讓我們可以輕鬆地處理依賴關係，在
Java 的世界裡這工具叫做 `Mockito`。<br/>
以前面的範例，其實只要想辦法把`OrderRepository`替換掉，使其不要真的去資料庫找資料，問題就解決了，而 `Mockito` 可以幫我們辦到這件事，參考下面的程式碼
```Java
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = RestApplication.class)
public class OrderFactoryTest {

    @Autowired
    private OrderFactory orderFactory;
    @MockBean
    private OrderRepository orderRepository;

    @Test
    void testReconstruction() {
        // 模擬 findById
        Long expectId = 10L;
        this.mock_findById(expectId);
        
        Order order = orderFactory.reconstruction(expectId);
        assertThat(order.getId()).isEqualTo(expectId);
    }
    
    void mock_findById(Long id) {
        Mockito.when(
            orderRepository.findById(Mockito.any()))
        .thenReturn(new Order(id));
    }
}
```

## 依賴於時間

## 測查詢/資料

## 測行為

## 測 API

## Test Double

https://martinfowler.com/bliki/TestDouble.html
https://yu-jack.github.io/2020/10/12/unit-test-best-practice-part-5/