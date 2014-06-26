
public class Dispatcher {

	public Dispatcher()
	{
		(new SocketServer()).start();//thread-enabled server
	}
	
}
