import com.sage.ws.service.*;

public class Hello implements SageTask {
    public Hello() {
        
    }
	
    public byte[] runTask(long taskNum, byte[] data) {
	byte[] hello = (new String("Hello World!")).getBytes();
	return hello;
    }
}
