package at.tripwire.vbeltcontroller;

import android.support.v7.app.AppCompatActivity;

import org.androidannotations.annotations.EActivity;

@EActivity(R.layout.activity_navigation)
public class NavigationActivity extends AppCompatActivity {

    public static final String EXTRA_START = "at.tripwire.vbeltcontroller.extra.start";

    public static final String EXTRA_END = "at.tripwire.vbeltcontroller.extra.end";

}
