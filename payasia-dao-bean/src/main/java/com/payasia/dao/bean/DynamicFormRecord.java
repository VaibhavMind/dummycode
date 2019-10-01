package com.payasia.dao.bean;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * The persistent class for the Dynamic_Form_Record database table.
 * 
 */
@Entity
@Table(name = "Dynamic_Form_Record")
public class DynamicFormRecord extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Record_ID")
	private long recordId;

	@Column(name = "Col_1")
	private String col1;

	@Column(name = "col_10")
	private String col10;

	@Column(name = "col_100")
	private String col100;

	@Column(name = "col_11")
	private String col11;

	@Column(name = "col_12")
	private String col12;

	@Column(name = "col_13")
	private String col13;

	@Column(name = "col_14")
	private String col14;

	@Column(name = "col_15")
	private String col15;

	@Column(name = "col_16")
	private String col16;

	@Column(name = "col_17")
	private String col17;

	@Column(name = "col_18")
	private String col18;

	@Column(name = "col_19")
	private String col19;

	@Column(name = "Col_2")
	private String col2;

	@Column(name = "col_20")
	private String col20;

	@Column(name = "col_21")
	private String col21;

	@Column(name = "col_22")
	private String col22;

	@Column(name = "col_23")
	private String col23;

	@Column(name = "col_24")
	private String col24;

	@Column(name = "col_25")
	private String col25;

	@Column(name = "col_26")
	private String col26;

	@Column(name = "col_27")
	private String col27;

	@Column(name = "col_28")
	private String col28;

	@Column(name = "col_29")
	private String col29;

	@Column(name = "Col_3")
	private String col3;

	@Column(name = "col_30")
	private String col30;

	@Column(name = "col_31")
	private String col31;

	@Column(name = "col_32")
	private String col32;

	@Column(name = "col_33")
	private String col33;

	@Column(name = "col_34")
	private String col34;

	@Column(name = "col_35")
	private String col35;

	@Column(name = "col_36")
	private String col36;

	@Column(name = "col_37")
	private String col37;

	@Column(name = "col_38")
	private String col38;

	@Column(name = "col_39")
	private String col39;

	@Column(name = "Col_4")
	private String col4;

	@Column(name = "col_40")
	private String col40;

	@Column(name = "col_41")
	private String col41;

	@Column(name = "col_42")
	private String col42;

	@Column(name = "col_43")
	private String col43;

	@Column(name = "col_44")
	private String col44;

	@Column(name = "col_45")
	private String col45;

	@Column(name = "col_46")
	private String col46;

	@Column(name = "col_47")
	private String col47;

	@Column(name = "col_48")
	private String col48;

	@Column(name = "col_49")
	private String col49;

	@Column(name = "Col_5")
	private String col5;

	@Column(name = "col_50")
	private String col50;

	@Column(name = "col_51")
	private String col51;

	@Column(name = "col_52")
	private String col52;

	@Column(name = "col_53")
	private String col53;

	@Column(name = "col_54")
	private String col54;

	@Column(name = "col_55")
	private String col55;

	@Column(name = "col_56")
	private String col56;

	@Column(name = "col_57")
	private String col57;

	@Column(name = "col_58")
	private String col58;

	@Column(name = "col_59")
	private String col59;

	@Column(name = "Col_6")
	private String col6;

	@Column(name = "col_60")
	private String col60;

	@Column(name = "col_61")
	private String col61;

	@Column(name = "col_62")
	private String col62;

	@Column(name = "col_63")
	private String col63;

	@Column(name = "col_64")
	private String col64;

	@Column(name = "col_65")
	private String col65;

	@Column(name = "col_66")
	private String col66;

	@Column(name = "col_67")
	private String col67;

	@Column(name = "col_68")
	private String col68;

	@Column(name = "col_69")
	private String col69;

	@Column(name = "col_7")
	private String col7;

	@Column(name = "col_70")
	private String col70;

	@Column(name = "col_71")
	private String col71;

	@Column(name = "col_72")
	private String col72;

	@Column(name = "col_73")
	private String col73;

	@Column(name = "col_74")
	private String col74;

	@Column(name = "col_75")
	private String col75;

	@Column(name = "col_76")
	private String col76;

	@Column(name = "col_77")
	private String col77;

	@Column(name = "col_78")
	private String col78;

	@Column(name = "col_79")
	private String col79;

	@Column(name = "col_8")
	private String col8;

	@Column(name = "col_80")
	private String col80;

	@Column(name = "col_81")
	private String col81;

	@Column(name = "col_82")
	private String col82;

	@Column(name = "col_83")
	private String col83;

	@Column(name = "col_84")
	private String col84;

	@Column(name = "col_85")
	private String col85;

	@Column(name = "col_86")
	private String col86;

	@Column(name = "col_87")
	private String col87;

	@Column(name = "col_88")
	private String col88;

	@Column(name = "col_89")
	private String col89;

	@Column(name = "col_9")
	private String col9;

	@Column(name = "col_90")
	private String col90;

	@Column(name = "col_91")
	private String col91;

	@Column(name = "col_92")
	private String col92;

	@Column(name = "col_93")
	private String col93;

	@Column(name = "col_94")
	private String col94;

	@Column(name = "col_95")
	private String col95;

	@Column(name = "col_96")
	private String col96;

	@Column(name = "col_97")
	private String col97;

	@Column(name = "col_98")
	private String col98;

	@Column(name = "col_99")
	private String col99;

	@Column(name = "Company_ID")
	private long company_ID;

	@Column(name = "Entity_ID")
	private long entity_ID;

	@Column(name = "Entity_Key")
	private long entityKey;

	@Column(name = "Form_ID")
	private long form_ID;

	@Column(name = "Version")
	private int version;

	public DynamicFormRecord() {
	}

	public long getRecordId() {
		return this.recordId;
	}

	public void setRecordId(long recordId) {
		this.recordId = recordId;
	}

	public String getCol1() {
		return this.col1;
	}

	public void setCol1(String col1) {
		this.col1 = col1;
	}

	public String getCol10() {
		return this.col10;
	}

	public void setCol10(String col10) {
		this.col10 = col10;
	}

	public String getCol100() {
		return this.col100;
	}

	public void setCol100(String col100) {
		this.col100 = col100;
	}

	public String getCol11() {
		return this.col11;
	}

	public void setCol11(String col11) {
		this.col11 = col11;
	}

	public String getCol12() {
		return this.col12;
	}

	public void setCol12(String col12) {
		this.col12 = col12;
	}

	public String getCol13() {
		return this.col13;
	}

	public void setCol13(String col13) {
		this.col13 = col13;
	}

	public String getCol14() {
		return this.col14;
	}

	public void setCol14(String col14) {
		this.col14 = col14;
	}

	public String getCol15() {
		return this.col15;
	}

	public void setCol15(String col15) {
		this.col15 = col15;
	}

	public String getCol16() {
		return this.col16;
	}

	public void setCol16(String col16) {
		this.col16 = col16;
	}

	public String getCol17() {
		return this.col17;
	}

	public void setCol17(String col17) {
		this.col17 = col17;
	}

	public String getCol18() {
		return this.col18;
	}

	public void setCol18(String col18) {
		this.col18 = col18;
	}

	public String getCol19() {
		return this.col19;
	}

	public void setCol19(String col19) {
		this.col19 = col19;
	}

	public String getCol2() {
		return this.col2;
	}

	public void setCol2(String col2) {
		this.col2 = col2;
	}

	public String getCol20() {
		return this.col20;
	}

	public void setCol20(String col20) {
		this.col20 = col20;
	}

	public String getCol21() {
		return this.col21;
	}

	public void setCol21(String col21) {
		this.col21 = col21;
	}

	public String getCol22() {
		return this.col22;
	}

	public void setCol22(String col22) {
		this.col22 = col22;
	}

	public String getCol23() {
		return this.col23;
	}

	public void setCol23(String col23) {
		this.col23 = col23;
	}

	public String getCol24() {
		return this.col24;
	}

	public void setCol24(String col24) {
		this.col24 = col24;
	}

	public String getCol25() {
		return this.col25;
	}

	public void setCol25(String col25) {
		this.col25 = col25;
	}

	public String getCol26() {
		return this.col26;
	}

	public void setCol26(String col26) {
		this.col26 = col26;
	}

	public String getCol27() {
		return this.col27;
	}

	public void setCol27(String col27) {
		this.col27 = col27;
	}

	public String getCol28() {
		return this.col28;
	}

	public void setCol28(String col28) {
		this.col28 = col28;
	}

	public String getCol29() {
		return this.col29;
	}

	public void setCol29(String col29) {
		this.col29 = col29;
	}

	public String getCol3() {
		return this.col3;
	}

	public void setCol3(String col3) {
		this.col3 = col3;
	}

	public String getCol30() {
		return this.col30;
	}

	public void setCol30(String col30) {
		this.col30 = col30;
	}

	public String getCol31() {
		return this.col31;
	}

	public void setCol31(String col31) {
		this.col31 = col31;
	}

	public String getCol32() {
		return this.col32;
	}

	public void setCol32(String col32) {
		this.col32 = col32;
	}

	public String getCol33() {
		return this.col33;
	}

	public void setCol33(String col33) {
		this.col33 = col33;
	}

	public String getCol34() {
		return this.col34;
	}

	public void setCol34(String col34) {
		this.col34 = col34;
	}

	public String getCol35() {
		return this.col35;
	}

	public void setCol35(String col35) {
		this.col35 = col35;
	}

	public String getCol36() {
		return this.col36;
	}

	public void setCol36(String col36) {
		this.col36 = col36;
	}

	public String getCol37() {
		return this.col37;
	}

	public void setCol37(String col37) {
		this.col37 = col37;
	}

	public String getCol38() {
		return this.col38;
	}

	public void setCol38(String col38) {
		this.col38 = col38;
	}

	public String getCol39() {
		return this.col39;
	}

	public void setCol39(String col39) {
		this.col39 = col39;
	}

	public String getCol4() {
		return this.col4;
	}

	public void setCol4(String col4) {
		this.col4 = col4;
	}

	public String getCol40() {
		return this.col40;
	}

	public void setCol40(String col40) {
		this.col40 = col40;
	}

	public String getCol41() {
		return this.col41;
	}

	public void setCol41(String col41) {
		this.col41 = col41;
	}

	public String getCol42() {
		return this.col42;
	}

	public void setCol42(String col42) {
		this.col42 = col42;
	}

	public String getCol43() {
		return this.col43;
	}

	public void setCol43(String col43) {
		this.col43 = col43;
	}

	public String getCol44() {
		return this.col44;
	}

	public void setCol44(String col44) {
		this.col44 = col44;
	}

	public String getCol45() {
		return this.col45;
	}

	public void setCol45(String col45) {
		this.col45 = col45;
	}

	public String getCol46() {
		return this.col46;
	}

	public void setCol46(String col46) {
		this.col46 = col46;
	}

	public String getCol47() {
		return this.col47;
	}

	public void setCol47(String col47) {
		this.col47 = col47;
	}

	public String getCol48() {
		return this.col48;
	}

	public void setCol48(String col48) {
		this.col48 = col48;
	}

	public String getCol49() {
		return this.col49;
	}

	public void setCol49(String col49) {
		this.col49 = col49;
	}

	public String getCol5() {
		return this.col5;
	}

	public void setCol5(String col5) {
		this.col5 = col5;
	}

	public String getCol50() {
		return this.col50;
	}

	public void setCol50(String col50) {
		this.col50 = col50;
	}

	public String getCol51() {
		return this.col51;
	}

	public void setCol51(String col51) {
		this.col51 = col51;
	}

	public String getCol52() {
		return this.col52;
	}

	public void setCol52(String col52) {
		this.col52 = col52;
	}

	public String getCol53() {
		return this.col53;
	}

	public void setCol53(String col53) {
		this.col53 = col53;
	}

	public String getCol54() {
		return this.col54;
	}

	public void setCol54(String col54) {
		this.col54 = col54;
	}

	public String getCol55() {
		return this.col55;
	}

	public void setCol55(String col55) {
		this.col55 = col55;
	}

	public String getCol56() {
		return this.col56;
	}

	public void setCol56(String col56) {
		this.col56 = col56;
	}

	public String getCol57() {
		return this.col57;
	}

	public void setCol57(String col57) {
		this.col57 = col57;
	}

	public String getCol58() {
		return this.col58;
	}

	public void setCol58(String col58) {
		this.col58 = col58;
	}

	public String getCol59() {
		return this.col59;
	}

	public void setCol59(String col59) {
		this.col59 = col59;
	}

	public String getCol6() {
		return this.col6;
	}

	public void setCol6(String col6) {
		this.col6 = col6;
	}

	public String getCol60() {
		return this.col60;
	}

	public void setCol60(String col60) {
		this.col60 = col60;
	}

	public String getCol61() {
		return this.col61;
	}

	public void setCol61(String col61) {
		this.col61 = col61;
	}

	public String getCol62() {
		return this.col62;
	}

	public void setCol62(String col62) {
		this.col62 = col62;
	}

	public String getCol63() {
		return this.col63;
	}

	public void setCol63(String col63) {
		this.col63 = col63;
	}

	public String getCol64() {
		return this.col64;
	}

	public void setCol64(String col64) {
		this.col64 = col64;
	}

	public String getCol65() {
		return this.col65;
	}

	public void setCol65(String col65) {
		this.col65 = col65;
	}

	public String getCol66() {
		return this.col66;
	}

	public void setCol66(String col66) {
		this.col66 = col66;
	}

	public String getCol67() {
		return this.col67;
	}

	public void setCol67(String col67) {
		this.col67 = col67;
	}

	public String getCol68() {
		return this.col68;
	}

	public void setCol68(String col68) {
		this.col68 = col68;
	}

	public String getCol69() {
		return this.col69;
	}

	public void setCol69(String col69) {
		this.col69 = col69;
	}

	public String getCol7() {
		return this.col7;
	}

	public void setCol7(String col7) {
		this.col7 = col7;
	}

	public String getCol70() {
		return this.col70;
	}

	public void setCol70(String col70) {
		this.col70 = col70;
	}

	public String getCol71() {
		return this.col71;
	}

	public void setCol71(String col71) {
		this.col71 = col71;
	}

	public String getCol72() {
		return this.col72;
	}

	public void setCol72(String col72) {
		this.col72 = col72;
	}

	public String getCol73() {
		return this.col73;
	}

	public void setCol73(String col73) {
		this.col73 = col73;
	}

	public String getCol74() {
		return this.col74;
	}

	public void setCol74(String col74) {
		this.col74 = col74;
	}

	public String getCol75() {
		return this.col75;
	}

	public void setCol75(String col75) {
		this.col75 = col75;
	}

	public String getCol76() {
		return this.col76;
	}

	public void setCol76(String col76) {
		this.col76 = col76;
	}

	public String getCol77() {
		return this.col77;
	}

	public void setCol77(String col77) {
		this.col77 = col77;
	}

	public String getCol78() {
		return this.col78;
	}

	public void setCol78(String col78) {
		this.col78 = col78;
	}

	public String getCol79() {
		return this.col79;
	}

	public void setCol79(String col79) {
		this.col79 = col79;
	}

	public String getCol8() {
		return this.col8;
	}

	public void setCol8(String col8) {
		this.col8 = col8;
	}

	public String getCol80() {
		return this.col80;
	}

	public void setCol80(String col80) {
		this.col80 = col80;
	}

	public String getCol81() {
		return this.col81;
	}

	public void setCol81(String col81) {
		this.col81 = col81;
	}

	public String getCol82() {
		return this.col82;
	}

	public void setCol82(String col82) {
		this.col82 = col82;
	}

	public String getCol83() {
		return this.col83;
	}

	public void setCol83(String col83) {
		this.col83 = col83;
	}

	public String getCol84() {
		return this.col84;
	}

	public void setCol84(String col84) {
		this.col84 = col84;
	}

	public String getCol85() {
		return this.col85;
	}

	public void setCol85(String col85) {
		this.col85 = col85;
	}

	public String getCol86() {
		return this.col86;
	}

	public void setCol86(String col86) {
		this.col86 = col86;
	}

	public String getCol87() {
		return this.col87;
	}

	public void setCol87(String col87) {
		this.col87 = col87;
	}

	public String getCol88() {
		return this.col88;
	}

	public void setCol88(String col88) {
		this.col88 = col88;
	}

	public String getCol89() {
		return this.col89;
	}

	public void setCol89(String col89) {
		this.col89 = col89;
	}

	public String getCol9() {
		return this.col9;
	}

	public void setCol9(String col9) {
		this.col9 = col9;
	}

	public String getCol90() {
		return this.col90;
	}

	public void setCol90(String col90) {
		this.col90 = col90;
	}

	public String getCol91() {
		return this.col91;
	}

	public void setCol91(String col91) {
		this.col91 = col91;
	}

	public String getCol92() {
		return this.col92;
	}

	public void setCol92(String col92) {
		this.col92 = col92;
	}

	public String getCol93() {
		return this.col93;
	}

	public void setCol93(String col93) {
		this.col93 = col93;
	}

	public String getCol94() {
		return this.col94;
	}

	public void setCol94(String col94) {
		this.col94 = col94;
	}

	public String getCol95() {
		return this.col95;
	}

	public void setCol95(String col95) {
		this.col95 = col95;
	}

	public String getCol96() {
		return this.col96;
	}

	public void setCol96(String col96) {
		this.col96 = col96;
	}

	public String getCol97() {
		return this.col97;
	}

	public void setCol97(String col97) {
		this.col97 = col97;
	}

	public String getCol98() {
		return this.col98;
	}

	public void setCol98(String col98) {
		this.col98 = col98;
	}

	public String getCol99() {
		return this.col99;
	}

	public void setCol99(String col99) {
		this.col99 = col99;
	}

	public long getCompany_ID() {
		return this.company_ID;
	}

	public void setCompany_ID(long company_ID) {
		this.company_ID = company_ID;
	}

	public long getEntity_ID() {
		return this.entity_ID;
	}

	public void setEntity_ID(long entity_ID) {
		this.entity_ID = entity_ID;
	}

	public long getEntityKey() {
		return this.entityKey;
	}

	public void setEntityKey(long entityKey) {
		this.entityKey = entityKey;
	}

	public long getForm_ID() {
		return this.form_ID;
	}

	public void setForm_ID(long form_ID) {
		this.form_ID = form_ID;
	}

	public int getVersion() {
		return this.version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

}