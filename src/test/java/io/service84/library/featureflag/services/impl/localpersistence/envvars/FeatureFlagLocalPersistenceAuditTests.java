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

package io.service84.library.featureflag.services.impl.localpersistence.envvars;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManagerFactory;

import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import io.service84.library.featureflag.services.impl.localpersistence.persistence.model.FlagUserValue;
import io.service84.library.featureflag.services.impl.localpersistence.persistence.model.FlagValue;
import io.service84.library.featureflag.services.impl.localpersistence.persistence.repository.FlagUserValueRepository;
import io.service84.library.featureflag.services.impl.localpersistence.persistence.repository.FlagValueRepository;
import io.service84.library.featureflag.services.impl.localpersistence.services.FeatureFlagLocalPersistence;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@EnableJpaAuditing
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class FeatureFlagLocalPersistenceAuditTests {
  @TestConfiguration
  public static class Configuration {
    @Autowired private EntityManagerFactory entityManagerFactory;

    @Bean
    AuditReader auditReader() {
      return AuditReaderFactory.get(entityManagerFactory.createEntityManager());
    }

    @Bean
    public FeatureFlagLocalPersistence getKeyValueService() {
      return new FeatureFlagLocalPersistence();
    }
  }

  @Autowired private FeatureFlagLocalPersistence fflpService;
  @Autowired private FlagValueRepository fvRepository;
  @Autowired private FlagUserValueRepository fuvRepository;
  @Autowired private AuditReader auditReader;

  @Test
  public void exists() {
    assertNotNull(fflpService);
    assertNotNull(fvRepository);
    assertNotNull(fuvRepository);
    assertNotNull(auditReader);
  }

  private UUID getFlagValueId(FlagValue flagValue) {
    try {
      Field field = flagValue.getClass().getDeclaredField("id");
      field.setAccessible(true);
      return (UUID) field.get(flagValue);
    } catch (Exception e) {
      return null;
    }
  }

  private UUID getFlagUserValueId(FlagUserValue flagUserValue) {
    try {
      Field field = flagUserValue.getClass().getDeclaredField("id");
      field.setAccessible(true);
      return (UUID) field.get(flagUserValue);
    } catch (Exception e) {
      return null;
    }
  }

  @Test
  public void isAuditedClass() {
    assertTrue(auditReader.isEntityClassAudited(FlagValue.class));
    assertTrue(auditReader.isEntityClassAudited(FlagUserValue.class));
  }

  @Test
  public void isAuditedName() {
    assertTrue(auditReader.isEntityNameAudited(FlagValue.class.getCanonicalName()));
    assertTrue(auditReader.isEntityNameAudited(FlagUserValue.class.getCanonicalName()));
  }

  @Test
  public void flagValueVersions() {
    String flag = UUID.randomUUID().toString();
    Boolean value = Boolean.TRUE;

    for (int revision = 0; revision < 10; revision++) {
      fflpService.setValue(flag, value);
      value = !value;
    }

    FlagValue flagValue = fvRepository.getByFlag(flag).get();
    UUID id = getFlagValueId(flagValue);
    List<Number> revisions = auditReader.getRevisions(FlagValue.class, id);
    assertEquals(10, revisions.size());
  }

  @Test
  public void flagUserValueVersions() {
    String flag = UUID.randomUUID().toString();
    String user = UUID.randomUUID().toString();
    Boolean value = Boolean.TRUE;

    for (int revision = 0; revision < 10; revision++) {
      fflpService.setValue(flag, user, value);
      value = !value;
    }

    FlagUserValue flagUserValue = fuvRepository.getByFlagAndUser(flag, user).get();
    UUID id = getFlagUserValueId(flagUserValue);
    List<Number> revisions = auditReader.getRevisions(FlagUserValue.class, id);
    assertEquals(10, revisions.size());
  }
}
