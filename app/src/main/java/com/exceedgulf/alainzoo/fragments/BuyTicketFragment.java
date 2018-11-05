package com.exceedgulf.alainzoo.fragments;


import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.bumptech.glide.Glide;
import com.exceedgulf.alainzoo.R;
import com.exceedgulf.alainzoo.activity.HomeActivity;
import com.exceedgulf.alainzoo.databinding.FragmentBuyTicketBinding;
import com.exceedgulf.alainzoo.utils.DisplayDialog;

/**
 * Created by P.P on 19/01/2018
 */
public class BuyTicketFragment extends BaseFragment {

    boolean loadingFinished = true;
    boolean redirect = false;
    private FragmentBuyTicketBinding buyTicketBinding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        buyTicketBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_buy_ticket, container, false);
        return buyTicketBinding.getRoot();
    }

    @Override
    public void initView(View view) {
        ((HomeActivity) getActivity()).disableCollapse();
        ((HomeActivity) getActivity()).setToolbarWithCenterTitle(getString(R.string.title_buy_ticket), getResources().getColor(R.color.very_light_brown), null, true);
        Glide.with(getActivity()).asGif().load(R.drawable.zoo_loading).into(buyTicketBinding.imgloading);
        buyTicketBinding.frBuyTicketsWebView.setFitsSystemWindows(false);
        final WebSettings settings = buyTicketBinding.frBuyTicketsWebView.getSettings();
        settings.setDefaultTextEncodingName("utf-8");
        settings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        buyTicketBinding.frBuyTicketsWebView.loadUrl(getString(R.string.url_buy_tickets));
        buyTicketBinding.frBuyTicketsWebView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(
                    WebView view, WebResourceRequest request) {
                if (!loadingFinished) {
                    redirect = true;
                }

                loadingFinished = false;
                buyTicketBinding.frBuyTicketsWebView.loadUrl(request.getUrl().toString());
                return true;
            }

            @Override
            public void onPageStarted(
                    WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                loadingFinished = false;
                //SHOW LOADING IF IT ISNT ALREADY VISIBLE
                //DisplayDialog.getInstance().showProgressDialog(getActivity(), "Loading", false);
                buyTicketBinding.imgloading.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (!redirect) {
                    loadingFinished = true;
                }

                if (loadingFinished && !redirect) {
                    //HIDE LOADING IT HAS FINISHED
//                    DisplayDialog.getInstance().dismissProgressDialog();
                    buyTicketBinding.imgloading.setVisibility(View.GONE);
                } else {
                    redirect = false;
                }

            }
        });

        buyTicketBinding.frBuyTicketsIvPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (buyTicketBinding.frBuyTicketsWebView.canGoBack()) {
                    buyTicketBinding.frBuyTicketsWebView.goBack();
                }
            }
        });

        buyTicketBinding.frBuyTicketsIvNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (buyTicketBinding.frBuyTicketsWebView.canGoBack()) {
                    buyTicketBinding.frBuyTicketsWebView.goForward();
                }
            }
        });

        buyTicketBinding.frBuyTicketsIvRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buyTicketBinding.frBuyTicketsWebView.loadUrl(getString(R.string.url_buy_tickets));
            }
        });

        buyTicketBinding.frBuyTicketsIvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buyTicketBinding.frBuyTicketsWebView.stopLoading();
                buyTicketBinding.imgloading.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            ((HomeActivity) getActivity()).selectedBottomMenuPosition = 3;
            ((HomeActivity) getActivity()).disableCollapse();
            ((HomeActivity) getActivity()).setToolbarWithCenterTitle(getString(R.string.title_buy_ticket), getResources().getColor(R.color.very_light_brown), null, true);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        buyTicketBinding.frBuyTicketsWebView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        buyTicketBinding.frBuyTicketsWebView.onPause();
    }

    /**
     * Called when the fragment is no longer in use. Destroys the internal state of the WebView.
     */
    @Override
    public void onDestroy() {
        if (buyTicketBinding.frBuyTicketsWebView != null) {
            buyTicketBinding.frBuyTicketsWebView.destroy();
        }
        super.onDestroy();
    }

}
