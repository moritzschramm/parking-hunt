package com.moritzschramm.parkinghunt;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.moritzschramm.parkinghunt.Model.Spot;
import com.moritzschramm.parkinghunt.parkingHuntAPIClient.SpotClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Moritz on 29.08.2016.
 */
public class OwnSpotsListFragment extends ListFragment {

    private OwnSpotsArrayAdapter adapter;

    private void loadList() {

        SpotClient.getSpots(getContext(), new JsonHttpResponseHandler() {

            @Override
            public void onStart() {

                setListShown(false);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                try {
                    ArrayList<Spot> spots = new ArrayList<>();

                    JSONArray spotsJson = response.getJSONArray("spots");

                    if(spotsJson.length() == 0) {

                        setListShown(true);
                        setListAdapter(new EmptyAdapter(getContext(), getEmptyList()));
                        getListView().setDivider(null);
                        getListView().setDividerHeight(0);
                        getListView().setSelector(new StateListDrawable());
                        adapter = null;
                        return;
                    }

                    for (int i = 0; i < spotsJson.length(); i++) {

                        JSONObject spotJson = spotsJson.getJSONObject(i);

                        Spot spot = new Spot(spotJson.getDouble("lat"),
                                spotJson.getDouble("lng"),
                                spotJson.getInt("amount"),
                                spotJson.getString("type"));

                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                        spot.setCreatedAt(formatter.parse(spotJson.getString("createdAt")));
                        spot.setId(spotJson.getString("_id"));

                        spot.setPlace(spotJson.getString("place"));
                        spot.setStreetName(spotJson.getString("street"));

                        spots.add(spot);
                    }

                    setListShown(true);
                    if(adapter == null) {
                        adapter = new OwnSpotsArrayAdapter(getContext(), spots);
                        setListAdapter(adapter);
                        getListView().setDivider(null);
                        getListView().setDividerHeight(0);
                        getListView().setSelector(new StateListDrawable());
                    } else {
                        adapter.updateData(spots);
                    }


                } catch(JSONException | ParseException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

                Toast.makeText(getContext(), "Fehler beim laden", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        loadList();
    }

    public void notifyDataSetChanged() {

        loadList();
    }

    public class OwnSpotsArrayAdapter extends ArrayAdapter<Spot> {

        private Context context;

        private ArrayList<Spot> element;

        public OwnSpotsArrayAdapter(Context context, ArrayList<Spot> element) {

            super(context, R.layout.list_item_own_spots, element);
            this.context = context;
            this.element = element;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            convertView = ((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.list_item_own_spots, null);

            ((TextView)convertView.findViewById(R.id.name)).setText(element.get(position).getPlace() + ", " + element.get(position).getStreetName());

            ((TextView)convertView.findViewById(R.id.date)).setText(element.get(position).getCreatedAt().toString());

            convertView.findViewById(R.id.list_item).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(context, MapsActivity.class);
                    intent.putExtra("lat", element.get(position).getLat());
                    intent.putExtra("lng", element.get(position).getLng());
                    context.startActivity(intent);
                }
            });

            convertView.findViewById(R.id.list_item).setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    Toast.makeText(getContext(),
                            "pos:" +position+ " objid:"+element.get(position).getId(),
                            Toast.LENGTH_LONG).show();
                    return true;
                }
            });

            return convertView;
        }

        @Override
        public Spot getItem(int position) {
            return element.get(position);
        }

        public void updateData(ArrayList<Spot> spots) {

            element.clear();
            element.addAll(spots);
            notifyDataSetChanged();
        }
    }
    public class EmptyAdapter extends ArrayAdapter<String> {

        private ArrayList<String> element;

        private Context context;

        public EmptyAdapter(Context context, ArrayList<String> element) {

            super(context, R.layout.empty_list_item, element);
            this.context = context;
            this.element = element;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            convertView = ((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.empty_list_item, null);

            return convertView;
        }

        @Override
        public String getItem(int position) {
            return element.get(position);
        }
    }
    public static ArrayList<String> getEmptyList() {

        ArrayList<String> element = new ArrayList<>();
        element.add("null");
        return element;
    }
}
