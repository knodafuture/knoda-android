package builders;

import com.android.volley.Response;

import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;

import java.io.File;

import core.networking.MultipartRequest;

/**
 * Created by nick on 1/22/14.
 */
public class MultipartRequestBuilder {


    private MultipartEntityBuilder builder = MultipartEntityBuilder.create();

    private Response.Listener<String> listener;
    private Response.ErrorListener errorListener;
    private String url;

    public static MultipartRequestBuilder create() {
        return new MultipartRequestBuilder();
    }

    public MultipartRequestBuilder forUrl(String url) {
        this.url = url;
        return this;
    }

    public MultipartRequestBuilder addListener(Response.Listener<String> listener) {
        this.listener = listener;
        return this;
    }

    public MultipartRequestBuilder addErrorListener(Response.ErrorListener errorListener) {
        this.errorListener = errorListener;
        return this;
    }

    public MultipartRequestBuilder addFilePart(String name, File file, ContentType contentType, String filename) {
        builder.addBinaryBody(name, file, contentType, filename);
        return this;
    }


    public MultipartRequest build() {

        HttpEntity entity = builder.build();

        MultipartRequest request = new MultipartRequest(url, errorListener, listener, entity);

        return request;
    }


}
