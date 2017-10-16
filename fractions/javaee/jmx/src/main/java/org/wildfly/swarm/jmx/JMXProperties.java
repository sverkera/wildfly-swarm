/**
 * Copyright 2015-2017 Red Hat, Inc, and individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.wildfly.swarm.jmx;

/**
 * Configuration property keys for the JMX fraction.
 *
 * @author Bob McWhirter
 */
public interface JMXProperties {

    /**
     * Key for configuration value to enable and optionally select remote JMX endpoint.
     *
     * <p>Values may include</p>
     *
     * <ul>
     * <li><code>management</code> to explicitly select the management interface</li>
     * <li><code>http</code> to select the HTTP interface</li>
     * <li>any other non-null value to allow for auto-detection.</li>
     * </ul>
     */
    String REMOTE = "swarm.jmx.remote";
}
