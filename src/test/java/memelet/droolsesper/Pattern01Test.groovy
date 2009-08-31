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

public class Pattern01Test extends AbstractEsperEventPatternsTest {

	def List<String> drlFilenames() { ["Pattern_01.drl"] }
	
	@Test
	def void rateOfArrivalInAGivenTimePeriod() {
        10.times { 
            advanceTime 100, MILLISECONDS
			insert new MarketDataEvent(ticker: it as String)
		}
		fireAllRules()	
		assert results["arrivalRate"] == 10
		
        10.times { 
			advanceTime 100, MILLISECONDS
			insert new MarketDataEvent(ticker: it as String)
			fireAllRules()			
			assert results["arrivalRate"] == 10
		}
	}
}
