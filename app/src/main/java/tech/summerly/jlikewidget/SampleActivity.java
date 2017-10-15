package tech.summerly.jlikewidget;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import tech.summerly.likewidget.InteractionView;
import tech.summerly.likewidget.SlidingNumberView;

public class SampleActivity extends AppCompatActivity {
    private InteractionView interactionView;
    private SlidingNumberView numberView;
    private EditText editNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);
        interactionView = findViewById(R.id.interactionLayout);
        numberView = findViewById(R.id.numberView);
        editNumber = findViewById(R.id.editNumber);
        numberView.setNumber(999);
        numberView.setTextSize(48);
        interactionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean selected = !interactionView.isSelectedState();
                interactionView.setSelectedState(selected);
                if (selected) {
                    numberView.animateToNumber(numberView.getNumber() + 1);
                } else {
                    numberView.animateToNumber(numberView.getNumber() - 1);
                }
            }
        });
        numberView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                interactionView.performClick();
                interactionView.animateToState(!interactionView.isSelectedState());
            }
        });
    }

    public void plusOne(View view) {
        numberView.animateToNumber(numberView.getNumber() + 1);
    }

    public void minusOne(View view) {
        numberView.animateToNumber(numberView.getNumber() - 1);
    }

    public void toNumber(View view) {
        String string = editNumber.getText().toString();
        int value = Integer.valueOf(string);
        numberView.animateToNumber(value);
    }
}
