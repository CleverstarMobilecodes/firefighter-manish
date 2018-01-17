package com.firefighterscalendar.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firefighterscalendar.R;
import com.firefighterscalendar.bean.CategoryBean;
import com.firefighterscalendar.bean.SubCategoryBean;

import java.util.List;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<CategoryBean> listCategory; // header titles

    public ExpandableListAdapter(Context context, List<CategoryBean> listDataHeader) {
        this.context = context;
        this.listCategory = listDataHeader;
    }

    @Override
    public SubCategoryBean getChild(int groupPosition, int childPosititon) {
        return listCategory.get(groupPosition).listSubCategory.get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.item_theme_child, null);
        }

        TextView txtThemeSubName = (TextView) convertView.findViewById(R.id.txtThemeSubName);
        txtThemeSubName.setText(getChild(groupPosition, childPosition).subCategoryName);

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return listCategory.get(groupPosition).listSubCategory.size();
    }

    @Override
    public CategoryBean getGroup(int groupPosition) {
        return listCategory.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return listCategory.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.item_theme, null);
        }

        ImageView imageThemePhoto = (ImageView) convertView.findViewById(R.id.imageThemePhoto);
        TextView txtThemeName = (TextView) convertView.findViewById(R.id.txtThemeName);

        Glide.with(context).load(getGroup(groupPosition).categoryIcon).centerCrop().into(imageThemePhoto);
        txtThemeName.setText(getGroup(groupPosition).categoryName);

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}