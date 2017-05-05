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

package cn.edu.thss.iise.beehivez.util.loginfo;

import org.processmining.framework.models.petrinet.PetriNet;

/**
 * The interface of algorithms for calculate the degree of information completeness of an event log 
 * for a specified workflow model.
 * 
 * @author hedong
 *
 */
public interface LogInfo {
	/**
	 * Given the log file and the worklfow model, calculate the degree of information completeness of the log.
	 * @param logfile	event log file.
	 * @param pnfile	worklfow model described with petri net.
	 * @return	the degree of information completeness of the event log.
	 * @throws Exception	
	 */
	double info(String logfile,PetriNet	pn)throws Exception;
}
