/*
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

package io.service84.library.featureflag.services.impl.localpersistence.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import io.service84.library.featureflag.model.FlagPage;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class FeatureFlagLocalPersistenceTests {
  public static class AClass {
    public String memberA;
    public String memberB;
  }

  @TestConfiguration
  public static class Configuration {
    @Bean
    public FeatureFlagLocalPersistence getKeyValueService() {
      return new FeatureFlagLocalPersistence();
    }

    @Bean
    public Translator getTranslator() {
      return new Translator();
    }
  }

  // Test Subject
  @Autowired private FeatureFlagLocalPersistence fflpService;

  @Test
  public void existenceTest() {
    assertNotNull(fflpService);
  }

  @Test
  public void defaultFalse() {
    String flag = UUID.randomUUID().toString();
    String user = UUID.randomUUID().toString();
    Boolean value = fflpService.getFlag(flag, user, Boolean.FALSE);
    assertFalse(value);
  }

  @Test
  public void defaultTrue() {
    String flag = UUID.randomUUID().toString();
    String user = UUID.randomUUID().toString();
    Boolean value = fflpService.getFlag(flag, user, Boolean.TRUE);
    assertTrue(value);
  }

  @Test
  public void getValueDefaultFalse() {
    String flag = UUID.randomUUID().toString();
    String user = UUID.randomUUID().toString();
    Boolean value = fflpService.getValue(flag, user, Boolean.FALSE);
    assertFalse(value);
  }

  @Test
  public void getValueDefaultTrue() {
    String flag = UUID.randomUUID().toString();
    String user = UUID.randomUUID().toString();
    Boolean value = fflpService.getValue(flag, user, Boolean.TRUE);
    assertTrue(value);
  }

  @Test
  public void getValueDefaultImplicit() {
    String flag = UUID.randomUUID().toString();
    String user = UUID.randomUUID().toString();
    Boolean value = fflpService.getValue(flag, user);
    assertTrue(value);
  }

  @Test
  public void setAndGetFlagTrue() {
    String flag = UUID.randomUUID().toString();
    String user = UUID.randomUUID().toString();
    Boolean value = Boolean.TRUE;
    fflpService.setValue(flag, value);
    Boolean gotValue = fflpService.getFlag(flag, user, Boolean.TRUE);
    assertEquals(value, gotValue);
  }

  @Test
  public void setAndGetFlagFalse() {
    String flag = UUID.randomUUID().toString();
    String user = UUID.randomUUID().toString();
    Boolean value = Boolean.FALSE;
    fflpService.setValue(flag, value);
    Boolean gotValue = fflpService.getFlag(flag, user, Boolean.TRUE);
    assertEquals(value, gotValue);
  }

  @Test
  public void setAndGetValueTrue() {
    String flag = UUID.randomUUID().toString();
    String user = UUID.randomUUID().toString();
    Boolean value = Boolean.TRUE;
    fflpService.setValue(flag, value);
    Boolean gotValue = fflpService.getValue(flag, user, Boolean.TRUE);
    assertEquals(value, gotValue);
  }

  @Test
  public void setAndGetValueFalse() {
    String flag = UUID.randomUUID().toString();
    String user = UUID.randomUUID().toString();
    Boolean value = Boolean.FALSE;
    fflpService.setValue(flag, value);
    Boolean gotValue = fflpService.getValue(flag, user, Boolean.TRUE);
    assertEquals(value, gotValue);
  }

  @Test
  public void userOverride() {
    String flag = UUID.randomUUID().toString();
    String user = UUID.randomUUID().toString();
    Boolean flagValue = Boolean.TRUE;
    Boolean userValue = !flagValue;
    fflpService.setValue(flag, user, userValue);
    fflpService.setValue(flag, flagValue);
    Boolean gotValue = fflpService.getValue(flag, user);
    assertEquals(userValue, gotValue);
  }

  @Test
  public void clearUserOverride() {
    String flag = UUID.randomUUID().toString();
    String user = UUID.randomUUID().toString();
    Boolean flagValue = Boolean.TRUE;
    Boolean userValue = !flagValue;
    fflpService.setValue(flag, user, userValue);
    fflpService.setValue(flag, flagValue);
    fflpService.clearValue(flag, user);
    Boolean gotValue = fflpService.getValue(flag, user);
    assertEquals(flagValue, gotValue);
  }

  @Test
  public void clearAllUserOverride() {
    String flag = UUID.randomUUID().toString();
    String user = UUID.randomUUID().toString();
    Boolean flagValue = Boolean.TRUE;
    Boolean userValue = !flagValue;
    fflpService.setValue(flag, user, userValue);
    fflpService.setValue(flag, flagValue);
    fflpService.clearAllUserValues(flag);
    Boolean gotValue = fflpService.getValue(flag, user);
    assertEquals(flagValue, gotValue);
  }

  @Test
  public void clearFlagValue() {
    String flag = UUID.randomUUID().toString();
    String user = UUID.randomUUID().toString();
    Boolean value = Boolean.FALSE;
    Boolean defaultValue = !value;
    fflpService.setValue(flag, value);
    fflpService.clearValue(flag);
    Boolean gotValue = fflpService.getValue(flag, user, defaultValue);
    assertEquals(defaultValue, gotValue);
  }

  @Test
  public void createFlag() {
    String flag = UUID.randomUUID().toString();
    FlagPage initialGotFlags = fflpService.getFlags(null, Integer.MAX_VALUE);
    assertFalse(initialGotFlags.getFlags().contains(flag));
    fflpService.createFlag(flag);
    FlagPage gotFlags = fflpService.getFlags(null, Integer.MAX_VALUE);
    assertTrue(gotFlags.getFlags().contains(flag));
  }

  @Test
  public void setValueCreatesFlag() {
    String flag = UUID.randomUUID().toString();
    Boolean value = Boolean.TRUE;
    FlagPage initialGotFlags = fflpService.getFlags(null, Integer.MAX_VALUE);
    assertFalse(initialGotFlags.getFlags().contains(flag));
    fflpService.setValue(flag, value);
    FlagPage gotFlags = fflpService.getFlags(null, Integer.MAX_VALUE);
    assertTrue(gotFlags.getFlags().contains(flag));
  }

  @Test
  public void clearValueCreatesFlag() {
    String flag = UUID.randomUUID().toString();
    FlagPage initialGotFlags = fflpService.getFlags(null, Integer.MAX_VALUE);
    assertFalse(initialGotFlags.getFlags().contains(flag));
    fflpService.clearValue(flag);
    FlagPage gotFlags = fflpService.getFlags(null, Integer.MAX_VALUE);
    assertTrue(gotFlags.getFlags().contains(flag));
  }

  @Test
  public void getValueCreatesFlag() {
    String flag = UUID.randomUUID().toString();
    String user = UUID.randomUUID().toString();
    FlagPage initialGotFlags = fflpService.getFlags(null, Integer.MAX_VALUE);
    assertFalse(initialGotFlags.getFlags().contains(flag));
    fflpService.getValue(flag, user);
    FlagPage gotFlags = fflpService.getFlags(null, Integer.MAX_VALUE);
    assertTrue(gotFlags.getFlags().contains(flag));
  }

  @Test
  public void setUserValueCreatesFlag() {
    String flag = UUID.randomUUID().toString();
    String user = UUID.randomUUID().toString();
    Boolean value = Boolean.TRUE;
    FlagPage initialGotFlags = fflpService.getFlags(null, Integer.MAX_VALUE);
    assertFalse(initialGotFlags.getFlags().contains(flag));
    fflpService.setValue(flag, user, value);
    FlagPage gotFlags = fflpService.getFlags(null, Integer.MAX_VALUE);
    assertTrue(gotFlags.getFlags().contains(flag));
  }

  @Test
  public void clearUserValueCreatesFlag() {
    String flag = UUID.randomUUID().toString();
    String user = UUID.randomUUID().toString();
    FlagPage initialGotFlags = fflpService.getFlags(null, Integer.MAX_VALUE);
    assertFalse(initialGotFlags.getFlags().contains(flag));
    fflpService.clearValue(flag, user);
    FlagPage gotFlags = fflpService.getFlags(null, Integer.MAX_VALUE);
    assertTrue(gotFlags.getFlags().contains(flag));
  }
}
