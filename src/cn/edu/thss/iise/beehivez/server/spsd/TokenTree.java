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

package cn.edu.thss.iise.beehivez.server.spsd;

import java.util.Vector;

import org.processmining.framework.models.petrinet.Place;
import org.processmining.framework.models.petrinet.Token;
import org.processmining.framework.models.petrinet.Transition;

public class TokenTree extends Token {
	private TokenTree parent = null;
	private Vector<TokenTree> children = new Vector<TokenTree>();
	private Vector<Transition> sequence = new Vector<Transition>();
	private Vector<Place> places = new Vector<Place>();

	public void setParent(TokenTree tk) {
		parent = tk;
	}

	public TokenTree getParent() {
		return parent;
	}

	public Vector<TokenTree> getChildren() {
		return children;
	}

	public Vector<Transition> getSequence() {
		return sequence;
	}

	public Vector<Transition> getTransitions() {
		return sequence;
	}

	public Vector<Place> getPlaces() {
		return places;
	}

	public boolean isRoot() {
		return parent == null;
	}

	public void addChild(TokenTree c) {
		children.add(c);
	}

	public int getChildrenNum() {
		return children.size();
	}

	public void addTransition(Transition t) {
		if (!sequence.contains(t)) {
			sequence.add(t);
		}
	}

	public void addPlace(Place p) {
		if (!places.contains(p)) {
			places.add(p);
		}
	}
}
