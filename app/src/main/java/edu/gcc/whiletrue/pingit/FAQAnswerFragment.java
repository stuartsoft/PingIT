package edu.gcc.whiletrue.pingit;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Zared on 2/23/2016.
 */
public class FAQAnswerFragment extends Fragment {
    public FAQAnswerFragment(){
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_faq_answer, container, false);

        Bundle bundle = this.getArguments();
        String title = bundle.getString("sentTitle");
        String answer = bundle.getString("sentAnswer");

        TextView titleText = (TextView) view.findViewById(R.id.titleText);
        TextView answerText = (TextView) view.findViewById(R.id.titleText);
        titleText.setText(title);
        answerText.setText(answer);

        return view;
    }


}
