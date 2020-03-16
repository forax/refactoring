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

import static java.util.function.Function.identity;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import pro.tremblay.core.Preferences.Key;

@SuppressWarnings("static-method")
public class PreferencesTest {
  private final Preferences preferences = Preferences.ofProperty();

  @BeforeEach
  public void setup() {
    System.setProperty("a_property", "123");
    System.setProperty("another_property", "text");
  }

  @AfterEach
  public void after() {
    System.clearProperty("a_property");
    System.clearProperty("another_property");
  }
  
  @Test
  public void getString_property() {
    var key = new Key<>("a_property", String.class, identity());
    assertEquals("123", preferences.get(key).orElseThrow());
  }
  
  @Test
  public void getInteger_property() {
    var key = new Key<>("a_property", Integer.class, Integer::parseInt);
    assertEquals(123, preferences.get(key).orElseThrow());
  }
  
  @Test
  public void getInteger_property_invalidNumber() {
    var key = new Key<>("another_property", Integer.class, Integer::parseInt);
    assertThrows(NumberFormatException.class, () -> preferences.get(key).orElseThrow());
  }
  
  @Test
  public void getString_unknownPreference() {
    var key = new Key<>("aaa", String.class, identity());
    assertTrue(preferences.get(key).isEmpty());
  }

  @Test
  public void getInteger_unknownPreference() {
    var key = new Key<>("aaa", Integer.class, Integer::parseInt);
    assertThrows(IllegalArgumentException.class, () -> preferences.get(key).orElseThrow(IllegalArgumentException::new));
  }

  @Test
  public void getString() {
    var key = new Key<>("a", String.class, identity());
    var preferences = this.preferences.or(Preferences.of(key, "value"));
    assertEquals("value", preferences.get(key).orElseThrow());
  }

  @Test
  public void getInteger() {
    var key = new Key<>("a", Integer.class, Integer::parseInt);
    var preferences = this.preferences.or(Preferences.of(key, 123));
    assertEquals(123, preferences.get(key).orElseThrow());
  }
}
