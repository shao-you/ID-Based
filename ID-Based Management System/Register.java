
public class Register {
	int number_of_policy_engine;
	//Boolean forwarding_or_not;
	int [][] array;
	int index = 0;
	final int record_field = 12;
	Callback [] callback;
	//JSONObject record_json;
	
	public Register(int num)
	{
		number_of_policy_engine = num;
		array = new int [num][record_field];
		callback = new Callback[num];
	}
	public void Register_related_ID(Callback callback, int [] register_index)//store the registered info.
	{  
        for(int i=0;i<record_field;i++) array[index][i] = register_index[i];
		this.callback[index] = callback;
		index++;
	}
}
