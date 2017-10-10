package com.brum.scoreboard.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Team data object. Holds team name and score and perform some basic
 * validations.
 */
public class TeamData {
    String teamName;
    List<Integer> scores;

    /**
     * Create team data object.
     *
     * @param name name of the team; must not be null.
     * @param scores score of the team.
     */
    public TeamData(String name, List<Integer> scores) {
        validateTeamName(name);

        if (scores == null) {
            scores = new ArrayList<Integer>();
        }

        this.teamName = name;
        this.scores = scores;
    }

    private void validateTeamName(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Team name is requred.");
        }
    }

    /**
     * Get the team name.
     *
     * @return team name.
     */
    public String getName() {
        return teamName;
    }

    /**
     * Set the team name.
     *
     * @param name team name; must not be null.
     */
    public void setName(String name) {
        validateTeamName(name);
        teamName = name;
    }

    /**
     * Get the team score.
     *
     * @return team score list, can not be null.
     */
    public List<Integer> getScores() {
        return scores;
    }
}
