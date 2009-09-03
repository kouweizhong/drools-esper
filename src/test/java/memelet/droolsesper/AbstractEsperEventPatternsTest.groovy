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
import org.drools.definition.type.FactType

import static org.junit.Assert.fail;

//class Fact {
//
//	Fact(factType) { 
//		this.droolsFactType = factType
//		this.droolsFact = factType.newInstance()
//	}
//
//	def droolsFactType
//	def droolsFact
//
//	def propertyMissing(String name, args) {
//		droolsFactType.set droolsFact, name, args
//	}
//	
//}

abstract class AbstractEsperEventPatternsTest {

	abstract def List<String> drlFilenames()
	def Boolean fireUntilHalt() { false }
	def String entryPointName() { "stream" }

	def KnowledgeBase kbase
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

	def newFact(packageName, className) {
		kbase.getFactType(packageName, className).newInstance()
	}

	def newFact(packageName, className, properties) {
		def fact = newFact(packageName, className)
		properties.each { prop, value ->
			fact."${prop}" = value
		}
		fact
	}

	def methodMissing(String name, args) {
		if (args instanceof Object[] && args.length == 1 && args[0] instanceof HashMap) {
			newFact(this.getClass().getPackage().getName(), name, args[0])
		} else {
			throw new MissingMethodException(name, getClass(), args)
		}
	}

	@Before
	def void setupDrools() {
		def kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder()
		for (drlFilename in drlFilenames()) {
			Resource resource = ResourceFactory.newClassPathResource(drlFilename, getClass())
			kbuilder.add(resource, ResourceType.DRL)
		}
		if (kbuilder.hasErrors()) {
			fail(kbuilder.getErrors().toString())
		}
		
		def kbaseConfig = KnowledgeBaseFactory.newKnowledgeBaseConfiguration()
		kbaseConfig.setOption(EventProcessingOption.STREAM)
		kbase = KnowledgeBaseFactory.newKnowledgeBase(kbaseConfig)
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
