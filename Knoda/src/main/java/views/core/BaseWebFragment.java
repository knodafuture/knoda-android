package views.core;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

    public BaseWebFragment() {
    }

    public static BaseWebFragment newInstance(String url, boolean disableNav) {
        BaseWebFragment fragment = new BaseWebFragment();
        fragment.url = url;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
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
        webView.setWebViewClient(new WebViewClient());
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
