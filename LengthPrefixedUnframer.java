import java.io.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public class LengthPrefixedUnframer implements Supplier<ItchMessage> {

    private final InputStream in;
    private long counter = 0;
    private long maximum = -1;
    public long started = System.currentTimeMillis();
    public LengthPrefixedUnframer(InputStream in) {
        this.in = new DataInputStream(new BufferedInputStream(in));
    }

    @Override
    public ItchMessage get() {
	try {
	    return (this.counter == this.maximum)
		? null
		: this.readMessage();
	} catch (Exception ex) {
	    ex.printStackTrace();
	}
	return null;
    }

    public void setMaximum(long max) {
	this.maximum = max;
    }

    public long getMaximum() {
	return this.maximum;
    }
    
    public long getCounter() {
	return this.counter;
    }
  
    public ItchMessage readMessage() throws IOException {

	if (this.in.available() <= 0) return null; 

        int length = ((DataInputStream) this.in).readShort(); // Read the length of the message
        if (length <= 0) {
	    this.in.close();
            return null; // Handle invalid length
        }

	byte[] bytes = new byte[length];
        ((DataInputStream) this.in).readFully(bytes); // Read the message bytes

	this.counter += 1;

	return new ItchMessage(bytes, counter); 
	
    }
    
    /**
    public static void main(String[] args) {
	try {
	    LengthPrefixedUnframer unframe = new LengthPrefixedUnframer(args.length == 0
									? System.in
									: new FileInputStream(args[0]));

	    StatusThread status = new StatusThread(unframe);
	    Thread t = new Thread(status);
	    t.start();
	    
	    long count = Stream.generate(() -> unframe.get())
		.takeWhile(Objects::nonNull)
		.collect(Collectors.counting());

	    status.halt();
	    t = null;

	    System.exit(0);
	    
	} catch (FileNotFoundException fnf) {
	    System.err.println("File " + args[0] + " not found");
	    System.exit(1);
	} catch (Exception ex) {
	    ex.printStackTrace();
	    System.exit(1);
	} 
    }    
    */
}
