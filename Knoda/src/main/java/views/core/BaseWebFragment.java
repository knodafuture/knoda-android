package views.core;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Toast;

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
        inflater.inflate(R.menu.web, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_web) {
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(i);
        }
        return true;
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
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int progress) {
                if (progress == 100) {
                    spinner.hide();
                }
            }
        });
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setBuiltInZoomControls(true);

        webView.loadUrl(url.toLowerCase());
        android.os.Handler handler = new android.os.Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (spinner != null && spinner.isVisible()) {
                    spinner.hide();
                    Toast.makeText(getActivity(), "Failed to load site. Try again later.", Toast.LENGTH_SHORT).show();
                }
            }
        }, 10000);
        if (disableNav)
            ((MainActivity) getActivity()).hideNavbar();

        System.out.println("Loading: " + url.toLowerCase());
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
