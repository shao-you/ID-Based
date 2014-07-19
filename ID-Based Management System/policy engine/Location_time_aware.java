
public class Location_time_aware implements Callback{
	private Dispatcher dispatcher;
	//final private String uid;
	//final private java.sql.Timestamp time;
	//final private long sw_dpid;
	
	public Location_time_aware(Dispatcher dispatcher)
	{
		this.dispatcher = dispatcher;
		register_index[1] = 1;
		register_index[3] = 1;
		register_index[11] = 1;
		this.dispatcher.Register_related_ID(Location_time_aware.this, register_index);//register interested IDs
	}

	@Override  
    public void policy_action(final String result) {  
        new Thread(new Runnable() {  
            @Override  
            public void run() {  
            	//set rules
            	System.out.println(result);  
            }  
        }).start();  
        
    }  
}
