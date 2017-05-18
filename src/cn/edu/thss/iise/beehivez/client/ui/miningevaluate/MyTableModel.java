/**
 * BeehiveZ is a business process model and instance management system.
 * Copyright (C) 2011  
 * Institute of Information System and Engineering, School of Software, Tsinghua University,
 * Beijing, China
 *
 * Contact: jintao05@gmail.com 
 *
 * This program is a free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation with the version of 2.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package cn.edu.thss.iise.beehivez.client.ui.miningevaluate;

import javax.swing.table.AbstractTableModel;

/**
 * �̳�AbstractTableModel
 */

public class MyTableModel extends AbstractTableModel {

    private String[] columnNames;
    private Object[][] datas;

    public void setColumnNames(String[] columnNames) {
        this.columnNames = columnNames;
    }

    public String[] getColumnNames() {
        return this.columnNames;
    }

    public void setDatas(Object[][] datas) {
        this.datas = datas;
    }

    public Object[][] getDatas() {
        return this.datas;
    }

    /**
     * �õ��е�����
     * @return int
     */
    public int getColumnCount() {
        return columnNames.length;
    }

    /**
     * �õ��е�����
     * @return int
     */
    public int getRowCount() {
        return datas.length;
    }

    /**
     * ��ȡ����Ϣ
     * @param col int
     * @return String
     */
    public String getColumnName(int col) {
        return columnNames[col];
    }

    /**
     * ��ȡ��Ԫ����Ϣ
     * @param row int
     * @param col int
     * @return Object
     */
    public Object getValueAt(int row, int col) {
        return datas[row][col];
    }

    /**
     * ʵ���������boolean�Զ�ת��JCheckbox
     * ��Ҫ�Լ���celleditor��ô�鷳�ɡ�jtable�Զ�֧��Jcheckbox
     * ֻҪ����tablemodel��getColumnClass����һ��boolean��class
     * jtable���Զ���һ��Jcheckbox����
     * ���value��true����falseֱ�Ӷ�table���Ǹ�cell��ֵ�Ϳ���
     * @param c int
     * @return Class
     */
    public Class getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }

    /**
     * �����ĸ���Ԫ���ǿɱ༭
     * @param row int
     * @param col int
     * @return boolean
     */
    public boolean isCellEditable(int row, int col) {
        if (col ==0) { //�������һ���ǿɱ༭
            return true;
        } else {
            return false;
        }

    }

    /*
     * Don't need to implement this method unless your table's
     * data can change.
     */
    public void setValueAt(Object value, int row, int col) {
        datas[row][col] = value;
        fireTableCellUpdated(row, col);
        //printDebugData();
    }

    /**
     * ��ʾ��Ϣ
     */
//    private void printDebugData() {
//        int numRows = getRowCount();
//        int numCols = getColumnCount();
//
//        for (int i = 0; i < numRows; i++) {
//            System.out.print("    row " + i + ":");
//            for (int j = 0; j < numCols; j++) {
//                System.out.print(" " + datas[i][j]);
//            }
//            System.out.println();
//        }
//        System.out.println("--------------------------");
//    }
}