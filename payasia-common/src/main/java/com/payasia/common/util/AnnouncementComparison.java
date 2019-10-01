package com.payasia.common.util;

import java.sql.Timestamp;
import java.util.Comparator;

import org.apache.commons.lang.StringUtils;

import com.payasia.common.bean.util.UserContext;
import com.payasia.common.form.AnouncementForm;

public class AnnouncementComparison implements Comparator<AnouncementForm> {

	public String sortParam;
	public String paramName;
	
	public AnnouncementComparison() {

	}
	
	public AnnouncementComparison(String sortParam, String paramName) {
		super();
		this.sortParam = sortParam;
		this.paramName = paramName;
	}
	
	@Override
	public int compare(AnouncementForm obj1, AnouncementForm obj2) {
		int var = 0;
		Timestamp t1;
		Timestamp t2;
		
		switch(this.paramName) {
		case "title":
			var = obj1.getTitle().compareToIgnoreCase(obj2.getTitle());
			break;

		case "description":
			var = obj1.getDescription().compareToIgnoreCase(obj2.getDescription());
			break;
			
		case "scope":
			var = obj1.getScope().compareToIgnoreCase(obj2.getScope());
			break;
			
		case "postDateTime":
			t1 = DateUtils.stringToTimestamp(obj1.getPostDateTime(), UserContext.getWorkingCompanyDateFormat());
			t2 = DateUtils.stringToTimestamp(obj2.getPostDateTime(), UserContext.getWorkingCompanyDateFormat());
			var = t1.compareTo(t2);
			break;
		
		case "removeDateTime":
			if(StringUtils.isNotBlank(obj1.getRemoveDateTime()) && StringUtils.isNotBlank(obj2.getRemoveDateTime())) {
				t1 = DateUtils.stringToTimestamp(obj1.getRemoveDateTime(), UserContext.getWorkingCompanyDateFormat());
				t2 = DateUtils.stringToTimestamp(obj2.getRemoveDateTime(), UserContext.getWorkingCompanyDateFormat());
				var = t1.compareTo(t2);
			}
			else if(StringUtils.isNotBlank(obj1.getRemoveDateTime()) && StringUtils.isBlank(obj2.getRemoveDateTime())) {
				var = -1;
			}
			else if(StringUtils.isBlank(obj1.getRemoveDateTime()) && StringUtils.isNotBlank(obj2.getRemoveDateTime())) {
				var = 1;
			}
			break;
		}
		
		if(this.sortParam.equalsIgnoreCase("DESC")){
			return (-1*var);
		}
		return var;
	}

}
