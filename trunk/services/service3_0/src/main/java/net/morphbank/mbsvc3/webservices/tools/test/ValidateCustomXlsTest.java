package net.morphbank.mbsvc3.webservices.tools.test;

import net.morphbank.MorphbankConfig;
import net.morphbank.mbsvc3.webservices.tools.ValidateCustomXls;

public class ValidateCustomXlsTest {

	/**
	 * Put files to validate (custom workbooks only)
	 * @param args
	 */
	public static void main(String[] args) {
		//ValidateCustomXls test = new ValidateCustomXls("/home/gjimenez/Morphbank/uploads/Rebecca/Aptostichus_mb_customWorkbook.xls"
		ValidateCustomXls test = new ValidateCustomXls("C:/sg11x/workbooks/test-webservices-workbook/customWorkbook_TTRS_Birds-0213.xls" 
				, true, MorphbankConfig.PERSISTENCE_MBPROD);
		boolean testResult = test.checkEverything();
		if(testResult)
			System.out.println("Test Passed");
		else
			System.out.println("Test Failed");
	}

}
