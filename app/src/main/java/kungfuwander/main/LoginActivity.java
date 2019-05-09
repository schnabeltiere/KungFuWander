package kungfuwander.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextEmail, editTextPassword;
    private Button loginButton;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Testuser: test@test.at,testtest

        firebaseAuth = FirebaseAuth.getInstance();

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        loginButton = findViewById(R.id.buttonLogin);

        loginButton.setOnClickListener(l -> {
            String pw = editTextPassword.getText().toString();
            String email = editTextEmail.getText().toString();
            
            if(TextUtils.isEmpty(pw) || TextUtils.isEmpty(email)){
                Toast.makeText(l.getContext(), "Please enter a Password and an E-Mail", Toast.LENGTH_SHORT).show();
            }else{
                firebaseAuth.signInWithEmailAndPassword(email,pw)
                        .addOnCompleteListener(ll -> {
                            l.getContext().startActivity(new Intent(l.getContext(),MainActivity.class));

                        }).addOnFailureListener(lll -> {
                    Toast.makeText(l.getContext(), "Failed to login!", Toast.LENGTH_SHORT).show();
                    editTextPassword.setText("");
                    editTextEmail.setText("");
                });
            }
        });
    }

}
