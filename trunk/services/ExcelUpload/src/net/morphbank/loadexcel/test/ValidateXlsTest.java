package net.morphbank.loadexcel.test;

import net.morphbank.loadexcel.SheetReader;
import net.morphbank.loadexcel.ValidateXls;

public class ValidateXlsTest {

	/**
	 * Put test files here.
	 * @param args
	 */
	public static void main(String[] args) {
		SheetReader sheetReader = new SheetReader("/home/gjimenez/palearticTestmb3a-1.xls", null);
//		SheetReader sheetReader = new SheetReader("/home/gjimenez/Morphbank/uploads/Elijah/African_Paridris_mb3a_DPand ET_GJ.xls", null);
		ValidateXls test = new ValidateXls(sheetReader, true, "loadData.properties");
		System.out.println(test.checkEverything());
	}

}
