package scores;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.patch.jsonpatch.JsonDiff;

import com.fasterxml.jackson.databind.JsonNode;

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
		List<Game> newScores = deepCloneList(games);
		
		// choose a game to update
		int gameIndex = (int)(Math.random() * newScores.size());
		
		// choose either a touchdown or a field goal
		int points = Math.random() > 0.6 ? 7 : 3;
		String pointType = points == 3 ? "field goal" : "touchdown";
		
		// choose either home or away team
		double homeOrAway = Math.random();
		
		// adjust score
		Game game = newScores.get(gameIndex);
		if (homeOrAway > 0.5) {
			logger.info("The " + game.getHomeTeam() + " scored a " + pointType + " against the " + game.getAwayTeam());
			game.incrementHomeTeamScore(points);
		} else {
			logger.info("The " + game.getAwayTeam() + " scored a " + pointType + " against the " + game.getHomeTeam());
			game.incrementAwayTeamScore(points);
		}

		publishUpdatedScores(newScores);
	}
	
	private void publishUpdatedScores(List<Game> newScores) {
		// send all scores...for now
		// send patch to scores...later

		JsonNode diff = new JsonDiff().diff(games, newScores);
		
		messageTemplate.convertAndSend("/topic/scores", diff);
	}
	
	//
	// private helpers
	//
	
	private List<Game> deepCloneList(List<Game> original) {
		List<Game> copy = new ArrayList<Game>(original.size());
		for(Game t : original) {
			copy.add((Game) SerializationUtils.clone((Serializable) t)); 
		}
		return copy;
	}
	
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
