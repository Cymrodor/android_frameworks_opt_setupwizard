/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.setupwizardlib.items;

import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * An adapter typically used with ListView to display a list of Items. The list of items used to
 * create this adapter can be inflated by {@link ItemInflater} from XML.
 */
public class ItemAdapter extends BaseAdapter {

    private final Item[] mItems;
    private ViewTypes mViewTypes = new ViewTypes();

    public static ItemAdapter create(ItemGroup items) {
        return new ItemAdapter(items.getChildren());
    }

    public ItemAdapter(Item[] items) {
        mItems = items;
        refreshViewTypes();
    }

    @Override
    public int getCount() {
        return mItems.length;
    }

    @Override
    public Item getItem(int position) {
        return mItems[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        int layoutRes = getItem(position).getLayoutResource();
        return mViewTypes.get(layoutRes);
    }

    @Override
    public int getViewTypeCount() {
        return mViewTypes.size();
    }

    public void refreshViewTypes() {
        for (Item item : mItems) {
            mViewTypes.add(item.getLayoutResource());
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Item item = getItem(position);
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            convertView = inflater.inflate(item.getLayoutResource(), parent, false);
        }
        item.onBindView(convertView);
        return convertView;
    }

    /**
     * A helper class to pack a sparse set of integers (e.g. resource IDs) to a contiguous list of
     * integers (e.g. adapter positions), providing mapping to retrieve the original ID from a given
     * position. This is used to pack the view types of the adapter into contiguous integers from
     * a given layout resource.
     */
    private static class ViewTypes {
        private SparseIntArray mPositionMap = new SparseIntArray();
        private int nextPosition = 0;

        public int add(int id) {
            if (mPositionMap.indexOfKey(id) < 0) {
                mPositionMap.put(id, nextPosition);
                nextPosition++;
            }
            return mPositionMap.get(id);
        }

        public int size() {
            return mPositionMap.size();
        }

        public int get(int id) {
            return mPositionMap.get(id);
        }
    }
}