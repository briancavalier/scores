package scores;

import java.io.Serializable;

public class Game implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Long id = null;
	
	private final String homeTeam;
	private final String awayTeam;
	private int homeTeamScore = 0;
	private int awayTeamScore = 0;
	
	public Game(String homeTeam, String awayTeam) {
		this.homeTeam = homeTeam;
		this.awayTeam = awayTeam;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public Long getId() {
		return id;
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
