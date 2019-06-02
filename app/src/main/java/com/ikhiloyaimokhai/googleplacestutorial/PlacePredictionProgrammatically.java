package com.ikhiloyaimokhai.googleplacestutorial;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.PlacesClient;

public class PlacePredictionProgrammatically extends AppCompatActivity {
    private static final String TAG = PlacePredictionProgrammatically.class.getSimpleName();
    private EditText queryText;
    private Button mSearchButton;
    private TextView mSearchResult;
    private StringBuilder mResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_prediction_programmatically);
        queryText = findViewById(R.id.inputEditText);
        mSearchButton = findViewById(R.id.searchButton);
        mSearchResult = findViewById(R.id.searchResult);
        String apiKey = getString(R.string.api_key);

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), apiKey);
        }

        // Create a new Places client instance.
        PlacesClient placesClient = Places.createClient(this);


        mSearchButton.setOnClickListener(v -> {
            Toast.makeText(PlacePredictionProgrammatically.this, queryText.getText().toString(), Toast.LENGTH_SHORT).show();
            // Create a new token for the autocomplete session. Pass this to FindAutocompletePredictionsRequest,
            // and once again when the user makes a selection (for example when calling fetchPlace()).
            AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();
            // Create a RectangularBounds object.
            RectangularBounds bounds = RectangularBounds.newInstance(
                    new LatLng(-33.880490, 151.184363), //dummy lat/lng
                    new LatLng(-33.858754, 151.229596));
            // Use the builder to create a FindAutocompletePredictionsRequest.
            FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                    // Call either setLocationBias() OR setLocationRestriction().
                    .setLocationBias(bounds)
                    //.setLocationRestriction(bounds)
                    .setCountry("ng")//Nigeria
                    .setTypeFilter(TypeFilter.ADDRESS)
                    .setSessionToken(token)
                    .setQuery(queryText.getText().toString())
                    .build();


            placesClient.findAutocompletePredictions(request).addOnSuccessListener(response -> {
                mResult = new StringBuilder();
                for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {
                    mResult.append(" ").append(prediction.getFullText(null) + "\n");
                    Log.i(TAG, prediction.getPlaceId());
                    Log.i(TAG, prediction.getPrimaryText(null).toString());
                    Toast.makeText(PlacePredictionProgrammatically.this, prediction.getPrimaryText(null) + "-" + prediction.getSecondaryText(null), Toast.LENGTH_SHORT).show();
                }
                mSearchResult.setText(String.valueOf(mResult));
            }).addOnFailureListener((exception) -> {
                if (exception instanceof ApiException) {
                    ApiException apiException = (ApiException) exception;
                    Log.e(TAG, "Place not found: " + apiException.getStatusCode());
                }
            });
        });


    }
}
