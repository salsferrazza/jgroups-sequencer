import org.jgroups.*;
import org.jgroups.protocols.*;
import org.jgroups.util.Util;

import java.net.URL;
import java.io.*;
import java.util.*;

public class Subscriber extends StatefulClusterMember implements Receiver {

    public Subscriber(String configurationUrl, String clusterName) {
	super(configurationUrl, clusterName);
    }
    
    @Override
    public void receive(Message msg) {
	this.incrementState(msg);
	System.out.println(((ItchMessage) msg.getObject()).getType() + " " + msg.printHeaders());
    }

    @Override
    public void eventLoop() {
	System.err.println("in sub event loop");
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
	
	System.err.println("new sub " + args.length);

	if (args.length == 2) {
	    configUrl = args[0];
	    clusterName = args[1];
	    System.err.println("new sub 2");
	    Subscriber sub = new Subscriber(configUrl, clusterName);
	    System.err.println(sub);
	    try {
		System.err.println("new sub 3");
		sub.start();
	    } catch (Exception ex) {
		System.err.println("new sub start exc");
	    	ex.printStackTrace();
	    }
	} else {
	    System.exit(1);
	}
	System.exit(0);
    }
}
