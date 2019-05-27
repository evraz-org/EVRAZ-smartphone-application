package com.ngse.ui.main;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bitshares.bitshareswallet.BaseFragment;
import com.bitshares.bitshareswallet.BitsharesApplication;
import com.bitshares.bitshareswallet.OnFragmentInteractionListener;
import com.bitshares.bitshareswallet.ScannerActivity;
import com.bitshares.bitshareswallet.room.BitsharesAsset;
import com.bitshares.bitshareswallet.room.BitsharesAssetObject;
import com.bitshares.bitshareswallet.room.BitsharesBalanceAsset;
import com.bitshares.bitshareswallet.viewmodel.SendViewModel;
import com.bitshares.bitshareswallet.wallet.BitsharesWalletWraper;
import com.bitshares.bitshareswallet.wallet.account_object;
import com.bitshares.bitshareswallet.wallet.asset;
import com.bitshares.bitshareswallet.wallet.common.ErrorCode;
import com.bitshares.bitshareswallet.wallet.exception.ErrorCodeException;
import com.bitshares.bitshareswallet.wallet.exception.NetworkStatusException;
import com.bitshares.bitshareswallet.wallet.fc.crypto.sha256_object;
import com.bitshares.bitshareswallet.wallet.graphene.chain.signed_transaction;
import com.bituniverse.network.Status;
import com.bituniverse.utils.NumericUtil;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.evrazcoin.evrazwallet.R;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.bitsharesmunich.graphenej.Invoice;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.schedulers.Schedulers;

import static android.app.Activity.RESULT_OK;
import static com.ngse.utility.Utils.*;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SendFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SendFragment extends BaseFragment implements View.OnTouchListener,Handler.Callback  {

    private static final int CLICK_ON_WEBVIEW = 1;
    private static final int CLICK_ON_URL = 2;
    @BindView(R.id.editTextTo)
    EditText mEditTextTo;
    @BindView(R.id.textViewToId)
    TextView mTextViewId;
    @BindView(R.id.editTextQuantity)
    EditText mEditTextQuantitiy;
    @BindView(R.id.editTextAvailable)
    TextView mEditTextAvailable;
    @BindView(R.id.editTextFee)
    TextView editTextFee;
    @BindView(R.id.qrScan)
    ImageView qrScan;

    private List<String> symbolList;

    private KProgressHUD mProcessHud;
    private Spinner mSpinner;
    private Spinner feeSpinner;
    private OnFragmentInteractionListener mListener;
    private View mView;
    private Handler mHandler = new Handler();
    private ArrayAdapter<String> feeAdapter;
    private ArrayAdapter<String> assetAdapter;


    public SendFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SendFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SendFragment newInstance(String param1, String param2) {
        return new SendFragment();
    }

    public static void hideSoftKeyboard(View view, Context context) {
        if (view != null && context != null) {
            InputMethodManager imm = (InputMethodManager) context
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static void showSoftKeyboard(View view, Context context) {
        if (view != null && context != null) {
            InputMethodManager imm = (InputMethodManager) context
                    .getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, 0);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.new_fragment_send, container, false);
        ButterKnife.bind(this, mView);

        EditText editTextFrom = (EditText) mView.findViewById(R.id.editTextFrom);

        String strName = BitsharesWalletWraper.getInstance().get_account().name;
        editTextFrom.setText(strName);

        sha256_object.encoder encoder = new sha256_object.encoder();
        encoder.write(strName.getBytes());

        WebView webViewFrom = (WebView) mView.findViewById(R.id.webViewAvatarFrom);
        loadWebView(webViewFrom, 40, encoder.result().toString());
        webViewFrom.setOnTouchListener(this);

        TextView textView = (TextView) mView.findViewById(R.id.textViewFromId);
        String strId = String.format(
                Locale.ENGLISH, "#%d",
                BitsharesWalletWraper.getInstance().get_account().id.get_instance()
        );
        textView.setText(strId);

        mProcessHud = KProgressHUD.create(getActivity())
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please Wait")
                .setCancellable(false)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);

        mView.findViewById(R.id.btn_send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processSendClick(mView);
            }
        });

        mEditTextTo.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                final String strText = mEditTextTo.getText().toString();
                if (hasFocus == false) {
                    processGetTransferToId(strText, mTextViewId);
                }
            }
        });

        final WebView webViewTo = (WebView) mView.findViewById(R.id.webViewAvatarTo);
        mEditTextTo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                sha256_object.encoder encoder = new sha256_object.encoder();
                encoder.write(s.toString().getBytes());
                loadWebView(webViewTo, 40, encoder.result().toString());
                processGetTransferToId(mEditTextTo.getText().toString(), mTextViewId);
            }
        });

        qrScan.setOnClickListener(v -> {
            startActivityForResult(new Intent(getActivity(), ScannerActivity.class), ScannerActivity.REQUEST_CODE);
        });


        mEditTextQuantitiy.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    return;
                }

                String strQuantity = mEditTextQuantitiy.getText().toString();
                if (TextUtils.isEmpty(strQuantity)) {
                    return;
                }

                double quantity = NumericUtil.parseDouble(strQuantity, -1.0D);
                if (Double.compare(quantity, 0.0D) < 0) {   // 越界或者格式错误
                    mEditTextQuantitiy.setText("0");
                    // TODO toast
                    return;
                }

                processCalculateFee();
            }
        });

        mSpinner = mView.findViewById(R.id.spinner_unit);
        feeSpinner = mView.findViewById(R.id.spinner_fee_unit);

        SendViewModel viewModel = ViewModelProviders.of(this).get(SendViewModel.class);
        viewModel.getBalancesList().observe(this, bitsharesBalanceAssetList -> {
            symbolList = new ArrayList<>();
            for (BitsharesBalanceAsset bitsharesBalanceAsset : bitsharesBalanceAssetList) {
                symbolList.add(bitsharesBalanceAsset.quote);
            }

            String selectedItem = (String) mSpinner.getSelectedItem();
            selectedItem = selectedItem == null ? getString(R.string.label_evraz) : selectedItem;

            assetAdapter = new ArrayAdapter<String>(
                    getActivity(),
                    R.layout.new_custom_spinner_item,
                    symbolList
            ) {
                @NonNull
                @Override
                public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    view.findViewById(R.id.text1).setSelected(true);
                    return view;
                }
            };

            assetAdapter.setDropDownViewResource(R.layout.new_spinner_style);
            mSpinner.setAdapter(assetAdapter);

            int position = assetAdapter.getPosition(selectedItem);
            mSpinner.setSelection(position);

            feeAdapter = new ArrayAdapter<String>(
                    getActivity(),
                    R.layout.new_custom_spinner_item,
                    symbolList
            );

            selectedItem = (String) feeSpinner.getSelectedItem();
            selectedItem = selectedItem == null ? getString(R.string.label_evraz) : selectedItem;

            feeAdapter.setDropDownViewResource(R.layout.new_spinner_style);
            feeSpinner.setAdapter(feeAdapter);

            position = feeAdapter.getPosition(selectedItem);
            feeSpinner.setSelection(position);
        });

        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                processCalculateFee();
                viewModel.changeBalanceAsset((String) mSpinner.getSelectedItem());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        feeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                processCalculateFee();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        viewModel.getAvaliableBalance().observe(this, bitsharesAssetResource -> {
            String str = "";
            if (bitsharesAssetResource.status == Status.SUCCESS) {
                BitsharesAsset bitsharesAsset = bitsharesAssetResource.data;
                if (bitsharesAsset != null) {
                    str = decimalFormat.format((double) bitsharesAsset.amount / bitsharesAsset.precision);
                } else {
                    str = "0";
                }
            }

            mEditTextAvailable.setText(str);
        });

        mEditTextAvailable.setOnClickListener(view -> {
            if (mSpinner.getSelectedItem() == feeSpinner.getSelectedItem()) {
                Double available = getAvailable();
                Double fee = getFee();
                double number = Math.max(available - fee, 0);
                mEditTextQuantitiy.setText(decimalFormat.format(number));
            } else {
                mEditTextQuantitiy.setText(mEditTextAvailable.getText());
            }
        });

        mEditTextQuantitiy.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                processLock();
            }
        });

        editTextFee.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                processLock();
            }
        });

        return mView;
    }

    private void processLock() {
        mView.findViewById(R.id.btn_send).setEnabled(getSendAvailable());

        if (getSendAvailable()) {
            mEditTextAvailable.setTextColor(getResources().getColor(R.color.beige_color));
            mView.findViewById(R.id.btn_send).setBackground(getResources().getDrawable(R.drawable.btn_green_background));
        } else {
            mEditTextAvailable.setTextColor(getResources().getColor(R.color.label_red));
            mView.findViewById(R.id.btn_send).setBackground(getResources().getDrawable(R.drawable.btn_red_background));
        }
    }

    private boolean getSendAvailable() {
        Double available = getAvailable();
        Double amount = getAmount();
        Double fee = getFee();
        if (mSpinner.getSelectedItem() == feeSpinner.getSelectedItem()) {
            double diff = available - sumDouble(amount, fee);
            return diff >= 0.0d;
        } else {
            double diff = available - amount;
            return diff >= 0.0d;
        }
    }

    private Double getAvailable() {
        return parseDouble(mEditTextAvailable.getText().toString());
    }

    private Double getFee() {
        return parseDouble(editTextFee.getText().toString());
    }

    private Double getAmount() {
        return parseDouble(mEditTextQuantitiy.getText().toString());
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == ScannerActivity.REQUEST_CODE && resultCode == RESULT_OK) {
            String scanData = data.getStringExtra("DATA");
            if(scanData.startsWith("btswallet")) {
                String[] splited = scanData.substring(9).split("'");
                mEditTextTo.setText(splited[0]);
                mEditTextQuantitiy.setText(splited[1]);
                int index = symbolList.indexOf(splited[2]);
                if (index >= 0) {
                    mSpinner.setSelection(index);
                    feeSpinner.setSelection(index);
                } else {
                    Toast.makeText(getActivity(), R.string.no_req_token, Toast.LENGTH_SHORT).show();
                }
            } else {
                Invoice invoice = Invoice.fromQrCode(scanData);
                mEditTextTo.setText(invoice.getTo());
                mEditTextQuantitiy.setText(String.valueOf(invoice.getLineItems()[0].getPrice()));
                String asset = invoice.getCurrency().toUpperCase();
                if(asset.startsWith("BIT")) asset = asset.substring(3);
                int index = symbolList.indexOf(asset);
                if (index >= 0) {
                    mSpinner.setSelection(index);
                    feeSpinner.setSelection(index);
                } else {
                    Toast.makeText(getActivity(), R.string.no_req_token, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onShow() {
        super.onShow();
        if (mEditTextTo.getText().length() > 0) {
            processGetTransferToId(mEditTextTo.getText().toString(), mTextViewId);
        }
        notifyUpdate();
    }

    @Override
    public void onHide() {
        super.onHide();
        hideSoftKeyboard(mEditTextTo, getActivity());
    }

    private void processTransfer(final String strFrom,
                                 final String strTo,
                                 final String strQuantity,
                                 final String strSymbol,
                                 final String strMemo, String strFeeSymbol) {
        mProcessHud.show();
        Flowable.just(0)
                .subscribeOn(Schedulers.io())
                .map(integer -> {
                    signed_transaction signedTransaction = BitsharesWalletWraper.getInstance().transfer(
                            strFrom,
                            strTo,
                            strQuantity,
                            strSymbol,
                            strMemo,
                            strFeeSymbol
                    );
                    return signedTransaction;
                }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(signedTransaction -> {
                    if (getActivity() != null && getActivity().isFinishing() == false) {
                        mProcessHud.dismiss();
                        if (signedTransaction != null) {
                            Toast.makeText(getActivity(), "Sent Successfully", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getActivity(), "Fail to send", Toast.LENGTH_LONG).show();
                        }
                    }

                }, throwable -> {
                    if (throwable instanceof NetworkStatusException) {
                        throwable.printStackTrace();
                        if (getActivity() != null && getActivity().isFinishing() == false) {
                            mProcessHud.dismiss();
                            Toast.makeText(getActivity(), throwable.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    } else {
                        throw Exceptions.propagate(throwable);
                    }
                });
    }

    private void processSendClick(final View view) {
        if (BitsharesWalletWraper.getInstance().is_locked()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater layoutInflater = getActivity().getLayoutInflater();
            final View viewGroup = layoutInflater.inflate(R.layout.dialog_password_confirm, null);
            builder.setPositiveButton(
                    R.string.password_confirm_button_confirm,
                    null);

            builder.setNegativeButton(
                    R.string.password_confirm_button_cancel,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }
            );
            builder.setView(viewGroup);
            final AlertDialog dialog = builder.create();
            dialog.show();

            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EditText editText = (EditText) viewGroup.findViewById(R.id.editTextPassword);
                    String strPassword = editText.getText().toString();
                    int nRet = BitsharesWalletWraper.getInstance().unlock(strPassword);
                    if (nRet == 0) {
                        dialog.dismiss();
                        String strFrom = ((EditText) view.findViewById(R.id.editTextFrom)).getText().toString();
                        String strTo = ((EditText) view.findViewById(R.id.editTextTo)).getText().toString();
                        String strQuantity = ((EditText) view.findViewById(R.id.editTextQuantity)).getText().toString();
                        String strSymbol = (String) mSpinner.getSelectedItem();
                        String strFeeSymbol = (String) feeSpinner.getSelectedItem();
                        String strMemo = ((EditText) view.findViewById(R.id.editTextMemo)).getText().toString();
                        processTransfer(strFrom, strTo, strQuantity, strSymbol, strMemo, strFeeSymbol);
                    } else {
                        viewGroup.findViewById(R.id.textViewPasswordInvalid).setVisibility(View.VISIBLE);
                    }
                }
            });

        } else {
            String strFrom = ((EditText) view.findViewById(R.id.editTextFrom)).getText().toString();
            String strTo = ((EditText) view.findViewById(R.id.editTextTo)).getText().toString();
            String strQuantity = ((EditText) view.findViewById(R.id.editTextQuantity)).getText().toString();
            String strSymbol = (String) mSpinner.getSelectedItem();
            String strFeeSymbol = (String) feeSpinner.getSelectedItem();
            String strMemo = ((EditText) view.findViewById(R.id.editTextMemo)).getText().toString();

            processTransfer(strFrom, strTo, strQuantity, strSymbol, strMemo, strFeeSymbol);
        }
    }

    private void processGetTransferToId(final String strAccount, final TextView textViewTo) {
        Flowable.just(strAccount)
                .subscribeOn(Schedulers.io())
                .map(accountName -> {
                    account_object accountObject = BitsharesWalletWraper.getInstance().get_account_object(accountName);
                    if (accountObject == null) {
                        throw new ErrorCodeException(ErrorCode.ERROR_NO_ACCOUNT_OBJECT, "it can't find the account");
                    }

                    return accountObject;
                }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(accountObject -> {
                    if (getActivity() != null && getActivity().isFinishing() == false) {
                        textViewTo.setText("#" + accountObject.id.get_instance());
                    }
                }, throwable -> {
                    if (throwable instanceof NetworkStatusException || throwable instanceof ErrorCodeException) {
                        if (getActivity() != null && getActivity().isFinishing() == false) {
                            textViewTo.setText("#none");
                        }
                    } else {
                        throw Exceptions.propagate(throwable);
                    }
                });
    }

    @Override
    public void notifyUpdate() {

    }

    private void processCalculateFee() {
        final String strQuantity = ((EditText) mView.findViewById(R.id.editTextQuantity)).getText().toString();
        final String strSymbol = (String) mSpinner.getSelectedItem();
        final String strFeeSymbol = (String) feeSpinner.getSelectedItem();
        final String strMemo = ((EditText) mView.findViewById(R.id.editTextMemo)).getText().toString();

        // 用户没有任何货币，这个symbol会为空，则会出现崩溃，进行该处理进行规避
        if (TextUtils.isEmpty(strQuantity) || TextUtils.isEmpty(strSymbol)) {
            return;
        }

        Flowable.just(0)
                .subscribeOn(Schedulers.io())
                .map(integer -> {
                    asset fee = BitsharesWalletWraper.getInstance().transfer_calculate_fee(
                            strQuantity,
                            strSymbol,
                            strFeeSymbol,
                            strMemo
                    );

                    BitsharesAssetObject assetObject = BitsharesApplication.getInstance()
                            .getBitsharesDatabase().getBitsharesDao().queryAssetObjectById(fee.asset_id.toString());
                    return new Pair<>(fee, assetObject);
                }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(resultPair -> {
                    if (getActivity() != null && getActivity().isFinishing() == false) {
                        processDisplayFee(resultPair.first, resultPair.second);
                    }
                }, throwable -> {
                    if (throwable instanceof NetworkStatusException) {
                        if (getActivity() != null && getActivity().isFinishing() == false) {
                            editTextFee.setText("N/A");
                        }
                    } else {
                        throw Exceptions.propagate(throwable);
                    }
                });
    }

    DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
    DecimalFormat decimalFormat = new DecimalFormat("#.#####", symbols);

    private void processDisplayFee(asset fee, BitsharesAssetObject assetObject) {
        String str = decimalFormat.format((double) fee.amount / assetObject.precision);
        editTextFee.setText(str);
    }

    private void loadWebView(WebView webView, int size, String encryptText) {
        String htmlShareAccountName = "<html><head><style>body,html {margin:0; padding:0; text-align:center;}</style><meta name=viewport content=width=" + size + ",user-scalable=no/></head><body><canvas width=" + size + " height=" + size + " data-jdenticon-hash=" + encryptText + "></canvas><script src=https://cdn.jsdelivr.net/jdenticon/1.3.2/jdenticon.min.js async></script></body></html>";
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.loadData(htmlShareAccountName, "text/html", "UTF-8");
    }

    public void processDonate(String strName, int nAmount, String strUnit) {
        if (isAdded()) {
            mEditTextTo.setText(strName);
            mEditTextQuantitiy.setText(Integer.toString(nAmount));
            mSpinner.setSelection(0);
        }
    }

    private final Handler handler = new Handler(this);
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (view.getId() == R.id.webViewAvatarFrom && motionEvent.getAction() == MotionEvent.ACTION_DOWN){
            handler.sendEmptyMessageDelayed(CLICK_ON_WEBVIEW, 500);
        }
        return false;
    }

    @Override
    public boolean handleMessage(Message msg) {
        if (msg.what == CLICK_ON_URL){
            handler.removeMessages(CLICK_ON_WEBVIEW);
            return true;
        }
        if (msg.what == CLICK_ON_WEBVIEW){
            generateQR(mProcessHud, getActivity());
            return true;
        }
        return false;
    }
}
