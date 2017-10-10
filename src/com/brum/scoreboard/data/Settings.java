package com.brum.scoreboard.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Configuration options for the score board. Holds also the team data.
 */
public class Settings {
    private int fontSize = 18;
    private List<TeamData> teams = new ArrayList<TeamData>();
    private List<Integer> undoHistory = new ArrayList<Integer>();
    private int minScore;
    private int maxScore;

    /**
     * Get the app font size.
     *
     * @return font size.
     */
    public int getFontSize() {
        return fontSize;
    }

    /**
     * Set the app font size property value.
     *
     * @param fontSize
     */
    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    /**
     * Set the teams.
     *
     * @param teams the teams data.
     */
    public void setTeams(List<TeamData> teams) {
        this.teams = teams;
    }

    /**
     * Get the teams.
     *
     * @return list of {@link TeamData}.
     */
    public List<TeamData> getTeams() {
        return teams;
    }

    /**
     * Get the undo history (column indexes).
     *
     * @return list of last modified columns.
     */
    public List<Integer> getUndoHistory() {
        return undoHistory;
    }

    /**
     * Set the undo history.
     *
     * @param undoHistory the undo history.
     */
    public void setUndoHistory(List<Integer> undoHistory) {
        this.undoHistory = undoHistory;
    }

    public int getMinScore() {
        return minScore;
    }

    public void setMinScore(int minScore) {
        this.minScore = minScore;
    }

    public int getMaxScore() {
        return maxScore;
    }

    public void setMaxScore(int maxScore) {
        this.maxScore = maxScore;
    }
}