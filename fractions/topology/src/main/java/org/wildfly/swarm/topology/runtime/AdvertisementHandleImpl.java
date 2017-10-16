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
package org.wildfly.swarm.topology.runtime;

import org.jboss.msc.service.ServiceController;
import org.wildfly.swarm.topology.AdvertisementHandle;

/**
 * @author Bob McWhirter
 */
class AdvertisementHandleImpl implements AdvertisementHandle {

    AdvertisementHandleImpl(ServiceController<?>...controllers) {
        this.controllers = controllers;
    }

    @Override
    public void unadvertise() {
        for (ServiceController<?> controller : this.controllers) {
            controller.setMode(ServiceController.Mode.REMOVE);
        }
    }

    private final ServiceController<?>[] controllers;

}
