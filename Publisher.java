import org.jgroups.*;
import org.jgroups.protocols.*;
import org.jgroups.util.Util;

import java.net.URL;
import java.io.*;
import java.util.*;

public class Publisher extends StatefulClusterMember implements Receiver {

    private int maxMessages = -1;
    private String fileName;
    private JChannel channel;
    
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
	this.channel = new JChannel(this.getConfigurationUrl())
	    .setReceiver(this); 
	this.channel.setDiscardOwnMessages(true);
	this.channel.connect(this.getClusterName());
        this.channel.getState(null, this.getTimeout());
        this.eventLoop();
        this.channel.close();
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
		    this.channel.send(msg);
		} else {
		    break;
		}
	    }
	    catch(Exception e) {
		e.printStackTrace();
		break;
	    }
	}
    }

    public static void main(String[] args) {
	if (args.length < 4) {
	    System.exit(1);
	} else {
	    Publisher pub = new Publisher(args[0],
					  args[1],
					  args[2],
					  Integer.parseInt(args[3]));
	    try {
		pub.start();
	    } catch (Exception ex) {
		ex.printStackTrace();
		System.exit(1);
	    }
	}
	System.exit(0);
    }
}
