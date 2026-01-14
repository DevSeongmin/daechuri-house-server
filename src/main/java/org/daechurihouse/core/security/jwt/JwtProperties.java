package org.daechurihouse.core.security.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Setter;

@Component
@ConfigurationProperties(prefix = "spring.jwt")
@Setter
public class JwtProperties {
	public String SECRET_KEY;
	public String ACCESS_TOKEN_TYPE;
	public String REFRESH_TOKEN_TYPE;
	public Long ACCESS_TOKEN_EXPIRY_MS;
	public Long REFRESH_TOKEN_EXPIRY_MS;
}