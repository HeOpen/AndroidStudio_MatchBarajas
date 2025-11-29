package com.example.dm2e_parejabarajas_olaheliabe;

import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import android.app.AlertDialog;
import android.database.Cursor;

public class GameActivity extends AppCompatActivity {

    private TextView playerNameTv;
    private GridLayout cardsGrid;

    private int currentLevel = 1;
    private int score = 0;
    private Card firstCard = null;
    private Card secondCard = null;
    private boolean isBusy = false;
    private List<Card> currentCards = new ArrayList<>();

    private final List<Integer> availableImages = Arrays.asList(
            R.drawable.card1,
            R.drawable.card2,
            R.drawable.card3,
            R.drawable.card4,
            R.drawable.card5,
            R.drawable.card6
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        String playerName = getIntent().getStringExtra("PLAYER_NAME");
        setTitle(getString(R.string.title_game_prefix) + playerName);

        playerNameTv = findViewById(R.id.tv_player_name);
        cardsGrid = findViewById(R.id.grid_cards);

        playerNameTv.setText(getString(R.string.label_player) + playerName);

        setupLevel(currentLevel);
    }

    private void setupLevel(int level) {
        int numPairs;
        int numCards;

        switch (level) {
            case 1:
                numPairs = 2; // 4 cards
                break;
            case 2:
                numPairs = 3; // 6 cards
                break;
            case 3:
                numPairs = 6; // 12 cards
                break;
            default:
                return;
        }

        numCards = numPairs * 2;
        currentLevel = level;
        cardsGrid.removeAllViews();
        currentCards.clear();

        List<Integer> selectedImages = new ArrayList<>(availableImages.subList(0, numPairs));
        List<Integer> gameImages = new ArrayList<>();
        gameImages.addAll(selectedImages);
        gameImages.addAll(selectedImages);
        Collections.shuffle(gameImages);

        if (level == 1) {
            cardsGrid.setColumnCount(2);
            cardsGrid.setRowCount(2);
        } else {
            cardsGrid.setColumnCount(3);
            cardsGrid.setRowCount(numCards / 3);
        }

        for (int i = 0; i < numCards; i++) {
            Button cardButton = createCardButton(gameImages.get(i));
            cardsGrid.addView(cardButton);
        }
    }

    private Button createCardButton(int imageId) {
        Button button = new Button(this);
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = (int) (getResources().getDisplayMetrics().density * 90);
        params.height = (int) (getResources().getDisplayMetrics().density * 130);
        params.setMargins(8, 8, 8, 8);
        button.setLayoutParams(params);

        Card card = new Card(imageId, button);
        button.setTag(card);
        currentCards.add(card);

        card.flipToBack();

        button.setOnClickListener(v -> handleCardClick(card));
        return button;
    }


    private void handleCardClick(Card card) {
        if (isBusy || card.isMatched() || card == firstCard) {
            return;
        }

        score++;

        card.flipToFront();

        if (firstCard == null) {
            firstCard = card;
        } else {
            secondCard = card;
            isBusy = true;

            new Handler().postDelayed(() -> {
                if (firstCard.getFrontImageId() == secondCard.getFrontImageId()) {
                    matchFound();
                } else {
                    noMatchFound();
                }
            }, 1000);
        }
    }

    private void matchFound() {
        firstCard.setMatched(true);
        secondCard.setMatched(true);
        firstCard.getButton().setBackgroundColor(Color.GREEN);
        secondCard.getButton().setBackgroundColor(Color.GREEN);

        MediaPlayer mp = MediaPlayer.create(this, R.raw.applause);
        mp.start();
        mp.setOnCompletionListener(MediaPlayer::release);

        resetTurn();

        if (checkLevelComplete()) {
            advanceLevel();
        }
    }

    private void noMatchFound() {
        firstCard.flipToBack();
        secondCard.flipToBack();

        resetTurn();
    }

    private void resetTurn() {
        firstCard = null;
        secondCard = null;
        isBusy = false;
    }

    private boolean checkLevelComplete() {
        for (Card card : currentCards) {
            if (!card.isMatched()) {
                return false;
            }
        }
        return true;
    }

    private void advanceLevel() {
        if (currentLevel < 3) {
            String msg = getString(R.string.level_prefix) + currentLevel + getString(R.string.toast_level_complete);
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            setupLevel(currentLevel + 1);
        } else {
            String msg = getString(R.string.toast_game_over) + score;
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
            saveScoreToDatabase(getIntent().getStringExtra("PLAYER_NAME"), score);
        }
    }
    private void saveScoreToDatabase(String playerName, int finalScore) {
        ScoreDbHelper dbHelper = new ScoreDbHelper(this);
        dbHelper.saveOrUpdateScore(playerName, finalScore);

        Toast.makeText(this, getString(R.string.toast_score_saved), Toast.LENGTH_LONG).show();

        showRankingDialog();
    }

    private void showRankingDialog() {
        ScoreDbHelper dbHelper = new ScoreDbHelper(this);
        Cursor cursor = dbHelper.getAllScores();

        if (cursor.getCount() == 0) {
            // No hay datos
            return;
        }

        StringBuilder buffer = new StringBuilder();

        // Recorremos el cursor
        while (cursor.moveToNext()) {
            // Índice 1 es player_name, Índice 2 es score (según tu CREATE TABLE)
            String name = cursor.getString(1);
            int pScore = cursor.getInt(2);

            buffer.append(name).append(": ").append(pScore).append(" clicks\n");
        }
        cursor.close(); // Siempre cerrar el cursor

        // Mostrar el AlertDialog
        new AlertDialog.Builder(this)
                .setTitle(R.string.leaderboard)
                .setMessage(buffer.toString())
                .setPositiveButton("OK", (dialog, which) -> {
                    // Al pulsar OK, cerramos la actividad y volvemos al inicio (opcional)
                    finish();
                })
                .setCancelable(false) // Obliga a pulsar OK
                .show();
    }

}