package pokercc.android.ui.views;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.IntRange;
import androidx.core.math.MathUtils;

/**
 * 分数条
 */
public class ScoreBar extends View {

    private TextPaint progressTextPaint, titleTextPaint;
    private Paint progressPaint, progressBackgroundPaint;

    private static final int MAX_PROGRESS = 100;
    private int progress = 75;
    private int progressTextSize;
    private int progressTextColor;
    private String title;
    private int titleTextColor;
    private int titleTextSize;
    private int titleMarginTop;
    private int progressColor;
    private int progressWidth;
    private int progressBackground;
    private int progressBackgroundWidth;
    private int radius;
    private final Rect progressTextBounds = new Rect();
    private final Rect titleBounds = new Rect();

    private final RectF progressRect = new RectF();
    private ValueAnimator progressAnimtor;

    public ScoreBar(Context context) {
        super(context);
        init(null, 0);
    }

    public ScoreBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public ScoreBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
            attrs, R.styleable.ScoreBar, defStyle, 0);

        radius = a.getDimensionPixelSize(R.styleable.ScoreBar_radius, 60);
        progressTextSize = a.getDimensionPixelSize(R.styleable.ScoreBar_progress_text_size, 30);
        progressTextColor = a.getColor(R.styleable.ScoreBar_progress_text_color, Color.WHITE);
        title = a.getString(R.styleable.ScoreBar_title);
        titleTextColor = a.getColor(R.styleable.ScoreBar_title_text_color, Color.WHITE);
        titleTextSize = a.getDimensionPixelSize(R.styleable.ScoreBar_title_text_size, 24);
        titleMarginTop = a.getDimensionPixelSize(R.styleable.ScoreBar_title_margin_top, 10);
        progressColor = a.getColor(R.styleable.ScoreBar_progress_color, Color.WHITE);
        progressWidth = a.getDimensionPixelSize(R.styleable.ScoreBar_progress_width, 8);
        progressBackground = a.getColor(R.styleable.ScoreBar_progress_background, Color.GRAY);
        progressBackgroundWidth = a.getDimensionPixelSize(R.styleable.ScoreBar_progress_background_width, 4);

        a.recycle();


        progressTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        progressTextPaint.setTextAlign(Paint.Align.CENTER);
        progressTextPaint.setColor(progressTextColor);
        progressTextPaint.setTextSize(progressTextSize);

        titleTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        titleTextPaint.setTextAlign(Paint.Align.CENTER);
        titleTextPaint.setColor(titleTextColor);
        titleTextPaint.setTextSize(titleTextSize);

        progressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        progressPaint.setColor(progressColor);
        progressPaint.setStrokeWidth(progressWidth);
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeCap(Paint.Cap.ROUND);


        progressBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        progressBackgroundPaint.setColor(progressBackground);
        progressBackgroundPaint.setStrokeWidth(progressBackgroundWidth);
        progressBackgroundPaint.setStyle(Paint.Style.STROKE);
        progressBackgroundPaint.setStrokeCap(Paint.Cap.BUTT);
        measureText();
    }

    private void measureText() {

        String progressText = getFormatProgressText();
        progressTextPaint.getTextBounds(progressText, 0, progressText.length() - 1, progressTextBounds);
        if (!TextUtils.isEmpty(title)) {
            titleTextPaint.getTextBounds(title, 0, title.length() - 1, titleBounds);
        }

    }

    public void setProgress(@IntRange(from = 0, to = 100) int progress, final boolean anim) {
        final int oldProgress = this.progress;
        this.progress = MathUtils.clamp(progress, 0, 100);
        if (oldProgress != this.progress) {
            measureText();
            if (!anim) {
                invalidate();
            } else {
                cancelAnimator();
                progressAnimtor = ValueAnimator.ofInt(oldProgress, this.progress);
                progressAnimtor
                    .setDuration(400)
                    .addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            int value = (int) animation.getAnimatedValue();
                            setProgress(value, false);
                        }
                    });
                progressAnimtor.start();
            }

        }
    }

    private void cancelAnimator() {
        if (progressAnimtor != null) {
            progressAnimtor.cancel();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        cancelAnimator();
    }

    private String getFormatProgressText() {
        if (progressTextFormatter != null) {
            return progressTextFormatter.getFormatProgressText(progress);
        }
        return String.valueOf(progress);
    }

    private ProgressTextFormatter progressTextFormatter;

    public ScoreBar setProgressTextFormatter(ProgressTextFormatter progressTextFormatter) {
        this.progressTextFormatter = progressTextFormatter;
        return this;
    }

    public interface ProgressTextFormatter {
        String getFormatProgressText(@IntRange(from = 0, to = 100) int progress);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (widthMode == MeasureSpec.UNSPECIFIED || heightMode == MeasureSpec.UNSPECIFIED) {
            int width = Math.max(getMinimumWidth(), 2 * radius + getPaddingLeft() + getPaddingRight());
            int height = Math.max(getMinimumHeight(), 2 * radius + getPaddingTop() + getPaddingBottom());
            setMeasuredDimension(width, height);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        final int paddingLeft = getPaddingLeft();
        final int paddingTop = getPaddingTop();
        final int paddingRight = getPaddingRight();
        final int paddingBottom = getPaddingBottom();

        final int contentWidth = getWidth() - paddingLeft - paddingRight;
        final int contentHeight = getHeight() - paddingTop - paddingBottom;
        final int progressWidth = Math.min(contentWidth, contentHeight);

        final int contentContentX = (contentWidth - progressWidth >> 1) + paddingLeft + (progressWidth >> 1);
        final int contentContentY = (contentHeight - progressWidth >> 1) + paddingTop + (progressWidth >> 1);

        final int progressAngle = (int) (progress / 100.0f * 360);
        final int progressLeft = (getWidth() - progressWidth) >> 1;
        final int progressTop = (getHeight() - progressWidth) >> 1;
        画进度:
        {

            int saveCount = canvas.save();
            canvas.rotate(-90, contentContentX, contentContentY);

            progressRect.set(progressLeft, progressTop, progressLeft + progressWidth, progressTop + progressWidth);
            // 画背景
            canvas.drawArc(progressRect, progressAngle, 360 - progressAngle, false, progressBackgroundPaint);
            // 画进度
            canvas.drawArc(progressRect, 0, progressAngle, false, progressPaint);
            canvas.restoreToCount(saveCount);
        }
        画文字:
        {
            String progressText = getFormatProgressText();
            int textPaddingTop = (progressWidth - progressTextBounds.height() - titleBounds.height() - titleMarginTop) >> 1;

            int progressTextBottom = progressTop + textPaddingTop + progressTextBounds.height();
            canvas.drawText(progressText, contentContentX, progressTextBottom, progressTextPaint);
            if (!TextUtils.isEmpty(title)) {
                canvas.drawText(title, contentContentX, progressTextBottom + titleMarginTop + titleBounds.height(), titleTextPaint);
            }

        }

    }

}
