/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package jgroups.sequencer;

public class App {
    
    public static void main(String[] args) {

	String configUrl = null;
	String clusterName = null;
	String fileName = null;
	int messageLimit = -1;
	
	switch(args.length) {
	case 2: // always a subscriber
	    configUrl = args[0];
	    clusterName = args[1];
	    break;
	case 3: // publisher, no msg limit specified
	    configUrl = args[0];
	    clusterName = args[1];
	    fileName = args[2];
	    break;
	case 4: // publisher, msg limit specified
	    configUrl = args[0];
	    clusterName = args[1];
	    fileName = args[2];
	    messageLimit = Integer.parseInt(args[3]);
	    break;
	default:
	    System.exit(1);
	}

	try {
	    if (null != fileName) {
		new Publisher(configUrl, clusterName,
			      fileName, messageLimit).start();
	    } else {
		new Subscriber(configUrl, clusterName).start();
	    }
	} catch (Exception ex) {
	    ex.printStackTrace();
	    System.exit(1);
	}
	System.exit(0);
    }
}
