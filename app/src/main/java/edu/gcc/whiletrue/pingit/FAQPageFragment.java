package edu.gcc.whiletrue.pingit;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import android.support.v4.view.ViewPager;
import android.util.Log;

import android.support.v4.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import java.lang.reflect.Array;

import java.util.ArrayList;

import com.andexert.expandablelayout.library.ExpandableLayoutListView;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FAQPageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FAQPageFragment extends Fragment {

    private FragmentManager fragmentManager;
    final ArrayList<FAQ> faqData = new ArrayList<FAQ>();


    public class internalArrayAdapter extends BaseAdapter {
        Context myContext;
        int myResource;
        int textResource;
        ArrayList<ArrayList<String>> questionsAndAnswers;

            public internalArrayAdapter(Context context, int resource, int textid, ArrayList<ArrayList<String>> objects){
                myContext = context;
                myResource = resource;
                textResource = textid;
                questionsAndAnswers = objects;
            }

            @Override // Gets the data into a presentable form to be displayed.
            public View getView(int position, View convertView, ViewGroup parent) {
                LayoutInflater inflater = ((Activity) myContext).getLayoutInflater();

                // Get references for view elements
                View row = inflater.inflate(R.layout.internal_view_row, parent, false);
                TextView textLine = (TextView) row.findViewById(textResource);
                TextView answerLine = (TextView) row.findViewById(R.id.FAQ_Answer);

                // Set the values from the data.
                textLine.setText(questionsAndAnswers.get(position).get(0));
                answerLine.setText((questionsAndAnswers.get(position).get(1)));
                return row;
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public Object getItem(int position) {
                return questionsAndAnswers.get(position);
            }

        @Override
        public int getCount(){
            return questionsAndAnswers.size();
        }
    }

    public class FAQArrayAdapter extends ArrayAdapter<FAQ> {
        Context myContext;
        int myResource;
        int textResource;
        int secondResource;
        ArrayList<FAQ> FAQs;

        public FAQArrayAdapter(Context context, int resource, int textid, int internalReference, ArrayList<FAQ> objects) {
            super(context, resource, objects);
            myContext = context;
            myResource = resource;
            textResource = textid;
            secondResource = internalReference;
            FAQs = objects;

        }

        @Override // Gets the data into a presentable form to be displayed.
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = ((Activity)myContext).getLayoutInflater();

            // Get references for view elements
            View row = inflater.inflate(myResource, parent, false);
            TextView textLine = (TextView) row.findViewById(textResource);
            ExpandableLayoutListView internalList = (ExpandableLayoutListView) row.findViewById(R.id.nestedListView);

            // Set the values from the data.
            textLine.setText(FAQs.get(position).getCategory());
            //Make new internalAdapter
            internalArrayAdapter internalAdapter;
            internalAdapter = new internalArrayAdapter(inflater.getContext(), R.layout.internal_view_row, R.id.internal_header_text, FAQs.get(position).getQuestionArr());
            internalList.setAdapter(internalAdapter);
            return row;
        }
    }

    FAQArrayAdapter faqArrayAdapter;

    public FAQPageFragment() {
        // Required empty public constructor
    }

    public static FAQPageFragment newInstance() {
        FAQPageFragment fragment = new FAQPageFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentManager = getFragmentManager();

        //Load FAQ data
        //TODO replace dummy data with parse stuff and move this all to an async task
        ArrayList<ArrayList<String>> arr1 =  new ArrayList<ArrayList<String>>();
        arr1.add(new ArrayList<String>());
        arr1.get(0).add("Example question A");
        arr1.get(0).add("Example answer");
        arr1.add(new ArrayList<String>());
        arr1.get(1).add("Example question B");
        arr1.get(1).add("Example answer");

        ArrayList<ArrayList<String>> arr2 =  new ArrayList<ArrayList<String>>();
        arr2.add(new ArrayList<String>());
        arr2.get(0).add("Example question C");
        arr2.get(0).add("Example answer");
        arr2.add(new ArrayList<String>());
        arr2.get(1).add("Example question D");
        arr2.get(1).add("bad shit");

        ArrayList<ArrayList<String>> arr3 =  new ArrayList<ArrayList<String>>();
        arr3.add(new ArrayList<String>());
        arr3.get(0).add("A question");
        arr3.get(0).add("An answer");

        ArrayList<ArrayList<String>> arr4 =  new ArrayList<ArrayList<String>>();
        arr4.add(new ArrayList<String>());
        arr4.add(new ArrayList<String>());
        arr4.add(new ArrayList<String>());
        arr4.get(0).add("Questions for second");
        arr4.get(0).add("Another answer");
        arr4.get(1).add("It's because your dumb");
        arr4.get(1).add("Stop being dumb");
        arr4.get(2).add("My light is orange!");
        arr4.get(2).add("It's because it reflects every other color except orange.");

        faqData.add(new FAQ("Example Category 1", arr1));
        faqData.add(new FAQ("Example Category 2", arr2));
        faqData.add(new FAQ("My computer won't turn on", arr3));
        faqData.add(new FAQ("I can't connect to the World Wide Webernet", arr4));

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_faqpage, container, false);

        //final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this.getContext(), R.layout.view_row, R.id.header_text, array);
        final ExpandableLayoutListView expandableLayoutListView = (ExpandableLayoutListView) rootView.findViewById(R.id.expandableLayoutListView);

        faqArrayAdapter = new FAQArrayAdapter(inflater.getContext(), R.layout.view_row, R.id.header_text, R.id.internalRow, faqData);

        expandableLayoutListView.setAdapter(faqArrayAdapter);

        return rootView;
    }

}
