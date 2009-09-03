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

public class Pattern05cTest extends AbstractEsperEventPatternsTest {

	def List<String> drlFilenames() { ["Pattern_05c.drl"] }
	
	// [every a -> s where timer:within(..)] - out of order 
	@Test
	def void abortFollowedByStartAfterTime() {
		insert AbortedEvent(id: "ae1", exchangeId: "AAA")
		advanceTime 1, SECONDS
		fireAllRules()
		assert results.isEmpty()
		
		insert StartEvent(id: "se1", exchangeId: "AAA")
		fireAllRules()
		assert results["startEvent"].id == "se1"
		assert results["abortedEvent"].id == "ae1"
	}
}
