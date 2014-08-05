
public class Register {
	int [][] array;
	int index = 0;
	int record_field;
	Callback [] callback;
	
	public Register(int policy_engine_num, int record_field_num)
	{
		record_field = record_field_num;
		array = new int [policy_engine_num][record_field];
		callback = new Callback[policy_engine_num];
	}
	public void Register_related_ID(Callback callback, int [] register_index)//store the registered info. for each policy engine
	{  
        for(int i=0;i<record_field;i++) array[index][i] = register_index[i];
		this.callback[index] = callback;
		index++;
	}
}
