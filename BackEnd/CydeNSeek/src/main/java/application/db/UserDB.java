package application.db;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import application.model.User;

@Repository
public interface UserDB extends JpaRepository<User, Integer> {

	public default Optional<User> findUserByUsername(String username) {
		return findAll().stream().filter(x -> username.equals(x.getUsername())).findFirst();
	}

	public default List<User> findAllUsersSorted(Comparator<? super User> comparator) {
		return findAll().stream().sorted(comparator).collect(Collectors.toList());
	}
}