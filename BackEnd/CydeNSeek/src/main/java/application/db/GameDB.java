package application.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import application.model.Game;

@Repository
public interface GameDB extends JpaRepository<Game, Integer> {}