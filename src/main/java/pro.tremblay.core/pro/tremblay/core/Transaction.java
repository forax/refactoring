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

import javax.annotation.concurrent.ThreadSafe;

import static java.util.Objects.requireNonNull;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * A transaction that happened on a position.
 */
@ThreadSafe
public record Transaction(
    /** Type of transaction */
    TransactionType type,
    /** Date at which the transaction occurred */
    LocalDate date,
    /** 
     * Amount of cash exchanged during the transaction.
     * The amount is always positive, the side of the transaction is determined by its type
     */
    BigDecimal cash,
    /** Security bought or sold if a security is involved in the transaction */
    Security security,
    /**
     * Quantity of securities exchanged during the transaction.
     * The quantity is always positive, the side of the transaction is determined by its type
     */
    BigDecimal quantity
    ) {
  public Transaction {
    requireNonNull(type);
    requireNonNull(date);
    requireNonNull(cash);
    requireNonNull(quantity);
    if(cash.compareTo(BigDecimal.ZERO) < 0) {
      throw new IllegalArgumentException("cash must be posotive");
    }
    if(quantity.compareTo(BigDecimal.ZERO) < 0) {
      throw new IllegalArgumentException("quantity must be posotive");
    }
  }
}
