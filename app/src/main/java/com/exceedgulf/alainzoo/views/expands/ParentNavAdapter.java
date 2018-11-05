package com.exceedgulf.alainzoo.views.expands;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.exceedgulf.alainzoo.Interfaces.OnNavChildClick;
import com.exceedgulf.alainzoo.Interfaces.OnNavParentClick;
import com.exceedgulf.alainzoo.R;

import java.util.List;

public class ParentNavAdapter extends ExpandableRecyclerAdapter<ParentNavViewHolder, ChildsViewHolder> {

    private LayoutInflater mInflator;
    private OnNavParentClick onNavParentClick;
    private OnNavChildClick onNavChildClick;

    public ParentNavAdapter(final Context context, List<? extends ParentListItem> parentItemList, final OnNavParentClick onNavParentClick, final OnNavChildClick onNavChildClick) {
        super(parentItemList);
        mInflator = LayoutInflater.from(context);
        this.onNavParentClick = onNavParentClick;
        this.onNavChildClick = onNavChildClick;
    }

    @Override
    public ParentNavViewHolder onCreateParentViewHolder(ViewGroup parentViewGroup) {
        View parentView = mInflator.inflate(R.layout.nav_parent_view, parentViewGroup, false);
        return new ParentNavViewHolder(parentView);
    }

    @Override
    public ChildsViewHolder onCreateChildViewHolder(ViewGroup childViewGroup) {
        View childsView = mInflator.inflate(R.layout.nav_child_view, childViewGroup, false);
        return new ChildsViewHolder(childsView);
    }

    @Override
    public void onBindParentViewHolder(ParentNavViewHolder parentNavViewHolder, final int position, ParentListItem parentListItem) {
        final NavigationParent navigationParent = (NavigationParent) parentListItem;
        parentNavViewHolder.bind(navigationParent, position);
        if (position != 10) {
            parentNavViewHolder.mArrowExpandImageView.setVisibility(View.GONE);
            parentNavViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onNavParentClick != null) {
                        onNavParentClick.onParentClicked(position, navigationParent.getName());
                    }
                }
            });
        } else {
            parentNavViewHolder.mArrowExpandImageView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBindChildViewHolder(final ChildsViewHolder childViewHolder, final int position, Object childListItem) {
        final Children child = (Children) childListItem;
        childViewHolder.bind(child, position);
        childViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onNavParentClick != null) {
                    onNavChildClick.onChildClicked(position, child.getName());
                }
            }
        });
    }
}
