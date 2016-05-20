package ro.pub.cs.systems.eim.practicaltest02;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class PracticalTest02MainActivity extends Activity {


    final public static String TAG = "[PracticalTest02]";
    final public static boolean DEBUG = true;
	// Server widgets
    private EditText serverPortEditText = null;
    private Button connectButton = null;

    // Client widgets
    private EditText clientAddressEditText = null;
    private EditText clientPortEditText = null;
    private EditText cityEditText = null;
    private Spinner informationTypeSpinner = null;
    private Button getWeatherForecastButton = null;
    private TextView weatherForecastTextView = null;

    private ServerThread serverThread = null;
    private ClientThread clientThread = null;
    private ConnectButtonClickListener connectButtonClickListener = new ConnectButtonClickListener();
    private GetWeatherForecastButtonClickListener getWeatherForecastButtonClickListener = new GetWeatherForecastButtonClickListener();
    
    private class GetWeatherForecastButtonClickListener implements Button.OnClickListener {
    	  @Override
    	  public void onClick(View view) {
    	    String clientAddress = clientAddressEditText.getText().toString();
    	    String clientPort    = clientPortEditText.getText().toString();
    	    if (clientAddress == null || clientAddress.isEmpty() ||
    	      clientPort == null || clientPort.isEmpty()) {
    	      Toast.makeText(
    	        getApplicationContext(),
    	        "Client connection parameters should be filled!",
    	        Toast.LENGTH_SHORT
    	      ).show();
    	      return;
    	    }
    	    if (serverThread == null || !serverThread.isAlive()) {
    	      Log.e(TAG, "[MAIN ACTIVITY] There is no server to connect to!");
    	      return;
    	    }
    	    String city = cityEditText.getText().toString();
    	    String informationType = informationTypeSpinner.getSelectedItem().toString();
    	    if (city == null || city.isEmpty() ||
    	      informationType == null || informationType.isEmpty()) {
    	      Toast.makeText(
    	        getApplicationContext(),
    	        "Parameters from client (city / information type) should be filled!",
    	        Toast.LENGTH_SHORT
    	      ).show();
    	      return;
    	    }
    	    weatherForecastTextView.setText("");
    	    clientThread = new ClientThread(
    	      clientAddress,
    	      Integer.parseInt(clientPort),
    	      city,
    	      informationType,
    	      weatherForecastTextView);
    	    clientThread.start();
    	  }
    	}
    
    
	private class ConnectButtonClickListener implements Button.OnClickListener {
		  @Override
		  public void onClick(View view) {
		    String serverPort = serverPortEditText.getText().toString();
		    if (serverPort == null || serverPort.isEmpty()) {
		      Toast.makeText(
		        getApplicationContext(),
		        "Server port should be filled!",
		        Toast.LENGTH_SHORT
		      ).show();
		      return;
		    }
		    serverThread = new ServerThread(Integer.parseInt(serverPort));
		    if (serverThread.getServerSocket() != null) {
		      serverThread.start();
		    } else {
		      Log.e(TAG, "[MAIN ACTIVITY] Could not creat server thread!");
		    }
		  }
		}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_practical_test02_main);
		
		serverPortEditText = (EditText)findViewById(R.id.server_port_edit_text);
        connectButton = (Button)findViewById(R.id.connect_button);
        connectButton.setOnClickListener(connectButtonClickListener);

        clientAddressEditText = (EditText)findViewById(R.id.client_address_edit_text);
        clientPortEditText = (EditText)findViewById(R.id.client_port_edit_text);
        cityEditText = (EditText)findViewById(R.id.city_edit_text);
        informationTypeSpinner = (Spinner)findViewById(R.id.information_type_spinner);
        getWeatherForecastButton = (Button)findViewById(R.id.get_weather_forecast_button);
        getWeatherForecastButton.setOnClickListener(getWeatherForecastButtonClickListener);
        weatherForecastTextView = (TextView)findViewById(R.id.weather_forecast_text_view);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.practical_test02_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
    protected void onDestroy() {
        if (serverThread != null) {
            serverThread.stopThread();
        }
        super.onDestroy();
    }
}
