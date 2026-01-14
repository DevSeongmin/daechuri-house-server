package org.daechurihouse.domain.user.repository;

import java.util.List;
import java.util.Optional;

import org.daechurihouse.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByUsername(String username);

	@Query("""
		   SELECT u
		   FROM User u
		   WHERE u.role != 'ROLE_ADMIN'
		   ORDER BY
		       CASE WHEN u.status = 'PENDING' THEN 0 ELSE 1 END ASC
		""")
	List<User> findUserInfos();
}
