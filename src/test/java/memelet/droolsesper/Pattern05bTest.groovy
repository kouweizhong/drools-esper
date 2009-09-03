package memelet.droolsesper;

import org.drools.WorkingMemory;
import org.drools.event.rule.AfterActivationFiredEvent;
import org.drools.event.rule.AgendaEventListener
import org.drools.event.rule.DefaultAgendaEventListener
import org.drools.event.rule.DebugWorkingMemoryEventListener
import org.drools.event.rule.DebugAgendaEventListener

import java.util.concurrent.TimeUnit;
import static java.util.concurrent.TimeUnit.*;

import org.junit.Rule
import org.junit.Test
import org.junit.Before
import org.junit.Ignore

public class Pattern05bTest extends AbstractEsperEventPatternsTest {

	def List<String> drlFilenames() { ["Pattern_05b.drl"] }
	
	// [every s -> (f where timer:within(..) and not (a where timer:within(..))] 
	@Test
	def void startFollowedByFinishedAndNotAbortedAfterTime() {
		insert StartEvent(id: "se1", exchangeId: "BBB")
		advanceTime 10, SECONDS
		fireAllRules()
		assert results.isEmpty()

		advanceTime 10, SECONDS
		insert FinishedEvent(id: "fe1", exchangeId: "BBB")
		fireAllRules()
		assert results.isEmpty()
		
		// TODO Why does this need to be 30 and not 10? It seems that 
		// timer does not start until both the previous conditions are
		// true. 
		//
		// see http://www.nabble.com/Conditional-%27not%27-invalid-for-event-streams--td25217095.html
		advanceTime 30, SECONDS
		assert results["startEvent"].id == "se1"
		assert results["finishedEvent"].id == "fe1"

//		advanceTime 20, SECONDS
//		assert results["startEvent"].id == "se1"
	}
}
