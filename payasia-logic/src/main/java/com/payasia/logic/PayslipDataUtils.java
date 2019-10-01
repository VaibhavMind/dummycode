package com.payasia.logic;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Image;
import com.mind.payasia.xml.bean.Field;
import com.mind.payasia.xml.bean.Tab;
import com.payasia.common.dto.ColumnPropertyDTO;
import com.payasia.dao.bean.DynamicForm;
import com.payasia.dao.bean.DynamicFormRecord;
import com.payasia.dao.bean.Payslip;

@Transactional
public interface PayslipDataUtils {

	void getEmployeeFieldValue(Payslip payslip, Field field,
			HashMap<Long, Tab> dataTabMap,
			HashMap<Long, DynamicForm> dynamicFormMap,
			HashMap<String, ColumnPropertyDTO> staticPropMap,
			List<DynamicFormRecord> dynamicFormRecordList)
			throws NoSuchMethodException, IllegalAccessException,
			InvocationTargetException;

	Image getLogoImage(Payslip payslip) throws BadElementException,
			MalformedURLException, IOException;

}
