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

public class Pattern02Test extends AbstractEsperEventPatternsTest {

	def List<String> drlFilenames() { ["Pattern_02.drl"] }
	
	@Test
	def void rateOfArrivalInAGivenTimePeriodPerCategory() {
		30.times {
			advanceTime 100, MILLISECONDS
			insert MarketDataEvent(ticker: it as String, feed: "feed1")
			insert MarketDataEvent(ticker: it as String, feed: "feed2")
		}
		fireAllRules()		
		assert results["arrivalRates"]["feed1"].value == 10
		assert results["arrivalRates"]["feed2"].value == 10
	}
}
