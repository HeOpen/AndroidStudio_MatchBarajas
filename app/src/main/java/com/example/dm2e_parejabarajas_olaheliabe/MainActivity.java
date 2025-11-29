package com.example.dm2e_parejabarajas_olaheliabe;
import android.content.Intent;
import android.os.Bundle;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.dm2e_parejabarajas_olaheliabe.GameActivity;
import com.example.dm2e_parejabarajas_olaheliabe.R;

public class MainActivity extends AppCompatActivity {

    private EditText playerNameEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set the window title as required
        setTitle("JuegoDeParejas_tunombre");

        playerNameEt = findViewById(R.id.et_player_name);

        // Listen for the "Done" or "Intro" key press on the keyboard
        playerNameEt.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                // This method is called when the user presses 'Intro'
                attemptStartGame();
                return true; // Consume the event
            }
            return false;
        });
    }

    private void attemptStartGame() {
        String playerName = playerNameEt.getText().toString().trim();

        if (playerName.isEmpty()) {
            // If the name is empty, it continues on this first window (as required)
            Toast.makeText(this, "Por favor, escribe tu nombre para empezar.", Toast.LENGTH_SHORT).show();
        } else {
            // Start the second activity and pass the name
            Intent intent = new Intent(MainActivity.this, GameActivity.class);
            intent.putExtra("PLAYER_NAME", playerName);
            startActivity(intent);
        }
    }
}