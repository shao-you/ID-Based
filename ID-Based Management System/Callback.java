
public interface Callback {  
	final int record_field = 12;
	int [] register_index = new int [record_field];
	//for(int i=0;i<register_index;i++);
	public void register_IDs(Register register);
    public Boolean policy_action(String metadata);
}  
