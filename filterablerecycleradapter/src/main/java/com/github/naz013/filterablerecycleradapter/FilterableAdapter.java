package com.github.naz013.filterablerecycleradapter;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple filterable RecyclerView adapter implementation.
 *
 * @param <V>  Object used for adapter as model.
 * @param <Q>  Object for models filtering (ex. String).
 * @param <VH> ViewHolder for RecyclerView.Adapter.
 */
public abstract class FilterableAdapter<V, Q, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    /**
     * All data that you set to adapter.
     */
    private List<V> originalData = new ArrayList<>();

    /**
     * Data that used for adapter after filtering process.
     */
    private List<V> usedData = new ArrayList<>();
    private Filter<V, Q> filter;
    private FilterCallback<V, Q> filterCallback;
    private Q lastQuery;

    public FilterableAdapter() {

    }

    public FilterableAdapter(List<V> data) {
        this.originalData = data;
    }

    public FilterableAdapter(List<V> data, Filter<V, Q> filter) {
        this.originalData = data;
        this.usedData = new ArrayList<>(data);
        this.filter = filter;
    }

    public void setData(List<V> data) {
        this.originalData = data;
        this.usedData = new ArrayList<>(data);
        if (this.lastQuery != null) {
            filter(this.lastQuery);
        }
    }

    public void setFilter(Filter<V, Q> filter) {
        this.filter = filter;
        this.lastQuery = null;
    }

    public void setFilterCallback(FilterCallback<V, Q> filterCallback) {
        this.filterCallback = filterCallback;
    }

    public List<V> getUsedData() {
        return usedData;
    }

    public V getItem(int position) {
        return usedData.get(position);
    }

    public void addItem(V v) {
        originalData.add(v);
        if (filter != null) {
            if (lastQuery != null) {
                if (filter.filter(v, lastQuery)) {
                    addToList(v);
                }
            } else {
                addToList(v);
            }
        } else {
            notifyItemInserted(originalData.size() - 1);
            notifyItemRangeChanged(0, originalData.size());
        }
    }

    public void addItem(int position, V v) {
        if (position >= originalData.size()) {
            addItem(v);
            return;
        }
        V current = originalData.get(position);
        originalData.add(position, v);
        if (filter != null) {
            if (lastQuery != null) {
                if (filter.filter(v, lastQuery)) {
                    int index = usedData.indexOf(current);
                    addToList(index, v);
                }
            } else {
                int index = usedData.indexOf(current);
                addToList(index, v);
            }
        } else {
            notifyItemInserted(position);
            notifyItemRangeChanged(0, originalData.size());
        }
    }

    public void filter(Q q) {
        this.lastQuery = q;
        if (filter != null) {
            List<V> res = getFiltered(q);
            animateTo(res);
            if (filterCallback != null) filterCallback.onFilter(res, q);
        }
    }

    @Nullable
    public V removeItem(int position) {
        if (position < usedData.size()) {
            V v = usedData.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(0, usedData.size());
            int index = originalData.indexOf(v);
            if (index != -1) originalData.remove(index);
            return v;
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return usedData.size();
    }

    public int getOriginalItemCount() {
        return originalData.size();
    }

    private void addToList(V v) {
        usedData.add(v);
        notifyItemInserted(usedData.size() - 1);
        notifyItemRangeChanged(0, usedData.size());
    }

    private void addToList(int position, V v) {
        usedData.add(position, v);
        notifyItemInserted(position);
        notifyItemRangeChanged(0, usedData.size());
    }

    private List<V> getFiltered(Q query) {
        if (query == null) return originalData;
        List<V> list = new ArrayList<>();
        for (V model : originalData) {
            if (filter.filter(model, query)) {
                list.add(model);
            }
        }
        return list;
    }

    protected V remove(int position) {
        V model = usedData.remove(position);
        notifyItemRemoved(position);
        return model;
    }

    protected void add(int position, V model) {
        usedData.add(position, model);
        notifyItemInserted(position);
    }

    protected void move(int fromPosition, int toPosition) {
        System.out.println("from " + fromPosition + ", to " + toPosition);
        V model = usedData.remove(fromPosition);
        usedData.add(toPosition, model);
        notifyItemMoved(fromPosition, toPosition);
    }

    private void animateTo(List<V> models) {
        applyAndAnimateRemovals(models);
        applyAndAnimateAdditions(models);
        applyAndAnimateMovedItems(models);
    }

    protected void applyAndAnimateRemovals(List<V> newModels) {
        for (int i = usedData.size() - 1; i >= 0; i--) {
            V model = usedData.get(i);
            if (!newModels.contains(model)) {
                remove(i);
            }
        }
    }

    protected void applyAndAnimateAdditions(List<V> newModels) {
        for (int i = 0, count = newModels.size(); i < count; i++) {
            V model = newModels.get(i);
            if (!usedData.contains(model)) {
                add(i, model);
            }
        }
    }

    protected void applyAndAnimateMovedItems(List<V> newModels) {
        for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
            V model = newModels.get(toPosition);
            final int fromPosition = usedData.indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                move(fromPosition, toPosition);
            }
        }
    }

    public interface Filter<V, Q> {
        boolean filter(V v, Q query);
    }

    public interface FilterCallback<V, Q> {
        void onFilter(List<V> result, Q query);
    }
}
