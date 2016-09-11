package no.test.qr;


import com.google.zxing.*;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;



public class Scanner {

    private Logger logger = LoggerFactory.getLogger(Scanner.class);
    private static final String FILE_NAME = "/run/shm/scan.png";

    public static void main(String[] args) {
        while(true) {
            new Scanner().scan();
        }
    }

    public String scan() {
        String result = null;
        try {
            Result r = new QRCodeReader().decode(acquireBitmapFromCamera());
            result = r.getText();
            logger.info("Scan Decode is successful: " + result);

            System.out.println(result);

        } catch (NotFoundException e) {
            logger.error("QR Code was not found in the image. It might have been partially detected but could not be confirmed.");
        } catch (ChecksumException e) {
            logger.error("QR Code was successfully detected and decoded, but was not returned because its checksum feature failed.");
        } catch (FormatException e) {
            logger.error("QR Code was successfully detected, but some aspect of the content did not conform to the barcode's format rules. This could have been due to a mis-detection.");
        } catch (InterruptedException e) {
           logger.error("Error acquiring bitmap", e);
        } catch (IOException e) {
            logger.error("I/O error acquiring bitmap: {}", e.getMessage());
        } catch (Exception e) {
            logger.error("error acquiring bitmap", e);
        }

        return result;
    }

    private BinaryBitmap acquireBitmapFromCamera() throws InterruptedException, IOException {

        getImageFromCamera();

        File imageFile = new File(FILE_NAME);

        logger.trace("Reading file:" + FILE_NAME + " for  QR code");
        BufferedImage image = ImageIO.read(imageFile);
        int[] pixels = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());
        RGBLuminanceSource source = new RGBLuminanceSource(image.getWidth(), image.getHeight(), pixels);
        return new BinaryBitmap(new HybridBinarizer(source));
    }

    private void getImageFromCamera() throws IOException, InterruptedException {
        String cmd = "raspistill --timeout 5 --output " + FILE_NAME + " --width 400 --height 300 --nopreview";
        logger.trace("Executing: " + cmd);
        Process process = Runtime.getRuntime().exec(cmd);

        int code = process.waitFor();
        logger.trace("Exit code: " + code);
    }
}