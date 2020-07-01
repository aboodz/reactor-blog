package io.github.aboodz.server;

import lombok.Getter;
import lombok.SneakyThrows;

import javax.inject.Singleton;
import java.io.InputStream;
import java.util.Properties;

@Getter
@Singleton
public class ServerConfiguration {
    private static final String SERVER_CONFIGURATION_PATH = "/server.properties";
    private static final String SERVER_PORT_PROP = "server.port";

    private final Integer port;

    @SneakyThrows
    ServerConfiguration() {
        try (InputStream resourceAsStream = ServerConfiguration.class.getResourceAsStream(SERVER_CONFIGURATION_PATH)) {
            Properties properties = new Properties();
            properties.load(resourceAsStream);
            this.port = Integer.parseInt(properties.getProperty(SERVER_PORT_PROP));
        }
    }
}
