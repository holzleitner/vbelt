package at.tripwire.vbeltcontroller;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.Toast;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_main)
public class MainActivity extends AppCompatActivity {

    @ViewById(R.id.start)
    protected EditText startEditText;

    @ViewById(R.id.end)
    protected EditText endEditText;

    @Click(R.id.navigate)
    protected void navigateClicked() {
        String start = startEditText.getText().toString();
        String end = endEditText.getText().toString();
        if (!start.isEmpty() && !end.isEmpty()) {
            Intent intent = new Intent(this, NavigationActivity_.class);
            intent.putExtra(NavigationActivity.EXTRA_START, start);
            intent.putExtra(NavigationActivity.EXTRA_END, end);
            startActivity(intent);
        } else {
            Toast.makeText(this, R.string.fill_out_fields, Toast.LENGTH_SHORT).show();
        }
    }
}
