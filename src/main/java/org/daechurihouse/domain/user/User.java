package org.daechurihouse.domain.user;

import org.daechurihouse.core.entity.AuditableEntity;
import org.daechurihouse.domain.user.dto.UserSignupRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@ToString(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
	name = "users",
	uniqueConstraints = {
		@UniqueConstraint(columnNames = "username", name = "username_unique")
	}
)
public class User extends AuditableEntity {

	private String username;

	private String name;

	private String passwordHash;

	@Column(name = "role", nullable = false)
	@Enumerated(EnumType.STRING)
	private RoleType role = RoleType.ROLE_USER;

	@Enumerated(EnumType.STRING)
	private UserStatus status = UserStatus.PENDING;

	private String helloMessage;

	@Setter
	private String refreshToken;

	public static User of(String userName, RoleType role) {
		User user = new User();
		user.username = userName;
		user.role = role;
		return user;
	}

	public static User register(UserSignupRequest request, BCryptPasswordEncoder bCryptPasswordEncoder) {
		User user = new User();
		user.username = request.username();
		user.name = request.name();
		user.passwordHash = bCryptPasswordEncoder.encode(request.password());
		user.role = RoleType.ROLE_USER;
		user.helloMessage = request.helloMessage();
		return user;
	}

	public void changePasswordHash(String encodedNewPassword) {
		this.passwordHash = encodedNewPassword;
	}

	public void approve() {
		this.status = UserStatus.APPROVED;
	}
}
