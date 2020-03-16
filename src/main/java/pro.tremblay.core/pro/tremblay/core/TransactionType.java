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

/**
 * Type of transactions.
 */
@ThreadSafe
public enum TransactionType {
  /** Securities were bought using cash */
  BUY(true),
  /** Securities were sold to get cash */
  SELL(true),
  /** Cash was added to the position */
  DEPOSIT(false),
  /** Cash was removed from the position */
  WITHDRAWAL(false);

  private final boolean hasQuantity;

  private TransactionType(boolean hasQuantity) {
    this.hasQuantity = hasQuantity;
  }

  /**
   * Tells if this type of transactions involve a security movement.
   *
   * @return if some security quantity was exchanged
   */
  public boolean hasQuantity() {
    return hasQuantity;
  }
}
