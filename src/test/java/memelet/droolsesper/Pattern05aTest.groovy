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

import static memelet.droolsesper.DroolsFixture.FIRE_UNTIL_HALT;

public class Pattern05aTest extends AbstractEsperEventPatternsTest {

	def List<String> drlFilenames() { ["Pattern_05a.drl"] }
	
	// [every s -> a where timer:within(..)]
	@Test
	def void startFollowedByAbortedAfterTime() {
		// StartEvent followed by AbortEvent after 30s
		insert new StartEvent(exchangeId: "AAA")
		advanceTime 31, SECONDS
		insert new AbortedEvent(exchangeId: "AAA")
		fireAllRules()
		assert results.isEmpty()

		// StartEvent followed by AbortEvent within 30s
		insert new StartEvent(id: "se1", exchangeId: "BBB")
		advanceTime 20, SECONDS
		insert new AbortedEvent(id: "ae1", exchangeId: "BBB")
		fireAllRules()		
		assert results["startEvent"].id == "se1"
		assert results["abortedEvent"].id == "ae1"

		// With Esper patterns, a second Aborted event should not match
		results.clear()
		insert new AbortedEvent(id: "ae2", exchangeId: "BBB")
		fireAllRules()
		assert results.isEmpty()

		// And an out of order StartEvent should also not match
		advanceTime 1, SECONDS
		insert new StartEvent(id: "se2", exchangeId: "BBB")
		fireAllRules()		
		assert results.isEmpty()
	}
}
