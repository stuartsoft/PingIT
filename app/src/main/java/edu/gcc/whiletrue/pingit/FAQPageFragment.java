package edu.gcc.whiletrue.pingit;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.support.v4.app.FragmentTransaction;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
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
            //Log.w(getString(R.string.log_warning), "getCount: " + questionsAndAnswers.size() );
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
            Log.w(getString(R.string.log_warning), "FAQ num: " + FAQs.get(position).getQuestionArr().size());
            internalAdapter = new internalArrayAdapter(inflater.getContext(), R.layout.internal_view_row, R.id.header_text, FAQs.get(position).getQuestionArr());
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

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_faqpage, container, false);

        final ArrayList<FAQ> faqData = new ArrayList<FAQ>();

        ArrayList<ArrayList<String>> arr =  new ArrayList<ArrayList<String>>();
        arr.add(new ArrayList<String>());
        arr.get(0).add("A question");
        arr.get(0).add("An answer");

        ArrayList<ArrayList<String>> arr2 =  new ArrayList<ArrayList<String>>();
        arr2.add(new ArrayList<String>());
        arr2.add(new ArrayList<String>());
        arr2.add(new ArrayList<String>());
        arr2.get(0).add("Questions for second");
        arr2.get(0).add("Another answer");
        arr2.get(1).add("It's because your dumb");
        arr2.get(1).add("Stop being dumb");
        arr2.get(2).add("My light is orange!");
        arr2.get(2).add("It's because it reflects every other color except orange.");

        faqData.add(new FAQ("My computer won't turn on", arr));
        faqData.add(new FAQ("I can't connect to the World Wide Webernet", arr2));


        //final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this.getContext(), R.layout.view_row, R.id.header_text, array);
        final ExpandableLayoutListView expandableLayoutListView = (ExpandableLayoutListView) rootView.findViewById(R.id.expandableLayoutListView);

        faqArrayAdapter = new FAQArrayAdapter(inflater.getContext(), R.layout.view_row, R.id.header_text, R.id.internalRow, faqData);

        expandableLayoutListView.setAdapter(faqArrayAdapter);

        return rootView;
    }

}
