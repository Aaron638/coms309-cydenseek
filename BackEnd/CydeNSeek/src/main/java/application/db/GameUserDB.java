package application.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import application.model.GameUser;

@Repository
public interface GameUserDB extends JpaRepository<GameUser, Integer> {}