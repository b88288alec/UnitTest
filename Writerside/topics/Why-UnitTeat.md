# 為什麼要 Unit Teat

1. 減輕 QA 壓力<br/>
   假如一輪 Sprint 可以開發 10 個功能，那 QA 在第一輪 Sprint 也應該要測試 10 個功能，如果開發團隊在第二輪的 Sprint
   也穩定輸出，那麼 QA 在第二輪結束後會需要測試幾個功能？<br/>
   正常直覺是 10 個功能，然而現實是還必須要加上回歸測試，所以實際上是要測 20 個功能，而且隨著專案開發這數字會一直成長。
2. 安全重構<br/>
   日常開發一定會遇到某功能出現問題被開了 Bug
   單，然而這功能當初開發時可能迫於時間或對領域知識理解不全，導致程式並沒有明確地展現需求意圖，幸運的是到了專案後期，時間比較也充裕了，我們有了很多時間可以來重構這些毫無組織的程式碼，於是找了
   SA、PM 討論要實行這項計畫，但往往都會被潑了一桶冷水，理由是因為 User
   已經測試完了，不可能再多花時間把該功能從頭再測一次。<br/>
   但是如果我們把單元測試融入到日常開發，因為有測試程式保護，所以我們可以很快地知道重構後的程式是不是可以達到預期的結果，也可以增加重構的信心。
3. 加快開發速度<br/>
   有些需求情境會有依賴關係，也就是 B 功能需要 A 功能的資料，但開發通常是平行開發，A 功能及 B 功能會同時開發，這時開發 B
   功能的人就需要自行先建立好測試資料，才能進行開發，如果資料簡單那倒還好，但如果資料複雜的話那就會花費大半的時間在整理測資，更何況準備出的測資還必須要符合所有的使用情境。
   如果搭配單元測試來進行開發的話，就可以破除功能的相依性，測資準備就相對的容易。

4. 提升發佈品質<br/>
   單元測試可以整合到 CI/CD
   達到自動化測試，每次的版本發佈都必須跑過所有的單元測試，如果有任何一個測試失敗，程式也就無法編譯，順利打包出``jar``
   或``war``，藉此確保程式品質。

## 什麼是 Unit Teat

### 單元測試 VS 整合測試

![測試金字塔](TestPyramid.png)

簡單定義，只要被測試對象情境會誇越邊界或涉及 I/O，就是整合測試，例如測試的過程需要呼叫 REST API 到其它外部系統以取得資料來做計算。
只要牽涉到誇越邊界就是一種不確定性，誰會知道現在呼叫的 REST API 會不會在下個月改規格，或者突然改用 MQ
介接，所以在撰寫單元測試程式就勢必多花心力去處理外部依賴，也就會增加測試成本。

> 上面的圖是「測試金字塔」最早由 Mike Cohn 提出，主要在說靠近金字塔底部為低層次，越往上就是高層次，通常越高層次的測試都會需要依賴於其它東西，可能是資料庫、外部系統、UI
> 畫面，而這種測試需要花費的成本較高，因為通常都只能夠由人工來確認正確性。而低層次所涵蓋的通常都是領域的業務邏輯，像是計算某間公司在當季度的營收，這類不需要考慮外在不確定性的測試(
> 不需要伸手去別人家要資料)，在撰寫測試程式上相對較為容易，且每次執行測試所花費的時間也較少。

### 單元測試的三大定義

Martin Fowler 對單元測試整理出三點基本定義

1. 單元測試應開要是低層次的(不誇越邊界)
2. 單元測試通常是用一些測試工具或框架撰寫(如 Junit)
3. 單元測試執行的速度要比其他類型的測試快(如整合測試)

> 原文如下：<br/>
> Firstly there is a notion that unit tests are low-level, focusing on a small part of the software system. Secondly
> unit
> tests are usually written these days by the programmers themselves using their regular tools - the only difference
> being
> the use of some sort of unit testing framework. Thirdly unit tests are expected to be significantly faster than other
> kinds of tests.

<seealso>
   <category ref="why">
      <a href="https://martinfowler.com/bliki/UnitTest.html">UnitTest</a>
      <a href="https://martinfowler.com/bliki/TestPyramid.html">TestPyramid</a>
      <a href="https://martinfowler.com/articles/practical-test-pyramid.html">The Practical Test Pyramid</a>
   </category>
</seealso>