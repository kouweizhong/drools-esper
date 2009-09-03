package memelet.droolsesper;

import static java.util.concurrent.TimeUnit.*;

import java.sql.ResultSet;

import org.junit.Test
import org.junit.Ignore

public class Pattern07Test extends AbstractEsperEventPatternsTest {

	def List<String> drlFilenames() { ["Pattern_07.drl"] }

	@Test
	def void correlateEventsWithSimilarProperties() {
		insert Trade(id: "a", userId: 'U1000', ccypair: "US/CA", direction: "BUY")

		advanceTime 5.m
		insert Trade(id: "b1", userId: 'U1000', ccypair: "US/CA", direction: "BUY")

		advanceTime 6.m
		insert Trade(id: "b2", userId: 'U1001', ccypair: "US/CA", direction: "BUY")
		advanceTime 1.ms
		insert Trade(id: "b3", userId: 'U1002', ccypair: "US/CA", direction: "BUY")
		
		fireAllRules
		def userIds = results.collect { key, trade -> trade.userId }
		assert userIds.sort() == ["U1000", "U1001", "U1002"]
        assert results.find { key, trade -> trade.id == "a" } == null
	}

	
}
