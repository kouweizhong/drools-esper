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

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseConfiguration;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.conf.EventProcessingOption;
import org.drools.io.Resource;
import org.drools.io.ResourceFactory;
import org.drools.runtime.KnowledgeSessionConfiguration;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.WorkingMemoryEntryPoint
import org.drools.runtime.conf.ClockTypeOption;
import org.drools.time.SessionPseudoClock;

import static org.junit.Assert.fail;

abstract class AbstractEsperEventPatternsTest {

	abstract def List<String> drlFilenames()
	def Boolean fireUntilHalt() { false }
	def String entryPointName() { "stream" }

	def StatefulKnowledgeSession session
	def SessionPseudoClock clock
	def WorkingMemoryEntryPoint entryPoint
	
	def Map<String,Object> results = new HashMap<String,Object>()

	def advanceTime(duration, timeUnit) {
		clock.advanceTime(duration, timeUnit)
	}

	def insert(fact) { 
		session.insert(fact)
	}

	def fireAllRules() {
		session.fireAllRules()
	}

	@Before
	def void setupDrools() {
		def kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder()
		for (drlFilename in ["declarations.drl"] + drlFilenames()) {
			Resource resource = ResourceFactory.newClassPathResource(drlFilename, getClass())
			kbuilder.add(resource, ResourceType.DRL)
		}
		if (kbuilder.hasErrors()) {
			fail(kbuilder.getErrors().toString())
		}
		
		def kbaseConfig = KnowledgeBaseFactory.newKnowledgeBaseConfiguration()
		kbaseConfig.setOption(EventProcessingOption.STREAM)
		def kbase = KnowledgeBaseFactory.newKnowledgeBase(kbaseConfig)
		kbase.addKnowledgePackages(kbuilder.getKnowledgePackages())
		
		def ksessionConfig = KnowledgeBaseFactory.newKnowledgeSessionConfiguration()
		ksessionConfig.setOption(ClockTypeOption.get("pseudo"))

		session= kbase.newStatefulKnowledgeSession(ksessionConfig, null)
		entryPoint = session.getWorkingMemoryEntryPoint(entryPointName())
		clock = session.getSessionClock()
		session.setGlobal("results", results);

		if (fireUntilHalt()) {
			Thread.start {
				session.fireUntilHalt()
			}
		}
	}

	@After
	def void teardownDrools() {
		if (fireUntilHalt()) {
			Thread.sleep(1000);
			session.halt();
		}		
	}
}
