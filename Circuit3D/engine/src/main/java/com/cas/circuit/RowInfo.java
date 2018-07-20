package com.cas.circuit;

import lombok.Getter;
import lombok.Setter;

// info about each row/column of the matrix for simplification purposes
@Getter
@Setter
public class RowInfo {
	public static final int ROW_NORMAL = 0; // ordinary value 原始数据
	public static final int ROW_CONST = 1; // value is constant 固定值
	public static final int ROW_EQUAL = 2; // value is equal to another value 等效数据

	private int nodeEq;
	private int type = ROW_NORMAL;
	private int mapCol;
	private int mapRow;

	private double value;
	private boolean rsChanges; // row's right side changes
	private boolean lsChanges; // row's left side changes
	private boolean dropRow; // row is not needed in matrix
	@Override
	public String toString() {
		return "RowInfo [nodeEq=" + nodeEq + ", type=" + type + ", mapCol=" + mapCol + ", mapRow=" + mapRow + ", value=" + value + ", rsChanges=" + rsChanges + ", lsChanges=" + lsChanges + ", dropRow=" + dropRow + "]";
	}
	
	
}
