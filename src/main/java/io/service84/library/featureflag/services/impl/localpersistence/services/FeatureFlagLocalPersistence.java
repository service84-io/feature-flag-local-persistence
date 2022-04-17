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

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import io.service84.library.featureflag.model.FlagPage;
import io.service84.library.featureflag.services.FeatureFlagAPIBase;
import io.service84.library.featureflag.services.impl.localpersistence.persistence.models.Flag;
import io.service84.library.featureflag.services.impl.localpersistence.persistence.models.FlagUserValue;
import io.service84.library.featureflag.services.impl.localpersistence.persistence.models.FlagValue;
import io.service84.library.featureflag.services.impl.localpersistence.persistence.repositories.FlagRepository;
import io.service84.library.featureflag.services.impl.localpersistence.persistence.repositories.FlagUserValueRepository;
import io.service84.library.featureflag.services.impl.localpersistence.persistence.repositories.FlagValueRepository;

@Service("2A94DE64-B0EE-4BD2-B4D0-F845F469C373")
public class FeatureFlagLocalPersistence extends FeatureFlagAPIBase {
  private static final Logger logger = LoggerFactory.getLogger(FeatureFlagLocalPersistence.class);

  @Autowired private Translator translator;
  @Autowired private FlagRepository flagRepository;
  @Autowired private FlagValueRepository fvRepository;
  @Autowired private FlagUserValueRepository fuvRepository;

  @Override
  public void createFlag(String flagName) {
    logger.debug("createFlag");
    getFlagObject(flagName);
  }

  @Override
  public FlagPage getFlags(String cursor, Integer limit) {
    logger.debug("getFlags");
    Pageable pageable = translator.getPageable(cursor, limit);
    Page<Flag> flagPage = flagRepository.findAll(pageable);
    return translator.translateFlagPage(flagPage);
  }

  /*
   * @deprecated Use getValue instead
   */
  @Deprecated(since = "1.3.0")
  @Override
  public Boolean getFlag(String flagName, String user, Boolean defaultValue) {
    logger.debug("getFlag");
    return getValue(flagName, user, defaultValue);
  }

  @Override
  public Boolean getValue(String flagName, String user) {
    logger.debug("getValue");
    return getValue(flagName, user, Boolean.TRUE);
  }

  @Override
  public Boolean getValue(String flagName, String user, Boolean defaultValue) {
    logger.debug("getValue");
    Flag flag = getFlagObject(flagName);
    Optional<FlagUserValue> flagUserValue = fuvRepository.getByFlagAndUserIdentity(flag, user);

    if (flagUserValue.isPresent()) {
      return flagUserValue.get().getValue();
    }

    Optional<FlagValue> flagValue = fvRepository.getByFlag(flag);

    if (flagValue.isPresent()) {
      return flagValue.get().getValue();
    }

    return defaultValue;
  }

  @Override
  public void setValue(String flagName, Boolean value) {
    logger.debug("setValue");
    Flag flag = getFlagObject(flagName);

    try {
      setValueHelper(flag, value);
    } catch (Exception e) {
      // This is a catch all, Transaction issues, Unique Violation, and others
      // We should catch specific Exceptions.
      // Final Attempt
      setValueHelper(flag, value);
    }
  }

  private void setValueHelper(Flag flag, Boolean value) {
    FlagValue flagValue = fvRepository.getByFlag(flag).orElse(new FlagValue(flag));
    flagValue.setValue(value);
    fvRepository.saveAndFlush(flagValue);
  }

  @Override
  public void setValue(String flagName, String user, Boolean value) {
    logger.debug("setValue");
    Flag flag = getFlagObject(flagName);

    try {
      setValueHelper(flag, user, value);
    } catch (Exception e) {
      // This is a catch all, Transaction issues, Unique Violation, and others
      // We should catch specific Exceptions.
      // Final Attempt
      setValueHelper(flag, user, value);
    }
  }

  private void setValueHelper(Flag flag, String user, Boolean value) {
    FlagUserValue flagUserValue =
        fuvRepository.getByFlagAndUserIdentity(flag, user).orElse(new FlagUserValue(flag, user));
    flagUserValue.setValue(value);
    fuvRepository.saveAndFlush(flagUserValue);
  }

  @Override
  public void clearAllUserValues(String flagName) {
    logger.debug("clearAllUserValues");
    Flag flag = getFlagObject(flagName);
    fuvRepository.deleteByFlag(flag);
  }

  @Override
  public void clearValue(String flagName) {
    logger.debug("clearValue");
    Flag flag = getFlagObject(flagName);
    Optional<FlagValue> flagValue = fvRepository.getByFlag(flag);

    if (flagValue.isPresent()) {
      fvRepository.delete(flagValue.get());
    }
  }

  @Override
  public void clearValue(String flagName, String user) {
    logger.debug("clearValue");
    Flag flag = getFlagObject(flagName);
    Optional<FlagUserValue> flagUserValue = fuvRepository.getByFlagAndUserIdentity(flag, user);

    if (flagUserValue.isPresent()) {
      fuvRepository.delete(flagUserValue.get());
    }
  }

  private Flag getFlagObject(String flagName) {
    try {
      return getFlagObjectHelper(flagName);
    } catch (Exception e) {
      // This is a catch all, Transaction issues, Unique Violation, and others
      // We should catch specific Exceptions.
      // Final Attempt
      return getFlagObjectHelper(flagName);
    }
  }

  private Flag getFlagObjectHelper(String flagName) {
    Flag flag = flagRepository.getByName(flagName).orElse(new Flag(flagName));
    return flagRepository.saveAndFlush(flag);
  }
}
