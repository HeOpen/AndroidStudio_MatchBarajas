package com.example.dm2e_parejabarajas_olaheliabe;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText playerNameEt;
    private View rankingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle(getString(R.string.title_main));

        playerNameEt = findViewById(R.id.et_player_name);

        rankingView = findViewById(R.id.bt_ranking);

        if (rankingView != null) {
            rankingView.setOnClickListener(v -> showRankingDialog());
        }

        playerNameEt.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                attemptStartGame();
                return true;
            }
            return false;
        });
    }

    private void attemptStartGame() {
        String playerName = playerNameEt.getText().toString().trim();

        if (playerName.isEmpty()) {
            Toast.makeText(this, getString(R.string.error_empty_name), Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(MainActivity.this, GameActivity.class);
            intent.putExtra("PLAYER_NAME", playerName);
            startActivity(intent);
        }
    }

    private void showRankingDialog() {
        ScoreDbHelper dbHelper = new ScoreDbHelper(this);
        Cursor cursor = dbHelper.getAllScores();

        if (cursor == null || cursor.getCount() == 0) {
            Toast.makeText(this, "No rankings yet!", Toast.LENGTH_SHORT).show();
            return;
        }

        StringBuilder buffer = new StringBuilder();
        while (cursor.moveToNext()) {
            String name = cursor.getString(1);
            int pScore = cursor.getInt(2);
            buffer.append(name).append(": ").append(pScore).append(" clicks\n");
        }
        cursor.close();

        new AlertDialog.Builder(this)
                .setTitle(R.string.leaderboard)
                .setMessage(buffer.toString())
                .setPositiveButton("OK", null)
                .show();
    }
}