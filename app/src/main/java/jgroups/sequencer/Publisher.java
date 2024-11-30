package jgroups.sequencer;

import org.jgroups.*;
import org.jgroups.protocols.*;
import org.jgroups.util.Util;

import java.net.URL;
import java.io.*;
import java.util.*;

public class Publisher extends StatefulClusterMember {

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

    @Override
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
		    this.channel.send(null, itchMsg);
		} else {
		    break;
		}
	    } catch(Exception e) {
		e.printStackTrace();
		break;
	    }
	}
    }
}
