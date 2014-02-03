package builders;

import java.util.HashMap;
import java.util.Map;

import managers.NetworkingManager;

/**
 * Created by nick on 1/20/14.
 */
public class ParamBuilder {

    private HashMap<String, String> params = new HashMap<String, String>();

    public ParamBuilder() {

    }

    public static ParamBuilder create() {
        return new ParamBuilder();
    }

    public ParamBuilder add(String key, String value) {
        params.put(key, value);
        return this;
    }

    public ParamBuilder withPageLimit() {
        params.put("limit", NetworkingManager.PAGE_LIMIT.toString());
        return this;
    }

    public ParamBuilder withLastId(Integer lastId) {
        if (lastId != 0)
            params.put("id_lt", lastId.toString());
        return this;
    }

    public String build() {

        String out = "";
        boolean first = true;

        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (first) {
                out += "?";
                first = false;
            } else
                out += "&";
            out += entry.getKey() + "=" + entry.getValue();
        }

        return out;
    }



}
