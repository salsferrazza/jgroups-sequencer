import org.jgroups.*;
import org.jgroups.protocols.*;
import org.jgroups.util.Util;

import java.net.URL;
import java.io.*;
import java.util.*;

public class Publisher extends StatefulClusterMember implements Receiver {

    private int maxMessages = -1;
    private String fileName;
    
    public Publisher(String configurationUrl,
		     String clusterName,
		     String fileName,
		     int maxMessages) {
	super(configurationUrl, clusterName);
	this.maxMessages = maxMessages;
	this.fileName = fileName;
    }

    @Override
    public void start() throws Exception {
	JChannel channel = this.getChannel();
	channel = new JChannel(this.getConfigurationUrl())
	    .setReceiver(this);
	channel.setDiscardOwnMessages(true);
        channel.connect(this.getClusterName());
        channel.getState(null, this.getTimeout());
        this.eventLoop();
        channel.close();
    }

    public void receive(Message msg) {
	System.err.println("received: " + msg);
    }
    
    @Override
    public void eventLoop() {
	LengthPrefixedUnframer unframe;
	try {
	    unframe = new LengthPrefixedUnframer(new FileInputStream(this.fileName));
	    unframe.setMaximum(this.maxMessages > 0 ? this.maxMessages : -1);
	} catch (Exception ex) {
	    ex.printStackTrace();
	    return;
	}

	while(true) {
	    try {
		ItchMessage itchMsg = unframe.get();
		if (null != itchMsg) {
		    Message msg=new ObjectMessage(null, itchMsg);
		    this.getChannel().send(msg);
		} else {
		    break;
		}
	    }
	    catch(Exception e) {
		e.printStackTrace();
	    }
	}
    }

    public static void main(String[] args) {

    }
}
