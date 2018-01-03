package com.ali.textfragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by Administrator on 2017/11/1.
 */

public class titleFragment extends Fragment {
    private Button hey;
    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
        TextV
        View view = inflater.inflate(R.layout.title_fragment,container,false);
        hey = (Button) view.findViewById(R.id.hey);
        hey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(),"heyAli",Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }
}
