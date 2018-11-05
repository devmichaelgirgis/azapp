package com.exceedgulf.alainzoo.fragments;

import android.content.Context;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.exceedgulf.alainzoo.Interfaces.ApiCallback;
import com.exceedgulf.alainzoo.R;
import com.exceedgulf.alainzoo.activity.HomeActivity;
import com.exceedgulf.alainzoo.database.AlainZooDB;
import com.exceedgulf.alainzoo.database.models.FAQs;
import com.exceedgulf.alainzoo.databinding.FragmentFaqBinding;
import com.exceedgulf.alainzoo.managers.FaqManager;
import com.exceedgulf.alainzoo.models.FAQModel;
import com.exceedgulf.alainzoo.models.FaqQuestionModel;
import com.exceedgulf.alainzoo.utils.DisplayDialog;
import com.exceedgulf.alainzoo.utils.LangUtils;
import com.exceedgulf.alainzoo.utils.SnackbarUtils;
import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;

import java.util.ArrayList;
import java.util.Locale;

public class FAQFragment extends BaseFragment {

    private static final float INITIAL_POSITION = 90f;
    private static final float ROTATED_POSITION = 0f;
    private static final float ARABIC_ROTATED_POSITION = -90f;
    private TreeNode parentNode;
    private TreeNode questionNode;
    private AndroidTreeView tView;
    private FragmentFaqBinding faqBinding;
    private ArrayList<FAQs> arrFAQsModels;
    private ArrayList<FAQModel> arrCategoryModels;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        faqBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_faq, container, false);
        return faqBinding.getRoot();
    }

    @Override
    public void initView(final View view) {
        arrFAQsModels = new ArrayList<>();
        arrCategoryModels = new ArrayList<>();
        ((HomeActivity) getActivity()).disableCollapse();
        ((HomeActivity) getActivity()).setToolbarWithCenterTitle(getString(R.string.faq), getResources().getColor(R.color.grape), null, false);
        checkForData();

    }

    private void checkForData() {
        AlainZooDB.getInstance().beginTransaction();
        final Cursor cursor = AlainZooDB.getInstance().query("SELECT DISTINCT category_id, category FROM faqs WHERE language = ?", new String[]{LangUtils.getCurrentLanguage()});
        AlainZooDB.getInstance().endTransaction();

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            final FAQModel faqModel = new FAQModel();
            faqModel.setId(cursor.getInt(0));
            faqModel.setName(Html.fromHtml(cursor.getString(1)).toString());
            arrCategoryModels.add(faqModel);
        }
        cursor.close();

        if (arrCategoryModels != null && arrCategoryModels.size() > 0) {
            for (int i = 0; i < arrCategoryModels.size(); i++) {
                final FAQModel faqModel = arrCategoryModels.get(i);
                ArrayList<FaqQuestionModel> questionModelArrayList = new ArrayList<>();
                AlainZooDB.getInstance().beginTransaction();
                final Cursor cursorQuestion = AlainZooDB.getInstance().query("SELECT question, answer FROM faqs WHERE language = ? AND category_id=?", new String[]{LangUtils.getCurrentLanguage(), String.valueOf(faqModel.getId())});
                AlainZooDB.getInstance().endTransaction();

                for (cursorQuestion.moveToFirst(); !cursorQuestion.isAfterLast(); cursorQuestion.moveToNext()) {
                    final FaqQuestionModel faqQuestionModel = new FaqQuestionModel();
                    faqQuestionModel.setQuestion(Html.fromHtml(cursorQuestion.getString(0)).toString());
                    faqQuestionModel.setAnswer(Html.fromHtml(cursorQuestion.getString(1)).toString());
                    questionModelArrayList.add(faqQuestionModel);
                }
                faqModel.setQuestionModelArrayList(questionModelArrayList);
                cursorQuestion.close();
            }
            faqBinding.frFaqLlFaqList.setVisibility(View.VISIBLE);
            faqBinding.frFaqTvEmptyView.setVisibility(View.GONE);
            setCategoryData(arrCategoryModels);
        } else {
            fetchFAQs();
            faqBinding.frFaqLlFaqList.setVisibility(View.GONE);
            faqBinding.frFaqTvEmptyView.setVisibility(View.VISIBLE);
        }

    }

    public void fetchFAQs() {
        DisplayDialog.getInstance().showProgressDialog(getActivity(), "", false);
        FaqManager.getFaqManager().getAllEntitiesData(0, new ApiCallback() {
            @Override
            public void onSuccess(ArrayList resultList, boolean isloadmore) {
                //arrFAQsModels.addAll(resultList);
                DisplayDialog.getInstance().dismissProgressDialog();
                if (isAdded()) {
                    checkForData();
                }
            }


            @Override
            public void onFaild(String message) {
                DisplayDialog.getInstance().dismissProgressDialog();
                SnackbarUtils.loadSnackBar(message, getActivity(), R.color.grape);
            }
        }, true);

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            ((HomeActivity) getActivity()).disableCollapse();
            ((HomeActivity) getActivity()).setToolbarWithCenterTitle(getString(R.string.faq), getResources().getColor(R.color.grape), null, false);

        }
    }

    private void setCategoryData(final ArrayList<FAQModel> arrFaqs) {
        final TreeNode root = TreeNode.root();
        for (int i = 0; i < arrFaqs.size(); i++) {
            final FAQModel categoryModel = arrFaqs.get(i);
            final MyHolder.IconTreeItem child2 = new MyHolder.IconTreeItem();
            child2.id = categoryModel.getId();
            child2.text = categoryModel.getName();
            final TreeNode parent = new TreeNode(child2).setViewHolder(new MyHolder(getActivity(), true));
            parent.setClickListener(new TreeNode.TreeNodeClickListener() {
                @Override
                public void onClick(TreeNode node, Object value) {
                    if (parentNode != null && parentNode != node) {
                        if (questionNode != null) {
                            tView.collapseNode(questionNode);
                            //rotateArrowAnimation(((MyQuestionHolder.IconTreeItem) questionNode.getValue()).ivArrow, false);
                            ((MyQuestionHolder.IconTreeItem) questionNode.getValue()).ivArrow.setRotation(90);
                        }
                        tView.collapseNode(parentNode);
                        //rotateArrowAnimation(((MyHolder.IconTreeItem) parentNode.getValue()).ivArrow, false);
                        ((MyHolder.IconTreeItem) parentNode.getValue()).ivArrow.setRotation(90);
                        parentNode = node;
                    } else {
                        parentNode = node;
                    }
                    if (node.isExpanded()) {
                        ((MyHolder.IconTreeItem) value).ivArrow.setRotation(90);
                        //rotateArrowAnimation(((MyHolder.IconTreeItem) parentNode.getValue()).ivArrow, false);
                        node.getViewHolder().toggle(false);
                    } else {
                        ((MyHolder.IconTreeItem) value).ivArrow.setRotation(270);
                    }
                }
            });


            if (categoryModel.getQuestionModelArrayList() != null && categoryModel.getQuestionModelArrayList().size() > 0) {
                for (int j = 0; j < categoryModel.getQuestionModelArrayList().size(); j++) {
                    final FaqQuestionModel categoryModel1 = categoryModel.getQuestionModelArrayList().get(j);
                    final MyQuestionHolder.IconTreeItem child = new MyQuestionHolder.IconTreeItem();
                    child.text = categoryModel1.getQuestion();
                    final TreeNode child1 = new TreeNode(child).setViewHolder(new MyQuestionHolder(getActivity(), true));
                    child1.setClickListener(new TreeNode.TreeNodeClickListener() {
                        @Override
                        public void onClick(TreeNode node, Object value) {
                            if (questionNode != null && questionNode != node) {
                                tView.collapseNode(questionNode);
                                ((MyQuestionHolder.IconTreeItem) questionNode.getValue()).ivArrow.setRotation(90);
                                //rotateArrowAnimation(((MyQuestionHolder.IconTreeItem) questionNode.getValue()).ivArrow, false);
                                questionNode = node;
                            } else {
                                questionNode = node;
                            }
                            if (node.isExpanded()) {
                                ((MyQuestionHolder.IconTreeItem) value).ivArrow.setRotation(90);
                                //rotateArrowAnimation(((MyQuestionHolder.IconTreeItem) questionNode.getValue()).ivArrow, false);
                                node.getViewHolder().toggle(false);
                            } else {
                                //rotateArrowAnimation(((MyQuestionHolder.IconTreeItem) questionNode.getValue()).ivArrow, true);
                                ((MyQuestionHolder.IconTreeItem) value).ivArrow.setRotation(270);
                                //categoryClick(node, (MyHolder.IconTreeItem) value);
                            }

                        }
                    });

                    final MyAnswerHolder.IconTreeItem childAnswer = new MyAnswerHolder.IconTreeItem();
                    childAnswer.answer = categoryModel1.getAnswer();
                    final TreeNode child3 = new TreeNode(childAnswer).setViewHolder(new MyAnswerHolder(getActivity(), false));
                    child3.setClickListener(new TreeNode.TreeNodeClickListener() {
                        @Override
                        public void onClick(TreeNode node, Object value) {
                            if (node.isExpanded()) {
                                node.getViewHolder().toggle(false);
                            } else {
                                //categoryClick(node, (MyHolder.IconTreeItem) value);
                            }
                        }
                    });
                    child1.addChild(child3);
                    parent.addChild(child1);
                }
            }
            root.addChild(parent);
        }
        tView = new AndroidTreeView(getActivity(), root);
        faqBinding.frFaqLlFaqList.addView(tView.getView());


    }


    static class MyHolder extends TreeNode.BaseNodeViewHolder<MyHolder.IconTreeItem> {

        private boolean isParent;

        MyHolder(Context context, boolean isParent) {
            super(context);
            this.isParent = isParent;
        }

        @Override
        public View createNodeView(final TreeNode node, IconTreeItem value) {
            final LayoutInflater inflater = LayoutInflater.from(context);
            final View view = inflater.inflate(R.layout.row_faq, null, false);
            final TextView tvValue = view.findViewById(R.id.row_faq_tv);
            value.ivArrow = view.findViewById(R.id.row_faq_iv);
            tvValue.setText(value.text);
            return view;
        }

        static class IconTreeItem {
            public ImageView ivArrow;
            public String text;
            public int id;
        }
    }

    static class MyQuestionHolder extends TreeNode.BaseNodeViewHolder<MyQuestionHolder.IconTreeItem> {

        private boolean isParent;

        MyQuestionHolder(Context context, boolean isParent) {
            super(context);
            this.isParent = isParent;
        }

        @Override
        public View createNodeView(final TreeNode node, IconTreeItem value) {
            final LayoutInflater inflater = LayoutInflater.from(context);
            final View view = inflater.inflate(R.layout.row_faq_question, null, false);
            final TextView tvValue = view.findViewById(R.id.row_faq_tv);
            value.ivArrow = view.findViewById(R.id.row_faq_iv);
            tvValue.setText(value.text);
            return view;
        }

        static class IconTreeItem {
            public ImageView ivArrow;
            public String text;
            public int id;
        }
    }

    static class MyAnswerHolder extends TreeNode.BaseNodeViewHolder<MyAnswerHolder.IconTreeItem> {

        private boolean isParent;

        MyAnswerHolder(Context context, boolean isParent) {
            super(context);
            this.isParent = isParent;
        }

        @Override
        public View createNodeView(final TreeNode node, IconTreeItem value) {
            final LayoutInflater inflater = LayoutInflater.from(context);
            final View view = inflater.inflate(R.layout.row_faq_answer, null, false);
            final TextView tvValue = view.findViewById(R.id.row_faq_tv);
            tvValue.setText(value.answer);
            return view;
        }

        static class IconTreeItem {
            public String answer;
            public int id;
        }
    }
}
