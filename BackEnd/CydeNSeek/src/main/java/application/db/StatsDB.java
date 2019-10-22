package application.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import application.model.Stats;

@Repository
public interface StatsDB extends JpaRepository<Stats, Integer> {}