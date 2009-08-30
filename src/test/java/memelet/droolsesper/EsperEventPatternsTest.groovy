package memelet.droolsesper;

import org.junit.Rule
import java.util.concurrent.TimeUnit;

import org.junit.Rule
import org.junit.Test

public class EsperEventPatternsTest {

	@Rule public DroolsFixture drools = new DroolsFixture(
			"stream", !DroolsFixture.FIRE_UNTIL_HALT, "declarations")

	def advanceTime(long millis) {
        drools.clock.advanceTime(millis, TimeUnit.MILLISECONDS)
	}

	def insert(Object fact) {
		drools.entryPoint.insert(fact)
	}
	
	@Test
	def void How_do_I_measure_the_rate_of_arrival_of_events_in_a_given_time_period() {
        (1..10).each { 
            advanceTime(100)
			insert(new MarketDataEvent(ticker: it as String))
		}
		drools.session.fireAllRules()
		assert drools.results["arrivalRate"] == 10
		
        (1..10).each { 
            advanceTime(100)
			insert(new MarketDataEvent(ticker: it as String))
			drools.session.fireAllRules()
			assert drools.results["arrivalRate"] == 10
		}
	}

	@Test
	def void How_do_I_measure_the_rate_of_arrival_of_events_in_a_given_time_period_per_another_category() {
		(1..30).each {
            advanceTime(100)
			insert(new MarketDataEvent(ticker: it as String, feed: "feed1"))
			insert(new MarketDataEvent(ticker: it as String, feed: "feed2"))
		}
		drools.session.fireAllRules()
		assert drools.results["arrivalRates"]["feed1"].value == 10
		assert drools.results["arrivalRates"]["feed2"].value == 10
	}
}
