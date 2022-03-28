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

package io.service84.library.featureflag.services.impl.localpersistence.persistence.model;

import java.time.LocalDateTime;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Audited
@EntityListeners(AuditingEntityListener.class)
public class FlagValue {
  @CreatedBy private String createdBy;
  @CreatedDate private LocalDateTime createdDate;
  @LastModifiedBy private String modifiedBy;
  @LastModifiedDate private LocalDateTime modifiedDate;

  @Id
  @GeneratedValue(generator = "UUID")
  @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
  private UUID id;

  @ManyToOne private Flag flag;

  @Column(nullable = false)
  private Boolean value;

  protected FlagValue() {}

  public FlagValue(Flag flag) {
    this.flag = flag;
    this.value = Boolean.FALSE;
  }

  public FlagValue(Flag flag, Boolean value) {
    this.flag = flag;
    this.value = value;
  }

  public Flag getFlag() {
    return flag;
  }

  public Boolean getValue() {
    return value;
  }

  public void setValue(Boolean value) {
    this.value = value;
  }
}
