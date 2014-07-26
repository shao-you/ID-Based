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
    public Boolean policy_action(final String result) {  //do the checking & set rules
		//restore JSON record
		try {
			JSONObject record_json = new JSONObject(result);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(result);
		
		//decide the intelligent
		Boolean forwarding_or_not = true;
		
		//set rules
		if(forwarding_or_not)
		{
			//Flow flow = new Flow();
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
