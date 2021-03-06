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

package io.service84.library.featureflaglocalpersistence.services;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import io.service84.library.featureflag.model.FlagPage;
import io.service84.library.featureflaglocalpersistence.persistence.models.Flag;
import io.service84.library.standardpersistence.services.PaginationTranslator;

@Service("5E062AC0-FC15-41FB-83C1-58D190563938")
public class Translator extends PaginationTranslator {
  private static final Logger logger = LoggerFactory.getLogger(FeatureFlagLocalPersistence.class);

  public static class FFLPFlagPage extends FlagPage implements PaginationDataStandard {
    public FFLPFlagPage() {}

    @Override
    public void setIndex(String index) {
      super.setCursor(index);
    }

    @Override
    public void setNextIndex(String nextIndex) {
      super.setNextCursor(nextIndex);
    }
  }

  public FlagPage translateFlagPage(Page<Flag> page) {
    logger.debug("translateFlagPage");
    FlagPage flagPage = this.metadata(page, FFLPFlagPage.class);
    flagPage.setFlags(translateFlagList(page.getContent()));
    return flagPage;
  }

  public List<String> translateFlagList(List<Flag> list) {
    logger.debug("translateFlagList");
    return list.stream().map(f -> f.getName()).collect(Collectors.toList());
  }
}
