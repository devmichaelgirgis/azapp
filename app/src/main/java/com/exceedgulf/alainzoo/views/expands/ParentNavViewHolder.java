package com.exceedgulf.alainzoo.views.expands;

import android.support.v7.widget.AppCompatImageView;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.exceedgulf.alainzoo.R;
import com.exceedgulf.alainzoo.preference.PrefCons;
import com.exceedgulf.alainzoo.preference.SharedPrefceHelper;

public class ParentNavViewHolder extends ParentViewHolder {

    private static final float INITIAL_POSITION = 0.0f;
    private static final float ROTATED_POSITION = 90f;
    private static final float ARABIC_ROTATED_POSITION = -90f;
    public final AppCompatImageView mArrowExpandImageView, mNavIcon;
    private int position;
    private TextView mNavParentTextView;

    public ParentNavViewHolder(View itemView) {
        super(itemView);
        mNavParentTextView = itemView.findViewById(R.id.tv_nav_parent);
        mNavIcon = itemView.findViewById(R.id.iv_nav_icon);
        mArrowExpandImageView = itemView.findViewById(R.id.iv_arrow_expand);
    }

    public void bind(NavigationParent navigationParent, int position) {
        this.position = position;
        mNavParentTextView.setText(navigationParent.getName());
        mNavIcon.setImageResource(navigationParent.getmImage());
    }

    @Override
    public void setExpanded(boolean expanded) {
        super.setExpanded(expanded);
        if (this.position == 10) {
            if (expanded) {
                if (SharedPrefceHelper.getInstance().get(PrefCons.APP_LANGUAGE, "en").equalsIgnoreCase("ar")) {
                    mArrowExpandImageView.setRotation(ARABIC_ROTATED_POSITION);
                } else {
                    mArrowExpandImageView.setRotation(ROTATED_POSITION);
                }
            } else {
                mArrowExpandImageView.setRotation(INITIAL_POSITION);
            }

        }
    }

    @Override
    public void onExpansionToggled(boolean expanded) {
        super.onExpansionToggled(expanded);
        if (this.position == 10) {
            RotateAnimation rotateAnimation;
            float rotatedPosition;
            if (SharedPrefceHelper.getInstance().get(PrefCons.APP_LANGUAGE, "en").equalsIgnoreCase("ar")) {
                rotatedPosition = ARABIC_ROTATED_POSITION;
            } else {
                rotatedPosition = ROTATED_POSITION;
            }

            if (expanded) { // rotate clockwise
                rotateAnimation = new RotateAnimation(rotatedPosition,
                        INITIAL_POSITION,
                        RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                        RotateAnimation.RELATIVE_TO_SELF, 0.5f);
            } else { // rotate counterclockwise
                rotateAnimation = new RotateAnimation(-1 * rotatedPosition,
                        INITIAL_POSITION,
                        RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                        RotateAnimation.RELATIVE_TO_SELF, 0.5f);
            }

            rotateAnimation.setDuration(200);
            rotateAnimation.setFillAfter(true);
            mArrowExpandImageView.startAnimation(rotateAnimation);
        }

    }
}
