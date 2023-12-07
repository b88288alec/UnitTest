# 第一個單元測試

假設我們正在為某間學校開發一個學生成績排名的系統，而計算排名就需要先計算每一位學生所有科目的成績，用以計算總平均，來排名。

而我們希望可以透過``StudentGrades``類別，來完成計算總平均這件事，就如下面這段程式碼
```Java
public StudentGrades {
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
public StudentGradesTest {

    @Test
    void testAverage() {
        var grades = new StudentGrades(74, 62, 92);
        var average grades.getAverage();
        assertThat(average).isEqualTo(76);
    }
}
```
夠簡單吧，但是現實幾乎不可能有像``StudentGrades``這種完全不依賴其它物件的類別。