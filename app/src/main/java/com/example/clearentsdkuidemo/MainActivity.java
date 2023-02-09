package com.example.clearentsdkuidemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult;
import androidx.appcompat.app.AppCompatActivity;

import com.clearent.idtech.android.wrapper.ClearentDataSource;
import com.clearent.idtech.android.wrapper.ClearentWrapper;
import com.clearent.idtech.android.wrapper.model.PaymentInfo;
import com.clearent.idtech.android.wrapper.ui.ClearentAction;
import com.clearent.idtech.android.wrapper.ui.ClearentSDKActivity;
import com.clearent.idtech.android.wrapper.ui.PaymentMethod;
import com.example.clearentsdkuidemo.databinding.ActivityMainBinding;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private final ClearentWrapper cw = ClearentWrapper.Companion.getInstance();

    private final ActivityResultLauncher<Intent> activityLauncher = registerForActivityResult(
            new StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    transactionOngoing = false;

                    // Check the result code of the action, if the activity returned RESULT_OK.
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        final Intent data = result.getData();

                        // Should never be null
                        if (data == null) return;

                        // Check code
                        // if ((data.getIntExtra(ClearentSDKActivity.CLEARENT_RESULT_CODE, 0) & SdkUiResultCode.TransactionSuccess.getValue()) != 0)
                    }
                }
            }
    );

    private ActivityMainBinding binding;
    private Boolean transactionOngoing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupSdkListener();
        setupClickListeners();
    }

    private void setupSdkListener() {
        cw.setListener(ClearentDataSource.INSTANCE);
    }

    private void setupClickListeners() {
        binding.pairReaderButton.setOnClickListener(
                view -> startSdkActivityForResult(new ClearentAction.Pairing())
        );
        binding.readersListButton.setOnClickListener(
                view -> startSdkActivityForResult(new ClearentAction.DevicesList())
        );
        binding.startTransactionButton.setOnClickListener(
                view -> startSdkActivityForResult(
                        new ClearentAction.Transaction(
                                new PaymentInfo(
                                        Double.parseDouble(Objects.requireNonNull(binding.chargeAmountEditText.getText()).toString()),
                                        null, null, null, null, null, null, null
                                ),
                                false,
                                true,
                                binding.cardReaderSwitch.isChecked() ? PaymentMethod.CARD_READER : PaymentMethod.MANUAL_ENTRY
                        )
                )
        );
    }

    private void startSdkActivityForResult(ClearentAction clearentAction) {
        if (transactionOngoing)
            return;

        transactionOngoing = true;

        // Now we create an intent to start the ClearentSDKActivity activity
        final Intent intent = new Intent(this, ClearentSDKActivity.class);
        clearentAction.prepareIntent(intent);

        activityLauncher.launch(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cw.removeListener();
    }
}