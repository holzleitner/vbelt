package at.tripwire.vbelt;

import android.app.IntentService;
import android.content.Intent;

import org.androidannotations.annotations.EIntentService;

@EIntentService
public class VibrationService extends IntentService {

    public VibrationService() {
        super("VibrationService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {

        }
    }
}
