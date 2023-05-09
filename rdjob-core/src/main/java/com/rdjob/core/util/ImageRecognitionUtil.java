package com.rdjob.core.util;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.springblade.core.tool.utils.Func;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collections;

/**
 * @description:
 * @author: ChenDong
 * @time: 2022/11/2 10:57
 */
@Slf4j
public class ImageRecognitionUtil {
    private static final String DEFAULT_AVATAR_IMAGE_URL = "http://thirdwx.qlogo.cn/mmopen/9OOCCUbJON7ImRrT0ibuT1G5324S9XkD53RucugPyLRINxiaSoiblHeD289e4lQbq7NXvFqleEWIkyic1p9RVwpvjSastpGtLnku/132";
    private static final String ICON_SUFFIX = ".png";
    private static final String DEFAULT_AVATAR_IMAGE_ICON = "DEFAULT_ICON" + ICON_SUFFIX;
    private static final double NNDR_RATIO = 0.5d;
    private static final Mat DEFAULT_SRC;

    static {
        System.loadLibrary(org.opencv.core.Core.NATIVE_LIBRARY_NAME);
        String defaultIconPath = System.getProperty("java.io.tmpdir") + "DEFAULT_ICON_PNG" + ICON_SUFFIX;
        File file0 = new File(defaultIconPath);
        if (!file0.exists()) {
            File temp = FileUtil.downloadFile(DEFAULT_AVATAR_IMAGE_URL, DEFAULT_AVATAR_IMAGE_ICON);
            file0 = FileUtil.createTempFile("DEFAULT_ICON_PNG", ICON_SUFFIX);
            BufferedImage bi;
            try {
                bi = ImageIO.read(temp);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            //转png，解决opencv: libpng warning: iCCP: known incorrect sRGB profile
            BufferedImage newBufferedImage = new BufferedImage(bi.getWidth(), bi.getHeight(), BufferedImage.TYPE_INT_RGB);
            newBufferedImage.createGraphics().drawImage(bi, 0, 0, Color.WHITE, null);
            try {
                ImageIO.write(newBufferedImage, "png", file0);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        DEFAULT_SRC = Imgcodecs.imread(file0.getAbsolutePath());
    }

    /**
     * 跟默认图比较相似度, 默认值0.75
     * 默认图 <a href="http://thirdwx.qlogo.cn/mmopen/9OOCCUbJON7ImRrT0ibuT1G5324S9XkD53RucugPyLRINxiaSoiblHeD289e4lQbq7NXvFqleEWIkyic1p9RVwpvjSastpGtLnku/132">...</a>
     *
     * @param iconUrl
     * @return
     */
    @SneakyThrows
    public static boolean similarity(String iconUrl) {
        //缓存方式
        //String mdfName = Func.md5Hex(iconUrl);
        //String iconPath = System.getProperty("java.io.tmpdir") + mdfName + ICON_SUFFIX;
        //File iconFile = new File(iconPath);
        //if (!iconFile.exists()) {
        //    iconFile = FileUtil.downloadFile(iconUrl, mdfName + ICON_SUFFIX);
        //}
        //
        //String newIconPath = System.getProperty("java.io.tmpdir") + mdfName + "_png.png";
        //File newFile = new File(newIconPath);
        //if (!newFile.exists()) {
        //    newFile = FileUtil.createTempFile(mdfName + "_png", ".png");
        //    BufferedImage bi = ImageIO.read(iconFile);
        //    if (bi == null) {
        //        log.warn("存在Image.IO无法读取的图片格式,这里暂不做处理: url: {}", iconUrl);
        //        return true;
        //    }
        //    //转png，解决opencv: libpng warning: iCCP: known incorrect sRGB profile
        //    BufferedImage newBufferedImage = new BufferedImage(bi.getWidth(), bi.getHeight(), BufferedImage.TYPE_INT_RGB);
        //    newBufferedImage.createGraphics().drawImage(bi, 0, 0, Color.WHITE, null);
        //    ImageIO.write(newBufferedImage, "png", newFile);
        //}

        //double score = similarityScore(DEFAULT_SRC, Imgcodecs.imread(newFile.getAbsolutePath()));
        //log.info("url: {}, Score: {}, temp: {}", iconUrl, score, newFile.getAbsolutePath());

        // *********************************************************
        //先访问一次，微信头像可能会变
        //HttpUtil.get(iconUrl, null);

        String mdfName = Func.md5Hex(iconUrl);
        String tempFile0 = System.getProperty("java.io.tmpdir") + File.separator + mdfName + ICON_SUFFIX;
        File iconFile = new File(tempFile0);
        if (!iconFile.exists()) {
            iconFile = FileUtil.downloadFile(iconUrl, mdfName + ICON_SUFFIX);
        }

        String tempFile1 = System.getProperty("java.io.tmpdir") + File.separator + mdfName + "_png.png";
        File iconFile2Png = new File(tempFile1);
        if (!iconFile2Png.exists()) {
            iconFile2Png = FileUtil.createTempFile(mdfName + "_png", ICON_SUFFIX);
            BufferedImage bi = ImageIO.read(iconFile);
            if (bi == null) {
                log.warn("存在Image.IO无法读取的图片格式,这里暂不做处理: url: {}", iconUrl);
                return true;
            }
            //转png，解决opencv: libpng warning: iCCP: known incorrect sRGB profile
            BufferedImage newBufferedImage = new BufferedImage(bi.getWidth(), bi.getHeight(), BufferedImage.TYPE_INT_RGB);
            newBufferedImage.createGraphics().drawImage(bi, 0, 0, Color.WHITE, null);
            ImageIO.write(newBufferedImage, "png", iconFile2Png);
            //log.info("original new file {} to png. {}", iconFile.getAbsolutePath(), iconFile2Png.getAbsolutePath());
        } else {
            log.info("cache file, url: {}", iconUrl);
        }

        //Mat src = Imgcodecs.imread(iconFile.getAbsolutePath());
        //byte[] bytes = FileUtils.readFileToByteArray(iconFile);
        //Mat src = Imgcodecs.imdecode(new MatOfByte(bytes), Imgcodecs.IMREAD_COLOR);
        //MatOfByte matOfByte = new MatOfByte();
        //Imgcodecs.imencode(".png",src, matOfByte);
        //Mat src1 = Imgcodecs.imdecode(matOfByte, Imgcodecs.IMREAD_COLOR);
        //
        //String newFile = System.getProperty("java.io.tmpdir") + System.currentTimeMillis() + ".png";
        //Imgcodecs.imwrite(newFile, src1);

        double score = similarityScore(DEFAULT_SRC, Imgcodecs.imread(iconFile2Png.getAbsolutePath()));
        log.info("url: {}, Score: {}, temp1: {}, temp2: {}", iconUrl, score, iconFile.getAbsolutePath(), iconFile2Png.getAbsolutePath());

        //iconFile.delete();
        //iconFile2Png.delete();
        return score > NNDR_RATIO;
    }

    public static boolean similarity(File file) {
        double score = similarityScore(DEFAULT_SRC, Imgcodecs.imread(file.getAbsolutePath()));
        return score > NNDR_RATIO;
    }

    //public static void main(String[] args) {
    //    boolean similarity = similarity("http://thirdwx.qlogo.cn/mmopen/NEumMdMUK0TRsicjwnh65xdaM7rBMJ27O9zKemhLAiaswMfLgv5PvNIf8oiaFE2CdAIKjRoDo9YGyFGen3LXeYNpIiaaYNqUibMlL/132");
    //    System.out.println(similarity);
    //}

    /**
     * 按图片url比较相似度, 默认值0.75
     *
     * @param iconUrl0
     * @param iconUrl1
     * @return
     */
    public static boolean similarity(String iconUrl0, String iconUrl1) {
        return similarity(iconUrl0, iconUrl1, NNDR_RATIO);
    }

    /**
     * 按图片url比较相似度
     *
     * @param iconUrl0
     * @param iconUrl1
     * @param ratio    相似度比较值
     * @return
     */
    public static boolean similarity(String iconUrl0, String iconUrl1, double ratio) {
        String mdfName0 = Func.md5Hex(iconUrl0);
        String mdfName1 = Func.md5Hex(iconUrl1);
        String iconPath0 = System.getProperty("java.io.tmpdir") + File.separator + mdfName0 + ICON_SUFFIX;
        String iconPath1 = System.getProperty("java.io.tmpdir") + File.separator + mdfName1 + ICON_SUFFIX;
        File iconFile0 = new File(iconPath0);
        File iconFile1 = new File(iconPath1);
        if (!iconFile0.exists()) {
            iconFile0 = FileUtil.downloadFile(iconUrl0, mdfName0 + ICON_SUFFIX);
        }
        if (!iconFile1.exists()) {
            iconFile1 = FileUtil.downloadFile(iconUrl1, mdfName1 + ICON_SUFFIX);
        }

        Mat src0 = Imgcodecs.imread(iconFile0.getAbsolutePath());
        Mat src1 = Imgcodecs.imread(iconFile1.getAbsolutePath());
        double score = similarityScore(src0, src1);
        return score > ratio;
    }

    /**
     * 按文件比较相似度, 默认值0.75
     *
     * @param file0
     * @param file1
     * @return
     */
    public static boolean similarity(File file0, File file1) {
        return similarity(file0, file1, NNDR_RATIO);
    }

    /**
     * 按文件比较相似度
     *
     * @param file0
     * @param file1
     * @param ratio 相似度比较值
     * @return
     */
    public static boolean similarity(File file0, File file1, double ratio) {
        if (!file0.exists() || !file1.exists()) {
            throw new IllegalArgumentException("file0 or file1 must exist");
        }
        Mat src0 = Imgcodecs.imread(file0.getAbsolutePath());
        Mat src1 = Imgcodecs.imread(file1.getAbsolutePath());
        double score = similarityScore(src0, src1);
        return score > ratio;
    }

    public static double similarityScore(Mat src1, Mat src2) {
        Mat hvs1 = new Mat();
        Mat hvs2 = new Mat();
        //图片转HSV
        Imgproc.cvtColor(src1, hvs1, Imgproc.COLOR_BGR2HSV);
        Imgproc.cvtColor(src2, hvs2, Imgproc.COLOR_BGR2HSV);

        Mat hist1 = new Mat();
        Mat hist2 = new Mat();

        //直方图计算
        Imgproc.calcHist(Collections.singletonList(hvs1), new MatOfInt(0), new Mat(), hist1, new MatOfInt(255), new MatOfFloat(0, 256));
        Imgproc.calcHist(Collections.singletonList(hvs2), new MatOfInt(0), new Mat(), hist2, new MatOfInt(255), new MatOfFloat(0, 256));

        //图片归一化
        Core.normalize(hist1, hist1, 1, hist1.rows(), Core.NORM_MINMAX, -1, new Mat());
        Core.normalize(hist2, hist2, 1, hist2.rows(), Core.NORM_MINMAX, -1, new Mat());

        //直方图比较
        return Imgproc.compareHist(hist1, hist2, Imgproc.CV_COMP_CORREL);
    }
}
