package application.db;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import application.model.General;

@Repository
public interface GeneralDB extends JpaRepository<General, Integer> {

	public default Optional<General> findRowBySession(String session) {
		return findAll().stream().filter(x -> session.equals(x.getSession())).findFirst();
	}
}