package db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import model.Game;

@Repository
public interface GameDB extends JpaRepository<Game, Integer> {}