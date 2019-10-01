package com.payasia.dao.bean;

import java.sql.Timestamp;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2013-02-11T18:11:06.131+0530")
@StaticMetamodel(Announcement.class)
public class Announcement_ extends BaseEntity_ {
	public static volatile SingularAttribute<Announcement, Long> announcementId;
	public static volatile SingularAttribute<Announcement, String> title;
	public static volatile SingularAttribute<Announcement, String> description;
	public static volatile SingularAttribute<Announcement, Timestamp> postDateTime;
	public static volatile SingularAttribute<Announcement, Timestamp> removeDateTime;
	public static volatile SingularAttribute<Announcement, Boolean> archive;
	public static volatile SingularAttribute<Announcement, String> scope;
	public static volatile SingularAttribute<Announcement, Company> company;
	public static volatile SingularAttribute<Announcement, CompanyGroup> companyGroup;
}
