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

import org.junit.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;
import static pro.tremblay.core.SecurityPosition.securityPosition;

public class SecurityPositionTest {

    @Test
    public void isFlat_notFlat() {
        SecurityPosition securityPosition = securityPosition(Security.GOOGL, BigDecimal.TEN);
        assertThat(securityPosition.isFlat()).isFalse();
    }

    @Test
    public void isFlat_flat() {
        SecurityPosition securityPosition = securityPosition(Security.GOOGL, BigDecimal.ZERO);
        assertThat(securityPosition.isFlat()).isTrue();
    }

    @Test
    public void testToString() {
        SecurityPosition securityPosition = securityPosition(Security.GOOGL, BigDecimal.TEN);
        assertThat(securityPosition.toString()).isEqualTo("SecurityPosition{security=GOOGL, quantity=10}");
    }
}