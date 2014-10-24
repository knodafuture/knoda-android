package views.core;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.knoda.knoda.R;

import butterknife.InjectView;

public class BaseWebFragment extends BaseFragment {
    @InjectView(R.id.webview)
    public WebView webView;
    private String url;
    private boolean disableNav = false;
    private String title;

    public BaseWebFragment() {
    }

    public static BaseWebFragment newInstance(String url, String title, boolean disableNav) {
        BaseWebFragment fragment = new BaseWebFragment();
        fragment.url = url;
        fragment.title = title;
        fragment.disableNav = disableNav;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        setTitle(title);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_base_web, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        spinner.show();
        webView.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView wv, String url) {
                spinner.hide();
            }

            public void onReceivedError(WebView wv, int error, String description, String failingUrl) {
                spinner.hide();
            }
        });
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.loadUrl(url);
        if (disableNav)
            ((MainActivity) getActivity()).hideNavbar();

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ((MainActivity) getActivity()).showNavbar();
        getActivity().getActionBar().setDisplayHomeAsUpEnabled(false);
    }

}
