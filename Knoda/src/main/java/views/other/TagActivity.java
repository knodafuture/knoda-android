package views.other;

/**
 * Created by jeffcailteux on 9/17/14.
 */

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.knoda.knoda.R;


public class TagActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_base_list);

        //Get the content URI
        Uri uri = getIntent().getData();
        //strip off hashtag from the URI
        String tag = uri.toString().split("/")[3];

        Toast.makeText(this, tag, Toast.LENGTH_SHORT).show();

        Intent hashtagIntent=new Intent();
        hashtagIntent.putExtra("hashtag", tag);
        setResult(Activity.RESULT_OK, hashtagIntent);
        finish();

    }

}