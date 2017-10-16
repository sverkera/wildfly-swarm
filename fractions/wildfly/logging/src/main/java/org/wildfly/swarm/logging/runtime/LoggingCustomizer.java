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
package org.wildfly.swarm.logging.runtime;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.inject.Inject;

import org.wildfly.swarm.bootstrap.logging.InitialLoggerManager;
import org.wildfly.swarm.bootstrap.logging.LevelNode;
import org.wildfly.swarm.config.logging.Level;
import org.wildfly.swarm.logging.LoggingFraction;
import org.wildfly.swarm.spi.api.Customizer;
import org.wildfly.swarm.spi.runtime.annotations.Post;

/**
 * @author Bob McWhirter
 */
@Post
@ApplicationScoped
public class LoggingCustomizer implements Customizer {

    @Inject
    @Any
    private LoggingFraction fraction;

    @Override
    public void customize() {
        LevelNode root = InitialLoggerManager.INSTANCE.getRoot();
        apply(root);
    }

    private void apply(LevelNode node) {
        if (!node.getName().equals("")) {
            this.fraction.logger(node.getName(), (l) -> {
                l.level(Level.valueOf(node.getLevel().toString()));
            });
        }
        for (LevelNode each : node.getChildren()) {
            apply(each);
        }
    }
}
