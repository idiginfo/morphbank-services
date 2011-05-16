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
import javax.persistence.EntityManager;
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
@Table(name = "MatrixCellValue")
public class MatrixCellValue implements IdObject {

	public String getObjectTypeIdStr(){
		return "MatrixCellValue";
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "matrixId")
	private Matrix matrix;
	private int row;
	private int col;
	private String value;

	// MatrixCell cell;

	public MatrixCellValue() {

	}

	public MatrixCellValue(Matrix matrix, int row, int col, String value) {
		setMatrix(matrix);
		setRow(row);
		setCol(col);
		setValue(value);
	}
	
	public boolean validate(){
		if (matrix == null){
			return false;
		}
		return true;
	}

	static final String insertQuery = "insert into MatrixCellValue (matrixId, row, col, value) values (?1,?2,?3,?4)";

	public static boolean create(int matrixId, int row, int col, String value) {
		try {
			EntityManager em = MorphbankConfig.getEntityManager();
			Query insert = em.createNativeQuery(insertQuery);
			insert.setParameter(1, matrixId);
			insert.setParameter(2, row);
			insert.setParameter(3, col);
			insert.setParameter(4, value);
			insert.executeUpdate();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public static MatrixCellValue getMatrixCellValue(Matrix matrix, int row,
			int col) {
		String query = "select m from MatrixCellValue m where m.matrix.id = ?1 and m.row= ?2 and m.col = ?3";
		Query sessionQuery = null;
		try {
			sessionQuery = MorphbankConfig.getEntityManager().createQuery(
					query.toString());
			sessionQuery.setParameter(1, matrix.getId());
			sessionQuery.setParameter(2, row);
			sessionQuery.setParameter(3, col);
			Object result = sessionQuery.getSingleResult();
			if (result instanceof MatrixCellValue) {
				return (MatrixCellValue) result;
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

	public int getId() {
		// TODO Auto-generated method stub
		return id;
	}

	public int getCol() {
		return col;
	}

	public void setCol(int col) {
		this.col = col;
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

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public void setId(int id) {
		this.id = id;
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
}
