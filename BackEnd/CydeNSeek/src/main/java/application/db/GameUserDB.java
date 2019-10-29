package application.db;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import application.model.GameUser;

@Repository
public interface GameUserDB extends JpaRepository<GameUser, Integer> {

	public default List<GameUser> findUsersByGame(String gameId, Comparator<? super GameUser> comparator) {
		return findAll().stream().filter(x -> x.getSession().equals(gameId)).sorted(comparator).collect(Collectors.toList());
	}
}