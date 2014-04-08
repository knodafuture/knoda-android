package networking;
/**
 * Created by nick on 1/8/14.
 */

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Map;

import factories.GsonF;
import unsorted.Logger;

/**
 * Volley adapter for JSON requests that will be parsed into Java objects by Gson.
 */
public class GsonArrayRequest<T> extends Request<T> {
    private Gson gson = GsonF.actory();
    private final Map<String, String> headers;
    private final Listener<T> listener;
    private Object payload;
    private final TypeToken<T> token;

    /**
     * Make a GET request and return a parsed object from JSON.
     *
     * @param url URL of the request to make
     * @param clazz Relevant class object, for Gson's reflection
     * @param headers Map of request headers
     */
    public GsonArrayRequest(int httpMethod, String url, TypeToken token, Map<String, String> headers,
                       Listener<T> listener, ErrorListener errorListener) {
        super(httpMethod, url, errorListener);
        this.token = token;
        this.headers = headers;
        this.listener = listener;
        this.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));

    }

    public void setPayload(Object jsonObject) {
        payload = jsonObject;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return headers != null ? headers : super.getHeaders();
    }

    @Override
    public byte[] getBody() {
        if (payload == null)
            return null;

        Logger.log("JSON = " + gson.toJson(payload));
        return gson.toJson(payload).getBytes(Charset.forName("UTF-8"));
    }

    @Override
    protected void deliverResponse(T response) {
        listener.onResponse(response);
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            String json = new String(
                    response.data, HttpHeaderParser.parseCharset(response.headers));
            return Response.success(
                    (T)gson.fromJson(json, token.getType()), HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JsonSyntaxException e) {
            return Response.error(new ParseError(e));
        }
    }
}