package com.example.dm2e_parejabarajas_olaheliabe;

import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

// Since Card is in the same package, this line is not strictly needed,
// but it is harmless if used:
// import com.example.dm2e_parejabarajas_olaheliabe.Card;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GameActivity extends AppCompatActivity {

    private TextView playerNameTv;
    private GridLayout cardsGrid;

    // --- Game State Variables ---
    private int currentLevel = 1;
    private int score = 0; // Tracks total clicks (pulsaciones)
    private Card firstCard = null;
    private Card secondCard = null;
    private boolean isBusy = false;
    private List<Card> currentCards = new ArrayList<>();

    // Available card images (front side: 6 unique images)
    // Available card images (front side: 6 unique images)
    // Note: card0 is reserved for the back of the card.
    private final List<Integer> availableImages = Arrays.asList(
            R.drawable.card1,
            R.drawable.card2,
            R.drawable.card3,
            R.drawable.card4,
            R.drawable.card5,
            R.drawable.card6
    );
    // R.drawable.a0_card is the back/default image, handled in Card.java

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        String playerName = getIntent().getStringExtra("PLAYER_NAME");
        setTitle("Cartas_" + playerName);

        playerNameTv = findViewById(R.id.tv_player_name);
        cardsGrid = findViewById(R.id.grid_cards);

        playerNameTv.setText("Jugador: " + playerName);

        setupLevel(currentLevel);
    }

    // --- LEVEL SETUP LOGIC ---
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

        // 1. Select and Shuffle Images
        List<Integer> selectedImages = new ArrayList<>(availableImages.subList(0, numPairs));
        List<Integer> gameImages = new ArrayList<>();
        gameImages.addAll(selectedImages);
        gameImages.addAll(selectedImages);
        Collections.shuffle(gameImages);

        // 2. Configure Grid
        cardsGrid.setColumnCount(numCards / 2);
        cardsGrid.setRowCount(2);
        if (numCards == 12) cardsGrid.setRowCount(4);

        // 3. Create Buttons (Cards)
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

        Card card = new Card(imageId, button); // Card is found correctly here
        button.setTag(card);
        currentCards.add(card);

        card.flipToBack();

        button.setOnClickListener(v -> handleCardClick(card));
        return button;
    }

    // --- GAME LOGIC (Rest of the methods remain the same) ---

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
        firstCard.getButton().setBackgroundColor(Color.YELLOW);
        secondCard.getButton().setBackgroundColor(Color.YELLOW);

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
            Toast.makeText(this, "Nivel " + currentLevel + " completado! Siguiente nivel...", Toast.LENGTH_SHORT).show();
            setupLevel(currentLevel + 1);
        } else {
            Toast.makeText(this, "Juego terminado! Puntuación final: " + score, Toast.LENGTH_LONG).show();
            saveScoreToDatabase(getIntent().getStringExtra("PLAYER_NAME"), score);
        }
    }

    private void saveScoreToDatabase(String playerName, int finalScore) {
        // Implementation for ScoreDbHelper goes here
    }
}