package com.example.lister;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentTransaction;

import java.io.File;
import java.util.List;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;

/**
 * ListItemAdapter used for handling custom listView list items
 */
public class ListItemAdapter extends ArrayAdapter<ListItem> {
    private Context mContext;
    private Activity mActivity;
    int mResource;
    ListDatabaseHelper helper;
    TextView totalPriceView;
    int listId;

    /**
     * Default constructor for ListItemAdapter
     */
    public ListItemAdapter(Activity activity, Context context, int resource, List<ListItem> objects){
        super(context, resource, objects);
        mActivity = activity;
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        helper = ListDatabaseHelper.getInstance(mContext);

        //Get list item information
        String name = Objects.requireNonNull(getItem(position)).getName();
        int quantity = Objects.requireNonNull(getItem(position)).getQuantity();
        double price = Objects.requireNonNull(getItem(position)).getPrice();
        listId = Objects.requireNonNull(getItem(position)).getListId();
        final int itemId = Objects.requireNonNull(getItem(position)).getItemId();

        //Create the list item object with the information
        ListItem listItem = new ListItem(name, quantity, price, listId);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        //Get custom listView elements
        EditText editName = convertView.findViewById(R.id.item_name);
        Spinner spinnerQuantity = convertView.findViewById(R.id.item_count);
        EditText editPrice = convertView.findViewById(R.id.item_price);
        ImageButton photoButton = convertView.findViewById(R.id.photo_button);

        //Set item information
        editName.setText(name);
        spinnerQuantity.setSelection(quantity);
        editPrice.setText(String.valueOf(price));

        setItemListener(itemId, editName, spinnerQuantity, editPrice, photoButton);

        return convertView;
    }

    /**
     * Sets the listeners for custom listView components spinner item quantity,
     * edit text item name, edit text price, and image button camera button
     * @param itemId the primary key of database table item
     * @param editName the EditText xml element for naming item
     * @param spinnerQuantity the Spinner xml element for setting item quantity
     * @param editPrice the EditText xml element for setting item price
     * @param photoButton the ImageButton xml element for camera usage
     */
    private void setItemListener(final int itemId, final EditText editName, final Spinner spinnerQuantity,
                                 final EditText editPrice, final ImageButton photoButton){
        //Select item quantity via Spinner
        spinnerQuantity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                helper.updateItemQuantity(i, itemId);
                updateTotalPrice();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Log.d("spinnerListener", "item not selected in spinner");
            }
        });

        //Edit name of item via EditText
        editName.setOnFocusChangeListener(new AdapterView.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                String itemName = editName.getText().toString();
                helper.updateItemName(itemName, itemId);
            }
        });

        //Edit price of item via EditText
        editPrice.setOnFocusChangeListener(new AdapterView.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                double itemPrice = Double.parseDouble(editPrice.getText().toString());
                helper.updateItemPrice(itemPrice, itemId);
                updateTotalPrice();
            }
        });

        //Take a photo, send it to the API, and update fields accordingly from the response.
        photoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Since the callback from the camera has to be in the activity, we need to send
                //the item id there. That way we can update the item from the activity after taking photo and analyzing it.
                MainActivity.itemIdForPhoto = itemId;
                MainActivity.itemContext = getContext();
                startCamera();
                //editPrice.setText(helper.getItemPrice(itemId));

                updateTotalPrice();
            }
        });
    }
    /**
     * initializes the total price TextView so that total price can be dynamically
     * whenever quantity or price changes
     *
     * @param totalPriceView the TextView representing the total price xml element
     */
    public void initializeTotalTV(TextView totalPriceView){
        this.totalPriceView = totalPriceView;
    }

    /**
     * Calculates the total price and displays it
     */
    public void updateTotalPrice(){
        if(helper != null && totalPriceView != null) {
            List<ListItem> itemsList = helper.getListItems(listId);
            double totalPrice = 0;
            for (int i = 0; i < itemsList.size(); i++) {
                ListItem item = itemsList.get(i);
                totalPrice += item.getPrice() * (item.getQuantity() + 1);
            }
            totalPriceView.setText(String.format("$%.2f", totalPrice));
        }
    }

    /**
     *
     * Camera and Cloud Vision API stuff.
     * From Google's Android Sample project at https://github.com/GoogleCloudPlatform/cloud-vision/tree/master/android
     *
     */

    public void startCamera() {
        if (PermissionUtils.requestPermission(
                mActivity,
                2,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            Uri photoUri = FileProvider.getUriForFile(mContext, mContext.getApplicationContext().getPackageName() + ".provider", getCameraFile());
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            mActivity.startActivityForResult(intent, 3);
        }
    }

    public File getCameraFile() {
        File dir = mActivity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return new File(dir, "temp.jpg");
    }
}
