package com.exceedgulf.alainzoo.views.expands;

import android.view.View;
import android.widget.TextView;

import com.exceedgulf.alainzoo.R;

public class ChildsViewHolder extends ChildViewHolder {

    private TextView mChildNamesTextView;

    public ChildsViewHolder(View itemView) {
        super(itemView);
        mChildNamesTextView = (TextView) itemView.findViewById(R.id.tv_childs);
    }

    public void bind(final Children childs, final int position) {
        mChildNamesTextView.setText(childs.getName());
//        mChildNamesTextView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Fragment selectedFragment = null;
//                String tag = null;
//                Fragment currentFragment = ((HomeActivity) v.getContext()).getSupportFragmentManager().findFragmentById(R.id.frame_container);
//
//                switch (position) {
//                    case 2:
//                        selectedFragment =null;
//                        break;
//                    case 3:
//                        selectedFragment = null;
//                        break;
//                    case 4:
//                        selectedFragment = null;
//                        break;
//                    case 5:
//                        selectedFragment = null;
//                        break;
//                    case 6:
//                        selectedFragment = null;
//                        break;
//                }
//
//
//            }
//        });
    }
}
