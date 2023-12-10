package com.example.student;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StudentScore {
  private Integer chinese;
  private Integer math;
  private Integer english;

  public Integer getAverage() {
    return (this.chinese + this.math + this.english) / 3;
  }
}
