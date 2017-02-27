# FilterableRecyclerViewAdapter
[![](https://jitpack.io/v/naz013/FilterableRecyclerViewAdapter.svg)](https://jitpack.io/#naz013/FilterableRecyclerViewAdapter)

Simple filterable RecyclerView Adapter library.


## Usage
Gradle

Add Jitpack repository:
```javascript
repositories {
			maven { url 'https://jitpack.io' }
		}
```

Add library dependency:
```javascript
dependencies {
  compile 'com.github.naz013:FilterableRecyclerViewAdapter:1.0.4'
}
```

And use it in your RecyclerView adapter:
```javascript
public class SimpleAdapter extends FilterableAdapter<Model, String, Holder> {

    public SimpleAdapter(List<Model> data, Filter<Model, String> filter) {
        super(data, filter);
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(mContext).inflate(R.layout.item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        holder.textView.setText(getItem(position).getTitle());
    }
}
```

## License

    Copyright 2017 Nazar Suhovich

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
