package at.tripwire.vbeltcontroller;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

@EBean(scope = EBean.Scope.Singleton)
public class ActionBroadcaster {

    private static final String BROKER_URI = "tcp://iot.soft.uni-linz.ac.at:1883";

    @RootContext
    protected Context context;

    private MqttAndroidClient mqttAndroidClient;

    public void connect() {
        mqttAndroidClient = new MqttAndroidClient(context, BROKER_URI, MqttClient.generateClientId());
        mqttAndroidClient.setCallback(mqttCallback);

        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setCleanSession(false);

        try {
            mqttAndroidClient.connect(mqttConnectOptions, mqttActionListener);
        } catch (MqttException e) {
            String message = context.getString(R.string.failed_to_connect);
            Log.e(context.getString(R.string.app_name), message, e);
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
    }

    public void publish(String topic, String payload) {
        if (mqttAndroidClient != null && mqttAndroidClient.isConnected()) {
            try {
                mqttAndroidClient.publish(topic, payload.getBytes(), 0, false);
            } catch (MqttException e) {
                String message = context.getString(R.string.failed_to_publish);
                Log.e(context.getString(R.string.app_name), message, e);
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void disconnect() {
        if (mqttAndroidClient != null && mqttAndroidClient.isConnected()) {
            try {
                mqttAndroidClient.disconnect();
                mqttAndroidClient = null;
            } catch (MqttException e) {
                String message = context.getString(R.string.failed_to_disconnect);
                Log.e(context.getString(R.string.app_name), message, e);
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public boolean isConnected() {
        return mqttAndroidClient != null && mqttAndroidClient.isConnected();
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

    private IMqttActionListener mqttActionListener = new IMqttActionListener() {
        @Override
        public void onSuccess(IMqttToken asyncActionToken) {
            Toast.makeText(context, R.string.connected, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
            Toast.makeText(context, R.string.failed_to_connect, Toast.LENGTH_SHORT).show();
        }
    };
}
