package at.tripwire.vbelt;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

@EActivity(R.layout.activity_main)
public class MainActivity extends AppCompatActivity {

    private static final String BROKER_URI = "tcp://iot.soft.uni-linz.ac.at:1883";

    @ViewById(R.id.position)
    protected EditText positionEditText;

    private MqttAndroidClient mqttAndroidClient;

    @Click(R.id.connect)
    protected void connectClicked() {
        String position = positionEditText.getText().toString();
        if (!position.isEmpty()) {
            mqttAndroidClient = new MqttAndroidClient(getApplicationContext(), BROKER_URI, MqttClient.generateClientId());
            mqttAndroidClient.setCallback(mqttCallback);

            MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
            mqttConnectOptions.setCleanSession(false);

            try {
                mqttAndroidClient.connect(mqttConnectOptions);
                subscribeToTopic(position);
            } catch (MqttException e) {
                String message = getString(R.string.failed_to_connect);
                Log.e(getString(R.string.app_name), message, e);
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, R.string.no_position_given, Toast.LENGTH_SHORT).show();
        }
    }

    private void subscribeToTopic(String topic) throws MqttException {
        if (mqttAndroidClient != null && mqttAndroidClient.isConnected()) {
            mqttAndroidClient.subscribe(topic, 0);
        }
    }

    private MqttCallback mqttCallback = new MqttCallback() {
        @Override
        public void connectionLost(Throwable cause) {
            Toast.makeText(getApplicationContext(), R.string.connection_lost, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
            Log.i(getString(R.string.app_name), new String(message.getPayload()));
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {

        }
    };
}
