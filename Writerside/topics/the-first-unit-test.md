# 第一個單元測試

假設我們正在為某間學校開發一個學生成績排名的系統，而計算排名就需要先計算每一位學生所有科目的成績，用以計算總平均，來排名。

而我們希望可以透過``StudentScore``類別，來完成計算總平均這件事，就如下面這段程式碼

```Java
public StudentScore {
    private Integer chinese;
    private Integer math;
    private Integer english;
    
    public Integer getAverage() {
        return (this.chinese + this.math + this.english) / 3
    }
}
```

接著我們為這個類別來撰寫測試程式

```Java
public StudentScoreTest {

    @Test
    void testAverage() {
        var grades = new StudentScore(74, 62, 92);
        var average grades.getAverage();
        assertThat(average).isEqualTo(76);
    }
}
```

夠簡單吧，但是現實幾乎不可能有像``StudentScore``這種完全不依賴其它物件的類別。

## 訂單系統範例
在開始討論依賴問題前，先來描述一個訂單系統的使用情境，因為後面的章節會接連使用這個系統來當範例。<br/>
1. 系統可以接受使用者下訂單
2. 每個訂單在下訂前需檢查庫存的數量是否充足
3. 若庫存充足，成立訂單，並且扣除庫存總數
4. 若庫存不足，拋出錯誤訊息
5. 可使用訂單編號查詢以成立的訂單

下圖 Class diagram 可幫助我們了解物件間的關係
TODO 補圖

## 外部依賴

依賴即引用，也就是指某個類別需要另一個類別的幫助才能完成任務，說白話點就是有程式裡有出現``import``的關鍵字。
如先前提到的訂單系統 ``OrderFactory`` 需要使用 `OrderRepository` 來把已存入資料庫中的 `Order` 物件重新載入到記憶體或把新的`Order`寫入進資料庫，可以參考下面的程式碼

```Java
import com.example.Order;
import com.example.OrderRepository;

@RequiredArgsConstructor
public class OrderFactory {

    private final OrderRepository orderRepository;
    
    public Order reconstruction(Long orderId) {
        return orderRepository.findById(orderId);
    }
}
```

以此處的範例，我們可以說``OrderFactory``需要依賴於`OrderRepository`。
接著我們試著寫測試程式

```Java
public class OrderFactoryTest {

    @Autowired
    private OrderFactory orderFactory;

    @Test
    void testReconstruction() {
        Order order = orderFactory.reconstruction(10L);
        assertThat(order.getId()).isEqualTo(10L);
    }
}
```

上面的測試能跑是能跑，但有個問題，也就是必須保證資料庫裡有 `ID = 10` 的這筆資料，才能夠亮起綠燈。或許你可以保證開發環境的資料庫會有這筆資料，但你能保證
SIT/UAT 的資料庫也會有嗎？<br/>
有的人也許會說，既然我們不能保證資料在各環境的資料庫都存在，那可以健一個專門給單元測試用的資料庫啊。確實健一個專有的資料庫可以解決問題，但別忘了多了一個資料庫就會需要維護，那麼是誰要去維護呢？

這些外部依賴會直接影響測試程式撰寫的難度，這也是很多知名的軟體大師常常在提倡的程式應該要「高內聚，低耦合」的原因，而要達到這項目標最直接的方式就是減少依賴，只要依賴少程式就會好用，好用的程式就會好測。

> 想要讓程式僅保留最低限度的依賴關係不是件容易的事，這也不是完全是技術議題，有時是對領域知識理解不全導致的，但若我們有了
> 100 分的領域模型，在程式實作上背後確實還是需要些理論跟設計方法，而其實現的方法大致也跟 SOLID 原則有關。