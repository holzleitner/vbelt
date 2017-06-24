package at.tripwire.vbeltcontroller;

import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_navigation)
public class NavigationActivity extends AppCompatActivity {

    public static final String EXTRA_START = "at.tripwire.vbeltcontroller.extra.start";

    public static final String EXTRA_END = "at.tripwire.vbeltcontroller.extra.end";

    @ViewById(R.id.location)
    protected TextView locationTextView;

    @AfterViews
    protected void init() {

    }
}
