package com.dixit.dairydaily.CowChart;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dixit.dairydaily.Models.RateChartModel;
import com.dixit.dairydaily.MyAdapters.RateChartAdapter;
import com.dixit.dairydaily.Others.DbHelper;
import com.dixit.dairydaily.R;

import java.util.ArrayList;
import java.util.List;

public class CowTab1 extends Fragment {

    private static final String TAG = "CowTab1";
    public static RecyclerView recyclerView;

    private OnFragmentInteractionListener mListener;

    public CowTab1() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tab1, container, false);
        recyclerView = view.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        List<RateChartModel> list = new ArrayList<>();
        DbHelper dbHelper = new DbHelper(getContext());

        String nameOfSnf = getActivity().getIntent().getStringExtra("Name");
        if(nameOfSnf.equals("Buffalo")){

            Cursor data = dbHelper.getBuffaloSNFTable();

            if(data.getCount() != 0){
                while(data.moveToNext()){
                    list.add(new RateChartModel(data.getString(0), data.getString(1)));
                    //Log.d(TAG, "CowTab1: " + data.getString(0) + " " + data.getString(1));
                }
            }else{
            }
            RateChartAdapter adapter = new RateChartAdapter(getContext(), list);
            recyclerView.setAdapter(adapter);
        }
        else if(nameOfSnf.equals("Cow")) {
            Cursor data = dbHelper.getSNFTable();
            list.add(new RateChartModel(data.getColumnName(1), ""));
            Log.d(TAG, "column: " + data.getColumnName(0));

            if (data.getCount() != 0) {
                while (data.moveToNext()) {
                    list.add(new RateChartModel(data.getString(0), data.getString(1)));
                    //Log.d(TAG, "CowTab1: " + data.getString(0) + " " + data.getString(1));
                }
            } else {}
            for(RateChartModel object : list){
                Log.d(TAG, "Fat: " +object.getFat() + " Rate: " + object.getRate());
            }
            Log.d(TAG, "list:" + (list.size()-1));
            RateChartAdapter adapter = new RateChartAdapter(getContext(), list);
            recyclerView.setAdapter(adapter);
        }
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
