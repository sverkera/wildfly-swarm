package org.wildfly.swarm.container.config;

import org.junit.Test;
import org.wildfly.swarm.spi.api.config.CompositeKey;
import org.wildfly.swarm.spi.api.config.ConfigKey;
import org.wildfly.swarm.spi.api.config.SimpleKey;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author Bob McWhirter
 */
public class ConfigKeyTest {

    @Test
    public void testParseWithOnlyDots() {
        ConfigKey key = ConfigKey.parse("foo.bar.baz");

        assertThat(key.head().name()).isEqualTo("foo");
        assertThat(key.subkey(1).head().name()).isEqualTo("bar");
        assertThat(key.subkey(2).head().name()).isEqualTo("baz");
        assertThat(key.subkey(3).head()).isEqualTo(ConfigKey.EMPTY);

        assertThat(key.name()).isEqualTo("foo.bar.baz");
        assertThat(key).isInstanceOf(CompositeKey.class);
    }

    @Test
    public void testOneSegmentInsideDelim() {
        ConfigKey key = ConfigKey.parse("[foo]");
        assertThat(key.head().name()).isEqualTo("foo");

        assertThat(key.name()).isEqualTo("foo");
        assertThat(key).isInstanceOf(SimpleKey.class);
    }


    @Test
    public void testSeveralSegmentsNoInternalDots() {
        ConfigKey key = ConfigKey.parse("[foo].[bar].[baz]");

        assertThat(key.head().name()).isEqualTo("foo");
        assertThat(key.subkey(1).head().name()).isEqualTo("bar");
        assertThat(key.subkey(2).head().name()).isEqualTo("baz");
        assertThat(key.subkey(3).head()).isEqualTo(ConfigKey.EMPTY);

        assertThat(key.name()).isEqualTo("foo.bar.baz");
    }

    @Test
    public void testSeveralSegmentsWithInternalDots() {
        ConfigKey key = ConfigKey.parse("foo.[bar.baz].taco");

        assertThat(key.head().name()).isEqualTo("foo");
        assertThat(key.subkey(1).head().name()).isEqualTo("bar.baz");
        assertThat(key.subkey(2).head().name()).isEqualTo("taco");
        assertThat(key.subkey(3).head()).isEqualTo(ConfigKey.EMPTY);

        assertThat(key.name()).isEqualTo("foo.[bar.baz].taco");
    }

    @Test
    public void testParentage() {
        ConfigKey child = ConfigKey.parse( "swarm.deployment.*.foo");
        ConfigKey parent = ConfigKey.parse( "swarm.deployment.*");
        ConfigKey cousin = ConfigKey.parse( "swarm.deployment.tacos");

        assertThat( child.isChildOf(parent)).isTrue();
        assertThat( parent.isChildOf(child)).isFalse();
        assertThat( cousin.isChildOf(child)).isFalse();
        assertThat( child.isChildOf(cousin)).isFalse();
        assertThat( cousin.isChildOf(parent)).isFalse();
        assertThat( parent.isChildOf(parent)).isFalse();
    }

    @Test
    public void testReplace() {
        ConfigKey key = ConfigKey.parse("swarm.deployment.*.foo");

        key.replace(2, "taco.jar");

        assertThat( key.head().name() ).isEqualTo( "swarm");
        assertThat( key.subkey(1).head().name() ).isEqualTo( "deployment");
        assertThat( key.subkey(2).head().name() ).isEqualTo( "taco.jar");
        assertThat( key.subkey(3).head().name() ).isEqualTo( "foo");

    }
}

