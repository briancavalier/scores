package scores;

import java.util.List;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.patch.Diff;
import org.springframework.patch.Patch;
import org.springframework.patch.json.JsonPatchMaker;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class UpdatePublicationAspect {

	// TODO: Reconsider this aspect. Instead have GameDayService publish a score update via Reactor.
	//       Rework this aspect into a handler for the update that sends to the client.

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

		Patch diff = new Diff().diff(original, updated);
		messaging.convertAndSend("/topic/scores", new JsonPatchMaker().toJsonNode(diff));
	}
	
}
