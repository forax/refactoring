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
import static java.util.Arrays.copyOf;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;
import static java.util.stream.IntStream.range;
import static pro.tremblay.core.Security.securities;

import java.math.BigDecimal;
import java.util.Arrays;

import javax.annotation.concurrent.NotThreadSafe;

/**
 * All positions (cash and security) of a user. There is only one cash position
 * since we are trading in only one currency.
 */
@NotThreadSafe
public final class Position {
  private static final int SECURITY_COUNT = securities().size();

  private BigDecimal cash;
  private final BigDecimal[] quantities;

  private Position(BigDecimal cash, BigDecimal[] quantities) {
    this.cash = cash;
    this.quantities = quantities;
  }

  public Position(BigDecimal cash) {
    this.cash = requireNonNull(cash);
    BigDecimal[] quantities = new BigDecimal[SECURITY_COUNT];
    Arrays.fill(quantities, ZERO);
    this.quantities = quantities;
  }
  
  public Position duplicate() {
    return new Position(cash, copyOf(quantities, quantities.length));
  }

  public BigDecimal cash() {
    return cash;
  }

  public Position cash(BigDecimal cash) {
    this.cash = requireNonNull(cash);
    return this;
  }

  public BigDecimal quantity(Security security) {
    requireNonNull(security);
    return quantities[security.ordinal()];
  }

  public Position quantity(Security security, BigDecimal quantity) {
    requireNonNull(security);
    requireNonNull(quantity);
    quantities[security.ordinal()] = quantity;
    return this;
  }
  
  @Override
  public String toString() {
      return "Position{" +
          "cash=" + cash +
          ", quantities=" + range(0, quantities.length).mapToObj(i -> securities().get(i) + ": " + quantities[i]).collect(joining(", ", "[", "]")) + 
          '}';
  }
}
