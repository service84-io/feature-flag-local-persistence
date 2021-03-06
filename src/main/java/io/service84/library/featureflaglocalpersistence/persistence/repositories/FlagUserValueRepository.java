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

package io.service84.library.featureflaglocalpersistence.persistence.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import io.service84.library.featureflaglocalpersistence.persistence.models.Flag;
import io.service84.library.featureflaglocalpersistence.persistence.models.FlagUserValue;

@Repository("3FDAD111-5D35-4E5A-9C07-53EC70C83BED")
public interface FlagUserValueRepository
    extends JpaRepository<FlagUserValue, UUID>, JpaSpecificationExecutor<FlagUserValue> {
  Optional<FlagUserValue> getByFlagAndUserIdentity(Flag flag, String user);

  void deleteByFlag(Flag flag);
}
