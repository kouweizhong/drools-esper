package memelet.droolsesper;

import static org.junit.Assert.fail;

import java.util.ArrayList;

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
import org.drools.runtime.conf.ClockTypeOption;
import org.drools.time.SessionPseudoClock;

public class DroolsFixture {
	
	public static final boolean FIRE_UNTIL_HALT = true;
	
	private final boolean fireUntilHalt;
	private final ArrayList<String> drlFilenames;
	
	public StatefulKnowledgeSession session;
	public SessionPseudoClock clock;

	public DroolsFixture(boolean fireUntilHalt, ArrayList<String> drlFilenames) {
		this.fireUntilHalt = fireUntilHalt;
		this.drlFilenames = drlFilenames;
	}
	
	public void setup() {
		KnowledgeBuilder  kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		for (String drlFilename : drlFilenames) {
			Resource resource = ResourceFactory.newClassPathResource(drlFilename, getClass());
			kbuilder.add(resource, ResourceType.DRL);
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

		if (fireUntilHalt) {
			new Thread(new Runnable() {
				public void run() {
					session.fireUntilHalt();
				}
			}).start();
		}
	}

	public void teardown() {
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
