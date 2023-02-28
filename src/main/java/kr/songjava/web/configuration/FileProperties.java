package kr.songjava.web.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "file")
public record FileProperties(String rootPath, String resourcePath) {

}
