# Service84.IO FeatureFlagLocalPersistence

## License
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

## Usage
This library defines an implementation for the Service84.IO FeatureFlagAPI that
uses the default persistence to store feature flag configurations.

### Availability
This library is available from Maven Central with more information at
https://mvnrepository.com/artifact/io.service84.library/featureflaglocalpersistence

### Dependencies
This library has a compile-time and run-time dependency on the following libraries,
versions are (built-tested)

    org.springframework:spring-core:(5.0.0.RELEASE-5.3.3)
    org.springframework.data:spring-data-jpa:(2.0.0.RELEASE-2.4.3)
    org.hibernate:hibernate-core:(5.0.0.Final-5.4.27.Final)
    com.fasterxml.jackson.core:jackson-databind:(2.0.0-2.11.4)
    org.hibernate:hibernate-envers:(5.0.0.Final-5.4.27.Final)
    org.slf4j:slf4j-api:(1.3.0-1.7.30)
    io.service84.library:standardpersistence:(1.2.0-1.2.0)

## Build
This is a Java 11 project that builds best with Gradle 6.3

## Versioning
This project makes a best effort to comply with [SemVer](https://semver.org/)
