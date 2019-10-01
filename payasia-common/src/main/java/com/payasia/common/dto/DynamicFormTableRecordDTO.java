package com.payasia.common.dto;

import java.util.ArrayList;
import java.util.List;

import com.payasia.dao.bean.DynamicFormTableRecord;

public class DynamicFormTableRecordDTO {
	
	private DynamicFormTableRecordDTO(){
		
	}
	
	public DynamicFormTableRecordDTO(DynamicFormTableRecord dynamicFormTableRecord){
		this();
		this.tableId=""+dynamicFormTableRecord.getId().getDynamicFormTableRecordId();
		this.seqNo=""+dynamicFormTableRecord.getId().getSequence();
		this.custcol_1=dynamicFormTableRecord.getCol1();
		this.custcol_2=dynamicFormTableRecord.getCol2();
		this.custcol_3=dynamicFormTableRecord.getCol3();
		this.custcol_4=dynamicFormTableRecord.getCol4();
		this.custcol_5=dynamicFormTableRecord.getCol5();
		this.custcol_6=dynamicFormTableRecord.getCol6();
		this.custcol_7=dynamicFormTableRecord.getCol7();
		this.custcol_8=dynamicFormTableRecord.getCol8();
		this.custcol_9=dynamicFormTableRecord.getCol9();
		this.custcol_10=dynamicFormTableRecord.getCol10();
		this.custcol_11=dynamicFormTableRecord.getCol11();
		this.custcol_12=dynamicFormTableRecord.getCol12();
		this.custcol_13=dynamicFormTableRecord.getCol13();
		this.custcol_14=dynamicFormTableRecord.getCol14();
		this.custcol_15=dynamicFormTableRecord.getCol15();
		this.custcol_16=dynamicFormTableRecord.getCol16();
		this.custcol_17=dynamicFormTableRecord.getCol17();
		this.custcol_18=dynamicFormTableRecord.getCol18();
		this.custcol_19=dynamicFormTableRecord.getCol19();
		this.custcol_20=dynamicFormTableRecord.getCol20();
		this.custcol_21=dynamicFormTableRecord.getCol21();
		this.custcol_22=dynamicFormTableRecord.getCol22();
		this.custcol_23=dynamicFormTableRecord.getCol23();
		this.custcol_24=dynamicFormTableRecord.getCol24();
		this.custcol_25=dynamicFormTableRecord.getCol25();
		this.custcol_26=dynamicFormTableRecord.getCol26();
		this.custcol_27=dynamicFormTableRecord.getCol27();
		this.custcol_28=dynamicFormTableRecord.getCol28();
		this.custcol_29=dynamicFormTableRecord.getCol29();
		this.custcol_30=dynamicFormTableRecord.getCol30();
		this.custcol_31=dynamicFormTableRecord.getCol31();
		this.custcol_32=dynamicFormTableRecord.getCol32();
		this.custcol_33=dynamicFormTableRecord.getCol33();
		this.custcol_34=dynamicFormTableRecord.getCol34();
		this.custcol_35=dynamicFormTableRecord.getCol35();
		this.custcol_36=dynamicFormTableRecord.getCol36();
		this.custcol_37=dynamicFormTableRecord.getCol37();
		this.custcol_38=dynamicFormTableRecord.getCol38();
		this.custcol_39=dynamicFormTableRecord.getCol39();
		this.custcol_40=dynamicFormTableRecord.getCol40();
		this.custcol_41=dynamicFormTableRecord.getCol41();
		this.custcol_42=dynamicFormTableRecord.getCol42();
		this.custcol_43=dynamicFormTableRecord.getCol43();
		this.custcol_44=dynamicFormTableRecord.getCol44();
		this.custcol_45=dynamicFormTableRecord.getCol45();
		this.custcol_46=dynamicFormTableRecord.getCol46();
		this.custcol_47=dynamicFormTableRecord.getCol47();
		this.custcol_48=dynamicFormTableRecord.getCol48();
		this.custcol_49=dynamicFormTableRecord.getCol49();
		this.custcol_50=dynamicFormTableRecord.getCol50();
		this.custcol_51=dynamicFormTableRecord.getCol51();
		this.custcol_52=dynamicFormTableRecord.getCol52();
		this.custcol_53=dynamicFormTableRecord.getCol53();
		this.custcol_54=dynamicFormTableRecord.getCol54();
		this.custcol_55=dynamicFormTableRecord.getCol55();
		this.custcol_56=dynamicFormTableRecord.getCol56();
		this.custcol_57=dynamicFormTableRecord.getCol57();
		this.custcol_58=dynamicFormTableRecord.getCol58();
		this.custcol_59=dynamicFormTableRecord.getCol59();
		this.custcol_60=dynamicFormTableRecord.getCol60();
		this.custcol_61=dynamicFormTableRecord.getCol61();
		this.custcol_62=dynamicFormTableRecord.getCol62();
		this.custcol_63=dynamicFormTableRecord.getCol63();
		this.custcol_64=dynamicFormTableRecord.getCol64();
		this.custcol_65=dynamicFormTableRecord.getCol65();
		this.custcol_66=dynamicFormTableRecord.getCol66();
		this.custcol_67=dynamicFormTableRecord.getCol67();
		this.custcol_68=dynamicFormTableRecord.getCol68();
		this.custcol_69=dynamicFormTableRecord.getCol69();
		this.custcol_70=dynamicFormTableRecord.getCol70();
		this.custcol_71=dynamicFormTableRecord.getCol71();
		this.custcol_72=dynamicFormTableRecord.getCol72();
		this.custcol_73=dynamicFormTableRecord.getCol73();
		this.custcol_74=dynamicFormTableRecord.getCol74();
		this.custcol_75=dynamicFormTableRecord.getCol75();
		this.custcol_76=dynamicFormTableRecord.getCol76();
		this.custcol_77=dynamicFormTableRecord.getCol77();
		this.custcol_78=dynamicFormTableRecord.getCol78();
		this.custcol_79=dynamicFormTableRecord.getCol79();
		this.custcol_80=dynamicFormTableRecord.getCol80();
		this.custcol_81=dynamicFormTableRecord.getCol81();
		this.custcol_82=dynamicFormTableRecord.getCol82();
		this.custcol_83=dynamicFormTableRecord.getCol83();
		this.custcol_84=dynamicFormTableRecord.getCol84();
		this.custcol_85=dynamicFormTableRecord.getCol85();
		this.custcol_86=dynamicFormTableRecord.getCol86();
		this.custcol_87=dynamicFormTableRecord.getCol87();
		this.custcol_88=dynamicFormTableRecord.getCol88();
		this.custcol_89=dynamicFormTableRecord.getCol89();
		this.custcol_90=dynamicFormTableRecord.getCol90();
		this.custcol_91=dynamicFormTableRecord.getCol91();
		this.custcol_92=dynamicFormTableRecord.getCol92();
		this.custcol_93=dynamicFormTableRecord.getCol93();
		this.custcol_94=dynamicFormTableRecord.getCol94();
		this.custcol_95=dynamicFormTableRecord.getCol95();
		this.custcol_96=dynamicFormTableRecord.getCol96();
		this.custcol_97=dynamicFormTableRecord.getCol97();
		this.custcol_98=dynamicFormTableRecord.getCol98();
		this.custcol_99=dynamicFormTableRecord.getCol99();	
		this.approvalReq=new ArrayList<String>();
	}
	
	private String tableId;
	private String seqNo;	
	private String custcol_1;
	private String custcol_2;
	private String custcol_3;
	private String custcol_4;
	private String custcol_5;
	private String custcol_6;
	private String custcol_7;
	private String custcol_8;
	private String custcol_9;
	private String custcol_10;
	private String custcol_11;
	private String custcol_12;
	private String custcol_13;
	private String custcol_14;
	private String custcol_15;
	private String custcol_16;
	private String custcol_17;
	private String custcol_18;
	private String custcol_19;
	private String custcol_20;
	private String custcol_21;
	private String custcol_22;
	private String custcol_23;
	private String custcol_24;
	private String custcol_25;
	private String custcol_26;
	private String custcol_27;
	private String custcol_28;
	private String custcol_29;
	private String custcol_30;
	private String custcol_31;
	private String custcol_32;
	private String custcol_33;
	private String custcol_34;
	private String custcol_35;
	private String custcol_36;
	private String custcol_37;
	private String custcol_38;
	private String custcol_39;
	private String custcol_40;
	private String custcol_41;
	private String custcol_42;
	private String custcol_43;
	private String custcol_44;
	private String custcol_45;
	private String custcol_46;
	private String custcol_47;
	private String custcol_48;
	private String custcol_49;
	private String custcol_50;
	private String custcol_51;
	private String custcol_52;
	private String custcol_53;
	private String custcol_54;
	private String custcol_55;
	private String custcol_56;
	private String custcol_57;
	private String custcol_58;
	private String custcol_59;
	private String custcol_60;
	private String custcol_61;
	private String custcol_62;
	private String custcol_63;
	private String custcol_64;
	private String custcol_65;
	private String custcol_66;
	private String custcol_67;
	private String custcol_68;
	private String custcol_69;
	private String custcol_70;
	private String custcol_71;
	private String custcol_72;
	private String custcol_73;
	private String custcol_74;
	private String custcol_75;
	private String custcol_76;
	private String custcol_77;
	private String custcol_78;
	private String custcol_79;
	private String custcol_80;
	private String custcol_81;
	private String custcol_82;
	private String custcol_83;
	private String custcol_84;
	private String custcol_85;
	private String custcol_86;
	private String custcol_87;
	private String custcol_88;
	private String custcol_89;
	private String custcol_90;
	private String custcol_91;
	private String custcol_92;
	private String custcol_93;
	private String custcol_94;
	private String custcol_95;
	private String custcol_96;
	private String custcol_97;
	private String custcol_98;
	private String custcol_99;
	private List<String> approvalReq;	
	
	
	
	public List<String> getApprovalReq() {
		return approvalReq;
	}

	public void setApprovalReq(List<String> approvalReq) {
		this.approvalReq = approvalReq;
	}

	public String getTableId() {
		return tableId;
	}

	public void setTableId(String tableId) {
		this.tableId = tableId;
	}

	public String getSeqNo() {
		return seqNo;
	}

	public void setSeqNo(String seqNo) {
		this.seqNo = seqNo;
	}

	public String getCustcol_1() {
		return custcol_1;
	}
	public void setCustcol_1(String custcol_1) {
		this.custcol_1 = custcol_1;
	}
	public String getCustcol_2() {
		return custcol_2;
	}
	public void setCustcol_2(String custcol_2) {
		this.custcol_2 = custcol_2;
	}
	public String getCustcol_3() {
		return custcol_3;
	}
	public void setCustcol_3(String custcol_3) {
		this.custcol_3 = custcol_3;
	}
	public String getCustcol_4() {
		return custcol_4;
	}
	public void setCustcol_4(String custcol_4) {
		this.custcol_4 = custcol_4;
	}
	public String getCustcol_5() {
		return custcol_5;
	}
	public void setCustcol_5(String custcol_5) {
		this.custcol_5 = custcol_5;
	}
	public String getCustcol_6() {
		return custcol_6;
	}
	public void setCustcol_6(String custcol_6) {
		this.custcol_6 = custcol_6;
	}
	public String getCustcol_7() {
		return custcol_7;
	}
	public void setCustcol_7(String custcol_7) {
		this.custcol_7 = custcol_7;
	}
	public String getCustcol_8() {
		return custcol_8;
	}
	public void setCustcol_8(String custcol_8) {
		this.custcol_8 = custcol_8;
	}
	public String getCustcol_9() {
		return custcol_9;
	}
	public void setCustcol_9(String custcol_9) {
		this.custcol_9 = custcol_9;
	}
	public String getCustcol_10() {
		return custcol_10;
	}
	public void setCustcol_10(String custcol_10) {
		this.custcol_10 = custcol_10;
	}
	public String getCustcol_11() {
		return custcol_11;
	}
	public void setCustcol_11(String custcol_11) {
		this.custcol_11 = custcol_11;
	}
	public String getCustcol_12() {
		return custcol_12;
	}
	public void setCustcol_12(String custcol_12) {
		this.custcol_12 = custcol_12;
	}
	public String getCustcol_13() {
		return custcol_13;
	}
	public void setCustcol_13(String custcol_13) {
		this.custcol_13 = custcol_13;
	}
	public String getCustcol_14() {
		return custcol_14;
	}
	public void setCustcol_14(String custcol_14) {
		this.custcol_14 = custcol_14;
	}
	public String getCustcol_15() {
		return custcol_15;
	}
	public void setCustcol_15(String custcol_15) {
		this.custcol_15 = custcol_15;
	}
	public String getCustcol_16() {
		return custcol_16;
	}
	public void setCustcol_16(String custcol_16) {
		this.custcol_16 = custcol_16;
	}
	public String getCustcol_17() {
		return custcol_17;
	}
	public void setCustcol_17(String custcol_17) {
		this.custcol_17 = custcol_17;
	}
	public String getCustcol_18() {
		return custcol_18;
	}
	public void setCustcol_18(String custcol_18) {
		this.custcol_18 = custcol_18;
	}
	public String getCustcol_19() {
		return custcol_19;
	}
	public void setCustcol_19(String custcol_19) {
		this.custcol_19 = custcol_19;
	}
	public String getCustcol_20() {
		return custcol_20;
	}
	public void setCustcol_20(String custcol_20) {
		this.custcol_20 = custcol_20;
	}
	public String getCustcol_21() {
		return custcol_21;
	}
	public void setCustcol_21(String custcol_21) {
		this.custcol_21 = custcol_21;
	}
	public String getCustcol_22() {
		return custcol_22;
	}
	public void setCustcol_22(String custcol_22) {
		this.custcol_22 = custcol_22;
	}
	public String getCustcol_23() {
		return custcol_23;
	}
	public void setCustcol_23(String custcol_23) {
		this.custcol_23 = custcol_23;
	}
	public String getCustcol_24() {
		return custcol_24;
	}
	public void setCustcol_24(String custcol_24) {
		this.custcol_24 = custcol_24;
	}
	public String getCustcol_25() {
		return custcol_25;
	}
	public void setCustcol_25(String custcol_25) {
		this.custcol_25 = custcol_25;
	}
	public String getCustcol_26() {
		return custcol_26;
	}
	public void setCustcol_26(String custcol_26) {
		this.custcol_26 = custcol_26;
	}
	public String getCustcol_27() {
		return custcol_27;
	}
	public void setCustcol_27(String custcol_27) {
		this.custcol_27 = custcol_27;
	}
	public String getCustcol_28() {
		return custcol_28;
	}
	public void setCustcol_28(String custcol_28) {
		this.custcol_28 = custcol_28;
	}
	public String getCustcol_29() {
		return custcol_29;
	}
	public void setCustcol_29(String custcol_29) {
		this.custcol_29 = custcol_29;
	}
	public String getCustcol_30() {
		return custcol_30;
	}
	public void setCustcol_30(String custcol_30) {
		this.custcol_30 = custcol_30;
	}
	public String getCustcol_31() {
		return custcol_31;
	}
	public void setCustcol_31(String custcol_31) {
		this.custcol_31 = custcol_31;
	}
	public String getCustcol_32() {
		return custcol_32;
	}
	public void setCustcol_32(String custcol_32) {
		this.custcol_32 = custcol_32;
	}
	public String getCustcol_33() {
		return custcol_33;
	}
	public void setCustcol_33(String custcol_33) {
		this.custcol_33 = custcol_33;
	}
	public String getCustcol_34() {
		return custcol_34;
	}
	public void setCustcol_34(String custcol_34) {
		this.custcol_34 = custcol_34;
	}
	public String getCustcol_35() {
		return custcol_35;
	}
	public void setCustcol_35(String custcol_35) {
		this.custcol_35 = custcol_35;
	}
	public String getCustcol_36() {
		return custcol_36;
	}
	public void setCustcol_36(String custcol_36) {
		this.custcol_36 = custcol_36;
	}
	public String getCustcol_37() {
		return custcol_37;
	}
	public void setCustcol_37(String custcol_37) {
		this.custcol_37 = custcol_37;
	}
	public String getCustcol_38() {
		return custcol_38;
	}
	public void setCustcol_38(String custcol_38) {
		this.custcol_38 = custcol_38;
	}
	public String getCustcol_39() {
		return custcol_39;
	}
	public void setCustcol_39(String custcol_39) {
		this.custcol_39 = custcol_39;
	}
	public String getCustcol_40() {
		return custcol_40;
	}
	public void setCustcol_40(String custcol_40) {
		this.custcol_40 = custcol_40;
	}
	public String getCustcol_41() {
		return custcol_41;
	}
	public void setCustcol_41(String custcol_41) {
		this.custcol_41 = custcol_41;
	}
	public String getCustcol_42() {
		return custcol_42;
	}
	public void setCustcol_42(String custcol_42) {
		this.custcol_42 = custcol_42;
	}
	public String getCustcol_43() {
		return custcol_43;
	}
	public void setCustcol_43(String custcol_43) {
		this.custcol_43 = custcol_43;
	}
	public String getCustcol_44() {
		return custcol_44;
	}
	public void setCustcol_44(String custcol_44) {
		this.custcol_44 = custcol_44;
	}
	public String getCustcol_45() {
		return custcol_45;
	}
	public void setCustcol_45(String custcol_45) {
		this.custcol_45 = custcol_45;
	}
	public String getCustcol_46() {
		return custcol_46;
	}
	public void setCustcol_46(String custcol_46) {
		this.custcol_46 = custcol_46;
	}
	public String getCustcol_47() {
		return custcol_47;
	}
	public void setCustcol_47(String custcol_47) {
		this.custcol_47 = custcol_47;
	}
	public String getCustcol_48() {
		return custcol_48;
	}
	public void setCustcol_48(String custcol_48) {
		this.custcol_48 = custcol_48;
	}
	public String getCustcol_49() {
		return custcol_49;
	}
	public void setCustcol_49(String custcol_49) {
		this.custcol_49 = custcol_49;
	}
	public String getCustcol_50() {
		return custcol_50;
	}
	public void setCustcol_50(String custcol_50) {
		this.custcol_50 = custcol_50;
	}
	public String getCustcol_51() {
		return custcol_51;
	}
	public void setCustcol_51(String custcol_51) {
		this.custcol_51 = custcol_51;
	}
	public String getCustcol_52() {
		return custcol_52;
	}
	public void setCustcol_52(String custcol_52) {
		this.custcol_52 = custcol_52;
	}
	public String getCustcol_53() {
		return custcol_53;
	}
	public void setCustcol_53(String custcol_53) {
		this.custcol_53 = custcol_53;
	}
	public String getCustcol_54() {
		return custcol_54;
	}
	public void setCustcol_54(String custcol_54) {
		this.custcol_54 = custcol_54;
	}
	public String getCustcol_55() {
		return custcol_55;
	}
	public void setCustcol_55(String custcol_55) {
		this.custcol_55 = custcol_55;
	}
	public String getCustcol_56() {
		return custcol_56;
	}
	public void setCustcol_56(String custcol_56) {
		this.custcol_56 = custcol_56;
	}
	public String getCustcol_57() {
		return custcol_57;
	}
	public void setCustcol_57(String custcol_57) {
		this.custcol_57 = custcol_57;
	}
	public String getCustcol_58() {
		return custcol_58;
	}
	public void setCustcol_58(String custcol_58) {
		this.custcol_58 = custcol_58;
	}
	public String getCustcol_59() {
		return custcol_59;
	}
	public void setCustcol_59(String custcol_59) {
		this.custcol_59 = custcol_59;
	}
	public String getCustcol_60() {
		return custcol_60;
	}
	public void setCustcol_60(String custcol_60) {
		this.custcol_60 = custcol_60;
	}
	public String getCustcol_61() {
		return custcol_61;
	}
	public void setCustcol_61(String custcol_61) {
		this.custcol_61 = custcol_61;
	}
	public String getCustcol_62() {
		return custcol_62;
	}
	public void setCustcol_62(String custcol_62) {
		this.custcol_62 = custcol_62;
	}
	public String getCustcol_63() {
		return custcol_63;
	}
	public void setCustcol_63(String custcol_63) {
		this.custcol_63 = custcol_63;
	}
	public String getCustcol_64() {
		return custcol_64;
	}
	public void setCustcol_64(String custcol_64) {
		this.custcol_64 = custcol_64;
	}
	public String getCustcol_65() {
		return custcol_65;
	}
	public void setCustcol_65(String custcol_65) {
		this.custcol_65 = custcol_65;
	}
	public String getCustcol_66() {
		return custcol_66;
	}
	public void setCustcol_66(String custcol_66) {
		this.custcol_66 = custcol_66;
	}
	public String getCustcol_67() {
		return custcol_67;
	}
	public void setCustcol_67(String custcol_67) {
		this.custcol_67 = custcol_67;
	}
	public String getCustcol_68() {
		return custcol_68;
	}
	public void setCustcol_68(String custcol_68) {
		this.custcol_68 = custcol_68;
	}
	public String getCustcol_69() {
		return custcol_69;
	}
	public void setCustcol_69(String custcol_69) {
		this.custcol_69 = custcol_69;
	}
	public String getCustcol_70() {
		return custcol_70;
	}
	public void setCustcol_70(String custcol_70) {
		this.custcol_70 = custcol_70;
	}
	public String getCustcol_71() {
		return custcol_71;
	}
	public void setCustcol_71(String custcol_71) {
		this.custcol_71 = custcol_71;
	}
	public String getCustcol_72() {
		return custcol_72;
	}
	public void setCustcol_72(String custcol_72) {
		this.custcol_72 = custcol_72;
	}
	public String getCustcol_73() {
		return custcol_73;
	}
	public void setCustcol_73(String custcol_73) {
		this.custcol_73 = custcol_73;
	}
	public String getCustcol_74() {
		return custcol_74;
	}
	public void setCustcol_74(String custcol_74) {
		this.custcol_74 = custcol_74;
	}
	public String getCustcol_75() {
		return custcol_75;
	}
	public void setCustcol_75(String custcol_75) {
		this.custcol_75 = custcol_75;
	}
	public String getCustcol_76() {
		return custcol_76;
	}
	public void setCustcol_76(String custcol_76) {
		this.custcol_76 = custcol_76;
	}
	public String getCustcol_77() {
		return custcol_77;
	}
	public void setCustcol_77(String custcol_77) {
		this.custcol_77 = custcol_77;
	}
	public String getCustcol_78() {
		return custcol_78;
	}
	public void setCustcol_78(String custcol_78) {
		this.custcol_78 = custcol_78;
	}
	public String getCustcol_79() {
		return custcol_79;
	}
	public void setCustcol_79(String custcol_79) {
		this.custcol_79 = custcol_79;
	}
	public String getCustcol_80() {
		return custcol_80;
	}
	public void setCustcol_80(String custcol_80) {
		this.custcol_80 = custcol_80;
	}
	public String getCustcol_81() {
		return custcol_81;
	}
	public void setCustcol_81(String custcol_81) {
		this.custcol_81 = custcol_81;
	}
	public String getCustcol_82() {
		return custcol_82;
	}
	public void setCustcol_82(String custcol_82) {
		this.custcol_82 = custcol_82;
	}
	public String getCustcol_83() {
		return custcol_83;
	}
	public void setCustcol_83(String custcol_83) {
		this.custcol_83 = custcol_83;
	}
	public String getCustcol_84() {
		return custcol_84;
	}
	public void setCustcol_84(String custcol_84) {
		this.custcol_84 = custcol_84;
	}
	public String getCustcol_85() {
		return custcol_85;
	}
	public void setCustcol_85(String custcol_85) {
		this.custcol_85 = custcol_85;
	}
	public String getCustcol_86() {
		return custcol_86;
	}
	public void setCustcol_86(String custcol_86) {
		this.custcol_86 = custcol_86;
	}
	public String getCustcol_87() {
		return custcol_87;
	}
	public void setCustcol_87(String custcol_87) {
		this.custcol_87 = custcol_87;
	}
	public String getCustcol_88() {
		return custcol_88;
	}
	public void setCustcol_88(String custcol_88) {
		this.custcol_88 = custcol_88;
	}
	public String getCustcol_89() {
		return custcol_89;
	}
	public void setCustcol_89(String custcol_89) {
		this.custcol_89 = custcol_89;
	}
	public String getCustcol_90() {
		return custcol_90;
	}
	public void setCustcol_90(String custcol_90) {
		this.custcol_90 = custcol_90;
	}
	public String getCustcol_91() {
		return custcol_91;
	}
	public void setCustcol_91(String custcol_91) {
		this.custcol_91 = custcol_91;
	}
	public String getCustcol_92() {
		return custcol_92;
	}
	public void setCustcol_92(String custcol_92) {
		this.custcol_92 = custcol_92;
	}
	public String getCustcol_93() {
		return custcol_93;
	}
	public void setCustcol_93(String custcol_93) {
		this.custcol_93 = custcol_93;
	}
	public String getCustcol_94() {
		return custcol_94;
	}
	public void setCustcol_94(String custcol_94) {
		this.custcol_94 = custcol_94;
	}
	public String getCustcol_95() {
		return custcol_95;
	}
	public void setCustcol_95(String custcol_95) {
		this.custcol_95 = custcol_95;
	}
	public String getCustcol_96() {
		return custcol_96;
	}
	public void setCustcol_96(String custcol_96) {
		this.custcol_96 = custcol_96;
	}
	public String getCustcol_97() {
		return custcol_97;
	}
	public void setCustcol_97(String custcol_97) {
		this.custcol_97 = custcol_97;
	}
	public String getCustcol_98() {
		return custcol_98;
	}
	public void setCustcol_98(String custcol_98) {
		this.custcol_98 = custcol_98;
	}
	public String getCustcol_99() {
		return custcol_99;
	}
	public void setCustcol_99(String custcol_99) {
		this.custcol_99 = custcol_99;
	}
	
	public DynamicFormTableRecord getDynamicFormTableRecord(){
		DynamicFormTableRecord dynamicFormTableRecord=new DynamicFormTableRecord();
		dynamicFormTableRecord.setCol1(this.custcol_1);
		dynamicFormTableRecord.setCol2(this.custcol_2);
		dynamicFormTableRecord.setCol3(this.custcol_3);
		dynamicFormTableRecord.setCol4(this.custcol_4);
		dynamicFormTableRecord.setCol5(this.custcol_5);
		dynamicFormTableRecord.setCol6(this.custcol_6);
		dynamicFormTableRecord.setCol7(this.custcol_7);
		dynamicFormTableRecord.setCol8(this.custcol_8);
		dynamicFormTableRecord.setCol9(this.custcol_9);
		dynamicFormTableRecord.setCol10(this.custcol_10);
		dynamicFormTableRecord.setCol11(this.custcol_11);
		dynamicFormTableRecord.setCol12(this.custcol_12);
		dynamicFormTableRecord.setCol13(this.custcol_13);
		dynamicFormTableRecord.setCol14(this.custcol_14);
		dynamicFormTableRecord.setCol15(this.custcol_15);
		dynamicFormTableRecord.setCol16(this.custcol_16);
		dynamicFormTableRecord.setCol17(this.custcol_17);
		dynamicFormTableRecord.setCol18(this.custcol_18);
		dynamicFormTableRecord.setCol19(this.custcol_19);
		dynamicFormTableRecord.setCol20(this.custcol_20);
		dynamicFormTableRecord.setCol21(this.custcol_21);
		dynamicFormTableRecord.setCol22(this.custcol_22);
		dynamicFormTableRecord.setCol23(this.custcol_23);
		dynamicFormTableRecord.setCol24(this.custcol_24);
		dynamicFormTableRecord.setCol25(this.custcol_25);
		dynamicFormTableRecord.setCol26(this.custcol_26);
		dynamicFormTableRecord.setCol27(this.custcol_27);
		dynamicFormTableRecord.setCol28(this.custcol_28);
		dynamicFormTableRecord.setCol29(this.custcol_29);
		dynamicFormTableRecord.setCol30(this.custcol_30);
		dynamicFormTableRecord.setCol31(this.custcol_31);
		dynamicFormTableRecord.setCol32(this.custcol_32);
		dynamicFormTableRecord.setCol33(this.custcol_33);
		dynamicFormTableRecord.setCol34(this.custcol_34);
		dynamicFormTableRecord.setCol35(this.custcol_35);
		dynamicFormTableRecord.setCol36(this.custcol_36);
		dynamicFormTableRecord.setCol37(this.custcol_37);
		dynamicFormTableRecord.setCol38(this.custcol_38);
		dynamicFormTableRecord.setCol39(this.custcol_39);
		dynamicFormTableRecord.setCol40(this.custcol_40);
		dynamicFormTableRecord.setCol41(this.custcol_41);
		dynamicFormTableRecord.setCol42(this.custcol_42);
		dynamicFormTableRecord.setCol43(this.custcol_43);
		dynamicFormTableRecord.setCol44(this.custcol_44);
		dynamicFormTableRecord.setCol45(this.custcol_45);
		dynamicFormTableRecord.setCol46(this.custcol_46);
		dynamicFormTableRecord.setCol47(this.custcol_47);
		dynamicFormTableRecord.setCol48(this.custcol_48);
		dynamicFormTableRecord.setCol49(this.custcol_49);
		dynamicFormTableRecord.setCol50(this.custcol_50);
		dynamicFormTableRecord.setCol51(this.custcol_51);
		dynamicFormTableRecord.setCol52(this.custcol_52);
		dynamicFormTableRecord.setCol53(this.custcol_53);
		dynamicFormTableRecord.setCol54(this.custcol_54);
		dynamicFormTableRecord.setCol55(this.custcol_55);
		dynamicFormTableRecord.setCol56(this.custcol_56);
		dynamicFormTableRecord.setCol57(this.custcol_57);
		dynamicFormTableRecord.setCol58(this.custcol_58);
		dynamicFormTableRecord.setCol59(this.custcol_59);
		dynamicFormTableRecord.setCol60(this.custcol_60);
		dynamicFormTableRecord.setCol61(this.custcol_61);
		dynamicFormTableRecord.setCol62(this.custcol_62);
		dynamicFormTableRecord.setCol63(this.custcol_63);
		dynamicFormTableRecord.setCol64(this.custcol_64);
		dynamicFormTableRecord.setCol65(this.custcol_65);
		dynamicFormTableRecord.setCol66(this.custcol_66);
		dynamicFormTableRecord.setCol67(this.custcol_67);
		dynamicFormTableRecord.setCol68(this.custcol_68);
		dynamicFormTableRecord.setCol69(this.custcol_69);
		dynamicFormTableRecord.setCol70(this.custcol_70);
		dynamicFormTableRecord.setCol71(this.custcol_71);
		dynamicFormTableRecord.setCol72(this.custcol_72);
		dynamicFormTableRecord.setCol73(this.custcol_73);
		dynamicFormTableRecord.setCol74(this.custcol_74);
		dynamicFormTableRecord.setCol75(this.custcol_75);
		dynamicFormTableRecord.setCol76(this.custcol_76);
		dynamicFormTableRecord.setCol77(this.custcol_77);
		dynamicFormTableRecord.setCol78(this.custcol_78);
		dynamicFormTableRecord.setCol79(this.custcol_79);
		dynamicFormTableRecord.setCol80(this.custcol_80);
		dynamicFormTableRecord.setCol81(this.custcol_81);
		dynamicFormTableRecord.setCol82(this.custcol_82);
		dynamicFormTableRecord.setCol83(this.custcol_83);
		dynamicFormTableRecord.setCol84(this.custcol_84);
		dynamicFormTableRecord.setCol85(this.custcol_85);
		dynamicFormTableRecord.setCol86(this.custcol_86);
		dynamicFormTableRecord.setCol87(this.custcol_87);
		dynamicFormTableRecord.setCol88(this.custcol_88);
		dynamicFormTableRecord.setCol89(this.custcol_89);
		dynamicFormTableRecord.setCol90(this.custcol_90);
		dynamicFormTableRecord.setCol91(this.custcol_91);
		dynamicFormTableRecord.setCol92(this.custcol_92);
		dynamicFormTableRecord.setCol93(this.custcol_93);
		dynamicFormTableRecord.setCol94(this.custcol_94);
		dynamicFormTableRecord.setCol95(this.custcol_95);
		dynamicFormTableRecord.setCol96(this.custcol_96);
		dynamicFormTableRecord.setCol97(this.custcol_97);
		dynamicFormTableRecord.setCol98(this.custcol_98);
		dynamicFormTableRecord.setCol99(this.custcol_99);
		return dynamicFormTableRecord;
		
	}
	
}
