package factories;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.ISODateTimeFormat;

import java.lang.reflect.Type;

/**
 * Created by nick on 1/30/14.
 */
public class GsonF {

    private final static class DateTimeDeserializer implements JsonDeserializer<DateTime>, JsonSerializer<DateTime> {
        final org.joda.time.format.DateTimeFormatter DATE_TIME_FORMATTER =
                ISODateTimeFormat.dateTime().withZone(DateTimeZone.UTC);

        @Override
        public DateTime deserialize(final JsonElement je, final Type type,
                                    final JsonDeserializationContext jdc) throws JsonParseException
        {
            return je.getAsString().length() == 0 ? null : DATE_TIME_FORMATTER.parseDateTime(je.getAsString());
        }

        @Override
        public JsonElement serialize(final DateTime src, final Type typeOfSrc,
                                     final JsonSerializationContext context)
        {
            return new JsonPrimitive(src == null ? "" : DATE_TIME_FORMATTER.print(src));
        }
    }

    private static GsonBuilder builder;

    static {
        builder = new GsonBuilder().registerTypeAdapter(DateTime.class, new DateTimeDeserializer());
    }


    public static Gson actory() {
        return builder.create();
    }
}
