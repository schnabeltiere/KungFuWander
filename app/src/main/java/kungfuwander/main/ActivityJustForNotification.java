package kungfuwander.main;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

public class ActivityJustForNotification extends AppCompatActivity {

    private EditText text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_just_for_notification);

        text = findViewById(R.id.editTextNotification);

        findViewById(R.id.btnStartService).setOnClickListener(v -> startService());
        findViewById(R.id.btnStopService).setOnClickListener(v -> stopService());
    }

    private void stopService() {
        Intent serviceIntent = new Intent(this, ExampleService.class);
        stopService(serviceIntent);
    }

    private void startService() {
        String input = text.getText().toString();

        Intent serviceIntent = new Intent(this, ExampleService.class);
        serviceIntent.putExtra("inputExtra", input);

        ContextCompat.startForegroundService(this, serviceIntent);
    }
}
