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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class BufferedRAFile {
	private final boolean readOnly;
	private final long bufferLength;
	private RandomAccessFile file;
	private MappedByteBuffer buffer;
	private FileChannel channel;
	private boolean bufferModified;

	public BufferedRAFile(String path, boolean onlyRead, long bufferLen)
			throws FileNotFoundException {
		readOnly = onlyRead;
		bufferLength = bufferLen;
		file = new RandomAccessFile(path, readOnly ? "r" : "rw");

		this.channel = file.getChannel();
		try {
			buffer = channel.map(readOnly ? FileChannel.MapMode.READ_ONLY
					: FileChannel.MapMode.READ_WRITE, 0, bufferLength);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public long length() throws IOException {
		return file.length();
	}

	public void seek(long pos) {
		buffer.position((int) pos);
	}

	public long getFilePointer() {
		return buffer.position();
	}

	public int readInt() {
		return buffer.getInt();
	}

	public long readLong() {
		return buffer.getLong();
	}

	public void read(byte[] b, int offset, int len) {
		buffer.get(b, offset, len);
	}

	public void read(byte[] b) {
		buffer.get(b);
	}

	public boolean readBoolean() {
		byte b = buffer.get();
		if (b == (byte) 0)
			return false;
		return true;
	}

	public void writeInt(int v) {
		bufferModified = true;
		buffer.putInt(v);
	}

	public void writeLong(long v) {
		bufferModified = true;
		buffer.putLong(v);
	}

	public void write(byte[] b) {
		bufferModified = true;
		buffer.put(b);
	}

	public void writeBoolean(boolean v) {
		bufferModified = true;
		if (v)
			buffer.put((byte) 1);
		else
			buffer.put((byte) 0);
	}

	public void close() throws IOException {
		if (buffer != null && bufferModified) {
			try {
				buffer.force();
			} catch (Throwable t) {
				try {
					buffer.force();
				} catch (Throwable t1) {

				}
			}
		}

		buffer = null;
		channel = null;

		file.close();
		System.gc();

	}

}
