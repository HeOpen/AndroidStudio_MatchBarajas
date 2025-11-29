package com.example.dm2e_parejabarajas_olaheliabe;

import android.widget.Button;
// No explicit import for R needed, as it is in the same package.

public class Card {
    private final int frontImageId;
    private final int backImageId = R.drawable.card0; // default back image
    private final Button button;
    private boolean isMatched = false;

    public Card(int frontImageId, Button button) {
        this.frontImageId = frontImageId;
        this.button = button;
    }

    public int getFrontImageId() {
        return frontImageId;
    }

    public Button getButton() {
        return button;
    }

    public boolean isMatched() {
        return isMatched;
    }

    public void setMatched(boolean matched) {
        isMatched = matched;
    }

    /** Flips the card to show its front image. */
    public void flipToFront() {
        button.setBackgroundResource(frontImageId);
    }

    /** Flips the card to show its back image. */
    public void flipToBack() {
        button.setBackgroundResource(backImageId);
    }
}