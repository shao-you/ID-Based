import org.json.JSONObject;

public class Dispatcher {
	int number_of_policy_engine;
	int [][] array;
	int index = 0;
	final int record_field = 12;
	Callback [] callback;
	
	public Dispatcher(int num)
	{
		number_of_policy_engine = num;
		array = new int [num][record_field];
		System.out.println("--------------1---------------");
	}
	public void Register_related_ID(Callback callback, int [] register_index)//store the registered info.
	{  
        for(int i=0;i<record_field;i++) array[index][i] = register_index[i];
		this.callback[index] = callback;
		index++;
	}
	public void trigger(JSONObject record_json)//do the checking & trigger policy engines
	{
        new Thread(new Runnable() {  
            @Override  
            public void run() {  
            	
            	int [] triggered_policy_engine = new int [5];//the number depends on the checking results
        		for(int i=0;i<triggered_policy_engine.length;i++) callback[i].policy_action("");//notice policy engine to take actions
            }  
        }).start();  
        System.out.println("--------------2---------------");
	}
}
