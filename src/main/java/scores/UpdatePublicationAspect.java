package scores;

import java.util.List;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.patch.jsonpatch.JsonDiff;

import com.fasterxml.jackson.databind.JsonNode;

@Aspect
@Component
public class UpdatePublicationAspect {

	private GameDayService service;
	private SimpMessagingTemplate messaging;

	@Autowired
	public UpdatePublicationAspect(GameDayService service, SimpMessagingTemplate messaging) {
		this.service = service;
		this.messaging = messaging;
	}
	
	@Around("execution(* save(..)) && args(updated)")
	public void publishChange(ProceedingJoinPoint jp, Object updated) throws Throwable {
		List<Game> original = service.getScores();
		
		jp.proceed();

		JsonNode diff = new JsonDiff().diff(original, updated);
		messaging.convertAndSend("/topic/scores", diff);
	}
	
}
