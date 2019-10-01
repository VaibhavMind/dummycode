package com.payasia.common.util;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.Utilities;

public class PayAsiaPDFConstants {
	 
	public static final Font BOLD_FONT = new Font(FontFamily.HELVETICA, 8,
			Font.BOLD, BaseColor.BLACK);
	public static final Font NORMAL_FONT = new Font(FontFamily.HELVETICA, 8,
			Font.NORMAL, BaseColor.BLACK);
	

	 
	public static final BaseColor TABLE_STANDARD_HEADER_NAVYBLUE_COLOR = new BaseColor(
			28, 52, 116);
	public static final BaseColor TABLE_STANDARD_HEADER_GRAY_COLOR = new BaseColor(
			192,192,192);
	

	 
	public static final Rectangle PAGE_SIZE = PageSize.A4;
	public static final float PAGE_LEFT_MARGIN = Utilities.inchesToPoints(0.5f);
	public static final float PAGE_TOP_MARGIN = Utilities.inchesToPoints(0.5f);
	public static final float PAGE_RIGHT_MARGIN = Utilities
			.inchesToPoints(0.5f);
	public static final float PAGE_BOTTOM_MARGIN = Utilities
			.inchesToPoints(0.5f);

	 
	public static final float X_PADDING = 0;
	public static final float Y_PADDING = 10;
	public static final float LINE_SPACING = Utilities.inchesToPoints(0.15f);
	public static final float CELL_PADDING = 4;
	
	 
	public static final float TEMPLATE_A_TABLE_WIDTH = 600f;
	public static final float[] TEMPLATE_A_LOGO_TAB_DIM = new float[] { 1 };
	public static final float[] TEMPLATE_A_INV_DET_TAB_DIM = new float[] { 1f,
			3.5f, 1f, 1f, 1f };
	public static final float[] TEMPLATE_A_INV_DET_FOOTER_DIM = new float[] {
			5.5f, 1f, 1f };
	public static final String PDF_MULTILINGUAL_FONT_PATH = "/etc/tomcat8/conf/payasia/ARIALUNI.TTF";
	
	 
		public static final float PAYSLIP_LOGO_IMAGE_LEFT_ALIGNMENT_X_PADDING = 40f;
		public static final float PAYSLIP_LOGO_IMAGE_CENTER_ALIGNMENT_X_PADDING = 245f;
		public static final float PAYSLIP_LOGO_IMAGE_RIGHT_ALIGNMENT_X_PADDING = 450f;
		public static final float PAYSLIP_LOGO_IMAGE_ALIGNMENT_Y_PADDING = 780f;
		
		public static final float PAYSLIP_LOGO_IMAGE_WIDTH = 102f;
		public static final float PAYSLIP_LOGO_IMAGE_HEIGHT = 50f;
	
}
