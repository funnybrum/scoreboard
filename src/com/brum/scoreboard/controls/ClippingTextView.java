package com.brum.scoreboard.controls;

import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Custom text view that clips its text upper part.
 */
public class ClippingTextView extends TextView {
    private boolean isTextClipped = false;
    private List<Integer> scores;
    private int textHeight;
    private String separator;

    public void setData(List<Integer> data, int textHeight, String separator) {
        this.scores = data;
        this.textHeight = textHeight;
        this.separator = separator;
    }

    public ClippingTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public ClippingTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ClippingTextView(Context context) {
        super(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (!isTextClipped) {
            // Calculate the max row count
            int freeRowCount = canvas.getHeight() / textHeight;

            int sum = 0;
            for (Integer s : scores) {
                sum += s;
            }
            StringBuilder scoreText = new StringBuilder();
            scoreText.append(separator);
            scoreText.append("\n");
            scoreText.append(sum);
            freeRowCount -= 2; // Just added two lines

            for (int i = scores.size() - 1; i >=0; i--) {
                if (freeRowCount == 1 && i > 0) {
                    scoreText.insert(0, "...\n");
                    break;
                }

                scoreText.insert(0,"\n");
                scoreText.insert(0,scores.get(i).toString());
                freeRowCount--;
            }
            setText(scoreText.toString());
            isTextClipped = true;
        }
        super.onDraw(canvas);
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        isTextClipped = false;
        super.setText(text, type);
    }
}
