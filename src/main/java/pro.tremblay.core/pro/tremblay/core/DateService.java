package pro.tremblay.core;

import static java.util.Objects.requireNonNull;

import java.time.LocalDate;
import java.util.function.Supplier;

import javax.annotation.Nonnull;

/**
 * A service that provides the current date.
 */
public class DateService {
  private final Supplier<? extends LocalDate> currentDateSupplier;

  /**
   * Creates a date service from a supplier of the current date.
   * @param currentDateSupplier a supplier of the current date.
   */
  public DateService(@Nonnull Supplier<? extends LocalDate> currentDateSupplier) {
    this.currentDateSupplier = requireNonNull(currentDateSupplier);
  }
  
  /**
   * Returns the current date.
   * @return the current date
   */
  public @Nonnull LocalDate currentDate() {
    return currentDateSupplier.get();
  }
}
