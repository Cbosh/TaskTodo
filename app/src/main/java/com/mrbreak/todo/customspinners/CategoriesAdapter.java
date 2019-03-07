package com.mrbreak.todo.customspinners;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mrbreak.todo.R;
import com.mrbreak.todo.model.Category;

import java.util.List;

public class CategoriesAdapter extends BaseAdapter {
    Context context;
    List<Category> categories;
    LayoutInflater inflater;

    public CategoriesAdapter(Context applicationContext, List<Category> categories) {
        this.context = applicationContext;
        this.categories = categories;
        inflater = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return categories.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.category_spinner, null);
        TextView categoryName = view.findViewById(R.id.categoryName);
        categoryName.setText(categories.get(i).getCategoryName());
        categoryName.setBackgroundResource(R.color.off_white);
        view.setBackgroundResource(R.color.off_white);
        view.setPadding(10, 20, view.getPaddingRight(),
                20);
        return view;
    }
}
