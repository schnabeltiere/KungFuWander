package kungfuwander.main.authenticate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import kungfuwander.main.MainActivity;
import kungfuwander.main.R;
import kungfuwander.main.helper.FirebaseHelper;

public class SignUpActivity extends AppCompatActivity {

    private EditText email, password, username;
    private Button registerButton;
    private TextView textViewLogin;
    public static FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        firebaseAuth = FirebaseAuth.getInstance();

        email = findViewById(R.id.editTextEmail);
        password = findViewById(R.id.editTextPassword);
        username = findViewById(R.id.editTextUsername);
        registerButton = findViewById(R.id.buttonSignUp);
        textViewLogin = findViewById(R.id.textViewLogin);

        //If the user already logged in once on the device AND if no authentification error occures, the user is automatically logged in
/*
        if (firebaseAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            MainActivity.currentFirebaseUser = firebaseAuth.getCurrentUser();
            finish();
        }
*/
        textViewLogin.setOnClickListener(log -> startActivity(new Intent(getApplicationContext(), LoginActivity.class)));

        registerButton.setOnClickListener(v -> {
            String email = this.email.getText().toString();
            String password = this.password.getText().toString();
            String username = this.username.getText().toString();

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(username)) {
                Toast.makeText(getApplicationContext(), "Pflichtfelder ausfüllen!", Toast.LENGTH_SHORT).show();
                this.email.setText("");
                this.password.setText("");
                this.username.setText("");
                return;
            }
            if (password.length() < 6) {
                Toast.makeText(getApplicationContext(), "Passwort muss mehr als 6 Zeichen beinhalten", Toast.LENGTH_SHORT).show();
                this.password.setText("");
                return;
            }

            if (username.length() < 5) {
                Toast.makeText(this, "Benutzername muss mehr als 5 Zeichen beinhalten", Toast.LENGTH_SHORT).show();
                return;
            }
            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    MainActivity.currentFirebaseUser = firebaseAuth.getCurrentUser();
                    // this creates at login, updates following later...
                    FirebaseHelper.createNewUserDatabase(username);
                    FirebaseHelper.updateDisplayName(username);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "E-mail or password wrong", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(e -> {
                if (e instanceof FirebaseAuthException) {
                    Log.e("kungfuwander.main.", ((FirebaseAuthException) e).getErrorCode());
                }
            });
        });
    }
}