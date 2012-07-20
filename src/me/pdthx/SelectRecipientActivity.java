package me.pdthx;

import me.pdthx.Helpers.PhoneNumberFormatter;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import me.pdthx.Widget.OnWheelChangedListener;
import me.pdthx.Widget.Adapters.ArrayWheelAdapter;
import me.pdthx.Widget.WheelView;
import android.widget.Button;
import android.os.Bundle;

public class SelectRecipientActivity extends BaseActivity
{
    private WheelView selectRecipients;
    private Button btnSubmitSelectRecipient;
    private int currentId;
    private String[] uris;


    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.select_recipient);

        selectRecipients = (WheelView) findViewById(R.id.select_recipients);
        btnSubmitSelectRecipient = (Button) findViewById(R.id.btnSubmitSelectRecipient);

        String[] recipientsArray = getIntent().getStringArrayExtra("recipients");

        for (int i = 0; i < recipientsArray.length; i++)
        {
            String fixedNumber = PhoneNumberFormatter.formatNumber(recipientsArray[i]);
            if (fixedNumber != null)
            {
                recipientsArray[i] = fixedNumber;
            }
        }

        uris = getIntent().getStringArrayExtra("uris");

        ArrayWheelAdapter<String> adapter = new ArrayWheelAdapter<String>(this,
                recipientsArray);
        adapter.setTextSize(14);
        selectRecipients.setViewAdapter(adapter);
        selectRecipients.setCurrentItem(0);

        selectRecipients.addChangingListener(new OnWheelChangedListener() {

            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                currentId = newValue;
            }

        });

        btnSubmitSelectRecipient.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent();
                intent.putExtra("uri", uris[currentId]);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}
