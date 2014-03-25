/* Copyright (c) 2012 Google Inc.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.google.samplesolutions.mobileassistant;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.samplesolutions.mobileassistant.checkinendpoint.Checkinendpoint;
import com.google.samplesolutions.mobileassistant.checkinendpoint.model.CheckIn;
import com.google.samplesolutions.mobileassistant.placeendpoint.Placeendpoint;
import com.google.samplesolutions.mobileassistant.placeendpoint.model.CollectionResponsePlace;
import com.google.samplesolutions.mobileassistant.placeendpoint.model.Place;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;
import java.io.IOException;
import java.util.List;


public class MainActivity extends Activity {

  private TextView resultsList;
  

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    new CheckInTask().execute();

    resultsList = (TextView) findViewById(R.id.results);

    // start retrieving the list of nearby places
    new ListOfPlacesAsyncRetriever().execute();
  }


  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.main, menu);
    return true;
  }

  /**
* AsyncTask for calling Mobile Assistant API for checking into a
* place (e.g., a store).
*/
  private class CheckInTask extends AsyncTask<Void, Void, Void> {

    /**
* Calls appropriate CloudEndpoint to indicate that user checked into a place.
*
* @param params the place where the user is checking in.
*/
    @Override
    protected Void doInBackground(Void... params) {
      CheckIn checkin = new CheckIn();

      // Set the ID of the store where the user is.
      checkin.setPlaceId("StoreNo123");

      Checkinendpoint.Builder builder = new Checkinendpoint.Builder(
          AndroidHttp.newCompatibleTransport(), new JacksonFactory(), null);

      builder = CloudEndpointUtils.updateBuilder(builder);

      Checkinendpoint endpoint = builder.build();


      try {
        endpoint.insertCheckIn(checkin).execute();
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }

      return null;
    }
  }


  /**
* AsyncTask for retrieving the list of places (e.g., stores) and updating the
* corresponding results list.
*/
  private class ListOfPlacesAsyncRetriever extends AsyncTask<Void, Void, CollectionResponsePlace> {

    @Override
    protected CollectionResponsePlace doInBackground(Void... params) {


      Placeendpoint.Builder endpointBuilder = new Placeendpoint.Builder(
          AndroidHttp.newCompatibleTransport(), new JacksonFactory(), null);
     
      endpointBuilder = CloudEndpointUtils.updateBuilder(endpointBuilder);


      CollectionResponsePlace result;

      Placeendpoint endpoint = endpointBuilder.build();

      try {
        result = endpoint.listPlace().execute();
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
        result = null;
      }
      return result;
    }

    @Override
    @SuppressWarnings("null")
    protected void onPostExecute(CollectionResponsePlace result) {
      

      if (result == null || result.getItems() == null || result.getItems().size() < 1) {
        if (result == null) {
          resultsList.setText("Retrieving places failed.");
        } else {
          resultsList.setText("No places found.");
        }

        return;
      }

      List<Place> places = result.getItems();
      StringBuffer placesFound = new StringBuffer();
      
      for (Place place : places){
        placesFound.append(place.getName() + "\r\n");
      }
      
      resultsList.setText(placesFound.toString());
     
    }
  }

}