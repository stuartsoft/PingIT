package edu.gcc.whiletrue.pingit;


import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
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

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;


import java.lang.reflect.Array;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
            View row = inflater.inflate(myResource, parent, false);
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_faqpage, container, false);

        GetFAQTask getFAQs = new GetFAQTask(ParseUser.getCurrentUser(),
                rootView, inflater.getContext());

        //Run the background async task to get the user's pings
        getFAQs.execute();
        return rootView;
    }

    private class GetFAQTask extends AsyncTask<String, Void, Integer> {
        ParseUser user;
        final View view;
        Context context;
        ArrayList<ParseObject> categoryList;
        ArrayList<ArrayList<ParseObject>> questionsList = new ArrayList<ArrayList<ParseObject>>();

        public GetFAQTask (ParseUser user, View view, Context context) {
            this.user = user;
            this.view = view;
            this.context = context;
        }

        @Override
        protected Integer doInBackground(String... params) {
            try { //Query Parse for the user's pings
                ParseQuery<ParseObject> query = ParseQuery.getQuery("FAQ_Category");
                categoryList = new ArrayList<ParseObject>(query.find());
                for (ParseObject category: categoryList) {
                    ArrayList<ParseObject> questionQuery = new ArrayList<ParseObject>(category.getRelation("Questions").getQuery().find());
                    ///Filler
                    questionsList.add(questionQuery);
                }
            } catch (ParseException e) {return e.getCode();}//return exception code
            return 0;//no issues
        }

        @Override
        protected void onPostExecute(Integer errorCode) {
            if (getContext() != null) {
                if (errorCode == 0) { //Populate the pings list if everything is clear
                    ArrayList<FAQ> faqData = new ArrayList<FAQ>();

                    for (int i = 0; i < categoryList.size(); i++) {
                        try {
                            ArrayList<ArrayList<String>> questionArr = new ArrayList<ArrayList<String>>();
                            for (int j = 0; j < questionsList.get(i).size(); j++) {
                                questionArr.add(new ArrayList<String>());
                                questionArr.get(j).add(questionsList.get(i).get(j).getString("Text"));
                                questionArr.get(j).add(questionsList.get(i).get(j).getString("AnswerText"));
                            }

                            //Populate each Ping object
                            //Make sure that the category isn't empty
                            if (questionArr.size() > 0)
                                faqData.add(new FAQ(categoryList.get(i).getString("Text"), questionArr));
                        } catch (Exception e) {
                        }
                        //TODO figure out why this is throwing an error when the user has no pings
                        //something to do with Date.getTime() being called on a null object reference
                    }

                    //final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this.getContext(), R.layout.view_row, R.id.header_text, array);
                    final ExpandableLayoutListView expandableLayoutListView = (ExpandableLayoutListView) view.findViewById(R.id.expandableLayoutListView);
                    faqArrayAdapter = new FAQArrayAdapter(getContext(), R.layout.view_row, R.id.header_text, R.id.internalRow, faqData);
                    expandableLayoutListView.setAdapter(faqArrayAdapter);
                } else {
                    Log.e(getString(R.string.log_error),
                            "onPostExecute: User has no network connection. Cannot load pings.");
                    //TODO: Add a custom page here for when the user has no network connection
                    //and allow them to refresh with a button
                }
            }
        }
    }

}