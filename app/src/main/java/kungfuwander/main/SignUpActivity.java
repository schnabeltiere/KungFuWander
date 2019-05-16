package kungfuwander.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;

public class SignUpActivity extends AppCompatActivity {

     private EditText email, password;
     private Button registerButton;
     private TextView textViewLogin;
     private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //Testuser: test@test.com,testtest

        firebaseAuth = FirebaseAuth.getInstance();

        email = findViewById(R.id.editTextEmail);
        password = findViewById(R.id.editTextPassword);
        registerButton = findViewById(R.id.buttonSignUp);
        textViewLogin = findViewById(R.id.textViewLogin);

        //If the user already logged in once on the device AND if no authentification error occures, the user is automatically logged in
        if(firebaseAuth.getCurrentUser()!=null)
            startActivity(new Intent(getApplicationContext(), MainActivity.class));

        textViewLogin.setOnClickListener(log -> startActivity(new Intent(getApplicationContext(), LoginActivity.class)));

        registerButton.setOnClickListener(v -> {
                    String email = this.email.getText().toString();
                    String password = this.password.getText().toString();

                    if (TextUtils.isEmpty(email)) {
                        Toast.makeText(getApplicationContext(), "Please fill in the required fields", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (TextUtils.isEmpty(password)) {
                        Toast.makeText(getApplicationContext(), "Please fill in the required fields", Toast.LENGTH_SHORT).show();
                    }

                    if (password.length() < 6) {
                        Toast.makeText(getApplicationContext(), "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                    }

                    firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
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
