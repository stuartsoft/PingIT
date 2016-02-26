package edu.gcc.whiletrue.pingit;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import android.widget.AdapterView;

/**
 * Created by Zared on 2/22/2016.
 */
public class FAQExtendedFragment extends Fragment {
    public FAQExtendedFragment() {
    }

    private FAQExtendedFragment thisFrag = this;

    ArrayList<ArrayList<String>> conciseQuestions = new  ArrayList<ArrayList<String>>();

    public class FAQArrayAdapter extends BaseAdapter {
        Context myContext;
        int myResource;
        ArrayList<ArrayList<String>> questions;
        LayoutInflater layoutInflater;

        @Override
        public long getItemId(int position) {

            return position;
        }

        @Override
        public Object getItem(int position) {

            return null;
        }

        @Override
        public int getCount() {

            return questions.size();
        }

        public FAQArrayAdapter(Context context, int resource, ArrayList<ArrayList<String>> objects) {
            super();
            myContext = context;
            myResource = resource;
            questions = objects;
            conciseQuestions = questions;
            layoutInflater = LayoutInflater.from(context);
        }

        @Override // Gets the data into a presentable form to be displayed.
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView= layoutInflater.inflate(R.layout.faq_list_template, null);

            TextView txt=(TextView)convertView.findViewById(R.id.faq_template_text);
            String myString = "";
            try{
                myString = questions.get(position).get(0).toString();
            }
            catch (NullPointerException e){
                throw new RuntimeException(e);
            }


            txt.setText(myString);

            return convertView;
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


        Bundle bundle =this.getArguments();

        ArrayList<ArrayList<String>> selected_list = (ArrayList<ArrayList<String>>) bundle.getSerializable("listToGet");
        ArrayList<ArrayList<String>> arrList = new ArrayList<ArrayList<String>>();

        for(int i = 0; i< selected_list.size(); i++){
            ArrayList<String> tempList = new ArrayList<String>();
            tempList.add(selected_list.get(i).get(0).toString());
            tempList.add(selected_list.get(i).get(1).toString());
            arrList.add(tempList);
        }

        faqArrayAdapter = new FAQArrayAdapter(getContext(), R.layout.faq_list_template, arrList);

        ListView listView = (ListView) view.findViewById(R.id.concise_faq_list);

        listView.setAdapter(faqArrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {
                // TODO Auto-generated method stub
                FAQAnswerFragment faqFrag = new FAQAnswerFragment();

                //Collects the title and answer of selected question
                String title = conciseQuestions.get(position).get(0).toString();
                String answer = conciseQuestions.get(position).get(1).toString();
                Bundle bundle = new Bundle();
                //bundles the strings to be sent to the new fragmemt
                bundle.putString("sentTitle", title);
                bundle.putString("sentAnswer", answer);
                faqFrag.setArguments(bundle);

                FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.fadein, R.anim.fadeout);
                fragmentTransaction.replace(R.id.expanded_faq_fragment, faqFrag);
                fragmentTransaction.addToBackStack(null);
                //fragmentTransaction.remove(thisFrag);
                fragmentTransaction.commit();
            }
        });



        return view;
    }
}