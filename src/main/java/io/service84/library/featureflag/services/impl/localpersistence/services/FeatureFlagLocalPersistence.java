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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.service84.library.featureflag.services.FeatureFlagAPIBase;
import io.service84.library.featureflag.services.impl.localpersistence.persistence.model.FlagUserValue;
import io.service84.library.featureflag.services.impl.localpersistence.persistence.model.FlagValue;
import io.service84.library.featureflag.services.impl.localpersistence.persistence.repository.FlagUserValueRepository;
import io.service84.library.featureflag.services.impl.localpersistence.persistence.repository.FlagValueRepository;

@Service("2A94DE64-B0EE-4BD2-B4D0-F845F469C373")
public class FeatureFlagLocalPersistence extends FeatureFlagAPIBase {
  @Autowired private FlagValueRepository fvRepository;
  @Autowired private FlagUserValueRepository fuvRepository;

  @Override
  public Boolean getFlag(String flag, String user, Boolean defaultValue) {
    Optional<FlagUserValue> flagUserValue = fuvRepository.getByFlagAndUser(flag, user);

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
  public void setValue(String flag, Boolean value) {
    try {
      setValueHelper(flag, value);
    } catch (Exception e) {
      // This is a catch all, Transaction issues, Unique Violation, and others
      // We should catch specific Exceptions.
      // Final Attempt
      setValueHelper(flag, value);
    }
  }

  private void setValueHelper(String flag, Boolean value) {
    FlagValue flagValue = fvRepository.getByFlag(flag).orElse(new FlagValue(flag));
    flagValue.setValue(value);
    fvRepository.saveAndFlush(flagValue);
  }

  @Override
  public void setValue(String flag, String user, Boolean value) {
    try {
      setValueHelper(flag, user, value);
    } catch (Exception e) {
      // This is a catch all, Transaction issues, Unique Violation, and others
      // We should catch specific Exceptions.
      // Final Attempt
      setValueHelper(flag, user, value);
    }
  }

  private void setValueHelper(String flag, String user, Boolean value) {
    FlagUserValue flagUserValue =
        fuvRepository.getByFlagAndUser(flag, user).orElse(new FlagUserValue(flag, user));
    flagUserValue.setValue(value);
    fuvRepository.saveAndFlush(flagUserValue);
  }

  @Override
  public void clearValue(String flag) {
    Optional<FlagValue> flagValue = fvRepository.getByFlag(flag);

    if (flagValue.isPresent()) {
      fvRepository.delete(flagValue.get());
    }
  }

  @Override
  public void clearValue(String flag, String user) {
    Optional<FlagUserValue> flagUserValue = fuvRepository.getByFlagAndUser(flag, user);

    if (flagUserValue.isPresent()) {
      fuvRepository.delete(flagUserValue.get());
    }
  }
}
