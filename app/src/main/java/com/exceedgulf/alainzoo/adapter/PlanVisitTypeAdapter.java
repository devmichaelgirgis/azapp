package com.exceedgulf.alainzoo.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.exceedgulf.alainzoo.R;
import com.exceedgulf.alainzoo.activity.HomeActivity;
import com.exceedgulf.alainzoo.database.models.Animal;
import com.exceedgulf.alainzoo.database.models.Attraction;
import com.exceedgulf.alainzoo.database.models.Experience;
import com.exceedgulf.alainzoo.database.models.WhatsNew;
import com.exceedgulf.alainzoo.databinding.RowPlanVisitTypeBinding;
import com.exceedgulf.alainzoo.fragments.AnimalDetailFragment;
import com.exceedgulf.alainzoo.fragments.AttractionsDetailFragment;
import com.exceedgulf.alainzoo.fragments.ExperianceDetailFragment;
import com.exceedgulf.alainzoo.fragments.WhatsNewDetailFragment;
import com.exceedgulf.alainzoo.utils.ImageUtil;

import java.util.ArrayList;

/**
 * Created by P.P. on 28/12/17
 */
public class PlanVisitTypeAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private ArrayList<T> planvisitTypeArrayListpe;

    public PlanVisitTypeAdapter(final Context mContext) {
        this.mContext = mContext;
        planvisitTypeArrayListpe = new ArrayList<>();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final RowPlanVisitTypeBinding planVisitTypeBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.row_plan_visit_type, parent, false);
        return new planViewHolder(planVisitTypeBinding);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        final Object object = planvisitTypeArrayListpe.get(position);
        if (object instanceof Animal) {
            final Animal animal = (Animal) object;
            ((planViewHolder) holder).getPlanVisitTypeBinding().rowPlntypeCbname.setChecked(((HomeActivity) mContext).getPlaneSelectedItems().containsKey(String.valueOf(animal.getId())));
            ((planViewHolder) holder).getPlanVisitTypeBinding().rowPlntypeTvName.setText(Html.fromHtml(animal.getName()).toString().trim());
            ImageUtil.loadImageFromPicasso(mContext, animal.getThumbnail(), ((planViewHolder) holder).getPlanVisitTypeBinding().rowPlntypeIvMain, ((planViewHolder) holder).getPlanVisitTypeBinding().rowPlntypeIvPlaceholder);
        } else if (object instanceof Experience) {
            final Experience experience = (Experience) object;
            ((planViewHolder) holder).getPlanVisitTypeBinding().rowPlntypeCbname.setChecked(((HomeActivity) mContext).getPlaneSelectedItems().containsKey(String.valueOf(experience.getId())));
            ((planViewHolder) holder).getPlanVisitTypeBinding().rowPlntypeTvName.setText(Html.fromHtml(experience.getName()).toString().trim());
            ImageUtil.loadImageFromPicasso(mContext, experience.getImage(), ((planViewHolder) holder).getPlanVisitTypeBinding().rowPlntypeIvMain, ((planViewHolder) holder).getPlanVisitTypeBinding().rowPlntypeIvPlaceholder);
        } else if (object instanceof Attraction) {
            final Attraction attraction = (Attraction) object;
            ((planViewHolder) holder).getPlanVisitTypeBinding().rowPlntypeCbname.setChecked(((HomeActivity) mContext).getPlaneSelectedItems().containsKey(String.valueOf(attraction.getId())));
            ((planViewHolder) holder).getPlanVisitTypeBinding().rowPlntypeTvName.setText(Html.fromHtml(attraction.getName()).toString().trim());
            ImageUtil.loadImageFromPicasso(mContext, attraction.getThumbnail(), ((planViewHolder) holder).getPlanVisitTypeBinding().rowPlntypeIvMain, ((planViewHolder) holder).getPlanVisitTypeBinding().rowPlntypeIvPlaceholder);
        } else if (object instanceof WhatsNew) {
            final WhatsNew whatsNew = (WhatsNew) object;
            ((planViewHolder) holder).getPlanVisitTypeBinding().rowPlntypeCbname.setChecked(((HomeActivity) mContext).getPlaneSelectedItems().containsKey(String.valueOf(whatsNew.getId())));
            ((planViewHolder) holder).getPlanVisitTypeBinding().rowPlntypeTvName.setText(Html.fromHtml(whatsNew.getName()).toString().trim());
            ImageUtil.loadImageFromPicasso(mContext, whatsNew.getThumbnail(), ((planViewHolder) holder).getPlanVisitTypeBinding().rowPlntypeIvMain, ((planViewHolder) holder).getPlanVisitTypeBinding().rowPlntypeIvPlaceholder);
        }
//        ((planViewHolder) holder).getPlanVisitTypeBinding().rowPlntypeCbname.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                selectUnselectItem(object);
//            }
//        });
        ((planViewHolder) holder).getPlanVisitTypeBinding().llParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (((planViewHolder) holder).getPlanVisitTypeBinding().rowPlntypeCbname.isChecked()) {
                    ((planViewHolder) holder).getPlanVisitTypeBinding().rowPlntypeCbname.setChecked(false);
                } else {
                    ((planViewHolder) holder).getPlanVisitTypeBinding().rowPlntypeCbname.setChecked(true);
                }
                selectUnselectItem(object);
            }
        });

        ((planViewHolder) holder).getPlanVisitTypeBinding().rowPlntypeIvInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (object instanceof Animal) {
                    ((HomeActivity)mContext).addFragment(((HomeActivity)mContext).getcurrentFragment(), AnimalDetailFragment.getAnimalDetailFragment(((Animal)object).getId()));

                } else if (object instanceof Experience) {
                    ((HomeActivity)mContext).addFragment(((HomeActivity)mContext).getcurrentFragment(), ExperianceDetailFragment.getExperianceDetailFragment(((Experience)object).getId()));

                } else if (object instanceof Attraction) {
                    ((HomeActivity)mContext).addFragment(((HomeActivity)mContext).getcurrentFragment(), AttractionsDetailFragment.getAttractionsDetailFragment(((Attraction)object).getId()));

                } else if (object instanceof WhatsNew) {
                    ((HomeActivity)mContext).addFragment(((HomeActivity)mContext).getcurrentFragment(), WhatsNewDetailFragment.getWhatsNewDetailFragment(((WhatsNew)object).getId()));
                }
            }
        });
    }

    private void selectUnselectItem(final Object object) {
        if (object instanceof Animal) {
            final Animal animal = (Animal) object;
            final String id = animal.getId().toString();
            if (((HomeActivity) mContext).getPlaneSelectedItems().containsKey(id)) {
                ((HomeActivity) mContext).getPlaneSelectedItems().remove(id);
            } else {
                ((HomeActivity) mContext).getPlaneSelectedItems().put(id, animal);
            }
        } else if (object instanceof Experience) {
            final Experience experience = (Experience) object;
            final String id = experience.getId().toString();
            if (((HomeActivity) mContext).getPlaneSelectedItems().containsKey(id)) {
                ((HomeActivity) mContext).getPlaneSelectedItems().remove(id);
            } else {
                ((HomeActivity) mContext).getPlaneSelectedItems().put(id, experience);
            }
        } else if (object instanceof Attraction) {
            final Attraction attraction = (Attraction) object;
            final String id = attraction.getId().toString();
            if (((HomeActivity) mContext).getPlaneSelectedItems().containsKey(id)) {
                ((HomeActivity) mContext).getPlaneSelectedItems().remove(id);
            } else {
                ((HomeActivity) mContext).getPlaneSelectedItems().put(id, attraction);
            }
        } else if (object instanceof WhatsNew) {
            final WhatsNew whatsNew = (WhatsNew) object;
            final String id = whatsNew.getId().toString();
            if (((HomeActivity) mContext).getPlaneSelectedItems().containsKey(id)) {
                ((HomeActivity) mContext).getPlaneSelectedItems().remove(id);
            } else {
                ((HomeActivity) mContext).getPlaneSelectedItems().put(id, whatsNew);
            }
        }

    }

    @Override
    public int getItemCount() {
        return planvisitTypeArrayListpe.size();
    }

    public void addAllModel(final ArrayList<T> planvisitTypeArrayListpe) {
        this.planvisitTypeArrayListpe.addAll(planvisitTypeArrayListpe);
        this.notifyDataSetChanged();
    }

    public ArrayList<T> getPlanvisitTypeArrayListpe() {
        return planvisitTypeArrayListpe;
    }

    public void clearItems() {
        this.planvisitTypeArrayListpe.clear();
        this.planvisitTypeArrayListpe.trimToSize();
        this.notifyDataSetChanged();
    }

    class planViewHolder extends RecyclerView.ViewHolder {
        private RowPlanVisitTypeBinding planVisitTypeBinding;

        planViewHolder(final RowPlanVisitTypeBinding planVisitTypeBinding) {
            super(planVisitTypeBinding.getRoot());
            this.planVisitTypeBinding = planVisitTypeBinding;

        }

        public RowPlanVisitTypeBinding getPlanVisitTypeBinding() {
            return planVisitTypeBinding;
        }
    }

}