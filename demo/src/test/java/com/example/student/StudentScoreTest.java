package com.example.student;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class StudentScoreTest {

  @Test
  void testAverage() {
    var grades = new StudentScore(74, 62, 92);
    var average = grades.getAverage();
    Assertions.assertThat(average).isEqualTo(76);
  }

}
