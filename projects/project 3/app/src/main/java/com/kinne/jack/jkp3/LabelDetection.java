package com.kinne.jack.jkp3;

import android.app.Activity;
import android.graphics.Bitmap;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 with assist from Sam Alston : CS 480
 This class handles taking the image byte array and sending it off to Google Vision API
 Class was pulled from Google documentation and modified
 */

public class LabelDetection extends Activity{
    private static final String LABEL_DETECTION = "LABEL_DETECTION";
    private static final int MAX_LABEL_RESULTS = 10;
    private static final String CLOUD_VISION_API_KEY = "AIzaSyD5UXhILEFjEMol189wgnkd4c8MJfp1Hag";
    private Bitmap bitmap;

    public static Map<String, Float> annotateImage(byte[] imageBytes) throws IOException {

        // Construct the Vision API instance
        HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
        VisionRequestInitializer initializer = new VisionRequestInitializer(CLOUD_VISION_API_KEY);
        Vision vision = new Vision.Builder(httpTransport, jsonFactory, null)
                .setVisionRequestInitializer(initializer)
                .build();

        // Create the image request
        AnnotateImageRequest imageRequest = new AnnotateImageRequest();
        Image img = new Image();
        img.encodeContent(imageBytes);
        imageRequest.setImage(img);

        // Add the features we want
        Feature labelDetection = new Feature();
        labelDetection.setType(LABEL_DETECTION);
        labelDetection.setMaxResults(MAX_LABEL_RESULTS);
        imageRequest.setFeatures(Collections.singletonList(labelDetection));

        // Batch and execute the request
        BatchAnnotateImagesRequest requestBatch = new BatchAnnotateImagesRequest();
        requestBatch.setRequests(Collections.singletonList(imageRequest));
        BatchAnnotateImagesResponse response = vision.images()
                .annotate(requestBatch)
                // Due to a bug: requests to Vision API containing large images fail when GZipped.
                .setDisableGZipContent(true)
                .execute();

        return convertResponseToMap(response);
    }

    //Google method modified by SRA
    private static Map<String, Float> convertResponseToMap(BatchAnnotateImagesResponse response) {

        // Convert response into a readable collection of annotations
        Map<String, Float> annotations = new HashMap<>();
        List<EntityAnnotation> labels = response.getResponses().get(0).getLabelAnnotations();

        if (labels != null) {

            for (EntityAnnotation label : labels) {

                annotations.put(label.getDescription(), label.getScore());
            }
        }
        return annotations;
    }
}