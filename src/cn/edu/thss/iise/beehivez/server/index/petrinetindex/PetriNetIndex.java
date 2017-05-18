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
package cn.edu.thss.iise.beehivez.server.index.petrinetindex;

import cn.edu.thss.iise.beehivez.server.datamanagement.pojo.ProcessObject;
import cn.edu.thss.iise.beehivez.server.index.BPMIndex;

/**
 * @author JinTao 2009.9.5
 * 
 */
public abstract class PetriNetIndex extends BPMIndex {

	// @Override
	// public String getSupportedModelType() {
	// return ProcessObject.TYPEPNML;
	// }

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.edu.thss.iise.beehivez.server.index.BPMIndex#getSupportedModelType()
	 */
	// // only the name
	// protected String name = null;
	//
	// // include the path of package
	// protected String javaClassName = null;
	//
	// public PetriNetIndex() {
	// init();
	// }
	//
	// // inintialize the name and javaClassName
	// protected abstract void init();
	//
	// public abstract boolean create();
	//
	// public abstract boolean destroy();
	//
	// public abstract void close();
	//
	// public abstract boolean open();
	//
	// public String getName() {
	// return name;
	// }
	//
	// public String getJavaClassName() {
	// return javaClassName;
	// }
	//	
	//
	// public abstract void addPetriNet(PetrinetObject pno);
	//
	// public abstract void delPetriNet(PetrinetObject pno);
	//
	// /**
	// * @param o
	// * maybe String or petri net object
	// *
	// * @param similarity
	// * float value between 0 and 1
	// *
	// * @return set of process id and similarity
	// */
	// public abstract TreeSet<ProcessQueryResult> getPetriNet(Object o,
	// float similarity);
	// public abstract boolean supportSimilarQuery();
	//
	// public abstract boolean supportSimilarLabel();
	//
	// public abstract boolean supportTextQuery();
	//
	// public abstract boolean supportGraphQuery();
	//
	// public abstract float getStorageSizeInMB();
}
