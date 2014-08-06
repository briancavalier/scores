package scores;

import java.util.List;

public interface ScoreRepository {

	List<Game> findAll();
	
	Game findOne(Long id);
	
	void save(List<Game> games);
	
	Game save(Game game);
	
}
