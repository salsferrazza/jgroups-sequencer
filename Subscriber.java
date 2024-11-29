import org.jgroups.*;
import org.jgroups.protocols.*;
import org.jgroups.util.Util;

import java.net.URL;
import java.io.*;
import java.util.*;

public class Subscriber extends StatefulClusterMember {
    
    public Subscriber(String configurationUrl, String clusterName) {
	super(configurationUrl, clusterName);
    }
    
    @Override
    public void receive(Message msg) {
	this.incrementState(msg.getObject());
	System.out.println(((ItchMessage) msg.getObject()).getType() + " " + msg.printHeaders());
    }

    @Override
    public void eventLoop() {
	try {
	    while (true) Thread.currentThread().yield();
	} catch (Exception iex) {
	    System.err.println(iex);
	    iex.printStackTrace();
	}
    }

    public static void main(String[] args) {

	String configUrl;
	String clusterName;

	if (args.length == 2) {
	    configUrl = args[0];
	    clusterName = args[1];
	    Subscriber sub = new Subscriber(configUrl, clusterName);
	    System.err.println(sub);
	    try {
		sub.start();
	    } catch (Exception ex) {
	    	ex.printStackTrace();
		System.exit(1);
	    }
	} else {
	    System.exit(1);
	}
	System.exit(0);
    }
}
