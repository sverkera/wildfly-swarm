package org.wildfly.swarm.jaxrs;

import java.io.IOException;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.swarm.Swarm;
import org.wildfly.swarm.arquillian.CreateSwarm;

import static org.fest.assertions.Assertions.assertThat;

@RunWith(Arquillian.class)
public class ApplicationPathTest extends SimpleHttp {

    @Deployment(testable = false)
    public static Archive createDeployment() throws Exception {
        return ShrinkWrap.create(JAXRSArchive.class, "app.war")
                .addAsResource("project-application-path.yml")
                .addClass(MyResource.class)
                .addAllDependencies();
    }

    @CreateSwarm
    public static Swarm newContainer() throws Exception {
        return new Swarm()
                .fraction(new JAXRSFraction())
                .withProfile("application-path");
    }

    @Test
    public void testChangeApplicationPath() throws IOException {
        SimpleHttp.Response response = getUrlContents("http://localhost:8080/api");
        assertThat(response.status).isEqualTo(200);
        assertThat(response.body).contains("Howdy at");
    }

}