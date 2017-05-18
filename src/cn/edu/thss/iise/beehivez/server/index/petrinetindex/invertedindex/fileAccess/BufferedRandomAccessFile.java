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

public class BufferedRandomAccessFile implements RAFile {
	private RandomAccessFile file;
	private byte[] buffer;
	private boolean bufferDirty = false;
	/**
	 * buffer����ʼλ�����ļ��е�ƫ��
	 */
	private long bufferOffset;

	/**
	 * ��ǰbuffer�е���Чbyte��
	 */
	private long bufferSize;

	private long seekPosition;

	private final boolean readOnly;

	public BufferedRandomAccessFile(String fileName, int bufSize,
			boolean onlyRead) throws Exception {
		if (bufSize > Integer.MAX_VALUE)
			throw new Exception("the size of buffer is too large!");
		readOnly = onlyRead;
		file = new RandomAccessFile(fileName, readOnly ? "r" : "rw");
		buffer = new byte[bufSize];

		bufferSize = 0;// file.length()<buffer.length?file.length():buffer.length;
		// file.readFully(buffer, 0, (int)bufferSize);
		bufferOffset = 0;
		seekPosition = 0;
	}

	private void writeOut() throws IOException {
		file.seek(bufferOffset);
		file.write(buffer, 0, (int) bufferSize);
	}

	private void readIn() throws IOException {
		long subOffset = seekPosition % buffer.length;
		long fileLength = file.length();
		long readLength = fileLength - (seekPosition - subOffset);

		if (readLength < 0)
			throw new IOException("read beyond the end!");
		if (readLength > buffer.length)
			readLength = buffer.length;

		bufferOffset = seekPosition - subOffset;
		bufferSize = readLength;
		bufferDirty = false;

		file.seek(bufferOffset);
		file.read(buffer, 0, (int) readLength);

	}

	public long getFilePointer() {
		return seekPosition;
	}

	public long length() throws IOException {
		if (bufferSize < buffer.length)
			return bufferSize + bufferOffset;
		return file.length();
	}

	public void seek(long pos) throws IOException {
		if (pos > file.length() && pos > bufferOffset + bufferSize) {
			if (pos > bufferOffset && pos < bufferOffset + buffer.length)
				bufferSize = pos - bufferOffset;
			else {
				long tempSize = pos - file.length();
				if (tempSize > 1 << 18) {
					tempSize = 1 << 18;
				}

				byte[] temp = new byte[(int) tempSize];

				long p = file.length();

				for (; p < pos - tempSize; p += tempSize) {
					file.seek(p);
					file.write(temp, 0, (int) tempSize);
				}
				file.seek(pos);
				file.write(temp, 0, (int) (pos - p));
				// realPosition = position;

			}
		}
		// throw new IOException("seek beyond the end!");
		seekPosition = pos;
	}

	private void readBytes(byte[] b)// throws IOException
	{
		try {
			checkBuffer();
			if (bufferOffset + bufferSize < seekPosition + b.length) {

				long prefixLength = bufferOffset + bufferSize - seekPosition;
				long suffixLength = b.length - prefixLength;

				System.arraycopy(buffer, (int) (seekPosition - bufferOffset),
						b, 0, (int) prefixLength);
				seekPosition += prefixLength;
				if (bufferDirty)
					writeOut();

				long bytePosition = prefixLength;
				if (suffixLength > buffer.length) {
					long readLen = suffixLength - suffixLength % buffer.length;
					file.read(b, (int) bytePosition, (int) readLen);
					seekPosition += readLen;
					bytePosition += readLen;
					suffixLength -= readLen;
				}

				readIn();
				if (suffixLength > bufferSize)
					throw new IOException("try to read beyond the end!");
				System.arraycopy(buffer, 0, b, (int) bytePosition,
						(int) suffixLength);
				seekPosition += suffixLength;

			} else {
				System.arraycopy(buffer, (int) (seekPosition - bufferOffset),
						b, 0, b.length);
				seekPosition += b.length;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void read(byte[] b) throws IOException {
		readBytes(b);
	}

	public int readInt() throws IOException {
		byte[] b = new byte[4];
		readBytes(b);
		// if(b[3] & 0x)

		if ((b[3] & 0x80) != 0) {
			b[3] = (byte) (b[3] & 0x7f);
			int ch3 = b[3] & 0xff;
			int ch2 = b[2] & 0xff;
			int ch1 = b[1] & 0xff;
			int ch0 = b[0] & 0xff;
			return -((ch3 << 24) + (ch2 << 16) + (ch1 << 8) + (ch0));
		} else {
			int ch3 = b[3] & 0xff;
			int ch2 = b[2] & 0xff;
			int ch1 = b[1] & 0xff;
			int ch0 = b[0] & 0xff;
			return ((ch3 << 24) + (ch2 << 16) + (ch1 << 8) + (ch0));
		}
		// return (b[3]<<24)+(b[2]<<16)+(b[1]<<8)+b[0];
	}

	public long readLong() throws IOException {
		byte[] b = new byte[8];
		readBytes(b);
		if ((b[7] & 0x80) != 0) {
			b[7] = (byte) (b[7] & 0x7f);
			long ch7 = b[7] & 0xff;
			long ch6 = b[6] & 0xff;
			long ch5 = b[5] & 0xff;
			long ch4 = b[4] & 0xff;
			long ch3 = b[3] & 0xff;
			long ch2 = b[2] & 0xff;
			long ch1 = b[1] & 0xff;
			long ch0 = b[0] & 0xff;
			return -((ch7 << 56) + (ch6 << 48) + (ch5 << 40) + (ch4 << 32)
					+ (ch3 << 24) + (ch2 << 16) + (ch1 << 8) + (ch0));
			// return -(((b[7] & 0xff)<<56)+((b[6]& 0xff)<<48)+((b[5]&
			// 0xff)<<40)
			// +((b[4]& 0xff)<<32)+((b[3]& 0xff)<<24)+((b[2]& 0xff)<<16)
			// +((b[1]& 0xff)<<8)+(b[0]));
		} else {
			long ch7 = b[7] & 0xff;
			long ch6 = b[6] & 0xff;
			long ch5 = b[5] & 0xff;
			long ch4 = b[4] & 0xff;
			long ch3 = b[3] & 0xff;
			long ch2 = b[2] & 0xff;
			long ch1 = b[1] & 0xff;
			long ch0 = b[0] & 0xff;
			return ((ch7 << 56) + (ch6 << 48) + (ch5 << 40) + (ch4 << 32)
					+ (ch3 << 24) + (ch2 << 16) + (ch1 << 8) + (ch0));
			// return ((b[7] & 0xff)<<56)+((b[6]& 0xff)<<48)+((b[5]& 0xff)<<40)
			// +((b[4]& 0xff)<<32)+((b[3]& 0xff)<<24)+((b[2]& 0xff)<<16)
			// +((b[1]& 0xff)<<8)+(b[0]);
		}

	}

	public boolean readBoolean() throws IOException {
		byte[] b = new byte[1];
		readBytes(b);
		if (b[0] == (byte) 1)
			return true;
		return false;
	}

	public String readString() throws IOException {
		int len = this.readInt();
		byte[] value = new byte[len];
		readBytes(value);
		return new String(value);
	}

	private void writeBytes(byte[] b) throws IOException {
		checkBuffer();
		/**
		 * ����buffer�ı߽�д�����ʣ���ֽ�����ֽ�ʱ
		 */
		if (bufferOffset + bufferSize < seekPosition + b.length) {
			/**
			 * �����ļ�ĩβд��ʱ
			 */
			if (bufferSize < buffer.length) {
				long suffixLength = b.length
						- (bufferOffset + bufferSize - seekPosition);
				bufferSize = seekPosition + b.length - bufferOffset;
				bufferSize = bufferSize > buffer.length ? buffer.length
						: bufferSize;
				writeBytes(b);
			} else {
				long prefixLength = bufferOffset + bufferSize - seekPosition;
				long suffixLength = b.length - prefixLength;

				copyByteToBuffer(b, 0, (int) prefixLength);
				writeOut();

				long bytePosition = prefixLength;
				for (; suffixLength > buffer.length; suffixLength -= buffer.length, bytePosition += buffer.length) {
					file.write(b, (int) bytePosition, buffer.length);
					seekPosition += buffer.length;
				}

				readIn();
				if (bufferSize < suffixLength) {
					bufferSize = suffixLength;
				}
				copyByteToBuffer(b, (int) bytePosition, (int) suffixLength);

			}
		} else {
			copyByteToBuffer(b, 0, b.length);
		}
	}

	private void copyByteToBuffer(byte[] src, int srcStart, int length) {
		System.arraycopy(src, srcStart, buffer,
				(int) (seekPosition - bufferOffset), length);
		seekPosition += length;
		bufferDirty = true;
	}

	private void checkBuffer() throws IOException {
		if (seekPosition < bufferOffset
				|| seekPosition >= bufferOffset + bufferSize) {
			if (bufferDirty)
				writeOut();
			readIn();
		}
	}

	public void write(byte[] b) throws IOException {
		writeBytes(b);
	}

	public void writeInt(int value) throws IOException {
		byte[] b = new byte[4];
		if (value < 0) {
			value = -value;
			b[0] = (byte) (value & 0xff);
			b[1] = (byte) ((value >> 8) & 0xff);
			b[2] = (byte) ((value >> 16) & 0xff);
			b[3] = (byte) (((value >> 24) | 0x80) & 0xff);
		} else {
			b[0] = (byte) (value & 0xff);
			b[1] = (byte) ((value >> 8) & 0xff);
			b[2] = (byte) ((value >> 16) & 0xff);
			b[3] = (byte) ((value >> 24) & 0xff);
		}
		// throw new IOException("not support negative number!");

		writeBytes(b);
	}

	public void writeLong(long value) throws IOException {
		byte[] b = new byte[8];
		if (value < 0) {
			value = -value;
			b[0] = (byte) (value & 0xff);
			b[1] = (byte) ((value >> 8) & 0xff);
			b[2] = (byte) ((value >> 16) & 0xff);
			b[3] = (byte) ((value >> 24) & 0xff);
			b[4] = (byte) ((value >> 32) & 0xff);
			b[5] = (byte) ((value >> 40) & 0xff);
			b[6] = (byte) ((value >> 48) & 0xff);
			b[7] = (byte) (((value >> 56) | 0x80) & 0xff);
		} else {
			b[0] = (byte) (value & 0xff);
			b[1] = (byte) ((value >> 8) & 0xff);
			b[2] = (byte) ((value >> 16) & 0xff);
			b[3] = (byte) ((value >> 24) & 0xff);
			b[4] = (byte) ((value >> 32) & 0xff);
			b[5] = (byte) ((value >> 40) & 0xff);
			b[6] = (byte) ((value >> 48) & 0xff);
			b[7] = (byte) (((value >> 56)) & 0xff);
		}
		// throw new IOException("not support negative number!");

		writeBytes(b);
	}

	public void writeBoolean(boolean value) throws IOException {
		if (value)
			writeBytes(new byte[] { 1 });
		else
			writeBytes(new byte[] { 0 });
	}

	public void writeString(String value) throws IOException {
		this.writeInt(value.length());
		writeBytes(value.getBytes());
	}

	public void close() throws IOException {
		if (bufferDirty)
			writeOut();
	}

	/**
	 * public void seek(long position)throws IOException {
	 * 
	 * if (!readOnly && file.length() < position) { long tempSize = position -
	 * file.length();
	 * 
	 * if (tempSize > 1 << 18) { tempSize = 1 << 18; }
	 * 
	 * byte[] temp = new byte[(int) tempSize];
	 * 
	 * try { long pos = file.length();
	 * 
	 * for (; pos < position - tempSize; pos += tempSize) { file.seek(pos);
	 * file.write(temp, 0, (int) tempSize); }
	 * 
	 * file.seek(pos); file.write(temp, 0, (int) (position - pos));
	 * 
	 * realPosition = position; } catch (IOException e) { //appLog.logContext(e,
	 * null);
	 * 
	 * throw e; } }
	 * 
	 * seekPosition = position; }
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * // byte read(long pos)����ȡ��ǰ�ļ�POSλ�����ڵ��ֽ� //
	 * bufstartpos��bufendpos���BUFӳ���ڵ�ǰ�ļ�����/βƫ�Ƶ�ַ�� //
	 * curposָ��ǰ���ļ�ָ���ƫ�Ƶ�ַ�� public byte read(long pos) throws IOException
	 * { if (pos < this.bufferOffset || pos > this.bufendpos ) {
	 * this.flushbuf(); this.seek(pos); if ((pos < this.bufstartpos) || (pos >
	 * this.bufendpos)) throw new IOException(); } this.curpos = pos; return
	 * this.buf[(int)(pos - this.bufstartpos)]; } // void
	 * flushbuf()��bufdirtyΪ�棬��buf[]����δд����̵���ݣ�д����̡� private void
	 * flushbuf() throws IOException { if (this.bufdirty == true) { if
	 * (super.getFilePointer() != this.bufstartpos) {
	 * super.seek(this.bufstartpos); } super.write(this.buf, 0,
	 * this.bufusedsize); this.bufdirty = false; } }
	 * 
	 * // void seek(long pos)���ƶ��ļ�ָ�뵽posλ�ã�����buf[]ӳ�������POS //���ڵ��ļ��顣
	 * public void seek(long pos) throws IOException { if ((pos <
	 * this.bufferStartPos) || (pos > this.bufferEndPos)) { // seek pos not in
	 * buf this.flushbuf(); if ((pos >= 0) && (pos <= this.fileendpos) &&
	 * (this.fileendpos != 0)) { // seek pos in file (file length > 0)
	 * this.bufstartpos = pos * bufbitlen / bufbitlen; this.bufusedsize =
	 * this.fillbuf(); } else if (((pos == 0) && (this.fileendpos == 0)) || (pos
	 * == this.fileendpos + 1)) { // seek pos is append pos this.bufstartpos =
	 * pos; this.bufusedsize = 0; } this.bufendpos = this.bufstartpos +
	 * this.bufsize - 1; } this.curpos = pos; } // int
	 * fillbuf()�����bufstartpos�����buf[]�� private int fillbuf() throws
	 * IOException { file.seek(this.bufferStartPos); this.bufferDirty = false;
	 * return file.read(this.buffer); } }
	 **/

}
