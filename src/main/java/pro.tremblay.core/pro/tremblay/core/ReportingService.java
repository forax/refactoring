/*
 * Copyright 2019-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package pro.tremblay.core;

import static java.math.BigDecimal.ZERO;
import static java.math.RoundingMode.HALF_UP;
import static java.math.RoundingMode.UNNECESSARY;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;
import static pro.tremblay.core.BigDecimalUtil.bd;
import static pro.tremblay.core.Preferences.LENGTH_OF_YEAR;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Comparator;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;

/**
 * Service reporting useful information on a position.
 */
@ThreadSafe
public class ReportingService {
  private final Preferences preferences;
  private final PriceService priceService;
  private final DateService dateService;

  /**
   * Create the reporting service from preferences, a price service and a date service.
   * @param preferences the preferences used to get the {@code LENGTH_OF_YEAR}
   * @param priceService the price service to get the price of the security
   * @param dateService the date service to get the current time
   */
  public ReportingService(@Nonnull Preferences preferences, @Nonnull PriceService priceService, @Nonnull DateService dateService) {
    this.preferences = requireNonNull(preferences);
    this.priceService = requireNonNull(priceService);
    this.dateService = requireNonNull(dateService);
  }

  /**
   * Calculate the annualized return on investment since the beginning of the year
   * (Year To Date). We use the simplest method possible. We have the following
   * <ul>
   * <li>Current value: {@code cash + security_quantity * current_security_price}
   * based on the current position</li>
   * <li>Initial value:
   * {@code cash + security_quantity * start_of_year_security_price} based on the
   * start of year position</li>
   * <li>Absolute ROI:
   * {@code (current_value - initial_value) / initial_value * 100}</li>
   * <li>Year length: As set in the preferences</li>
   * <li>Annualized ROI :
   * {@code absolute_roi * year_length / days_since_beginning_of_year}</li>
   * </ul>
   *
   * Then formula is {@code (current_value - initial_value) / initial_value}.
   *
   * @param current      the current position of today, won't be modified by this
   *                     call
   * @param transactions all transactions on this position, they are not sorted
   *                     and might be before the beginning of the year
   * @return annualized return on investment since beginning of the year
   */
  @Nonnull
  public BigDecimal calculateReturnOnInvestmentYTD(@Nonnull Position current, @Nonnull Collection<Transaction> transactions) {
    requireNonNull(current);
    requireNonNull(transactions);
      
    var now = dateService.currentDate();
    var beginningOfYear = now.withDayOfYear(1);

    var working = current.duplicate();

    var orderedTransaction = transactions.stream()
        .sorted(Comparator.comparing(Transaction::date).reversed()).collect(toList());

    var today = now;
    var transactionIndex = 0;
    while (!today.isBefore(beginningOfYear)) {
      if (transactionIndex >= orderedTransaction.size()) {
        break;
      }
      var transaction = orderedTransaction.get(transactionIndex);
      // It's a transaction on the date, process it
      if (transaction.date().equals(today)) {
        revert(working, transaction);
      }
      today = today.minusDays(1);
    }

    var initialCashValue = working.cash();
    var currentCashValue = current.cash();

    var initialSecPosValue = securitiesPositionValue(working, beginningOfYear);
    var currentSecPosValue = securitiesPositionValue(current, now);

    var initialValue = initialCashValue.add(initialSecPosValue);

    BigDecimal roi;
    if (initialValue.signum() == 0) {
      roi = ZERO.setScale(10, UNNECESSARY);
    } else {
      roi = currentCashValue.add(currentSecPosValue).subtract(initialValue)
          .divide(initialValue, 10, HALF_UP).multiply(bd(100));
    }
    var yearLength = preferences.get(LENGTH_OF_YEAR).orElseThrow();
    roi = roi.multiply(bd(yearLength)).divide(bd(now.getDayOfYear()), 2, HALF_UP);
    return roi;
  }

  private BigDecimal securitiesPositionValue(Position position, LocalDate date) {
    // using a stream here is less efficient
    var sum = ZERO;
    for(var security: Security.securities()) {
      sum = sum.add(position.quantity(security).multiply(priceService.getPrice(date, security)));
    }
    return sum;
  }
  
  private static void revert(Position current, Transaction transaction) {
    switch (transaction.type()) {
    case BUY -> {
      current.cash(current.cash().add(transaction.cash()));
      current.quantity(transaction.security(), current.quantity(transaction.security()).subtract(transaction.quantity()));
    }
    case SELL -> {
      current.cash(current.cash().subtract(transaction.cash()));
      current.quantity(transaction.security(), current.quantity(transaction.security()).add(transaction.quantity()));
    }
    case DEPOSIT -> current.cash(current.cash().subtract(transaction.cash()));
    case WITHDRAWAL -> current.cash(current.cash().add(transaction.cash()));
    }
  }
}
