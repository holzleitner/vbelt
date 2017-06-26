package at.tripwire.vbeltcontroller;

import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;

import at.tripwire.vbeltcontroller.networking.ActionBroadcaster;

@EActivity(R.layout.activity_demo)
public class DemoActivity extends AppCompatActivity {

    @Bean
    protected ActionBroadcaster actionBroadcaster;

    @Override
    protected void onResume() {
        super.onResume();
        actionBroadcaster.connect();
        Toast.makeText(this, "connect", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        actionBroadcaster.disconnect();
        Toast.makeText(this, "disconnect", Toast.LENGTH_SHORT).show();
    }

    @Click(R.id.left_100)
    protected void left100() {
        actionBroadcaster.publish("left", "100");
    }

    @Click(R.id.left_50)
    protected void left50() {
        actionBroadcaster.publish("left", "50");
    }

    @Click(R.id.left_10)
    protected void left10() {
        actionBroadcaster.publish("left", "10");
    }

    @Click(R.id.left_1)
    protected void left1() {
        actionBroadcaster.publish("left", "1");
    }

    @Click(R.id.center_100)
    protected void center100() {
        actionBroadcaster.publish("center", "100");
    }

    @Click(R.id.center_50)
    protected void center50() {
        actionBroadcaster.publish("center", "50");
    }

    @Click(R.id.center_10)
    protected void center10() {
        actionBroadcaster.publish("center", "10");
    }

    @Click(R.id.center_1)
    protected void center1() {
        actionBroadcaster.publish("center", "1");
    }

    @Click(R.id.right_100)
    protected void right100() {
        actionBroadcaster.publish("right", "100");
    }

    @Click(R.id.right_50)
    protected void right50() {
        actionBroadcaster.publish("right", "50");
    }

    @Click(R.id.right_10)
    protected void right10() {
        actionBroadcaster.publish("right", "10");
    }

    @Click(R.id.right_1)
    protected void right1() {
        actionBroadcaster.publish("right", "1");
    }
}
