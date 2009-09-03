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

public class Pattern01Test extends AbstractEsperEventPatternsTest {

	def List<String> drlFilenames() { ["Pattern_01.drl"] }

	@Test
	def void rateOfArrivalInAGivenTimePeriod() {
        10.times { 
            advanceTime 100.ms
			insert MarketDataEvent(ticker: it as String)
		}
		fireAllRules
		assert results["arrivalRate"] == 10
		
        10.times { 
			advanceTime 100.ms
			insert MarketDataEvent(ticker: it as String)
			fireAllRules				
			assert results["arrivalRate"] == 10
		}
	}
}
