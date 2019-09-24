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

	public default User findUserByUsername(String username) {
		Optional<User> user = findAll().stream().filter(x -> username.equals(x.getUsername())).findFirst();
		if(!user.isPresent()) return null;
		return user.get();
	}

	public default List<User> findAllUsersSorted(Comparator<? super User> comparator) {
		return findAll().stream().sorted(comparator).collect(Collectors.toList());
	}

	public default List<User> findUsersByGame(int gameId, Comparator<? super User> comparator) {
		return findAll().stream().filter(x -> x.getGameId() == gameId).sorted(comparator).collect(Collectors.toList());
	}
}