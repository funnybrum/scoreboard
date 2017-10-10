package com.brum.scoreboard;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.brum.scoreboard.common.Utils;
import com.brum.scoreboard.controls.ClippingTextView;
import com.brum.scoreboard.data.Settings;
import com.brum.scoreboard.data.TeamData;

public class BoardActivity extends Activity {
    private Settings settings;
    private List<LinearLayout> columns = new ArrayList<LinearLayout>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.board, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menu_item_exit:
            finish();
            return true;
        case R.id.menu_item_reset:
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.reset_dialog_title);
            builder.setMessage(R.string.reset_dialog_text);
            builder.setPositiveButton(R.string.reset_button, new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    for (TeamData td : settings.getTeams()) {
                        td.getScores().clear();
                    }
                    settings.getUndoHistory().clear();
                }
            });
            builder.setNegativeButton(R.string.cancel, null);
            builder.show();
            return true;
        case R.id.menu_item_undo:
            if (settings.getUndoHistory().size() > 0) {
                int index = settings.getUndoHistory().remove(
                        settings.getUndoHistory().size() - 1);
                if (index < settings.getTeams().size()) {
                    settings.getTeams().get(index).getScores().remove(
                            settings.getTeams().get(index).getScores().size() - 1);
                }
            }
            return true;
        case R.id.menu_item_settings:
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivityForResult(settingsIntent,
                    Constants.SETTINGS_REQUEST_CODE);
            return true;
        case R.id.menu_item_help:
            Intent helpIntent = new Intent(this, HelpActivity.class);
            startActivity(helpIntent);
            return true;
        }
        return false;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && shouldConfigure()) {
            configure();
        }
    }

    private boolean shouldConfigure() {
        if (columns.isEmpty()) {
            return true;
        }

        LinearLayout scoreBoard = (LinearLayout) findViewById(R.id.layout_score_board);
        if (scoreBoard.findViewById(columns.get(0).getId()) != null) {
            return true;
        }

        return false;
    }

    private void configure() {
        if (settings == null) {
            updateFromPreferences();
        }
        columns.clear();
        LinearLayout scoreBoard = (LinearLayout) findViewById(R.id.layout_score_board);

        scoreBoard.removeAllViews();

        int columnsCount = settings.getTeams().size();
        int separatorSize = convertDpToPixel(2, this);

        int columnWidth = (Utils.getScreenWidth(getWindowManager()) - (columnsCount - 1)
                * separatorSize)
                / columnsCount;

        // Calculate the highest text view size
        int headerHeight = 0;
        for (int i = 0; i < columnsCount; i++) {
            int height = Utils.measureTextHeight(
                    settings.getTeams().get(i).getName(),
                    columnWidth,
                    settings.getFontSize(),
                    this);
            headerHeight = Math.max(headerHeight, height);
        }

        int lineHeight = Utils.measureTextHeight(
                "0\n1\n2\n3\n4\n5\n6\n7\n8\n9",
                columnWidth,
                settings.getFontSize(),
                this) / 10;

        StringBuilder separatorText = new StringBuilder("---");
        while (Utils.measureTextWidth(separatorText.toString(),
                                      settings.getFontSize(),
                                      this) < columnWidth * 0.8) {
            separatorText.append("-");
        }

        for (int i = 0; i < columnsCount; i++) {
            addColumn(settings.getTeams().get(i),
                      i + 1,
                      columnWidth,
                      headerHeight,
                      lineHeight,
                      separatorSize,
                      separatorText.toString(),
                      scoreBoard);
        }
    }

    private void addColumn(final TeamData teamData,
                           final int index,
                           int width,
                           int headerHeight,
                           int lineHeight,
                           int separatorSize,
                           String separatorText,
                           LinearLayout container) {
        LayoutInflater layoutInflater =
                (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout column = (LinearLayout) layoutInflater.inflate(
                R.layout.team_column, new LinearLayout(this), true);

        column.setId(index);

        final TextView teamName = (TextView) column
                .findViewById(R.id.team_name);
        final ClippingTextView teamScore = (ClippingTextView) column
                .findViewById(R.id.team_score);

        teamName.setTextSize(TypedValue.COMPLEX_UNIT_SP, settings.getFontSize());
        teamName.setText(teamData.getName());
        teamName.setHeight(headerHeight);

        teamScore.setTextSize(TypedValue.COMPLEX_UNIT_SP,
                settings.getFontSize());

        teamScore.setData(teamData.getScores(), lineHeight, separatorText);

        LayoutParams params = new LayoutParams(width, LayoutParams.MATCH_PARENT);
        column.setLayoutParams(params);
        container.addView(column);

        if (index < settings.getTeams().size()) {
            // add separator
            params = new LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.MATCH_PARENT);

            View separator = new View(this);
            params.width = separatorSize;
            separator.setLayoutParams(params);
            separator.setBackgroundColor(getResources().getColor(
                    android.R.color.darker_gray));
            container.addView(separator);
        }

        // onClick handlers
        teamName.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showTeamNameDialog(teamData.getName(), index);
                return true;
            }
        });

        teamScore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (teamData.getScores().isEmpty()) {
                    showTeamScoreDialog(settings.getMinScore(), index);
                } else {
                    showTeamScoreDialog(
                            teamData.getScores().get(
                                    teamData.getScores().size() - 1),
                            index);
                }
            }
        });

        columns.add(column);
    }

    public static int convertDpToPixel(int dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return (int) px;
    }

    @Override
    protected void onStop() {
        storePrefs();
        super.onStop();
    }

    /**
     * Store the app settings using the settings preference storage.
     */
    private void storePrefs() {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());

        if (settings == null ||
            settings.getTeams() == null ||
            settings.getTeams().isEmpty()) {
            // Some strange corner case causing null pointer exception.
            return;
        }

        SharedPreferences.Editor editor = prefs.edit();

        StringBuilder teamNames = new StringBuilder();
        StringBuilder teamScores = new StringBuilder();
        for (TeamData teamData : settings.getTeams()) {
            teamNames.append(teamData.getName());
            teamNames.append(";");

            for (Integer score : teamData.getScores()) {
                teamScores.append(score);
                teamScores.append(",");
            }
            teamScores.append(";");
        }

        StringBuilder undoHistory = new StringBuilder();
        for (Integer i : settings.getUndoHistory()) {
            undoHistory.append(i);
            undoHistory.append(";");
        }

        editor.putString("team_names", teamNames.toString());
        editor.putString("team_scores", teamScores.toString());
        editor.putString("undo_history", undoHistory.toString());

        editor.commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.SETTINGS_REQUEST_CODE) {
            updateFromPreferences();
            findViewById(R.id.layout_score_board).postInvalidate();
        }
    }

    /**
     * Reads the team settings from the settings preference storage.
     */
    private void updateFromPreferences() {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());

        settings = new Settings();

        int fontSize = Integer.parseInt(prefs.getString("font_size", "18"));
        int minScore = Integer.parseInt(prefs.getString("min_score", "1"));
        int maxScore = Integer.parseInt(prefs.getString("max_score", "99"));

        settings.setFontSize(fontSize);
        settings.setMinScore(minScore);
        settings.setMaxScore(maxScore);
        settings.setTeams(new ArrayList<TeamData>());

        int teamCount = Integer.parseInt((prefs.getString("team_count", "4")));

        String[] names = prefs.getString("team_names", "").split(";");
        String[] scores = prefs.getString("team_scores", "").split(";");

        for (int i = 0; i < teamCount; i++) {
            String name;
            List<Integer> score = null;

            if (names.length > i && names[i] != null && !names[i].isEmpty()) {
                name = names[i];
            } else {
                name = Constants.DEFAULT_TEAM_NAMES[i];
            }

            if (scores.length > i && scores[i] != null && !scores[i].isEmpty()) {
                score = new ArrayList<Integer>();
                for (String s : scores[i].split(",")) {
                    if (s != null && !s.isEmpty()) {
                        score.add(Integer.parseInt(s));
                    }
                }
            }

            settings.getTeams().add(new TeamData(name, score));
        }

        String[] undoHistory = prefs.getString("undo_history", "").split(";");
        for (String s : undoHistory) {
            if (s != null && !s.isEmpty()) {
                settings.getUndoHistory().add(Integer.parseInt(s));
            }
        }
    }

    public void setTeamName(String name, int teamIndex) {
        settings.getTeams().get(teamIndex).setName(name);
        findViewById(R.id.layout_score_board).postInvalidate();
    }

    public void addTeamScore(Integer score, int teamIndex) {
        settings.getTeams().get(teamIndex).getScores().add(score);
        findViewById(R.id.layout_score_board).postInvalidate();
    }

    private void showTeamNameDialog(String currentName, final int teamIndex) {
        final BoardActivity listener = this;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        final EditText edit = new EditText(this);
        edit.setText(currentName);

        builder.setTitle(R.string.edit_team_name_title);
        builder.setView(edit);
        builder.setPositiveButton(R.string.ok,
                new AlertDialog.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.setTeamName(edit.getText().toString(),
                                teamIndex - 1);
                    }
                });

        builder.setNegativeButton(R.string.cancel, null);

        builder.create().show();
    }

    private void showTeamScoreDialog(int value, final int teamIndex) {
        final BoardActivity listener = this;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        final NumberPicker score = new NumberPicker(this);
        score.setMinValue(settings.getMinScore());
        score.setMaxValue(settings.getMaxScore());
        score.setValue(value);

        builder.setTitle(R.string.edit_team_score_title);
        builder.setView(score);
        builder.setPositiveButton(R.string.ok,
                new AlertDialog.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.addTeamScore(score.getValue(), teamIndex - 1);
                        settings.getUndoHistory().add(teamIndex - 1);
                    }
                });
        builder.setNegativeButton(R.string.cancel, null);

        builder.create().show();
    }
}