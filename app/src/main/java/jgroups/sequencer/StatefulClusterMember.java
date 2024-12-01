package jgroups.sequencer;

import org.jgroups.*;
import org.jgroups.protocols.*;
import org.jgroups.util.Util;

import java.net.URL;
import java.io.*;
import java.util.*;

public abstract class StatefulClusterMember implements Receiver {
    
    final List<ItchMessage> state=new LinkedList<>();

    private JChannel channel;
    private String configurationUrl;
    private String clusterName;
    private static final int timeout = 0; // don't timeout

    public int getTimeout() {
	return this.timeout;
    }
    
    public JChannel getChannel() {
	return this.channel;
    }

    public String getConfigurationUrl() {
	return this.configurationUrl;
    }

    public String getClusterName() {
	return this.clusterName;
    }
    
    public StatefulClusterMember(String configurationUrl, String clusterName) {
	this.configurationUrl = configurationUrl;
	this.clusterName = clusterName;
    }
    
    public void viewAccepted(View new_view) {
        System.out.println("** view: " + new_view);
    }

    public void getState(OutputStream output) throws Exception {
	System.err.println("State requested, have " + this.state.size() + " messages");
	DataOutputStream stream = new DataOutputStream(output);
	for (ItchMessage obj: this.state) {
	    Util.objectToStream(obj, stream);
	}
    }

    public void setState(InputStream input) throws Exception {
	if (this.state.size() > 0) this.clearState();
	DataInputStream stream = new DataInputStream(input);
	while (input.available() > 0) {
	    ItchMessage msg = Util.objectFromStream(stream);
	    this.incrementState(msg);
	}
	System.err.println("State set, now have " + this.state.size() + " messages");
    }

    private void clearState() {
	synchronized(this.state) {
	    this.state.clear();
	}
    }
    
    public void incrementState(ItchMessage msg) {
	// FIXME: if we enforce a single threaded JVM per cluster member
	//        then no need to synchronize
	synchronized(this.state) {
	    this.state.add(msg);
	}
    }

    public abstract void receive(Message msg);
    public abstract void eventLoop();

    public void start() throws Exception {
        this.channel = new JChannel(this.getConfigurationUrl()).setReceiver(this);
        this.channel.connect(this.getClusterName());
        this.channel.getState(null, this.getTimeout());
        this.eventLoop();
        this.channel.close();
    }

}
