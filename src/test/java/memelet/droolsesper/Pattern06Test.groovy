package memelet.droolsesper;

import static java.util.concurrent.TimeUnit.*;

import java.sql.ResultSet;

import org.junit.Test
import org.junit.Ignore

public class Pattern06Test extends AbstractEsperEventPatternsTest {

	def List<String> drlFilenames() { ["Pattern_06.drl"] }

	@Test
	def void detectEventsBetweenOtherEvents() {
		insert 1, 0
		insert 1, 1
		insert 2, 2, 'A' // ON
		insert 2, 3, 'B'
		insert 3, 5, 'C'
		insert 3, 5, 'D'
		insert 2, 3, 'E'
		insert 2, 0, 'F' // OFF
		insert 3, 1
		insert 2, 0,     // second OFF

		fireAllRules()

		assert results["begin"].id == 'A'
		assert results["end"].id == 'F'
		assert results["middle"].collect { it.id } == ['B', 'C', 'D', 'E']
	}

	def void insert(p1, p2, id=null) {
		advanceTime 1, SECONDS
		insert ParamEvent(id: id, param1: p1, param2: p2)
	}


	
}
