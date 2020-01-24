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

import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;

import static org.assertj.core.api.Assertions.*;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.mock;
import static org.easymock.EasyMock.replay;
import static pro.tremblay.core.BigDecimalUtil.bd;
import static pro.tremblay.core.Position.position;
import static pro.tremblay.core.Transaction.transaction;

public class ReportingServiceTest {

    private Preferences preferences = mock(Preferences.class);
    private StrangeTimeSource timeSource = new StrangeTimeSource();
    private PriceService priceService = mock(PriceService.class);

    private ReportingService reportingService = new ReportingService(preferences, timeSource, priceService);

    private final Position current = position()
            .cash(BigDecimal.ZERO);

    private final LocalDate today = LocalDate.ofYearDay(2019, 200);
    private final LocalDate hundredDaysAgo = today.minusDays(100);

    @Before
    public void setup() {
        expect(preferences.getInteger(ReportingService.LENGTH_OF_YEAR)).andStubReturn(360);
        replay(preferences);

        timeSource.today(today);
    }

    @Test
    public void calculateReturnOnInvestmentYTD_noTransactionAndPosition() {
        BigDecimal roi = reportingService.calculateReturnOnInvestmentYTD(current, Collections.emptyList());
        assertThat(roi).isEqualTo("0.00");
    }

    @Test
    public void calculateReturnOnInvestmentYTD_cashAdded() {
        current.cash(bd(200));

        Collection<Transaction> transactions = Collections.singleton(
                transaction()
                        .cash(bd(100))
                        .type(TransactionType.DEPOSIT)
                        .date(hundredDaysAgo));

        BigDecimal roi = reportingService.calculateReturnOnInvestmentYTD(current, transactions);
        assertThat(roi).isEqualTo("180.00"); // (200$ - 100$) / 100$ * 100% * 360 days / 200 days
    }

    @Test
    public void calculateReturnOnInvestmentYTD_secBought() {
        current.addSecurityPosition(Security.GOOGL, bd(50));

        Collection<Transaction> transactions = Collections.singleton(
            transaction()
                .security(Security.GOOGL)
                .quantity(bd(50))
                .cash(bd(100))
                .type(TransactionType.BUY)
                .date(hundredDaysAgo));

        expect(priceService.getPrice(today, Security.GOOGL)).andStubReturn(bd(2));
        replay(priceService);

        BigDecimal roi = reportingService.calculateReturnOnInvestmentYTD(current, transactions);

        // Cash value now: 0, Sec value now: 50 x 2 = 100, Total: 100
        // Cash value initial: 100, Sec value now: 0, Total: 100
        // (100$ - 100$) / 100$ * 100% * 360 days / 200 days
        assertThat(roi).isEqualTo("0.00");
    }

    @Test
    public void testTwoArgsConstructor() {
        new ReportingService(new Preferences(), new StrangeTimeSource().today(LocalDate.now()));
    }
}
