package game.blaster;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import com.google.android.material.button.MaterialButton;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
		MaterialButton btn = findViewById(R.id.login_button);
		btn.setOnClickListener(view->{
			startActivity(new Intent(this,MainActivity.class));
		});
    }
}