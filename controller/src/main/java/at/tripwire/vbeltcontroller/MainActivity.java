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

    @ViewById(R.id.destination)
    protected EditText destinationEditText;

    @Click(R.id.navigate)
    protected void navigateClicked() {
        String destination = destinationEditText.getText().toString();
        if (!destination.isEmpty()) {
            Intent intent = new Intent(this, NavigationActivity_.class);
            intent.putExtra(NavigationActivity.EXTRA_DESTINATION, destination);
            startActivity(intent);
        } else {
            Toast.makeText(this, R.string.fill_out_destination, Toast.LENGTH_SHORT).show();
        }
    }
}
