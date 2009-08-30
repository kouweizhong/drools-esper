package memelet.droolsesper;

import org.junit.Rule
import java.util.concurrent.TimeUnit;

import org.junit.Rule
import org.junit.Test
import org.junit.Before

import static memelet.droolsesper.DroolsFixture.FIRE_UNTIL_HALT;
import java.util.concurrent.TimeUnit;
import static java.util.concurrent.TimeUnit.*;

public class EsperEventPatternsTest {

	@Rule public DroolsFixture drools = new DroolsFixture(
			"stream", !FIRE_UNTIL_HALT, 
			"declarations")

	def advanceTime(Long duration, TimeUnit timeUnit) {
		drools.clock.advanceTime(duration, timeUnit)
	}

	def insert(Object fact) { 
		drools.entryPoint.insert(fact)
	}

	def fireAllRules() {
		drools.session.fireAllRules()
	}

	def Map<String,Object> results

	@Before
	def void before() {
		results = drools.results
	}

	//---- ---- ----

	@Test
	def void How_do_I_measure_the_rate_of_arrival_of_events_in_a_given_time_period() {
        (1..10).each { 
            advanceTime 100, MILLISECONDS
			insert new MarketDataEvent(ticker: it as String)
		}
		fireAllRules()
		
		assert results["arrivalRate"] == 10
		
        (1..10).each { 
			advanceTime 100, MILLISECONDS
			insert new MarketDataEvent(ticker: it as String)
			fireAllRules()
			
			assert results["arrivalRate"] == 10
		}
	}

	@Test
	def void How_do_I_measure_the_rate_of_arrival_of_events_in_a_given_time_period_per_another_category() {
		(1..30).each {
			advanceTime 100, MILLISECONDS
			insert new MarketDataEvent(ticker: it as String, feed: "feed1")
			insert new MarketDataEvent(ticker: it as String, feed: "feed2")
		}
		fireAllRules()
		
		assert results["arrivalRates"]["feed1"].value == 10
		assert results["arrivalRates"]["feed2"].value == 10
	}

	@Test
	def void How_do_I_correlate_events_arriving_in_2_or_more_streams() {
		// Not the same account
		insert new WithdrawalEvent(accountNumber: "AAA", amount: 100)
		insert new FraudWarningEvent(accountNumber: "BBB")
		fireAllRules()
		assert results.isEmpty()

		// Not within the 30s window
		advanceTime 31, SECONDS
		insert new FraudWarningEvent(accountNumber: "AAA")
		fireAllRules()
		assert results.isEmpty()

		// Correlated
		advanceTime 60, SECONDS
		insert new WithdrawalEvent(id: "w", accountNumber: "AAA", amount: 200)
		advanceTime 10, SECONDS
		insert new FraudWarningEvent(id: "f", accountNumber: "AAA")
		fireAllRules()
		
		assert results["accountNumber"] == "AAA"
		assert results["amount"] == 200
		
	}

	//@Test
	def void How_do_I_correlate_events_arriving_out_of_order() {
		
	}
}
