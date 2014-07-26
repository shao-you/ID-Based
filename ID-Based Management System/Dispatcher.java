import org.json.JSONObject;

public class Dispatcher implements Runnable{
	Boolean forwarding_or_not;
	JSONObject record_json;
	Register register;
	
	public Dispatcher(JSONObject record_json, Register register)
	{
		this.forwarding_or_not = true;
		this.record_json = record_json;
		this.register = register;
	}
	public Boolean forwarding_or_not()
	{
		return forwarding_or_not;
	}
	@Override  
    public void run() //reference register to trigger related policy engines
	{
		//this.register  
		//this.record_json
		int [] triggered_policy_engine = new int [5];//the number depends on the checking results
        //for(int i=0;i<triggered_policy_engine.length;i++) callback[i].policy_action("-------------");//notice policy engine to take actions
        forwarding_or_not = register.callback[0].policy_action(record_json.toString());
	}
}
