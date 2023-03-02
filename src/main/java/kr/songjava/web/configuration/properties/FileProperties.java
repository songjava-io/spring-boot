package kr.songjava.web.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "file")
public record FileProperties(
	String rootPath,
	String resourcePath
) {

}
