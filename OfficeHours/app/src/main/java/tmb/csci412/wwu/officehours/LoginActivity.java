package tmb.csci412.wwu.officehours;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by jungg2 on 5/11/19.
 * Modified by farriem on 5/11/19.
 */

public class LoginActivity extends AppCompatActivity {
    private EditText emailInput, passInput;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        emailInput = findViewById(R.id.loginEmailInput);
        passInput = findViewById(R.id.loginPassInput);
    }

    // Login Button Tapped
    public void onLogin(final View view){
        final String email = emailInput.getText().toString();
        String pass = passInput.getText().toString();

        // Email Empty
        if(TextUtils.isEmpty(email)) {
            emailInput.setError("This field cannot be empty!");
            return;
        }
        // Pass Empty
        if(TextUtils.isEmpty(pass)) {
            passInput.setError("This field cannot be empty!");
            return;
        }

        boolean isValid = validateCredentials(email);

        if(isValid){
            // get the user

            // Sign-in using FireBase Auth
            mAuth.signInWithEmailAndPassword(email, pass)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            // MOVE TO PROF HOUR MANAGER
                            if (task.isSuccessful()) {
                                Intent intent = new Intent(view.getContext(), ManageHoursActivity.class);
                                intent.putExtra("email", email);
                                startActivity(intent);
                            }
                            // WRONG PASSWORD
                            else{
                                invalidEmailPass();
                            }

                        }
                    });
        }
        // BAD EMAIL
        else{
            invalidEmailPass();
        }

    }

    // Method: invalidEmailPass
    // Description: Pop a toast to let the user know it's wrong
    protected void invalidEmailPass(){
        Toast t = Toast.makeText(this, "Invalid Email or Password",
                Toast.LENGTH_LONG);
        t.show();
    }

    // Method: validateCredentials
    // Description: Determine if email & password are good formats
    private boolean validateCredentials(String email){

        // Test if the email is of proper format ( text @ text . text )
        String emailExpression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pat = Pattern.compile(emailExpression, Pattern.CASE_INSENSITIVE);
        Matcher mat = pat.matcher(email);
        return mat.matches();
    }



}
