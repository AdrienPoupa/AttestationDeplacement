package com.poupa.attestationdeplacement.generator;

import android.graphics.Bitmap;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.WHITE;

/**
 * Generate the QR code
 */
public class QrCodeGenerator {
    private final Attestation attestation;
    private final File fileDir;

    public QrCodeGenerator(Attestation attestation, File fileDir) {
        this.attestation = attestation;
        this.fileDir = fileDir;
    }

    /**
     * Save the QR code file
     * @throws WriterException
     * @throws IOException
     */
    String generateBigQrCode(String text) throws WriterException, IOException {
        Bitmap bitmapQrCode = this.generateQrCode(text, 330, 330);

        File qrCodeFile = new File(fileDir + "/" + attestation.getId() + ".png");

        FileOutputStream ostream = new FileOutputStream(qrCodeFile);

        bitmapQrCode.compress(Bitmap.CompressFormat.PNG, 92, ostream);

        ostream.close();

        return fileDir + "/" + attestation.getId() + ".png";
    }

    /**
     * Append the small QR code to page 1
     * @throws WriterException
     */
    byte[] generateSmallQrCode(String text, int size) throws WriterException {
        Bitmap smallBitmapQrCode = this.generateQrCode(text, size, size);

        return convertBitmapToByteArray(smallBitmapQrCode);
    }


    /**
     * Convert bitmap to array
     * @param bitmap
     * @return
     */
    public byte[] convertBitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream streamQrCode = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 92, streamQrCode);
        byte[] byteArray = streamQrCode.toByteArray();
        bitmap.recycle();

        return byteArray;
    }

    /**
     * Generates the QR Code from a string
     * @param str
     * @param width
     * @param height
     * @return
     * @throws WriterException
     */
    public Bitmap generateQrCode(String str, int width, int height) throws WriterException {
        BitMatrix result;
        try {
            Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
            hints.put(EncodeHintType.MARGIN, 0);
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            result = new MultiFormatWriter().encode(str,
                    BarcodeFormat.QR_CODE, width, height, hints);
        } catch (IllegalArgumentException iae) {
            // Unsupported format
            return null;
        }
        int w = result.getWidth();
        int h = result.getHeight();
        int[] pixels = new int[w * h];
        for (int y = 0; y < h; y++) {
            int offset = y * w;
            for (int x = 0; x < w; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, w, h);
        return bitmap;
    }
}
