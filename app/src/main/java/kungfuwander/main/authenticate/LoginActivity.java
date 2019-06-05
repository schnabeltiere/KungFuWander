package kungfuwander.main.authenticate;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuthException;
import kungfuwander.main.MainActivity;
import kungfuwander.main.R;

public class LoginActivity extends AppCompatActivity {

    private EditText email,password;
    private Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.editTextEmail);
        password = findViewById(R.id.editTextPassword);
        login = findViewById(R.id.buttonLogin);

        login.setOnClickListener(l -> {
            String email = this.email.getText().toString();
            String pw = this.password.getText().toString();

            SignUpActivity.firebaseAuth.signInWithEmailAndPassword(email,pw).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    MainActivity.currentFirebaseUser = SignUpActivity.firebaseAuth.getCurrentUser();
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "E-mail or password is wrong", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(e -> {
                if (e instanceof FirebaseAuthException) {
                    Log.e("kungfuwander.main.",((FirebaseAuthException) e).getErrorCode());
                }
            });
        });
    }
}
