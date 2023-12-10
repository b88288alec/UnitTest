# 處理依賴

前面討論過程式依賴關係會直接影響測試的難度，但要完全杜絕依賴關係又不現實，因此我們必須仰賴工具來讓我們可以輕鬆地處理依賴關係，在
Java 的世界裡這工具叫做 `Mockito`。<br/>
以前面的範例，其實只要想辦法把`OrderRepository`替換掉，使其不要真的去資料庫找資料，問題就解決了，而 `Mockito`
可以幫我們輕鬆的辦到這件事，參考下面的程式碼

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

1. 首先透過第一行的`@ExtendWith(SpringExtension.class)`來引入 Spring 的 Junit 擴充，其中會自動設定啟動 Spring
   的程序，例如初始化`ApplicationContext`。
2. 接著再由`@SpringBootTest(classes = RestApplication.class)`告訴 Spring 要啟動哪一個
   Application，其中`RestApplication.class`內包含 Spring 啟動的 `main` 方法。
3. 再來`@Autowired`我們要測試的對象，也就是`OrderFactory`。
4. 透過`@MockBean` Spring 會幫我們自動建立一個假的`OrderRepository`，之後會透過 Spring DI 自動把`OrderFactory`
   裡的`OrderRepository`注入成這邊生成的假物件。
5. 最後再透過`mock_findById`方法，使用 Mockito 告訴它當呼叫到`orderRepository.findById`這個方法時，回傳一個預先 new
   好的一個`Order`物件。

透過上面的這些操作，就可以把`OrderFactory`與真實的`OrderRepository`給解耦，讓我們的測試可以順利執行。

## 依賴於時間

假設我們正在開發支付系統，系統必須將指定的金額轉帳給銀行，而銀行可接受轉帳時間為營業日的 08:00 ~ 15:00，所以有了`Transfer`
類別，其中的`isTransferable`為判斷現在時間是否可轉帳。

```Java
public class Transfer {
    
    public boolean isTransferable() {
        LocalDate now = LocalDate.now();
        // 一些判斷邏輯 code 略...
    }
}
```

接著我們想要測試不可測試的情境。 恩...要怎麼測？<br/>
如果跑測試的時間是在上午 9 點的話，那不就測不了，再說前面使用 Mockito 的範例都是替換一個已經被 new 出來的
instance，但這裡的 `LocalDate.now()` 是 static 方法，static 屬性的方法是在 JVM 啟動時就初始化完了，使用它也不需要先 new
它，該怎麼辦？<br/>

有兩種方法可以解決這個問題

1. 不要在方法內使用`LocalDate.now()`，改成用參數輸入來解耦
2. 使用 Mockito 在`3.4.0`後推出的`mockStatic`

```Java
LocalDate expected = LocalDate.of(2029, 12, 31);
Mockito.mockStatic(LocalDate.class).when(LocalDate::now).thenReturn(expected);
```

這樣我們的測試就可以在任何時間執行了。

## 測行為

日常開發不外乎是打隻 API 查點資料，或是做點計算再把資料存到資料庫，也就是 Query 和 Command，所謂的 Query
就是向某個物件要東西，像是前面範例用`OrderRepository`去資料庫查詢或是用`StudentScore`的`getAverage`
方法算出平均成績也算是查詢，而這種行為的測試很容易可以驗證其正確性，因為它總是會回傳資料回來，只要有回傳就可以透過`assert`
來檢驗有沒有達到預期效果。<br/>
而 Command 就是叫某個物件去做點事情，像是呼叫 Dao 把資料存到資料庫，這類的行為通常都不會有回傳值，既然沒回傳值，那怎麼知道程式跑得對不對呢？

在解釋怎麼測試前，先把前面的`OrderFactory`功能補齊，把下新訂單的功能給補上

```Java
@RequiredArgsConstructor
public class OrderFactory {

    private final OrderRepository orderRepository;
    private final StockService stockService;
    
    public Order reconstruction(Long orderId) {
        return orderRepository.findById(orderId);
    }
    
    public Order create(Product product, int quantity) {
        // 檢查產品是否還有庫存
        boolean isPossibleOrder = stockService.isPossibleOrder(product.getProductId(), quantity);
        if (isPossibleOrder) {
            // 訂單成立
            Order order = new Order(product, quantity);
            orderRepository.save(order);
            // 扣除庫存數量
            stockService.decrease(product.getProductId(), quantity);
            return order;
        } else {
            // 沒庫存拋錯誤
            throw new OutOfStockException("The product id " + product.getProductId() + " is out of stock");
        }
    }
}
```

接著寫測試程式

```Java
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = RestApplication.class)
public class OrderFactoryTest {

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
      
      Product product = new Product(20);
      Order order = orderFactory.create(product, 10);
      
      // 驗證真的有存到資料庫
      Mockito.verify(orderRepository, Mockito.times(1)).save(order);
      // 驗證真的有減少庫存量
      Mockito.verify(stockService, Mockito.times(1)).decrease(product.getProductId(), 10);
   }
   
   @Test
   @DisplayName("下新訂單(沒庫存)")
   void createOutOfStock() {
      this.mock_isPossibleOrder(false);
      
      Product product = new Product(20);
      try {
         Order order = orderFactory.create(product, 10);
         fail("An error should be thrown");
      } catch (OutOfStockException e) {
         /* 注意：catch 裡不能放 Exception，因為程式預期會為拋出的錯只有 OutOfStockException，
         如果拋出了非 OutOfStockException 的例外都屬非預期的行為，應該要讓測試失敗 */
         assertThat(e).hasMessageContaining("out of stock");
      }
   }
   
   private void mock_isPossibleOrder(boolean isPossibleOrder) {
      Mockito.when(stockService.isPossibleOrder(Mockito.any(), Mockito.any()))
      .thenReturn(isPossibleOrder);
   }
   
}
```

使用`Mockito.verify`方法，並輸入指定的驗證目標及次數，就可以驗證程式是不是真的有執行某項行為，到此測試就完成了。
同時還順便多補了當沒有庫存時的情境，其中在執行完`create`後用了`fail`語法，因為我預期這裡應該要拋出錯誤，如果到了`fail`
這裡程式還能正常跑完，那測試就應該要失敗。

## Test Double

其實前面處理依賴的方法有個專有的名稱叫做`Stubs`，而這也只是一個分類的名稱，它的完整概念叫做`Test Double`。<br/>
Test Double 指的是在測試的過程中所建造出用來替換真實物件的幾種模式。
- Dummy: 單純用來傳遞的假資料
- Fake: 一個擁有完整實做的物件，但其實做的內容透過某種捷徑來繞過真實行為，以達到測試的目的，像是 InMemoryTestDatabase
- Stubs: 提供只會回覆罐頭訊息的物件，像前面用`Mockito.when`
- Spies: 基本跟 Stubs 相同，但多紀錄一些呼叫資訓，例如某個方法在測試期間被呼叫了幾次，`Mockito.verify`就屬於這個類型
- Mocks: 跟 Spies 類似，與 Spies 差別在於 Spies 不管執行過程和結果的正確性，但 Mocks 會對其做檢核

<seealso>
   <category ref="TestDouble">
      <a href="https://martinfowler.com/bliki/TestDouble.html">Test Double from Martin Fowler</a>
      <a href="https://yu-jack.github.io/2020/10/12/unit-test-best-practice-part-5/">如何有效使用 Test Double</a>
   </category>
</seealso>