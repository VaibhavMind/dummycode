package com.payasia.web.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;

public final class PayAsiaCaptchaServletUtil {

	public static void writeImage(HttpServletResponse response, BufferedImage bi) {
		response.setHeader("Cache-Control", "private,no-cache,no-store");
		response.setContentType("image/png"); // PNGs allow for transparency.
												// JPGs do not.

		write(response, bi);

	}

	public static void write(HttpServletResponse response, BufferedImage bi) {

		try {
			OutputStream os = response.getOutputStream();
			ByteArrayOutputStream byteArrayOS = getByteArrayStream(bi);
			byteArrayOS.writeTo(os);
			os.flush();
			byteArrayOS.close();

		} catch (IOException e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
	}

	protected static ByteArrayOutputStream getByteArrayStream(BufferedImage img)
			throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();

		ImageIO.write(img, "png", bos);

		return bos;

		/*
		 * final ImageTypeSpecifier type = ImageTypeSpecifier
		 * .createFromRenderedImage(img); final Iterator<ImageWriter> iwi =
		 * ImageIO.getImageWriters(type, "png"); if (iwi == null ||
		 * !iwi.hasNext()) throw new IllegalStateException(
		 * "No image writer found for format = png"); final ImageWriter iw =
		 * iwi.next(); try { ImageWriteParam iwp = iw.getDefaultWriteParam();
		 * 
		 * final ImageOutputStream ios = ImageIO
		 * .createImageOutputStream(ostream); try { iw.setOutput(ios);
		 * iw.write(null, new IIOImage(img, null, null), iwp); } finally {
		 * ios.close(); } } finally { iw.dispose(); }
		 */
	}
}
