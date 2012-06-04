package net.morphbank.mbsvc3.webservices.tools.test;

import net.morphbank.MorphbankConfig;
import net.morphbank.mbsvc3.webservices.tools.ValidateCustomXls;

public class ValidateCustomXlsTest {

	/**
	 * Put files to validate (custom workbooks only)
	 * @param args
	 */
	public static void main(String[] args) {
		ValidateCustomXls test = new ValidateCustomXls("/home/gjimenez/Morphbank/tests/test.xls"
				, true, MorphbankConfig.PERSISTENCE_MBPROD);
		boolean passed = test.checkEverything();
		System.out.println(passed);
	}

}
