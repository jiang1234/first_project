package com.ali.downloadfragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class AppFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.app_fragment,container,false);
        RecyclerView appRecyclerView = (RecyclerView)view.findViewById(R.id.app_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        appRecyclerView.setLayoutManager(layoutManager);
        AppItemAadpter appItemAadpter = new AppItemAadpter(getAppItemList());
        appRecyclerView.setAdapter(appItemAadpter);
        return view;
    }
    private List<AppItem> getAppItemList(){
        List<AppItem> appItemList = new ArrayList<>();
        for(int i = 0;i<10;i++){
            AppItem appItem = new AppItem("aaaa"+i,"bbbb"+i,0);
            appItemList.add(appItem);
        }
        return appItemList;
    }
    public class AppItemAadpter extends RecyclerView.Adapter<AppItemAadpter.ViewHolder>{
        private List<AppItem> mAppItemList;
        public AppItemAadpter(List<AppItem> mAppItemList){
            this.mAppItemList = mAppItemList;
        }
         class ViewHolder extends RecyclerView.ViewHolder{
            ImageView appImage;
            TextView name;
            TextView type;

            public ViewHolder(View view){
                super(view);
                appImage = (ImageView) view.findViewById(R.id.image_item);
                name = (TextView) view.findViewById(R.id.app_name_item);
                type = (TextView) view.findViewById(R.id.app_type_item);
            }

        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.app_recycler_view_item,parent,false);
            ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            AppItem appItem = (AppItem)mAppItemList.get(position);
            holder.name.setText(appItem.getName());
            holder.type.setText(appItem.getType());
        }

        @Override
        public int getItemCount() {
            return mAppItemList.size();
        }
    }
}
