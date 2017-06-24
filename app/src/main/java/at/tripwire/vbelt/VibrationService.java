package at.tripwire.vbelt;

import android.app.IntentService;
import android.content.Intent;
import android.widget.Toast;

import org.androidannotations.annotations.EIntentService;
import org.androidannotations.annotations.UiThread;

@EIntentService
public class VibrationService extends IntentService {

    public static final String EXTRA_POSITION = "at.tripwire.vbelt.extra.position";

    private String topic;

    public VibrationService() {
        super("VibrationService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            topic = intent.getStringExtra(EXTRA_POSITION);
            // TODO connect to mqtt broker
            showToast(R.string.service_started);
        }
    }

    @UiThread
    protected void showToast(int stringId) {
        Toast.makeText(this, stringId, Toast.LENGTH_SHORT).show();
    }
}
