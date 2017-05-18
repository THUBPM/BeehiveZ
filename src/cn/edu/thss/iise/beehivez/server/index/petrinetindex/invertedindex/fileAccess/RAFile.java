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

package cn.edu.thss.iise.beehivez.server.index.petrinetindex.invertedindex.fileAccess;

import java.io.IOException;

public interface RAFile {
	public long length() throws IOException;

	public void seek(long pos) throws IOException;

	public void read(byte[] b) throws IOException;

	public int readInt() throws IOException;

	public long readLong() throws IOException;

	public String readString() throws IOException;

	public boolean readBoolean() throws IOException;

	public void write(byte[] b) throws IOException;

	public void writeInt(int v) throws IOException;

	public void writeLong(long v) throws IOException;

	public void writeString(String v) throws IOException;

	public void writeBoolean(boolean v) throws IOException;

	public void close() throws IOException;
}
