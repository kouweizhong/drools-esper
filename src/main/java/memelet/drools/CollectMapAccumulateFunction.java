package memelet.drools;

import java.io.Serializable;
import java.util.Collections;

import org.drools.base.accumulators.CollectSetAccumulateFunction;

public class CollectMapAccumulateFunction extends CollectSetAccumulateFunction {
	
	@Override
    public Object getResult(Serializable context) throws Exception {
        CollectListData data = (CollectListData) context;
        return Collections.unmodifiableMap( data.map );
    }

}
