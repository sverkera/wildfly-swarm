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
package org.wildfly.swarm.container.runtime;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.wildfly.swarm.Swarm;
import org.wildfly.swarm.spi.api.config.ConfigKey;
import org.wildfly.swarm.spi.api.config.ConfigView;
import org.wildfly.swarm.spi.api.config.SimpleKey;

/**
 * Created by bob on 5/24/17.
 */
@ApplicationScoped
public class ArtifactDeployer {

    @Inject
    ConfigView configView;

    @Inject
    private Instance<RuntimeDeployer> deployer;

    public void deploy() throws Exception {
        List<SimpleKey> subkeys = configView.simpleSubkeys(ConfigKey.of("swarm", "deployment"));

        for (SimpleKey subkey : subkeys) {
            String spec = subkey.name();
            if (spec.contains(":")) {
                String[] parts = spec.split(":");
                String groupId = parts[0];
                parts = parts[1].split("\\.");
                String artifactId = parts[0];
                String packaging = parts[1];

                JavaArchive artifact = Swarm.artifact(groupId + ":" + artifactId + ":" + packaging + ":*", artifactId + "." + packaging);
                deployer.get().deploy(artifact, spec);
            }
        }

    }
}
