package memelet.droolsesper;


import static java.util.concurrent.TimeUnit.*;

import org.junit.Test
import org.junit.Ignore

import static memelet.droolsesper.DroolsFixture.FIRE_UNTIL_HALT;

public class Pattern04Test extends AbstractEsperEventPatternsTest {

	def List<String> drlFilenames() { ["Pattern_04.drl"] }
	
	@Ignore 
	@Test
	def void correlateEventsOutOfOrder() {
		// TODO 
	}
}
