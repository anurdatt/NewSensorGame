package org.adgames.newsensorgame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void okButtonClicked(View button) {
        String name = ((EditText) findViewById(R.id.editText)).getText().toString();
        if (name == null || name.isEmpty()) {
            Toast.makeText(this, "Please enter your name to continue", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent("android.intent.action.RUN");
        intent.putExtra("EXTRA_PLAYER_NAME", name);
        startActivity(intent);
    }
}