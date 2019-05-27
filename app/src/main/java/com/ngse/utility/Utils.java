package com.ngse.utility;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.widget.ImageView;

import com.bitshares.bitshareswallet.ColorUtils;
import com.bitshares.bitshareswallet.wallet.BitsharesWalletWraper;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.evrazcoin.evrazwallet.R;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Utils {
    static DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
    static DecimalFormat decimalFormat3 = new DecimalFormat("#.###", symbols);
    static DecimalFormat decimalFormat5 = new DecimalFormat("#.#####", symbols);
    static DecimalFormat decimalFormat8 = new DecimalFormat("#.########", symbols);

    public static double sumDouble(double value1, double value2) {
        double sum = 0.0;
        String value1Str = Double.toString(value1);
        int decimalIndex = value1Str.indexOf(".");
        int value1Precision = 0;
        if (decimalIndex != -1) {
            value1Precision = (value1Str.length() - 1) - decimalIndex;
        }

        String value2Str = Double.toString(value2);
        decimalIndex = value2Str.indexOf(".");
        int value2Precision = 0;
        if (decimalIndex != -1) {
            value2Precision = (value2Str.length() - 1) - decimalIndex;
        }

        int maxPrecision = value1Precision > value2Precision ? value1Precision : value2Precision;
        sum = value1 + value2;
        String s = String.format(Locale.ENGLISH, "%." + maxPrecision + "f", sum);
        sum = Double.parseDouble(s);
        return sum;
    }

    public static Double parseDouble(String x) {
        Double a;
        try {
            a = parseDecimal(x);
        } catch (Exception ParseException) {
            a = (double) 0;
        }
        return a;
    }

    public static double parseDecimal(String input) throws ParseException {
        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
        ParsePosition parsePosition = new ParsePosition(0);
        Number number = numberFormat.parse(input, parsePosition);

        if (parsePosition.getIndex() != input.length()) {
            throw new ParseException("Invalid input", parsePosition.getIndex());
        }

        return number.doubleValue();
    }

    public static String formatDecimal(double num) {
        if (Double.compare(.00001, num) >   0) {
            return decimalFormat8.format(num);
        } else if (Double.compare(.001, num) > 0) {
            return decimalFormat5.format(num);
        } else {
            return decimalFormat3.format(num);
        }
    }

    public static  void generateQR(KProgressHUD progressHUD, Activity activity) {
        progressHUD.show();
        new Thread(() -> {
            String data = "btswallet" + BitsharesWalletWraper.getInstance().get_account().name + "'0' ";
            QRCodeWriter writer = new QRCodeWriter();
            try {
                BitMatrix bitMatrix = writer.encode(data, BarcodeFormat.QR_CODE, 1000, 1000);
                int width = bitMatrix.getWidth();
                int height = bitMatrix.getHeight();
                Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
                for (int x = 0; x < width; x++) {
                    for (int y = 0; y < height; y++) {
                        bmp.setPixel(x, y, bitMatrix.get(x, y) ? ColorUtils.getMainColor(activity) : Color.WHITE);
                    }
                }
                new Handler(Looper.getMainLooper()).post(() -> {
                    ImageView imageView = new ImageView(activity);
                    imageView.setImageBitmap(bmp);

                    AlertDialog dialog = new AlertDialog.Builder(activity   )
                            .setView(imageView)
                            .setTitle("")
                            .setNeutralButton(R.string.label_ok,null)
                            .setPositiveButton(R.string.share, (dialogInterface, i) -> {
                                shareImage(activity, bmp);
                            })
                            .create();

                    dialog.show();
                    progressHUD.dismiss();
                });
            } catch (WriterException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static void shareImage(Context context,Bitmap bitmap){
        String file_path = DebugUtility.getTempPath(context,"bitshare_qrcode");
        File dir = new File(file_path);
        if(!dir.exists())
            dir.mkdirs();

        String format = new SimpleDateFormat("yyyyMMddHHmmss",
                java.util.Locale.getDefault()).format(new Date());

        File file = new File(dir, format + ".png");
        FileOutputStream fOut;
        try {
            fOut = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 85, fOut);
            fOut.flush();
            fOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Uri uri = Uri.fromFile(file);
        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.setType("image/*");
        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "");
        intent.putExtra(android.content.Intent.EXTRA_TEXT, "");
        intent.putExtra(Intent.EXTRA_STREAM, uri);

        context.startActivity(Intent.createChooser(intent,"Sharing something"));
    }
}
