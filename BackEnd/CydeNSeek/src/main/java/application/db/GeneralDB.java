package application.db;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import application.model.General;

@Repository
public interface GeneralDB extends JpaRepository<General, Integer> {

	public default Optional<General> findRowBySession(String session) {
		return findAll().stream().filter(x -> session.equals(x.getSession())).findFirst();
	}

	public default List<General> findUsersByGame(int gameId, Comparator<? super General> comparator) {
		return findAll().stream().filter(x -> x.getGameUser().getGame().getId().equals(gameId)).sorted(comparator).collect(Collectors.toList());
	}
}