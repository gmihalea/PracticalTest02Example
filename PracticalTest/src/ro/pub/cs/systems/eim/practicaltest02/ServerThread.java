package ro.pub.cs.systems.eim.practicaltest02;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

import android.util.Log;
import org.apache.http.client.ClientProtocolException;

public class ServerThread extends Thread {

	private int port = 0;
	final public static String TAG = "[PracticalTest02]";
	private ServerSocket serverSocket = null;
	final public static boolean DEBUG = true;
	private HashMap<String, WeatherForecastInformation> data = null;
	
	public ServerThread(int port) {
        try {
            this.serverSocket = new ServerSocket(port);
        } catch (IOException ioException) {
            Log.e(TAG, "An exception has occurred: " + ioException.getMessage());
            if (DEBUG) {
                ioException.printStackTrace();
            }
        }
        this.data = new HashMap<String, WeatherForecastInformation>();
    }
	
	@Override
	public void run() {
	  try {
	    while (!Thread.currentThread().isInterrupted()) {
	      Log.i(TAG, "[SERVER] Waiting for a connection...");
	      Socket socket = serverSocket.accept();
	      Log.i(TAG, "[SERVER] A connection request was received from " + socket.getInetAddress() + ":" + socket.getLocalPort());
	      CommunicationThread communicationThread = new CommunicationThread(this, socket);
	      communicationThread.start();
	    }
	  } catch (ClientProtocolException clientProtocolException) {
	    Log.e(TAG, "An exception has occurred: " + clientProtocolException.getMessage());
	    if (DEBUG) {
	      clientProtocolException.printStackTrace();
	    }
	  } catch (IOException ioException) {
	    Log.e(TAG, "An exception has occurred: " + ioException.getMessage());
	    if (DEBUG) {
	      ioException.printStackTrace();
	    }
	  }
	}
	public void stopThread() {
	  if (serverSocket != null) {
	    interrupt();
	    try {
	      if (serverSocket != null) {
	        serverSocket.close();
	      }
	    } catch (IOException ioException) {
	      Log.e(TAG, "An exception has occurred: " + ioException.getMessage());
	      if (DEBUG) {
	        ioException.printStackTrace();
	      }
	    }
	  }
	}
	
	public synchronized void setData(String city, WeatherForecastInformation weatherForecastInformation) {
        this.data.put(city, weatherForecastInformation);
    }

    public synchronized HashMap<String, WeatherForecastInformation> getData() {
        return data;
    }
    
    public void setPort(int port) {
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    public void setServerSocker(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }
}
