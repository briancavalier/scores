package scores;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

@Controller
public class ScoresController {

	private GameDayService service;

	@Autowired
	public ScoresController(GameDayService service) {
		this.service = service;
	}

	/*
	 * For the client to get an initial list of game scores without waiting on the next update.
	 * Could also have been done via a GET request, but where's the fun in that?
	 */
	@SubscribeMapping("/scores")
	public List<Game> scores() {
		return service.getScores();
	}
	
}
