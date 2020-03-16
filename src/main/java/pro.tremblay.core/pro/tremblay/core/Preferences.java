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

import static java.util.Objects.requireNonNull;

import java.util.Optional;
import java.util.function.Function;

import javax.annotation.concurrent.ThreadSafe;

/**
 * A set of preferences of the application.
 */
@ThreadSafe
public interface Preferences {
  /**
   * A preference key. 
   * 
   * @param <T> the type of the key.
   */
  public record Key<T>(
      /** Name of the preference key */
      String name,
      /** Type of the preference key */
      Class<T> type,
      /** Function that decodes a String as a value of the type of the preference key */
      Function<? super String, ? extends T> decoder) {
    public Key {
      requireNonNull(name);
      requireNonNull(type);
      requireNonNull(decoder);
    }
  }
  
  /**
   * Returns the value of the key as an optional.
   * @param <T> the type of the key.
   * @param key the preference key.
   * @return the value of the key as an optonal.
   */
  <T> Optional<T> get(Key<T> key);
  
  /**
   * Returns a set preference that first look a key into the current preferences
   * and if not found look up to the preferences taken as parameter.
   * @param preferences the preferences used if the key is not found in the
   *        current preferences.
   * @return
   */
  default Preferences or(Preferences preferences) {
    return new Preferences() {
      @Override
      public <T> Optional<T> get(Key<T> key) {
        return Preferences.this.get(key).or(() -> preferences.get(key));
      }
    };
  }
  
  /**
   * Creates a set of preferences with only one key/value.
   * @param <T> the type of the key
   * @param key the key
   * @param value the value
   * @return a newly created preference with the key and the value. 
   */
  public static <T> Preferences of(Key<T> key, T value) {
    return new Preferences() {
      @Override
      public <U> Optional<U> get(Key<U> key2) {
        return (key == key2)? Optional.of(key2.type.cast(value)): Optional.empty();
      }
    };
  }
  
  /*private*/ enum PropertyPreferences implements Preferences {
    INSTANCE;
    
    @Override
    public <U> Optional<U> get(Key<U> key) {
      return Optional.ofNullable(System.getProperty(key.name)).map(key.decoder);
    }
  }
  
  /**
   * A set of preferences that maps the system properties.
   * Given that the system property values are strings, the {@link Key#decoder} is used
   * to transform the string into a real object of the right type.
   * @return a set of preferences that maps the system properties.
   */
  public static Preferences ofProperty() {
    return PropertyPreferences.INSTANCE;
  }
}
