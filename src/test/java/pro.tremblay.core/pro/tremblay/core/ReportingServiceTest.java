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
import static java.time.LocalDate.now;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static pro.tremblay.core.BigDecimalUtil.bd;
import static pro.tremblay.core.Preferences.ofProperty;
import static pro.tremblay.core.Security.GOOGL;
import static pro.tremblay.core.TransactionType.BUY;
import static pro.tremblay.core.TransactionType.DEPOSIT;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import pro.tremblay.core.Preferences.Key;

@SuppressWarnings("static-method")
public class ReportingServiceTest {
  public static final Key<Integer> LENGTH_OF_YEAR = new Key<>("LENGTH_OF_YEAR", Integer.class, Integer::parseInt);
  
  private final ReportingService reportingService = new ReportingService();
  private final Position current = new Position().cash(ZERO).securityPositions(new ArrayList<>());
  
  @BeforeEach
  public void setup() {
    System.setProperty(LENGTH_OF_YEAR.name(), "360");
  }

  @AfterEach
  public void after() {
    System.clearProperty(LENGTH_OF_YEAR.name());
  }

  @Test
  public void calculateReturnOnInvestmentYTD_noTransactionAndPosition() {
    var yearLength = ofProperty().get(LENGTH_OF_YEAR).orElseThrow();
    var roi = reportingService.calculateReturnOnInvestmentYTD(current, yearLength, Collections.emptyList());
    assertEquals(bd(0.00), roi);
  }

  @Test
  public void calculateReturnOnInvestmentYTD_cashAdded() {
    var yearLength = ofProperty().get(LENGTH_OF_YEAR).orElseThrow();
    current.cash(bd(200));

    var now = now();
    var transactions = List.of(new Transaction(DEPOSIT, now.minusDays(10), bd(100), null, ZERO));

    var roi = reportingService.calculateReturnOnInvestmentYTD(current, yearLength, transactions);

    // Current cash value = 200$, Current security value = 0$
    // Initial cash value = 100$, Initial cash value = 0$
    // Year length = 360
    var actual = bd((200.0 - 100.0) / 100.0 * 100.0 * 360.0 / now.getDayOfYear());
    assertEquals(actual, roi);
  }

  @Test
  public void calculateReturnOnInvestmentYTD_secBought() {
    var yearLength = ofProperty().get(LENGTH_OF_YEAR).orElseThrow();
    current.getSecurityPositions().add(new SecurityPosition().security(GOOGL).quantity(bd(50)));

    var now = now();
    var priceAtTransaction = PriceService.getPrice(now.minusDays(10), GOOGL);

    var transaction = new Transaction(BUY, now.minusDays(10), priceAtTransaction.multiply(bd(50)), GOOGL, bd(50));
    var transactions = Collections.singleton(transaction);

    var roi = reportingService.calculateReturnOnInvestmentYTD(current, yearLength, transactions);

    var priceNow = PriceService.getPrice(now, GOOGL);

    var initialCashValue = transaction.cash(); // initialSecValue = 0
    var currentSecValue = priceNow.multiply(bd(50)); // currentCashValue = 0

    var actual = currentSecValue.subtract(initialCashValue).divide(initialCashValue, 10, HALF_UP)
        .multiply(bd(100)).multiply(bd(360)).divide(bd(now.getDayOfYear()), 2, HALF_UP);
    assertEquals(actual, roi);
  }
}
