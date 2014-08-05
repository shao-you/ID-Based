import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import org.json.JSONException;
import org.json.JSONObject;

public class Location_time_aware implements Callback {
	private Register register;
	//final private String uid;
	//final private java.sql.Timestamp time;
	//final private long sw_dpid;
	
	public void register_IDs(Register register)
	{
		this.register = register;
		register_index[1] = 1;
		register_index[3] = 1;
		register_index[11] = 1;
		this.register.Register_related_ID(Location_time_aware.this, register_index);//register interested IDs
	}
	@Override  
    public Boolean policy_action(final String metadata) {  //do the checking & set rules
		//restore JSON record
		JSONObject record_json=null;

		try {
			record_json = new JSONObject(metadata);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		System.out.println(metadata);
		
		//read the external list
		
		//decide the intelligent
		Boolean forwarding_or_not = false;//need to set rules, forwarding should stop

		//set rules
		if(forwarding_or_not == false)
		{
			Flow flow = new Flow();
			Match match = new Match();
			ArrayList<Action>actions = new ArrayList<Action>();
			
			try {
				String src_mac = (String)record_json.get("src_mac");
				String sw_dpid = (String)record_json.get("sw_dpid");
				
				match.setDataLayerSource(src_mac);//use src_mac to block 
				flow.setSwitch(sw_dpid);
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			//actions.add(new Action("output", "1"));//no action will get the flow dropped
		    flow.setIdleTimeOut("0");//0 means infinite, in seconds
		    flow.setHardTimeOut("0");
		    
			flow.setMatch(match);
			flow.setActions(actions);
			flow.setName( Integer.toString(new Random().nextInt()) );//an unique name
			
			String result;
			try {
				result = REST_Commander.push(flow);
				System.out.println(result);
			} catch (IOException | JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}/**/
		}
		
		/*Thread thread = new Thread(new Runnable() {  
            @Override  
            public void run() {  
            	//set rules
            	System.out.println(result);  
            }  
        });
		thread.start();  
        try {
			thread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
        return forwarding_or_not;//return if this flow should be forwarded or not
    }
	
}
