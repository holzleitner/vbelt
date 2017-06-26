package at.tripwire.vbeltcontroller.networking;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import at.tripwire.vbeltcontroller.R;

@EBean(scope = EBean.Scope.Singleton)
public class ActionBroadcaster {

    private static final String BROKER_URI = "tcp://iot.soft.uni-linz.ac.at:1883";

    @RootContext
    protected Context context;

    private MqttClient mqttClient;

    public void connect() {
        try {
            mqttClient = new MqttClient(BROKER_URI, MqttClient.generateClientId(), new MemoryPersistence());
            mqttClient.setCallback(mqttCallback);

            MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
            mqttConnectOptions.setCleanSession(false);
            mqttClient.connect(mqttConnectOptions);
        } catch (MqttException e) {
            String message = context.getString(R.string.failed_to_connect);
            Log.e(context.getString(R.string.app_name), message, e);
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
    }

    public void publish(String topic, String payload) {
        if (mqttClient != null && mqttClient.isConnected()) {
            try {
                mqttClient.publish(topic, payload.getBytes(), 0, false);
            } catch (MqttException e) {
                String message = context.getString(R.string.failed_to_publish);
                Log.e(context.getString(R.string.app_name), message, e);
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(context, context.getString(R.string.failed_to_publish), Toast.LENGTH_SHORT).show();
        }
    }

    public void disconnect() {
        if (mqttClient != null && mqttClient.isConnected()) {
            try {
                mqttClient.disconnect();
                mqttClient = null;
            } catch (MqttException e) {
                String message = context.getString(R.string.failed_to_disconnect);
                Log.e(context.getString(R.string.app_name), message, e);
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public boolean isConnected() {
        return mqttClient != null && mqttClient.isConnected();
    }

    private MqttCallback mqttCallback = new MqttCallback() {

        @Override
        public void connectionLost(Throwable cause) {
            int message = cause == null ? R.string.disconnected : R.string.connection_lost;
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {
        }
    };
}
