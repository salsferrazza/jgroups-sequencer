package jgroups.sequencer;

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
}
