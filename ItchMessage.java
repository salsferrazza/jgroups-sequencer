import java.util.*;
import java.io.*;

public class ItchMessage implements Serializable {

    static final long serialVersionUID = 42L;

    private byte[] bytes;
    private char messageType;
    private long sequenceNumber; 
    
    public ItchMessage(byte[] bytes, long sequenceNumber) {
	this.reset(bytes, sequenceNumber);
    }
    
    public byte[] getBytes() {
	return this.bytes;
    }
    
    public int getSize() {
	return this.bytes.length;
    }

    public char getType() {
	return this.messageType;
    }

    public String toString() {
	return this.messageType + " (" + this.getSize() + ")";
    }

    public void reset(byte[] bytes, long sequenceNumber) {	
	this.sequenceNumber = sequenceNumber;
	this.bytes = bytes;
	// message type is the first byte of data
	// - use bitwise operator to convert to char
	if (bytes.length != 0) {
	    this.messageType = (char) (0xff & bytes[0]);
	}
    }
}
