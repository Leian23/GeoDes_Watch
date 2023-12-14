package com.example.geodes_____watch;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.activity.ComponentActivity;
import androidx.preference.PreferenceManager;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class GenerateQRCodeActivity extends ComponentActivity {

    private ImageView qrCodeImageView;
    private RelativeLayout qrCodeGeneratorButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_qrcode);
        qrCodeImageView = findViewById(R.id.QRCodeImg);
        qrCodeGeneratorButton = findViewById(R.id.QRCodeGeneratorBtn);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String data = sharedPreferences.getString("user_code", "");
        if(data.isEmpty()){
            Toast.makeText(GenerateQRCodeActivity.this, "Data is empty", Toast.LENGTH_SHORT).show();
        }else{

            // Initialize multi format writer
            MultiFormatWriter writer = new MultiFormatWriter();

            // Initialize bit matrix
            try {
                BitMatrix matrix = writer.encode(data, BarcodeFormat.QR_CODE, 250, 250);

                // Initialize barcode encoder
                BarcodeEncoder encoder = new BarcodeEncoder();

                // Initialize Bitmap
                Bitmap bitmap = encoder.createBitmap(matrix);

                //set bitmap on image view
                qrCodeImageView.setImageBitmap(bitmap);


            } catch (WriterException e) {
                e.printStackTrace();
            }


        }
        qrCodeGeneratorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String data = Constants.user_code.trim();
                if(data.isEmpty()){
                    Toast.makeText(GenerateQRCodeActivity.this, "Data is empty", Toast.LENGTH_SHORT).show();
                }else{

                    // Initialize multi format writer
                    MultiFormatWriter writer = new MultiFormatWriter();

                    // Initialize bit matrix
                    try {
                        BitMatrix matrix = writer.encode(data, BarcodeFormat.QR_CODE, 250, 250);

                        // Initialize barcode encoder
                        BarcodeEncoder encoder = new BarcodeEncoder();

                        // Initialize Bitmap
                        Bitmap bitmap = encoder.createBitmap(matrix);

                        //set bitmap on image view
                        qrCodeImageView.setImageBitmap(bitmap);


                    } catch (WriterException e) {
                        e.printStackTrace();
                    }


                }
            }
        });

    }
}