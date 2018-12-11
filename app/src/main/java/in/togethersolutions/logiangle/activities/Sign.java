package in.togethersolutions.logiangle.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.github.gcacace.signaturepad.views.SignaturePad;

import java.io.ByteArrayOutputStream;

import in.togethersolutions.logiangle.R;

public class Sign extends AppCompatActivity {
    public SignaturePad signPad;
    public Button btnSave;
    public Button btnClear;

    public String orderNumber = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);
        orderNumber = getIntent().getStringExtra("orderNumber");
        uiComponent();
    }

    private void uiComponent() {
        signPad = (SignaturePad)findViewById(R.id.signature_pad);
        btnSave = (Button)findViewById(R.id.save_button);
        btnClear = (Button)findViewById(R.id.clear_button);
        signPad.setOnSignedListener(new SignaturePad.OnSignedListener() {
            @Override
            public void onStartSigning() {

            }

            @Override
            public void onSigned() {
                btnSave.setEnabled(true);
                btnClear.setEnabled(true);
            }

            @Override
            public void onClear() {
                btnSave.setEnabled(false);
                btnClear.setEnabled(false);

            }
        });
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signPad.clear();
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap signatureBitmap = signPad.getSignatureBitmap();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                signatureBitmap.compress(Bitmap.CompressFormat.PNG,100,stream);
                byte[] byteArray = stream.toByteArray();
                 Intent intent = new Intent(Sign.this,OrderInformationNew.class);
                intent.putExtra("image",byteArray);
                intent.putExtra("orderNumber",orderNumber);
                startActivity(intent);

            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Sign.this,OrderInformation.class);
        intent.putExtra("orderNumber",orderNumber);
        startActivity(intent);
    }
}
