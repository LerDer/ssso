package com.sbt.util;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;
import java.util.UUID;
import javax.imageio.ImageIO;
import lombok.Data;
import sun.misc.BASE64Encoder;

/**
 * @author lww
 * @date 2019-04-12 11:41 PM
 */
public class ValidateCodeUtil {

    public static CodeRes createCode() {
        //验证码范围,去掉0(数字)和O(拼音)容易混淆的(小写的1和L也可以去掉,大写不用了)
        char[] codeSequence = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J',
                'K', 'L', 'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W',
                'X', 'Y', 'Z', '2', '3', '4', '5', '6', '7', '8', '9'};
        int x, fontHeight, codeY, red, green, blue;
        //图片的宽度。
        Integer width = 160;
        //验证码字符个数
        Integer codeCount = 4;
        //每个字符的宽度(左右各空出一个字符)
        x = width / (codeCount + 2);
        //图片的高度。
        Integer height = 40;
        //字体的高度
        fontHeight = height - 2;
        codeY = height - 4;
        // 图像buffer
        BufferedImage buffImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = buffImg.createGraphics();
        /*// 将图像背景填充为白色
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);*/
        // 增加下面代码使得背景透明
        buffImg = g.getDeviceConfiguration().createCompatibleImage(width, height, Transparency.TRANSLUCENT);
        g.dispose();
        g = buffImg.createGraphics();
        // 背景透明代码结束
        // 画图BasicStroke是JDK中提供的一个基本的画笔类,我们对他设置画笔的粗细，就可以在drawPanel上任意画出自己想要的图形了。
        g.setColor(new Color(255, 0, 0));
        g.setStroke(new BasicStroke(1f));
        g.fillRect(128, 128, width, height);
        // 生成随机数
        Random random = new Random();
        //设置字体类型、字体大小、字体样式　
        Font font = new Font("微软雅黑", Font.PLAIN, fontHeight);
        g.setFont(font);

        //验证码干扰线数
        Integer lineCount = 150;
        for (int i = 0; i < lineCount; i++) {
            // 设置随机开始和结束坐标
            //x坐标开始
            int xs = random.nextInt(width);
            //y坐标开始
            int ys = random.nextInt(height);
            //x坐标结束
            int xe = xs + random.nextInt(width / 8);
            //y坐标结束
            int ye = ys + random.nextInt(height / 8);
            // 产生随机的颜色值，让输出的每个干扰线的颜色值都将不同。
            red = random.nextInt(255);
            green = random.nextInt(255);
            blue = random.nextInt(255);
            g.setColor(new Color(red, green, blue));
            g.drawLine(xs, ys, xe, ye);
        }

        // randomCode记录随机产生的验证码
        StringBuffer randomCode = new StringBuffer();
        // 随机产生codeCount个字符的验证码。
        for (int i = 0; i < codeCount; i++) {
            String strRand = String.valueOf(codeSequence[random.nextInt(codeSequence.length)]);
            // 产生随机的颜色值，让输出的每个字符的颜色值都将不同。
            red = random.nextInt(255);
            green = random.nextInt(255);
            blue = random.nextInt(255);
            //指定某种颜色
            //g.setColor(new Color(252, 145, 83));
            g.setColor(new Color(red, green, blue));
            g.drawString(strRand, (i + 1) * x, codeY);
            // 将产生的四个随机数组合在一起。
            randomCode.append(strRand);
        }
        // 将四位数字的验证码保存到Session中。
        String code = randomCode.toString();
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(buffImg, "png", baos);
            byte[] bytes = baos.toByteArray();
            BASE64Encoder encoder = new BASE64Encoder();
            String pngBase64 = encoder.encodeBuffer(bytes).trim();
            pngBase64 = pngBase64.replaceAll("\n", "").replaceAll("\r", "");
            pngBase64 = "data:image/png;base64," + pngBase64;
            CodeRes res = new CodeRes();
            res.setBase64(pngBase64);
            res.setKey(UUID.randomUUID().toString().replace("-", ""));
            res.setCode(code);
            return res;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Data
    private static class CodeRes {

        private String base64;
        private String key;
        private String code;

    }
}
