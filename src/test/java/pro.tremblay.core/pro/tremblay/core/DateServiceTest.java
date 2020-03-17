package pro.tremblay.core;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

@SuppressWarnings("static-method")
public class DateServiceTest {
  @Test
  public void currentDate() {
    var date = LocalDate.of(2020, 3, 1);
    var dateService = new DateService(() -> date);
    assertEquals(date, dateService.currentDate());
  }
}
