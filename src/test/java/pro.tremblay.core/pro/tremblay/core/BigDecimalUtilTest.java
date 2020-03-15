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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static pro.tremblay.core.BigDecimalUtil.bd;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

@SuppressWarnings("static-method")
public class BigDecimalUtilTest {
  @Test
  public void bdInt() {
    assertEquals(BigDecimal.valueOf(4), bd(4));
  }

  @Test
  public void bdString() {
    assertEquals(new BigDecimal("4.12"), bd("4.12"));
  }
}
