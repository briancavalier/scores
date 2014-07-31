package scores;

public class Game {

	private final String homeTeam;
	private final String awayTeam;
	private int homeTeamScore = 0;
	private int awayTeamScore = 0;
	
	public Game(String homeTeam, String awayTeam) {
		this.homeTeam = homeTeam;
		this.awayTeam = awayTeam;
	}

	public String getHomeTeam() {
		return homeTeam;
	}
	
	public int getHomeTeamScore() {
		return homeTeamScore;
	}
	
	public void incrementHomeTeamScore(int points) {
		this.homeTeamScore += points;
	}
	
	public String getAwayTeam() {
		return awayTeam;
	}
	
	public int getAwayTeamScore() {
		return awayTeamScore;
	}
	
	public void incrementAwayTeamScore(int points) {
		this.awayTeamScore += points;
	}

}
