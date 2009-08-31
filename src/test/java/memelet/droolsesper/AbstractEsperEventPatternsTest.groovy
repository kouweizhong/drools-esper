package memelet.droolsesper;

import org.drools.WorkingMemory;
import org.drools.event.rule.AfterActivationFiredEvent;
import org.drools.event.rule.AgendaEventListener
import org.drools.event.rule.DefaultAgendaEventListener
import org.drools.event.rule.DebugWorkingMemoryEventListener
import org.drools.event.rule.DebugAgendaEventListener

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import static java.util.concurrent.TimeUnit.*;

import org.junit.Rule
import org.junit.Test
import org.junit.Before
import org.junit.After
import org.junit.Ignore

import static memelet.droolsesper.DroolsFixture.FIRE_UNTIL_HALT;

abstract class AbstractEsperEventPatternsTest {

	abstract def List<String> drlFilenames()
	def String entryPointName() { "stream" }

	def drools
	def session
	def clock
	def entryPoint
	def results = new HashMap<String,Object>()

	def advanceTime(duration, timeUnit) {
		clock.advanceTime(duration, timeUnit)
	}

	def insert(fact) { 
		session.insert(fact)
	}

	def fireAllRules() {
		drools.session.fireAllRules()
	}

	@Before
	def void setup() {
		drools = new DroolsFixture(!FIRE_UNTIL_HALT, ["declarations.drl"] + drlFilenames())
		drools.setup()
		session = drools.session
		clock = drools.clock
		entryPoint = session.getWorkingMemoryEntryPoint(entryPointName());
		session.setGlobal("results", results);
		
//		drools.addEventListener(new DebugWorkingMemoryEventListener())
//		drools.addEventListener(new DebugAgendaEventListener())
//		drools.addEventListener(agendaEventTracker)
	}

	@After
	def void teardown() {
		drools.teardown()
	}
}
