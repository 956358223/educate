package com.sora.common.utils;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class CodeTools {

    private static final String QR_CHARSET = "UTF-8";
    private static final String QR_SUFFIX = "png";
    private static final Integer QR_SIZE = 300;
    private static final Integer LOGO_WIDTH = 60;
    private static final Integer LOGO_HEIGHT = 60;

    private static BufferedImage create(String content, String path, boolean compress) throws WriterException, IOException {
        HashMap hints = new HashMap();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
        hints.put(EncodeHintType.CHARACTER_SET, QR_CHARSET);
        BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, QR_SIZE, QR_SIZE, hints);
        int width = bitMatrix.getWidth();
        int height = bitMatrix.getHeight();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
            }
        }
        if (path == null || "".equals(path)) {
            return image;
        }
        CodeTools.insert(image, path, compress);
        return image;
    }

    /**
     * 插入图片
     *
     * @param source   图片源
     * @param path     文件路径
     * @param compress 是否压缩
     * @throws IOException
     */
    private static void insert(BufferedImage source, String path, boolean compress) throws IOException {
        File file = new File(path);
        if (!file.exists()) {
            System.err.println("" + path + " 该文件不存在！");
            return;
        }
        Image src = ImageIO.read(new File(path));
        int width = src.getWidth(null);
        int height = src.getHeight(null);
        if (compress) {
            if (width > LOGO_WIDTH) {
                width = LOGO_WIDTH;
            }
            if (height > LOGO_HEIGHT) {
                height = LOGO_HEIGHT;
            }
            Image image = src.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            BufferedImage tag = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics g = tag.getGraphics();
            g.drawImage(image, 0, 0, null); // 绘制缩小后的图
            g.dispose();
            src = image;
        }
        Graphics2D graph = source.createGraphics();
        int x = (QR_SIZE - width) / 2;
        int y = (QR_SIZE - height) / 2;
        graph.drawImage(src, x, y, width, height, null);
        Shape shape = new RoundRectangle2D.Float(x, y, width, width, 6, 6);
        graph.setStroke(new BasicStroke(3f));
        graph.draw(shape);
        graph.dispose();
    }

    /**
     * 生成二维码(内嵌LOGO)
     *
     * @param content  内容
     * @param source   logo地址
     * @param target   存放目录
     * @param compress 是否压缩logo
     * @throws Exception
     */
    public static void encode(String content, String source, String target, boolean compress) throws Exception {
        BufferedImage image = CodeTools.create(content, source, compress);
        mkdirs(target);
        String file = new Random().nextInt(99999) + ".png";
        ImageIO.write(image, QR_SUFFIX, new File(target + "/" + file));
    }

    /**
     * 当文件夹不存在时，mkdirs会自动创建多层目录，区别于mkdir．(mkdir如果父目录不存在则会抛出异常)
     *
     * @param target 存放目录
     */
    public static void mkdirs(String target) {
        File file = new File(target);
        if (!file.exists() && !file.isDirectory()) {
            file.mkdirs();
        }
    }

    /**
     * 生成二维码(内嵌LOGO),没有压缩
     *
     * @param content 内容
     * @param source  LOGO地址
     * @param target  存储地址
     * @throws Exception
     */
    public static void encode(String content, String source, String target) throws Exception {
        CodeTools.encode(content, source, target, false);
    }

    /**
     * 生成二维码，无内嵌logo
     *
     * @param content  内容
     * @param target   存储地址
     * @param compress 是否压缩LOGO
     * @throws Exception
     */
    public static void encode(String content, String target, boolean compress) throws Exception {
        CodeTools.encode(content, null, target, compress);
    }

    /**
     * 生成二维码
     *
     * @param content 内容
     * @param target  存储地址
     * @throws Exception
     */
    public static void encode(String content, String target) throws Exception {
        CodeTools.encode(content, null, target, false);
    }

    /**
     * 生成二维码(内嵌LOGO)
     *
     * @param content  内容
     * @param source   LOGO地址
     * @param output   输出流
     * @param compress 是否压缩LOGO
     * @throws Exception
     */
    public static void encode(String content, String source, OutputStream output, boolean compress) throws Exception {
        BufferedImage image = CodeTools.create(content, source, compress);
        ImageIO.write(image, QR_SUFFIX, output);
    }

    /**
     * 生成二维码
     *
     * @param content 内容
     * @param output  输出流
     * @throws Exception
     */
    public static void encode(String content, OutputStream output) throws Exception {
        CodeTools.encode(content, null, output, false);
    }

    /**
     * 解析二维码
     * 按文件参数解析
     *
     * @param file 二维码图片
     * @return
     * @throws Exception
     */
    public static String decode(File file) throws Exception {
        BufferedImage image;
        image = ImageIO.read(file);
        if (image == null) {
            return null;
        }
        BufferedImageLuminanceSource source = new BufferedImageLuminanceSource(image);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
        Result result;
        HashMap hints = new HashMap();
        hints.put(DecodeHintType.CHARACTER_SET, QR_CHARSET);
        result = new MultiFormatReader().decode(bitmap, hints);
        String resultStr = result.getText();
        return resultStr;
    }

    /**
     * 解析二维码
     * 按二维码图片地址解析
     *
     * @param path 二维码图片地址
     * @return 不是二维码的内容返回null, 是二维码直接返回识别的结果
     * @throws Exception
     */
    public static String decode(String path) throws Exception {
        return CodeTools.decode(new File(path));
    }

    public static byte[] create(String content) throws WriterException, IOException {
        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix matrix = writer.encode(content, BarcodeFormat.QR_CODE, QR_SIZE, QR_SIZE);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(matrix, "PNG", stream);
        byte[] bytes = stream.toByteArray();
        return bytes;
    }

    @SuppressWarnings("all")
    public static void create(String contents, String pathname) throws WriterException, IOException {
        MultiFormatWriter multiFormatWrite = new MultiFormatWriter();
        Map hints = new HashMap();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
        BitMatrix bitMatrix = multiFormatWrite.encode(contents, BarcodeFormat.QR_CODE, 240, 240, hints);
        File file = new File(pathname);
        if (!file.exists()) {
            file.mkdirs();
        }
        System.out.println(file.getPath());
        MatrixToImageWriter.writeToFile(bitMatrix, "PNG", file);
    }

    public static void main(String[] args) throws Exception {
        CodeTools.encode("感谢有你，感谢相遇。", "src/main/resources/static/favicon.png", "src/main/resources/static");
    }

}
