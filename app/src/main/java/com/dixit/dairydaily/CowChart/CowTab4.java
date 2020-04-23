package com.dixit.dairydaily.CowChart;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dixit.dairydaily.Models.RateChartModel;
import com.dixit.dairydaily.MyAdapters.RateChartAdapter;
import com.dixit.dairydaily.Others.DbHelper;
import com.dixit.dairydaily.R;

import java.util.ArrayList;
import java.util.List;

public class CowTab4 extends Fragment {

    private OnFragmentInteractionListener mListener;

    private static final String TAG = "CowTab4";

    public CowTab4() {
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
        View view = inflater.inflate(R.layout.fragment_tab4, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        List<RateChartModel> list = new ArrayList<>();
        DbHelper dbHelper = new DbHelper(getContext());
        String nameOfSnf = getActivity().getIntent().getStringExtra("Name");
        if(nameOfSnf.equals("Buffalo")){

            Cursor data = dbHelper.getBuffaloSNFTable();

            if(data.getCount() != 0){
                while(data.moveToNext()){
                    list.add(new RateChartModel(data.getString(0), data.getString(4)));
                    //Log.d(TAG, "CowTab1: " + data.getString(0) + " " + data.getString(1));
                }
            }else{
                Toast.makeText(getContext(), "Data count is 0", Toast.LENGTH_SHORT).show();
            }
            RateChartAdapter adapter = new RateChartAdapter(getContext(),list);
            recyclerView.setAdapter(adapter);
        }
        else if(nameOfSnf.equals("Cow")) {
            Cursor data = dbHelper.getSNFTable();
            list.add(new RateChartModel(data.getColumnName(4), ""));
            if (data.getCount() != 0) {
                while (data.moveToNext()) {
                    list.add(new RateChartModel(data.getString(0), data.getString(4)));
                    //Log.d(TAG, "CowTab1: " + data.getString(0) + " " + data.getString(1));
                }
            } else {
                Toast.makeText(getContext(), "Data count is 0", Toast.LENGTH_SHORT).show();
            }
            RateChartAdapter adapter = new RateChartAdapter(getContext(),list);
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
