package edu.gcc.whiletrue.pingit;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.support.v4.app.FragmentTransaction;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.Toast;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FAQPageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FAQPageFragment extends Fragment {

    private FragmentManager fragmentManager;

    public class FAQArrayAdapter extends ArrayAdapter<FAQ> {
        Context myContext;
        int myResource;
        ArrayList<FAQ> FAQs;

        public FAQArrayAdapter(Context context, int resource, ArrayList<FAQ> objects) {
            super(context, resource, objects);
            myContext = context;
            myResource = resource;
            FAQs = objects;
        }

        @Override // Gets the data into a presentable form to be displayed.
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = ((Activity)myContext).getLayoutInflater();

            // Get references for view elements
            View row = inflater.inflate(myResource, parent, false);
            TextView textLine = (TextView) row.findViewById(R.id.faq_template_text);

            // Set the values from the data.
            textLine.setText(FAQs.get(position).getCategory());
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

        String[][] arr = new String[2][2];
        arr[0][0]= "A question";
        arr[0][1] = "An answer";
        arr[1][0]= "It's because... IDK";
        arr[1][1] = "JUST DO IT";

        String[][] arr2 = new String[2][2];
        arr2[0][0]= "Questions for second";
        arr2[0][1] = "Another answer";
        arr2[1][0]= "It's because your dumb";
        arr2[1][1] = "Stop being dumb";

        faqData.add(new FAQ("My computer won't turn on", arr));
        faqData.add(new FAQ("I can't connect to the World Wide Webernet", arr2));

        ListView listView = (ListView) rootView.findViewById(R.id.listView_categories);
        listView.setAdapter(faqArrayAdapter);

        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {
                // TODO Auto-generated method stub
                FAQExtendedFragment faqFrag = new FAQExtendedFragment();
                String[][] listToSend = faqData.get(position).getQuestionArr();
                Bundle bundle = new Bundle();
                bundle.putSerializable("listToGet", listToSend);
                faqFrag.setArguments(bundle);

                FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.fadein, R.anim.fadeout);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.replace(R.id.faq_containter,faqFrag);
                fragmentTransaction.commit();
                Toast.makeText(getContext(), "I clicked on the " + position + " one!", Toast.LENGTH_SHORT).show();
            }
        });

        faqArrayAdapter = new FAQArrayAdapter(getContext(), R.layout.faq_list_template, faqData);

        ListView faqListView = (ListView) rootView.findViewById(R.id.listView_categories);

        faqListView.setAdapter(faqArrayAdapter);


        return rootView;
    }

}
