package application.db;

import java.util.Optional;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import application.model.Game;

@Repository

public interface GameDB extends JpaRepository<Game, UUID> {

	public default Optional<Game> findGameBySession(final UUID gameSession) {
		return findAll().stream().filter(x -> gameSession.compareTo(x.getSession()) == 0).findFirst();
	}
}

