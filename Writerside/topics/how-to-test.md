# 處理依賴

前面討論過程式依賴關係會直接影響測試的難度，但要完全杜絕依賴關係又不現實，因此我們必須仰賴工具來讓我們可以輕鬆地處理依賴關係，在
Java 的世界裡這工具叫做 `Mockito`。<br/>
以前面的範例，其實只要想辦法把`OrderRepository`替換掉，使其不要真的去資料庫找資料，問題就解決了，而 `Mockito` 可以幫我們輕鬆的辦到這件事，參考下面的程式碼
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
1. 首先透過第一行的`@ExtendWith(SpringExtension.class)`來引入 Spring 的 Junit 擴充，其中會自動設定啟動 Spring 的程序，例如初始化`ApplicationContext`。
2. 接著再由`@SpringBootTest(classes = RestApplication.class)`告訴 Spring 要啟動哪一個 Application，其中`RestApplication.class`內包含 Spring 啟動的 `main` 方法。
3. 再來`@Autowired`我們要測試的對象，也就是`OrderFactory`。
4. 透過`@MockBean` Spring 會幫我們自動建立一個假的`OrderRepository`，之後會透過 Spring DI 自動把`OrderFactory`裡的`OrderRepository`注入成這邊生成的假物件。
5. 最後再透過`mock_findById`方法，使用 Mockito 告訴它當呼叫到`orderRepository.findById`這個方法時，回傳一個預先 new 好的一個`Order`物件。

透過上面的這些操作，就可以把`OrderFactory`與真實的`OrderRepository`給解耦，讓我們的測試可以順利執行。

## 依賴於時間

## 測查詢/資料

## 測行為

## 測 API

## Test Double

https://martinfowler.com/bliki/TestDouble.html
https://yu-jack.github.io/2020/10/12/unit-test-best-practice-part-5/