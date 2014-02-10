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

/**
 * Created by nick on 2/10/14.
 */
public class DateTimePicker implements View.OnClickListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    public interface OnCalenderChangedListener {
        void onCalenderChanged(Calendar calendar);
    }

    private static DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT);
    private static DateFormat timeFormat = DateFormat.getTimeInstance(DateFormat.SHORT);

    private Calendar calendar;
    private Calendar minimumCalender;

    private EditText dateEditText;
    private EditText timeEditText;

    private OnCalenderChangedListener callback;

    public DateTimePicker(EditText dateEditText, EditText timeEditText, Calendar minimumCalender, OnCalenderChangedListener listener) {
        this.dateEditText = dateEditText;
        this.timeEditText = timeEditText;
        this.callback = listener;
        this.calendar = minimumCalender;
        this.minimumCalender = minimumCalender;
        this.dateEditText.setOnClickListener(this);
        this.timeEditText.setOnClickListener(this);
        updateLabels();
    }


    @Override
    public void onClick(View v) {
        if (v == dateEditText)
            new DatePickerDialog(dateEditText.getContext(), this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        else if (v == timeEditText)
            new TimePickerDialog(timeEditText.getContext(), this, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), android.text.format.DateFormat.is24HourFormat(timeEditText.getContext())).show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        calendar.set(year, month, day);
        updateLabels();
        if (callback != null)
            callback.onCalenderChanged(calendar);
    }

    @Override
    public void onTimeSet(TimePicker view, int hour, int minutes) {
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minutes);
        updateLabels();
        if (callback != null)
            callback.onCalenderChanged(calendar);
    }

    public DateTime getDateTime() {
        return new DateTime(calendar.getTime());
    }

    private void updateLabels() {
        dateEditText.setText(dateFormat.format(calendar.getTime()));
        timeEditText.setText(timeFormat.format(calendar.getTime()));
    }

    public void setMinimumCalender(Calendar minimumCalender) {
        if (calendar.compareTo(minimumCalender) == -1) {
            calendar = minimumCalender;
            updateLabels();
        }

        this.minimumCalender = minimumCalender;
    }

    public Calendar getCalendar() {
        return calendar;
    }

}
