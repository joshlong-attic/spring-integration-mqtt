/*
 * Copyright 2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.integration.mqtt;

/**
 * Models the values from <A href="http://www.eclipse.org/paho/files/mqttdoc/Casync/qos.html">the Eclipse
 * reference on the quality of service values</A>.
 *
 * @author Josh Long
 * @author Andy Piper
 */

public enum QualityOfService {
    AT_MOST_ONCE,  // 0
    AT_LEAST_ONCE,    // 1
    EXACTLY_ONCE   // 2
}