package com.malalisy.flipview;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


/**
 * Easy and customizable FlipperView
 */

public class FlipView extends FrameLayout {

    // Default animation duration for both horizontal and vertical flipping .
    private static final int DEFAULT_ANIMATION_DURATION = 150;

    // Default minimum swiping distance to flip for both horizontal and vertical.
    private static final int DEFAULT_MIN_SWIPE_DISTANCE = 100;

    // Enable horizontal and vertical swiping
    protected boolean horizontalSwiping, verticalSwiping;

    // Horizontal flipping animation duration
    protected int horizontalDuration;

    // Vertical flipping animation duration
    protected int verticalDuration;


    // Direction for flipping
    @IntDef({Direction.TO_RIGHT, Direction.TO_LEFT,
            Direction.TO_BOTTOM, Direction.TO_TOP})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Direction {
        int TO_RIGHT = 0;
        int TO_LEFT = 1;
        int TO_BOTTOM = 2;
        int TO_TOP = 3;
    }

    // Default flipping direction
    private static final int DEFAULT_DIRECTION = Direction.TO_RIGHT;

    // Current displayed item
    protected int visibleIndex;

    // Animators
    protected Animator leftOutAnimator, leftInAnimator, rightOutAnimator, rightInAnimator, topOutAnimator, topInAnimator, bottomOutAnimator, bottomInAnimator;

    //Flipping flag
    protected boolean flipping = false;

    //Start touch
    protected float startX, startY;

    // Minimum swiping distance to flip
    protected int minHorizontalSwipingDistance, minVerticalSwipingDistance;


    protected OnFlipListener onFLipListener;

    public FlipView(@NonNull Context context) {
        super(context);
        init(null);
    }

    public FlipView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    private void init(AttributeSet attrs) {


        if (attrs == null) {
            // Setup default attributes
            horizontalSwiping = verticalSwiping = true;
            horizontalDuration = verticalDuration = DEFAULT_ANIMATION_DURATION;
            minHorizontalSwipingDistance = minVerticalSwipingDistance = DEFAULT_MIN_SWIPE_DISTANCE;
        } else {
            //Obtain attributes
            TypedArray arr = getContext().getTheme().obtainStyledAttributes(
                    attrs,
                    R.styleable.FlipView,
                    0, 0);

            try {
                horizontalSwiping = arr.getBoolean(R.styleable.FlipView_enableHorizontalSwiping, true);
                verticalSwiping = arr.getBoolean(R.styleable.FlipView_enableVerticalSwiping, true);

                int animDuration = arr.getInt(R.styleable.FlipView_flippingDuration, DEFAULT_ANIMATION_DURATION);
                horizontalDuration = arr.getInt(R.styleable.FlipView_horizontalFlippingDuration, animDuration);
                verticalDuration = arr.getInt(R.styleable.FlipView_verticalFlippingDuration, animDuration);

                minHorizontalSwipingDistance = minVerticalSwipingDistance = DEFAULT_MIN_SWIPE_DISTANCE;

                if (arr.hasValue(R.styleable.FlipView_minSwipingDistance)) {
                    minHorizontalSwipingDistance = arr.getDimensionPixelOffset(R.styleable.FlipView_minSwipingDistance, 0);
                    minVerticalSwipingDistance = arr.getDimensionPixelOffset(R.styleable.FlipView_minSwipingDistance, 0);
                }
                if (arr.hasValue(R.styleable.FlipView_minHorizontalSwipingDistance)) {
                    minHorizontalSwipingDistance = arr.getDimensionPixelOffset(R.styleable.FlipView_minHorizontalSwipingDistance, 0);
                }
                if (arr.hasValue(R.styleable.FlipView_minVerticalSwipingDistance)) {
                    minVerticalSwipingDistance = arr.getDimensionPixelOffset(R.styleable.FlipView_minVerticalSwipingDistance, 0);
                }

            } finally {
                arr.recycle();
            }
        }

        // Set current visible item to the first
        visibleIndex = 0;

        initAnimators();

    }

    private void initAnimators() {

        // Initialize animators
        LinearInterpolator interpolator = new LinearInterpolator();

        // A half of duration for in animation and a half for out animation
        int halfHorizontal = horizontalDuration / 2;

        rightOutAnimator = ObjectAnimator.ofFloat(this, "rotationY", 0f, 90f)
                .setDuration(halfHorizontal);
        rightOutAnimator.setInterpolator(interpolator);

        rightInAnimator = ObjectAnimator.ofFloat(this, "rotationY", -90f, 0f)
                .setDuration(halfHorizontal);
        rightInAnimator.setInterpolator(interpolator);

        leftOutAnimator = ObjectAnimator.ofFloat(this, "rotationY", 0, -90f)
                .setDuration(halfHorizontal);
        leftOutAnimator.setInterpolator(interpolator);

        leftInAnimator = ObjectAnimator.ofFloat(this, "rotationY", 90, 0f)
                .setDuration(halfHorizontal);
        leftInAnimator.setInterpolator(interpolator);

        int halfVertical = verticalDuration / 2;

        topOutAnimator = ObjectAnimator.ofFloat(this, "rotationX", 0f, 90f)
                .setDuration(halfVertical);
        topOutAnimator.setInterpolator(interpolator);

        topInAnimator = ObjectAnimator.ofFloat(this, "rotationX", -90f, 0f)
                .setDuration(halfVertical);
        topInAnimator.setInterpolator(interpolator);

        bottomOutAnimator = ObjectAnimator.ofFloat(this, "rotationX", 0, -90f)
                .setDuration(halfVertical);
        bottomOutAnimator.setInterpolator(interpolator);

        bottomInAnimator = ObjectAnimator.ofFloat(this, "rotationX", 90, 0f)
                .setDuration(halfVertical);
        bottomInAnimator.setInterpolator(interpolator);

    }


    // Flip with passed direction
    public void flip(@Direction int direction) {

        flipping = true;
        switch (direction) {
            case Direction.TO_RIGHT:
                animateOutIn(rightOutAnimator, rightInAnimator);
                break;

            case Direction.TO_LEFT:
                animateOutIn(leftOutAnimator, leftInAnimator);
                break;

            case Direction.TO_BOTTOM:
                animateOutIn(bottomOutAnimator, bottomInAnimator);
                break;

            case Direction.TO_TOP:
                animateOutIn(topOutAnimator, topInAnimator);
                break;
        }

    }

    // Flip with default direction
    public void flip() {
        flip(DEFAULT_DIRECTION);
    }

    // Flip without animation by displaying the next item
    public void flipWithoutAnimation() {
        displayNext();
    }

    // Set the visible item to next view
    private void displayNext() {
        getChildAt(visibleIndex).setVisibility(GONE);

        visibleIndex = (visibleIndex + 1) % getChildCount();

        getChildAt(visibleIndex).setVisibility(VISIBLE);
    }

    public int getVisibleItemIndex() {
        return visibleIndex;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        // Handle swiping if not already flipping
        if (!flipping) {
            switch (event.getAction()) {

                case MotionEvent.ACTION_DOWN:

                    startX = event.getX();
                    startY = event.getY();

                    break;

                case MotionEvent.ACTION_UP:

                    // Flag for horizontal flipping
                    boolean flipped = false;

                    // Check for horizontal swiping first
                    if (horizontalSwiping) {

                        float defX = startX - event.getX();

                        if (defX > minHorizontalSwipingDistance) {
                            flip(Direction.TO_LEFT);
                            flipped = true;
                        } else if (defX < -minHorizontalSwipingDistance) {
                            flip(Direction.TO_RIGHT);
                            flipped = true;
                        }

                    }

                    // If not flipped horizontally, check for vertical swiping
                    if (verticalSwiping && !flipped) {

                        float defY = startY - event.getY();

                        if (defY > minVerticalSwipingDistance) {
                            flip(Direction.TO_TOP);
                        } else if (defY < -minVerticalSwipingDistance) {
                            flip(Direction.TO_BOTTOM);
                        }
                    }

                    break;

            }
        }

        super.onTouchEvent(event);
        return true;
    }

    // Animate the current the out animation, change the visible view and then animate the in animation
    private void animateOutIn(Animator out, final Animator in) {
        // Remove previous listeners
        out.removeAllListeners();
        in.removeAllListeners();

        out.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                in.start();
                displayNext(); // When out animation finished, set the visible view to the next
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        in.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (onFLipListener != null)
                    onFLipListener.onFlipped();
                flipping = false;
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        out.start();
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        // Make sure all children are hidden except the first one ..
        getChildAt(0).setVisibility(VISIBLE);
        for (int i = 1; i < getChildCount(); i++) {
            getChildAt(i).setVisibility(GONE);
        }
    }

    // Set animation duration for all animators
    public void setAnimationDuration(int animationDuration) {
        verticalDuration = animationDuration;
        horizontalDuration = animationDuration;

        // Reset durations for animators
        resetHorizontalDurations();
        resetVerticalDurations();
    }

    private void resetHorizontalDurations() {
        int half = horizontalDuration / 2;

        rightOutAnimator.setDuration(half);
        rightInAnimator.setDuration(half);
        leftOutAnimator.setDuration(half);
        leftInAnimator.setDuration(half);

    }

    private void resetVerticalDurations() {
        int half = verticalDuration / 2;

        topInAnimator.setDuration(half);
        topOutAnimator.setDuration(half);
        bottomInAnimator.setDuration(half);
        bottomOutAnimator.setDuration(half);
    }

    public boolean isFlipping() {
        return flipping;
    }

    public void setFlipping(boolean flipping) {
        this.flipping = flipping;
    }

    public boolean isHorizontalSwiping() {
        return horizontalSwiping;
    }

    public void setHorizontalSwiping(boolean horizontalSwiping) {
        this.horizontalSwiping = horizontalSwiping;
    }

    public boolean isVerticalSwiping() {
        return verticalSwiping;
    }

    public void setVerticalSwiping(boolean verticalSwiping) {
        this.verticalSwiping = verticalSwiping;
    }

    public int getHorizontalDuration() {
        return horizontalDuration;
    }

    public void setHorizontalDuration(int horizontalDuration) {
        this.horizontalDuration = horizontalDuration;
        resetHorizontalDurations();
    }

    public int getVerticalDuration() {
        return verticalDuration;
    }

    public void setVerticalDuration(int verticalDuration) {
        this.verticalDuration = verticalDuration;
        resetVerticalDurations();
    }


    public Animator getLeftOutAnimator() {
        return leftOutAnimator;
    }

    public void setLeftOutAnimator(Animator leftOutAnimator) {
        this.leftOutAnimator = leftOutAnimator;
    }

    public Animator getLeftInAnimator() {
        return leftInAnimator;
    }

    public void setLeftInAnimator(Animator leftInAnimator) {
        this.leftInAnimator = leftInAnimator;
    }

    public Animator getRightOutAnimator() {
        return rightOutAnimator;
    }

    public void setRightOutAnimator(Animator rightOutAnimator) {
        this.rightOutAnimator = rightOutAnimator;
    }

    public Animator getRightInAnimator() {
        return rightInAnimator;
    }

    public void setRightInAnimator(Animator rightInAnimator) {
        this.rightInAnimator = rightInAnimator;
    }

    public Animator getTopOutAnimator() {
        return topOutAnimator;
    }

    public void setTopOutAnimator(Animator topOutAnimator) {
        this.topOutAnimator = topOutAnimator;
    }

    public Animator getTopInAnimator() {
        return topInAnimator;
    }

    public void setTopInAnimator(Animator topInAnimator) {
        this.topInAnimator = topInAnimator;
    }

    public Animator getBottomOutAnimator() {
        return bottomOutAnimator;
    }

    public void setBottomOutAnimator(Animator bottomOutAnimator) {
        this.bottomOutAnimator = bottomOutAnimator;
    }

    public Animator getBottomInAnimator() {
        return bottomInAnimator;
    }

    public void setBottomInAnimator(Animator bottomInAnimator) {
        this.bottomInAnimator = bottomInAnimator;
    }

    public int getMinHorizontalSwipingDistance() {
        return minHorizontalSwipingDistance;
    }

    public void setMinHorizontalSwipingDistance(int minHorizontalSwipingDistance) {
        this.minHorizontalSwipingDistance = minHorizontalSwipingDistance;
    }

    public int getMinVerticalSwipingDistance() {
        return minVerticalSwipingDistance;
    }

    public void setMinVerticalSwipingDistance(int minVerticalSwipingDistance) {
        this.minVerticalSwipingDistance = minVerticalSwipingDistance;
    }

    public OnFlipListener getOnFLipListener() {
        return onFLipListener;
    }

    public void setOnFLipListener(OnFlipListener onFLipListener) {
        this.onFLipListener = onFLipListener;
    }

    public interface OnFlipListener {
        void onFlipped();
    }

}
