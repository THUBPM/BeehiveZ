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
/*Attention: Must use UTF-8 character set, otherwise the query string 
 * can not be parsed.
 * */
package cn.edu.thss.iise.beehivez.server.index.petrinetindex.rltMatrix;

/**
 * define basic relationship between transitions as constants each relation
 * corresponds to a bit in a byte
 * 
 * @author zhougz 2010.03.31
 * 
 */
public interface RltConstants {

	static final String PARALLEL = "\u2225";
	static final String DIR_SUCC = ">";
	static final String INDIR_SUCC = "\u00BB";
	static final String DIR_CASUAL = "\u2192";
	static final String INDIR_CASUAL = "\u21A0";
	static final String CYCLE = "\u229A";
	static final String MUTEX = "\u00D7";

	static final byte BIT_PARALLEL = 1;
	static final byte BIT_DIR_SUCC = 2;
	static final byte BIT_INDIR_SUCC = 4;
	static final byte BIT_DIR_CASUAL = 8;
	static final byte BIT_INDIR_CASUAL = 16;
	static final byte BIT_CYCLE = 32;
	static final byte BIT_MUTEX = 64;

	static final String[] ARR_RELATIONS = { PARALLEL, DIR_SUCC, INDIR_SUCC,
			DIR_CASUAL, INDIR_CASUAL, CYCLE, MUTEX };
	static final byte[] ARR_BIT_RELATIONS = { BIT_PARALLEL, BIT_DIR_SUCC,
			BIT_INDIR_SUCC, BIT_DIR_CASUAL, BIT_INDIR_CASUAL, BIT_CYCLE,
			BIT_MUTEX };
	static final String[] ARR_STR_RELATIONS = { "Parallel", "Direct successor",
			"Indirect successor", "Direct casual", "Indirect casual", "Cycle",
			"Mutex" };

}
