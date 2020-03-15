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

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class PreferencesTest {

  private final Preferences preferences = Preferences.preferences();

  @Test
  public void getString_unknownPreference() {
    assertNull(preferences.getString("aaa"));
  }

  @Test
  public void getInteger_unknownPreference() {
    assertThrows(IllegalArgumentException.class, () -> preferences.getInteger("aaa"));
  }

  @Test
  public void getString() {
    preferences.put("a", "value");
    assertEquals("value", preferences.getString("a"));
  }

  @Test
  public void getInteger() {
    preferences.put("a", "123");
    assertEquals(123, preferences.getInteger("a"));
  }

  @Test
  public void getInteger_invalidNumber() {
    preferences.put("a", "value");
    assertThrows(NumberFormatException.class, () -> preferences.getInteger("a"));
  }
}
