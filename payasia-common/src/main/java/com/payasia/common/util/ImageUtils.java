package com.payasia.common.util;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

/**
 * The Class ImageUtils.
 */
/**
 * @author vivekjain
 *
 */
public class ImageUtils {

	

	/**
	 * Resize Image: Maintain aspect ratio with given height and width.
	 *
	 * @param image the image
	 * @param newWidth the new width
	 * @param newHeight the new height
	 * @param maintainAspectRatio the maintain aspect ratio
	 * @return the buffered image
	 */
	public static BufferedImage resize(final BufferedImage image, int newWidth,
			int newHeight, final boolean maintainAspectRatio) {
		if (image == null) {
			throw new IllegalArgumentException(
					"\"image\" param cannot be null.");
		}
		
		if (image.getHeight() <= newHeight && image.getWidth() <= newWidth){
			return image;
		}
		
		
		
		final int imageWidth = image.getWidth();
 		final int imageHeight = image.getHeight();
		 
		if (maintainAspectRatio) {
			final double newRatio = (double) newWidth / (double) newHeight;

			final double imageRatio = (double) imageWidth
					/ (double) imageHeight;

			if (newRatio < imageRatio) {
				newHeight = (int) (newWidth / imageRatio);
			} else {
				newWidth = (int) (newHeight * imageRatio);
			}
		}

		final BufferedImage dimg = new BufferedImage(newWidth, newHeight,
				image.getType());
		final Graphics2D g = dimg.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g.drawImage(image, 0, 0, newWidth, newHeight, 0, 0, imageWidth,
				imageHeight, null);
		g.dispose();
		return dimg;
	}
}
