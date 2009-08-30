package memelet.droolsesper;

import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseConfiguration;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.conf.EventProcessingOption;
import org.drools.io.ResourceFactory;
import org.drools.runtime.KnowledgeSessionConfiguration;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.conf.ClockTypeOption;
import org.drools.runtime.rule.WorkingMemoryEntryPoint;
import org.drools.time.SessionPseudoClock;
import org.junit.rules.TestWatchman;
import org.junit.runners.model.FrameworkMethod;

public class DroolsFixture extends TestWatchman {
	
	public static final boolean FIRE_UNTIL_HALT = true;
	
	private final String entryPointName;
	private final boolean fireUntilHalt;
	private final String[] commonDrls;
	
	public StatefulKnowledgeSession session;
	public SessionPseudoClock clock;
	public WorkingMemoryEntryPoint entryPoint;
	public Map<String,Object> results = new HashMap<String,Object>();

	public DroolsFixture(String entryPointName, boolean fireUntilHalt, String... commonDrls) {
		this.entryPointName = entryPointName;
		this.fireUntilHalt = fireUntilHalt;
		this.commonDrls = commonDrls;
	}
	
	public DroolsFixture(String entryPointName) {
		this(entryPointName, !FIRE_UNTIL_HALT);
	}
	
	@Override
	public void starting(FrameworkMethod method) {
		createSession(method.getName());
	}

	protected void createSession(String testName) {
		KnowledgeBuilder  kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		kbuilder.add(ResourceFactory.newClassPathResource(testName+".drl", getClass()), ResourceType.DRL);
		for (String commonDrl : commonDrls) {
			kbuilder.add(ResourceFactory.newClassPathResource(commonDrl+".drl", getClass()), ResourceType.DRL);
		}
		
		if (kbuilder.hasErrors()) {
			fail(kbuilder.getErrors().toString() );
		}
		
		KnowledgeBaseConfiguration kbaseConfig = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
		kbaseConfig.setOption(EventProcessingOption.STREAM);
		KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase(kbaseConfig);
		kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
		
		KnowledgeSessionConfiguration ksessionConfig = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
		ksessionConfig.setOption( ClockTypeOption.get( "pseudo" ) );

		session= kbase.newStatefulKnowledgeSession(ksessionConfig, null);
		clock = session.getSessionClock();
		entryPoint = session.getWorkingMemoryEntryPoint(entryPointName);
		
		session.setGlobal("results", results);

		if (fireUntilHalt) {
			new Thread(new Runnable() {
				public void run() {
					session.fireUntilHalt();
				}
			}).start();
		}
	}
	 
	public void finished(FrameworkMethod method) {
		if (fireUntilHalt) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
			session.halt();
		}
	}

}
