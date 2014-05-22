package views.addprediction;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import org.joda.time.DateTime;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by nick on 2/10/14.
 */
public class DateTimePicker implements View.OnClickListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    public interface OnCalenderChangedListener {
        void onCalenderChanged(Calendar calendar);
    }

    private static DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT);
    private static DateFormat timeFormat = DateFormat.getTimeInstance(DateFormat.SHORT);

    public Calendar calendar;
    public Calendar minimumCalender;

    private EditText dateEditText;
    private EditText timeEditText;

    private OnCalenderChangedListener callback;

    public DateTimePicker(EditText dateEditText, EditText timeEditText, Calendar minimumCalender, OnCalenderChangedListener listener) {
        this.dateEditText = dateEditText;
        this.timeEditText = timeEditText;
        this.callback = listener;
        this.calendar = Calendar.getInstance();
        this.calendar.setTime(minimumCalender.getTime());
        this.minimumCalender = minimumCalender;
        this.dateEditText.setOnClickListener(this);
        this.timeEditText.setOnClickListener(this);
        updateLabels();
    }


    @Override
    public void onClick(View v) {
        if (v == dateEditText) {
            DatePickerDialog dialog = new DatePickerDialog(dateEditText.getContext(), this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            dialog.show();
        } else if (v == timeEditText) {
            TimePickerDialog dialog = new TimePickerDialog(timeEditText.getContext(), this, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), android.text.format.DateFormat.is24HourFormat(timeEditText.getContext()));
            dialog.show();
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        calendar.set(year, month, day);
        if (calendar.getTime().before(minimumCalender.getTime()))
            calendar.setTime(minimumCalender.getTime());
        updateLabels();
        if (callback != null) {
            Calendar newCalender = Calendar.getInstance();
            newCalender.setTimeInMillis(calendar.getTimeInMillis());
            callback.onCalenderChanged(newCalender);
        }
    }

    @Override
    public void onTimeSet(TimePicker view, int hour, int minutes) {
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minutes);

        if (calendar.getTime().before(minimumCalender.getTime()))
            calendar.setTime(minimumCalender.getTime());

        updateLabels();
        if (callback != null) {
            Calendar newCalender = Calendar.getInstance();
            newCalender.setTime(calendar.getTime());
            callback.onCalenderChanged(newCalender);
        }
    }

    public DateTime getDateTime() {
        return new DateTime(calendar.getTime());
    }

    private void updateLabels() {
        dateEditText.setText(dateFormat.format(calendar.getTime()));
        timeEditText.setText(timeFormat.format(calendar.getTime()));
    }

    public void setMinimumCalender(Calendar minimumCalender) {
        if (this.calendar.getTime().before(minimumCalender.getTime())) {
            this.calendar.setTime(minimumCalender.getTime());
            updateLabels();
        }

        this.minimumCalender = minimumCalender;
    }

    public Calendar getCalendar() {
        return calendar;
    }

    public void setDateTime(DateTime dateTime) {
        if (dateTime.getMillis() < minimumCalender.getTime().getTime()) {
            calendar.setTime(minimumCalender.getTime());
        } else {
            calendar.setTime(new Date(dateTime.getMillis()));
        }
        updateLabels();
    }

}
