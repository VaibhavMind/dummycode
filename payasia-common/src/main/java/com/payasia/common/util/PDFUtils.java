package com.payasia.common.util;

import java.io.File;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Element;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfGState;
import com.itextpdf.text.pdf.PdfPCell;

/**
 * The Class PDFUtils.
 */
/**
 * @author vivekjain
 *
 */
public class PDFUtils {

	/** The payasia temp path. */
	@Value("#{payasiaptProperties['payasia.temp.path']}")
	private static String PAYASIA_TEMP_PATH;
	
	/**
	 * Draws a rectangle.
	 * 
	 * @param content
	 *            the direct content layer
	 * @param x
	 *            -axis the x
	 * @param y
	 *            -axis the y
	 * @param width
	 *            the width of the rectangle
	 * @param height
	 *            the height of the rectangle
	 */
	public static void drawRectangle(PdfContentByte content, float x, float y,
			float width, float height) {
		drawRectangle(content, x, y, width, height, BaseColor.BLACK);
	}

	/**
	 * Draws rectangle.
	 * 
	 * @param content
	 *            the content
	 * @param x
	 *            -axis the x
	 * @param y
	 *            -axis the y
	 * @param width
	 *            the width
	 */
	public static void drawRectangle(PdfContentByte content, float x, float y,
			float width) {
		float height = 0.50f;
		drawRectangle(content, x, y, width, height, BaseColor.BLACK);
	}

	/**
	 * Draws rectangle.
	 * 
	 * @param content
	 *            the content
	 * @param x
	 *            -axis the x
	 * @param y
	 *            -axis the y
	 * @param width
	 *            the width
	 * @param height
	 *            the height
	 * @param fillColor
	 *            the fill color
	 */
	public static void drawRectangle(PdfContentByte content, float x, float y,
			float width, float height, BaseColor fillColor) {
		content.saveState();
		PdfGState state = new PdfGState();
		content.setGState(state);
		content.setColorFill(fillColor);
		content.setColorStroke(fillColor);
		content.setLineWidth(height);
		content.rectangle(x, y, width, 0);
		content.fillStroke();
		content.restoreState();
	}

	/**
	 * Sets the default cell layout.
	 * 
	 * @param pdfCell
	 *            the new default cell layout
	 */
	public static void setDefaultCellLayout(PdfPCell pdfCell) {
		pdfCell.setBorder(Rectangle.NO_BORDER);
		pdfCell.setBorderWidth(0.5f);
		pdfCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		pdfCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		pdfCell.setUseAscender(true);
		pdfCell.setUseDescender(true);
		pdfCell.setUseBorderPadding(true);
	}

	
	
	/**
	 * Gets the temporary file.
	 * 
	 * @return the temporary file
	 */
	public static File getTemporaryFile(Long employeeId,String dirPath,String fileNamePrefix) {
		String ext = "pdf";
		String dirName = dirPath;
		File dir = new File(dirName);
		 
		if (!dir.exists()) {
			dir.mkdir();
		}
		UUID uuid = UUID.randomUUID();
        String  fileName = fileNamePrefix+"_" +employeeId +"_"+ uuid+"."+ext;
		File file = new File(dir, fileName);
		
		 
		file.deleteOnExit();
		return file;
	}
	
	/**
	 * Gets the temporary file.
	 * 
	 * @return the temporary file
	 */
	public static File getTempFileForTextPayslip(Long companyId,int year,Long month,int part,String employeenumber,String dirPath, String appDeployLocation, String tempPath) {
		String filePath = "";
		if(PayAsiaConstants.PAYASIA_APPLICATION_DEPLOY_LOCATION
				.equalsIgnoreCase(appDeployLocation)){
		filePath = tempPath + "/company/" + companyId + "/"
				+ PayAsiaConstants.COMPANY_DOCUMENT_PAYSLIP + "/" + year
				+ "/"+ employeenumber+ "/";	
		}
		else{
		filePath = dirPath + "/company/" + companyId + "/"
				+ PayAsiaConstants.COMPANY_DOCUMENT_PAYSLIP + "/" + year
				+ "/"+ employeenumber+ "/";
		}
		File folder = new File(filePath);
		if (!folder.exists()) {
			folder.mkdirs();
		}
		String ext = "pdf";
		String dirName = filePath;
		
		File dir = new File(dirName);
		 
		if (!dir.exists()) {
			dir.mkdir();
		}

		String  fileName = employeenumber+"_payslip_" +year +month+ part+"."+ext;
		File file = new File(dir, fileName);
		 
		file.deleteOnExit();
		return file;
	}
	
	public static File getTempFile() {
		String ext = "pdf";
		String dirName = System.getProperty("ilex.temp.path");
		if (dirName == null || "".equals(dirName.trim())) {
			dirName = System.getProperty("user.home") + "/ilex_temp";
		}
		File dir = new File(dirName);
		 
		if (!dir.exists()) {
			dir.mkdir();
		}
		UUID uuid = UUID.randomUUID();
		String name = String.format("%s.%s",
				uuid, ext);
		name = "payasia_pdf_" + name;
		File file = new File(dir, name);
		 
		file.deleteOnExit();
		return file;
	}
}
