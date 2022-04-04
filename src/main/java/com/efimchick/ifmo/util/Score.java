package com.efimchick.ifmo.util;

public enum Score {
    A("A",90),
    B("B",83),
    C("C",75),
    D("D",68),
    E("E",60),
    F("F",0);

    private final int minScore;
    private final String nameScore;

    Score(String nameScore, int minScore) {
        this.minScore = minScore;
        this.nameScore = nameScore;
    }

    public int getMinScore() {
        return minScore;
    }

    public String getNameScore() {
        return nameScore;
    }
}
