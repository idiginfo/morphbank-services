/*******************************************************************************
 * Copyright (c) 2011 Greg Riccardi, Guillaume Jimenez.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the GNU Public License v2.0
 *  which accompanies this distribution, and is available at
 *  http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 *  
 *  Contributors:
 *  	Greg Riccardi - initial API and implementation
 * 	Guillaume Jimenez - initial API and implementation
 ******************************************************************************/
package net.morphbank.object;

import java.io.Serializable;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "MatrixCell")
@DiscriminatorValue("MatrixCell")
public class MatrixCell extends BaseObject implements Serializable {
	static final long serialVersionUID = 1;

	private String value;
	private int rowNum;
	private int columnNum;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "matrixId")
	private Matrix matrix;

	public MatrixCell(int id, User user, Group group, int rowIndex,
			int colIndex, String value, Matrix matrix) {
		super(id, value, user, group);
		setRowNum(rowIndex);
		setColumnNum(colIndex);
		setValue(value);
		setMatrix(matrix);
	}

	public MatrixCell() {
		super();
	}

	public int getColumnNum() {
		return columnNum;
	}

	public Matrix getMatrix() {
		return matrix;
	}

	public void setMatrix(Matrix matrix) {
		this.matrix = matrix;
	}

	public void setColumnNum(int colIndex) {
		this.columnNum = colIndex;
	}

	public int getRowNum() {
		return rowNum;
	}

	public void setRowNum(int rowIndex) {
		this.rowNum = rowIndex;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
