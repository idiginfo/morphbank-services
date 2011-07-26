/*******************************************************************************
 * Copyright (c) 2011 Greg Riccardi, Guillaume Jimenez.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Greg Riccardi - initial API and implementation
 *     Guillaume Jimenez - initial API and implementation
 ******************************************************************************/
package net.morphbank.mbsvc3.webservices.tools;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;



public class RedirectSysStreams extends OutputStream implements Runnable {

	StringBuffer output;
	
	public RedirectSysStreams(StringBuffer output) {
		this.output = output;
	}
	
	
	@Override
	public void run() {
		System.setOut(new PrintStream(this, true));
		System.setErr(new PrintStream(this, true));
	}

	@Override
	public void write(int b) throws IOException {
		output.append(String.valueOf((char) b));
		
	}
	
	@Override  
    public void write(byte[] b, int off, int len) throws IOException {  
		output.append(new String(b, off, len)); 
    }  
  
    @Override  
    public void write(byte[] b) throws IOException {  
      write(b, 0, b.length);  
    }  




}
