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
import java.util.Iterator;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Query;
import javax.persistence.Table;

import net.morphbank.MorphbankConfig;

@Entity
@Table(name = "Matrix")
@DiscriminatorValue("Matrix")
public class Matrix extends BaseObject implements Serializable {
	static final long serialVersionUID = 1;

	public static final String ROW_ROLE = "row";
	public static final String COL_ROLE = "column";
	public static final String CELL_ROLE = "cell";

	private int numRows;
	private int numChars;
	private String missing;
	private String gap;
	@OneToMany(mappedBy = "matrix", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<MatrixCell> cells;

	public Matrix(int id, String name, User user, Group group) {
		super(id, name, user, group);
		// setObjectTypeId("Matrix");
	}

	public Matrix() {
		super();
		// setObjectTypeId("Matrix");
	}

	public MatrixCell getCell(int row, int col) {
		StringBuffer query = new StringBuffer();
		query
				.append("from MatrixCell where matrixId=? and rowNum=? and columnNum=?");
		Query sessionQuery = null;
		try {
			sessionQuery = MorphbankConfig.getEntityManager().createQuery(
					query.toString());
			sessionQuery.setParameter(1, this.getId());
			sessionQuery.setParameter(2, row);
			sessionQuery.setParameter(3, col);
			Object result = sessionQuery.getSingleResult();
			if (result instanceof MatrixCell) {
				return (MatrixCell) result;
			}
		} catch (Exception e) {
			e.printStackTrace(System.out);
			return null;
		}
		return null;
	}

	public MatrixCellValue getMatrixCellValue(int row, int col) {
		return MatrixCellValue.getMatrixCellValue(this, row, col);
	}

	public List<MatrixCell> getCells() {
		return cells;
	}

	public void setCells(List<MatrixCell> cells) {
		this.cells = cells;
	}

	public String getGap() {
		return gap;
	}

	public void setGap(String gap) {
		this.gap = gap;
	}

	public String getMissing() {
		return missing;
	}

	public void setMissing(String missing) {
		this.missing = missing;
	}

	public int getNumChars() {
		return numChars;
	}

	public void setNumChars(int numChars) {
		this.numChars = numChars;
	}

	public int getNumRows() {
		return numRows;
	}

	public void setNumRows(int numRows) {
		this.numRows = numRows;
	}

	public BaseObject getRowHeader(int index) {
		return getFirstRelatedObjectByRoleIndex(ROW_ROLE, index);
	}

	public BaseObject getColHeader(int index) {
		return getFirstRelatedObjectByRoleIndex(COL_ROLE, index);
	}

	public void print(java.io.PrintStream out) {
		out.print("Matrix ");
		out.print(getName());
		out.print(" id is: ");
		out.print(getId());
		out.println(" size: " + getNumRows() + " x " + getNumChars());
		out.println("Description:");
		out.println(getDescription());
		out.println("Comments:");
		List<Annotation> annots = Annotation.getAnnotations(getId());
		if (annots != null) {
			Iterator<Annotation> annotations = annots.iterator();
			while (annotations.hasNext()) {
				annotations.next().print(out);
			}
		}
		out.println("Column headings");
		for (int col = 1; col <= getNumChars(); col++) {
			MbCharacter mbCharacter = (MbCharacter) getColHeader(col);
			mbCharacter.print(out);
		}
		out.println("\nRows");
		for (int row = 1; row <= (getNumRows()); row++) {
			out.print(getRowHeader(row).getName());
			out.print('\t');
			// MatrixRow mRow = MatrixRow.getMatrixRowValue(this, row);
			// out.print(mRow.getRowValue());
			for (int i = 1; i <= getNumChars(); i++) {
				// out.print(getCell(row, i).getValue());
				out.print(getMatrixCellValue(row, i).getValue());
			}
			out.println();
		}
	}
}
