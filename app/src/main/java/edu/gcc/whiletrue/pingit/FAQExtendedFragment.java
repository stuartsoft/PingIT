package edu.gcc.whiletrue.pingit;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Zared on 2/22/2016.
 */
public class FAQExtendedFragment extends Fragment {
    public FAQExtendedFragment() {
    }

    public class FAQArrayAdapter extends ArrayAdapter<String[][]> {
        Context myContext;
        int myResource;
        ArrayList<String[][]> questions;

        public FAQArrayAdapter(Context context, int resource, ArrayList<String[][]> objects) {
            super(context, resource, objects);
            myContext = context;
            myResource = resource;
            questions = objects;
        }

        @Override // Gets the data into a presentable form to be displayed.
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = ((Activity)myContext).getLayoutInflater();

            // Get references for view elements
            View row = inflater.inflate(myResource, parent, false);
            TextView textLine = (TextView) row.findViewById(R.id.faq_template_text);

            // Set the values from the data.
            textLine.setText(Arrays.deepToString(questions.get(position)));
            return row;
        }
    }

    FAQArrayAdapter faqArrayAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_faq_expanded, container, false);


        Bundle bundle = new Bundle();

        String[][] selected_list = (String[][]) bundle.getSerializable("listToGet");
        ArrayList<String[][]> arrList = new ArrayList<String[][]>();
        arrList.add(selected_list);

        faqArrayAdapter = new FAQArrayAdapter(getContext(), R.layout.faq_list_template, arrList);

        ListView listView = (ListView) view.findViewById(R.id.concise_faq_list);

        listView.setAdapter(faqArrayAdapter);

        return view;
    }
}
