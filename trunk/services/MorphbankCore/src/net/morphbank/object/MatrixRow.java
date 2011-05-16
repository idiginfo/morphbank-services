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

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Query;
import javax.persistence.Table;

import net.morphbank.MorphbankConfig;

@Entity
@Table(name = "MatrixRow")
public class MatrixRow implements IdObject {

	public String getObjectTypeIdStr(){
		return "MatrixRow";
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "matrixId")
	private Matrix matrix;
	private int row;
	private String rowValue;

	public MatrixRow() {

	}

	public MatrixRow(Matrix matrix, int row, String value) {
		setMatrix(matrix);
		setRow(row);
		setRowValue(value);
	}

	public boolean persist() {
		if (!validate()){
			return false;
		}
		try {
			MorphbankConfig.getEntityManager().persist(this);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean validate(){
		if (matrix==null){
			return false;
		}
		return true;
	}

	public static MatrixRow getMatrixRowValue(Matrix matrix, int row) {
		String query = "from MatrixRow where matrixId=? and row=?";
		Query sessionQuery = null;
		try {
			sessionQuery = MorphbankConfig.getEntityManager().createQuery(query.toString());
			sessionQuery.setParameter(1, matrix.getId());
			sessionQuery.setParameter(2, row);
			Object result = sessionQuery.getSingleResult();
			if (result instanceof MatrixRow) {
				return (MatrixRow) result;
			}
		} catch (Exception e) {
			e.printStackTrace(System.out);
			return null;
		}
		return null;
	}

	public static String getCellValue(Matrix matrix, int row, int col) {

		return null;
	}

	public String getColValue(int col) {
		return getRowValue().substring(col, 1);
	}

	public void setColValue(int col, String value) {
		String pre = getRowValue().substring(0, col);
		String post = getRowValue().substring(col + 1);
		setRowValue(pre + value + post);
	}

	public int getId() {
		// TODO Auto-generated method stub
		return id;
	}

	public Matrix getMatrix() {
		return matrix;
	}

	public void setMatrix(Matrix matrix) {
		this.matrix = matrix;
	}

	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public String getRowValue() {
		return rowValue;
	}

	public void setRowValue(String value) {
		this.rowValue = value;
	}

	public void setId(int id) {
		this.id = id;
	}
}
