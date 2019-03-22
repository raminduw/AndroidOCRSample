package com.raminduweeraman.androidocrsample;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.Text;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;


public class MainActivity extends AppCompatActivity {
    private ImageView imageView;
    private Button btnProcess;
    private TextView txtView;
    private Bitmap ocrImageBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.imageView);
        btnProcess = findViewById(R.id.btnProcess);
        txtView = findViewById(R.id.txtView);

        btnProcess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextRecognizer txtRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
                if (!txtRecognizer.isOperational()) {
                    txtView.setText(R.string.error_prompt);
                } else {
                    if (ocrImageBitmap != null) {
                        Frame frame = new Frame.Builder().setBitmap(ocrImageBitmap).build();
                        SparseArray items = txtRecognizer.detect(frame);
                        StringBuilder strBuilder = new StringBuilder();
                        for (int i = 0; i < items.size(); i++) {
                            TextBlock item = (TextBlock) items.valueAt(i);
                            strBuilder.append(item.getValue());
                            strBuilder.append("/");
                            for (Text line : item.getComponents()) {
                                //extract scanned text lines here
                                Log.v("lines", line.getValue());
                                for (Text element : line.getComponents()) {
                                    Log.v("element", element.getValue());
                                }
                            }
                        }
                        if (!strBuilder.toString().isEmpty()) {
                            txtView.setText(strBuilder.toString().substring(0, strBuilder.toString().length() - 1));
                        } else {
                            txtView.setText(getString(R.string.ocr_reading_error));
                        }
                    } else {
                        txtView.setText(getString(R.string.ocr_reading_error));
                    }
                }
            }
        });
    }

    public void captureImage(View view) {
        CropImage.activity(null).setGuidelines(CropImageView.Guidelines.ON).start(this,true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                imageView.setImageURI(resultUri);
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                ocrImageBitmap = BitmapFactory.decodeFile(resultUri.getEncodedPath(), options);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                error.printStackTrace();
                txtView.setText(getString(R.string.ocr_reading_error));
            }
        }
    }
}
