package scores;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class GameDayService {
	
	private Logger logger = LoggerFactory.getLogger(GameDayService.class);

	private SimpMessagingTemplate messageTemplate;
	
	private List<Game> games;

	@Autowired
	public GameDayService(SimpMessagingTemplate messageTemplate) {
		this.messageTemplate = messageTemplate;
		initializeGames();
	}
	
	public List<Game> getScores() {
		return games;
	}
	
	@Scheduled(fixedRate=5000)
	public void updateScoresRandomly() {
		// choose a game to update
		int gameIndex = (int)(Math.random() * games.size());
		
		// choose either a touchdown or a field goal
		int points = Math.random() > 0.6 ? 7 : 3;
		String pointType = points == 3 ? "field goal" : "touchdown";
		
		// choose either home or away team
		double homeOrAway = Math.random();
		
		// adjust score
		Game game = games.get(gameIndex);
		if (homeOrAway > 0.5) {
			logger.info("The " + game.getHomeTeam() + " scored a " + pointType + " against the " + game.getAwayTeam());
			game.incrementHomeTeamScore(points);
		} else {
			logger.info("The " + game.getAwayTeam() + " scored a " + pointType + " against the " + game.getHomeTeam());
			game.incrementAwayTeamScore(points);
		}

		publishUpdatedScores();
	}
	
	private void publishUpdatedScores() {
		// send all scores...for now
		// send patch to scores...later
		messageTemplate.convertAndSend("/topic/scores", games);
	}
	
	//
	// private helpers
	//
	
	private void initializeGames() {
		List<String> teamPool = new ArrayList<String>(Arrays.asList(ALL_TEAMS));
		games = new ArrayList<Game>(ALL_TEAMS.length / 2);
		
		while(!teamPool.isEmpty()) {
			String homeTeam = teamPool.get((int)(Math.random() * teamPool.size()));
			teamPool.remove(homeTeam);
			String awayTeam = teamPool.get((int)(Math.random() * teamPool.size()));
			teamPool.remove(awayTeam);
			games.add(new Game(homeTeam, awayTeam));
		}
	}
	
	private static final String[] ALL_TEAMS = {
		"Ravens", "Bengals", "Browns", "Steelers",
		"Texans", "Colts", "Jaguars", "Titans",
		"Bills", "Dolphins", "Patriots", "Jets",
		"Broncos", "Chiefs", "Raiders", "Chargers",
		"Bears", "Lions", "Packers", "Vikings",
		"Falcons", "Panthers", "Saints", "Buccaneers",
		"Cowboys", "Giants", "Eagles", "Redskins",
		"Cardinals", "49ers", "Seahawks", "Rams"
	};
	
}
