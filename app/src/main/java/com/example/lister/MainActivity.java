package com.example.lister;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.vision.v1p2beta1.Vision;
import com.google.api.services.vision.v1p2beta1.VisionRequest;
import com.google.api.services.vision.v1p2beta1.VisionRequestInitializer;
import com.google.api.services.vision.v1p2beta1.model.GoogleCloudVisionV1p2beta1AnnotateImageRequest;
import com.google.api.services.vision.v1p2beta1.model.GoogleCloudVisionV1p2beta1BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1p2beta1.model.GoogleCloudVisionV1p2beta1BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1p2beta1.model.EntityAnnotation;
import com.google.api.services.vision.v1p2beta1.model.GoogleCloudVisionV1p2beta1EntityAnnotation;
import com.google.api.services.vision.v1p2beta1.model.GoogleCloudVisionV1p2beta1Feature;
import com.google.api.services.vision.v1p2beta1.model.GoogleCloudVisionV1p2beta1Image;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    public static int itemIdForPhoto = -1;
    public static ListDatabaseHelper helper;
    public static Context itemContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("Lifecycle", "MainActivity - onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        Log.d("Lifecycle", "MainActivity - onResume");
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.d("Lifecycle", "MainActivity - onPause");
        super.onPause();
    }

    /**
     *
     * Camera and Cloud Vision API stuff.
     * From Google's Android Sample project at https://github.com/GoogleCloudPlatform/cloud-vision/tree/master/android
     *
     */

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("MainActivity", "On Activity Result");

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            uploadImage(data.getData());
        } else if (requestCode == 3 && resultCode == RESULT_OK) {
            Uri photoUri = FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName() + ".provider", getCameraFile());
            uploadImage(photoUri);
        }
    }

    public File getCameraFile() {
        File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return new File(dir, "temp.jpg");
    }

    public void uploadImage(Uri uri) {
        if (uri != null) {
            try {
                // scale the image to save on bandwidth
                Bitmap bitmap = scaleBitmapDown( MediaStore.Images.Media.getBitmap(getContentResolver(), uri), 1200);

                callCloudVision(bitmap);

            } catch (IOException e) {
                Log.d("Image picking", "Image picking failed because " + e.getMessage());
                Toast.makeText(this, "There was an error picking the image", Toast.LENGTH_LONG).show();
            }
        } else {
            Log.d("Image picking", "Image picker gave us a null image.");
            Toast.makeText(this, "There was an error picking the image.", Toast.LENGTH_LONG).show();
        }
    }

    private Bitmap scaleBitmapDown(Bitmap bitmap, int maxDimension) {

        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();
        int resizedWidth = maxDimension;
        int resizedHeight = maxDimension;

        if (originalHeight > originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = (int) (resizedHeight * (float) originalWidth / (float) originalHeight);
        } else if (originalWidth > originalHeight) {
            resizedWidth = maxDimension;
            resizedHeight = (int) (resizedWidth * (float) originalHeight / (float) originalWidth);
        } else if (originalHeight == originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = maxDimension;
        }
        return Bitmap.createScaledBitmap(bitmap, resizedWidth, resizedHeight, false);
    }

    private void callCloudVision(final Bitmap bitmap) {
        //Todo: Indicate loading maybe.

        // Do the real work in an async task, because we need to use the network anyway
        try {
            AsyncTask<Object, Void, String> labelDetectionTask = new LableDetectionTask(this, prepareAnnotationRequest(bitmap));
            labelDetectionTask.execute();
        } catch (IOException e) {
            Log.d("CloudVision", "failed to make API request because of other IOException " + e.getMessage());
        }
    }

    private Vision.Images.Annotate prepareAnnotationRequest(final Bitmap bitmap) throws IOException {
        HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

        VisionRequestInitializer requestInitializer =
                //The google cloud vision api key is in the secrets.xml file and should not be committed to git.
                new VisionRequestInitializer(getString(R.string.cloud_vision_api_key)) {
                    /**
                     * We override this so we can inject important identifying fields into the HTTP
                     * headers. This enables use of a restricted cloud platform API key.
                     */
                    @Override
                    protected void initializeVisionRequest(VisionRequest<?> visionRequest) throws IOException {
                        super.initializeVisionRequest(visionRequest);

                        String packageName = getPackageName();
                        visionRequest.getRequestHeaders().set("X-Android-Package", packageName);

                        String sig = PackageManagerUtils.getSignature(getPackageManager(), packageName);

                        visionRequest.getRequestHeaders().set("X-Android-Cert", sig);
                    }
                };

        Vision.Builder builder = new Vision.Builder(httpTransport, jsonFactory, null);
        builder.setVisionRequestInitializer(requestInitializer);

        Vision vision = builder.build();

        GoogleCloudVisionV1p2beta1BatchAnnotateImagesRequest batchAnnotateImagesRequest = new GoogleCloudVisionV1p2beta1BatchAnnotateImagesRequest();
        batchAnnotateImagesRequest.setRequests(new ArrayList<GoogleCloudVisionV1p2beta1AnnotateImageRequest>() {{
            GoogleCloudVisionV1p2beta1AnnotateImageRequest annotateImageRequest = new GoogleCloudVisionV1p2beta1AnnotateImageRequest();

            // Add the image
            GoogleCloudVisionV1p2beta1Image base64EncodedImage = new GoogleCloudVisionV1p2beta1Image();
            // Convert the bitmap to a JPEG
            // Just in case it's a format that Android understands but Cloud Vision
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
            byte[] imageBytes = byteArrayOutputStream.toByteArray();

            // Base64 encode the JPEG
            base64EncodedImage.encodeContent(imageBytes);
            annotateImageRequest.setImage(base64EncodedImage);

            // add the features we want
            annotateImageRequest.setFeatures(new ArrayList<GoogleCloudVisionV1p2beta1Feature>() {{
                GoogleCloudVisionV1p2beta1Feature textDetection = new GoogleCloudVisionV1p2beta1Feature();
                textDetection.setType("DOCUMENT_TEXT_DETECTION");
                textDetection.setMaxResults(10);
                add(textDetection);
            }});

            // Add the list of one thing to the request
            add(annotateImageRequest);
        }});

        Vision.Images.Annotate annotateRequest = vision.images().annotate(batchAnnotateImagesRequest);
        // Due to a bug: requests to Vision API containing large images fail when GZipped.
        annotateRequest.setDisableGZipContent(true);
        Log.d("Cloud Vision", "created Cloud Vision request object, sending request");

        return annotateRequest;
    }

    private class LableDetectionTask extends AsyncTask<Object, Void, String> {
        private final WeakReference<MainActivity> mActivityWeakReference;
        private Vision.Images.Annotate mRequest;

        LableDetectionTask(MainActivity activity, Vision.Images.Annotate annotate) {
            mActivityWeakReference = new WeakReference<>(activity);
            mRequest = annotate;
        }

        @Override
        protected String doInBackground(Object... params) {
            try {
                Log.d("CloudVision", "created Cloud Vision request object, sending request");
                GoogleCloudVisionV1p2beta1BatchAnnotateImagesResponse response = mRequest.execute();
                return convertResponseToString(response);

            } catch (GoogleJsonResponseException e) {
                Log.d("CloudVision", "failed to make API request because " + e.getContent());
            } catch (IOException e) {
                Log.d("CloudVision", "failed to make API request because of other IOException " +
                        e.getMessage());
            }
            return "Cloud Vision API request failed. Check logs for details.";
        }

        protected void onPostExecute(String result) {
            MainActivity activity = mActivityWeakReference.get();
            if (activity != null && !activity.isFinishing()) {
                Log.d("CloudVision", "Success! " + result);
                helper = ListDatabaseHelper.getInstance(itemContext);
                helper.updateItemPrice(getPrice(result), itemIdForPhoto);
                //ListFragment fm = (ListFragment) getSupportFragmentManager().findFragmentById(R.id.list);
                //fm.refresh();
            }
        }
    }

    private static String convertResponseToString(GoogleCloudVisionV1p2beta1BatchAnnotateImagesResponse response) {
        StringBuilder message = new StringBuilder("I found these things:\n\n");

        List<GoogleCloudVisionV1p2beta1EntityAnnotation> textItems = response.getResponses().get(0).getTextAnnotations();
        if (textItems != null) {
            for (GoogleCloudVisionV1p2beta1EntityAnnotation text : textItems) {
                message.append(text.getDescription());
                message.append("\n");
            }
        } else {
            message.append("nothing");
        }

        return message.toString();
    }

    private static double getPrice(String message) {
        double price = -1.0;
        String[] texts = message.split("\n");
        String prev = texts[0];
        for (String s : texts) {
            if (s.equals("PRICE") || s.equals("$")) {
                prev = s;
                Log.d("Price Parse", "Primed for price with tag: " + prev);
                continue;
            }
            //TODO: Better discrimination
            if (prev.equals("PRICE") || prev.equals("$")) {
                try {
                    price = Double.parseDouble(s);
                    Log.d("Price Parse", "Price found: " + price);
                    break;
                } catch (Exception e) {
                    continue;
                }
            }
        }
        if (price == -1.0) Log.d("Price Parse", "No Price found");
        return price;
    }
}