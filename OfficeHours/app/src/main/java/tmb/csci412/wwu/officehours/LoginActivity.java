package tmb.csci412.wwu.officehours;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by jungg2 on 5/11/19.
 * Modified by farriem on 5/11/19.
 */

public class LoginActivity extends AppCompatActivity {
    private EditText emailInput, passInput;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        emailInput = findViewById(R.id.loginEmailInput);
        passInput = findViewById(R.id.loginPassInput);
    }

    public void onLogin(final View view){
        String email = emailInput.getText().toString();
        String pass = passInput.getText().toString();

        boolean isValid = validateCredentials(email,pass);
        if(isValid){
            // FIREBASE AUTH
        }
        else{
            Toast t = Toast.makeText(this, "Invalid Email or Password",
                    Toast.LENGTH_LONG);
            t.show();
        }

    }

    // Method: validateCredentials
    // Description: Test if the email is of proper format ( text @ text . text )
    private boolean validateCredentials(String email, String pass){
        String emailExpression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pat = Pattern.compile(emailExpression, Pattern.CASE_INSENSITIVE);
        Matcher mat = pat.matcher(email);
        return mat.matches();
    }



}
