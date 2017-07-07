/**
 * Copyright 2015-2016 Red Hat, Inc, and individual contributors.
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
package org.wildfly.swarm.container.runtime.cdi;

import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Named;

import org.jboss.weld.literal.AnyLiteral;
import org.jboss.weld.literal.NamedLiteral;
import org.wildfly.swarm.spi.api.SocketBindingGroup;
import org.wildfly.swarm.spi.api.cdi.CommonBean;
import org.wildfly.swarm.spi.api.cdi.CommonBeanBuilder;
import org.wildfly.swarm.spi.api.config.ConfigKey;
import org.wildfly.swarm.spi.api.config.ConfigView;
import org.wildfly.swarm.spi.api.config.SimpleKey;

/**
 * @author Bob McWhirter
 */
public class SocketBindingGroupExtension extends AbstractNetworkExtension<SocketBindingGroup> {

    private static ConfigKey ROOT = ConfigKey.of("swarm", "network", "socket-binding-groups");

    public SocketBindingGroupExtension(ConfigView configView) {
        super(configView);
    }

    @Override
    protected void applyConfiguration(SocketBindingGroup instance) {
        ConfigKey key = ROOT.append(instance.name());

        applyConfiguration(key.append("port-offset"), (offset) -> {
            instance.portOffset(offset.toString());
        });

        applyConfiguration(key.append("default-interface"), (offset) -> {
            instance.portOffset(offset.toString());
        });
    }

    @SuppressWarnings("unused")
    void afterBeanDiscovery(@Observes AfterBeanDiscovery abd, BeanManager beanManager) throws Exception {

        List<SimpleKey> configuredGroups = this.configView.simpleSubkeys(ROOT);
        for (SimpleKey groupName : configuredGroups) {
            Set<Bean<?>> groups = beanManager.getBeans(SocketBindingGroup.class, AnyLiteral.INSTANCE);
            AtomicBoolean producerRequired = new AtomicBoolean(false);
            if (groups
                    .stream()
                    .noneMatch(e -> e.getQualifiers()
                            .stream()
                            .anyMatch(anno -> anno instanceof Named && ((Named) anno).value().equals(groupName)))) {

                SocketBindingGroup group = new SocketBindingGroup(groupName.name(), null, "0");

                applyConfiguration(group);

                if (producerRequired.get()) {
                    CommonBean<SocketBindingGroup> interfaceBean = CommonBeanBuilder.newBuilder(SocketBindingGroup.class)
                            .beanClass(SocketBindingGroupExtension.class)
                            .scope(ApplicationScoped.class)
                            .addQualifier(AnyLiteral.INSTANCE)
                            .addQualifier(new NamedLiteral(group.name()))
                            .createSupplier(() -> group)
                            .addType(SocketBindingGroup.class)
                            .addType(Object.class)
                            .build();

                    abd.addBean(interfaceBean);
                }
            }
        }
    }
}
