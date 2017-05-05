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
package cn.edu.thss.iise.beehivez.server.index.petrinetindex.pathindex;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;

import cn.edu.thss.iise.beehivez.server.file.BufferFile;

/**
 * @author JinTao used to store the inverted file list containing the
 *         correspoinding item
 * 
 */
public class InvertedFile {

	private RandomAccessFile ifFile = null;
	private BufferFile bufferFile = null;

	// the format is:
	// count(int), fileid(long), fileid(long), ... , nextbuffernumber(long)
	private int maxFilesPerBuffer;
	// size in bytes
	private int bufferSize;

	// cache
	// the number of cache items
	private int cacheSize;
	// buffer number --> buffer,rank
	private Hashtable<Long, CachedObject> cache = null;
	// usedSpace in cache
	private int usedSpace = 0;

	private InvertedFile() {
	}

	// create new inverted file
	public static InvertedFile initializeInvertedFile(RandomAccessFile ifFile) {
		return initializeInvertedFile(ifFile, 100);
	}

	public static InvertedFile initializeInvertedFile(RandomAccessFile ifFile,
			int maxFilesPerBuffer) {
		return initializeInvertedFile(ifFile, maxFilesPerBuffer, 100);
	}

	public static InvertedFile initializeInvertedFile(RandomAccessFile ifFile,
			int maxFilesPerBuffer, int cacheSize) {
		InvertedFile ret = new InvertedFile();
		ret.ifFile = ifFile;

		ret.cacheSize = cacheSize;
		ret.cache = new Hashtable<Long, CachedObject>(ret.cacheSize);
		ret.usedSpace = 0;

		ret.maxFilesPerBuffer = maxFilesPerBuffer;
		ret.bufferSize = (ret.maxFilesPerBuffer + 1) * BufferFile.LONGSTORAGE
				+ BufferFile.INTSTORAGE; // + count of fileID + next buffer
		// number
		try {
			ret.bufferFile = BufferFile.InitializeBufferFileInStream(
					ret.ifFile, ret.bufferSize);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return ret;
	}

	// setup from existing inverted file
	public static InvertedFile setupFromExistingInvertedFile(
			RandomAccessFile ifFile) {
		return setupFromExistingInvertedFile(ifFile, 100);
	}

	public static InvertedFile setupFromExistingInvertedFile(
			RandomAccessFile ifFile, int cacheSize) {
		InvertedFile ret = new InvertedFile();
		ret.ifFile = ifFile;

		ret.cacheSize = cacheSize;
		ret.cache = new Hashtable<Long, CachedObject>(ret.cacheSize);
		ret.usedSpace = 0;

		try {
			ret.bufferFile = BufferFile.SetupFromExistingStream(ifFile);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		ret.bufferSize = ret.bufferFile.buffersize;
		ret.maxFilesPerBuffer = (ret.bufferSize - BufferFile.INTSTORAGE)
				/ BufferFile.LONGSTORAGE - 1;

		return ret;
	}

	/**
	 * @param fileID
	 * @return the buffer number
	 */
	public long createNewInvertedList(long fileID) {
		long newBufferNumber = -1;
		try {
			newBufferNumber = this.bufferFile.nextBufferNumber();
			byte[] buffer = new byte[this.bufferSize];
			BufferFile.StoreInt(1, buffer, 0);
			BufferFile.StoreLong(fileID, buffer, BufferFile.INTSTORAGE);
			BufferFile.StoreLong(-1, buffer, this.bufferSize
					- BufferFile.LONGSTORAGE);
			// for (int i = 1; i < this.maxFilesPerBuffer + 1; i++) {
			// BufferFile.StoreLong(-1, buffer, BufferFile.INTSTORAGE + i
			// * BufferFile.LONGSTORAGE);
			// }

			// write buffer to file
			this.bufferFile.setBuffer(newBufferNumber, buffer, 0,
					this.bufferSize);

			// write to cache
			this.writeToCache(newBufferNumber, buffer);

		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
		return newBufferNumber;
	}

	/**
	 * if the fileID is contained, nothing happens
	 * 
	 * @param bufferNumber
	 *            the first buffer number
	 * @param fileID
	 * 
	 */
	public void addFileID(long bufferNumber, long fileID) {
		try {
			boolean needMoreRead = true;
			while (needMoreRead) {
				byte[] buffer = this.readFromCache(bufferNumber);
				int nFiles = BufferFile.RetrieveInt(buffer, 0);
				for (int i = 0; i < nFiles; i++) {
					long l = BufferFile.RetrieveLong(buffer,
							BufferFile.INTSTORAGE + i * BufferFile.LONGSTORAGE);
					if (l == fileID) {
						return;
					}
				}
				// not found, write to the inverted file
				if (nFiles < this.maxFilesPerBuffer) {
					BufferFile.StoreLong(fileID, buffer, BufferFile.INTSTORAGE
							+ nFiles * BufferFile.LONGSTORAGE);
					nFiles++;
					BufferFile.StoreInt(nFiles, buffer, 0);
					this.bufferFile.setBuffer(bufferNumber, buffer, 0,
							this.bufferSize);
				} else {
					long nextBufferNumber = BufferFile.RetrieveLong(buffer,
							this.bufferSize - BufferFile.LONGSTORAGE);
					if (nextBufferNumber == -1) {
						// create new buffer
						nextBufferNumber = this.createNewInvertedList(fileID);
						BufferFile.StoreLong(nextBufferNumber, buffer,
								this.bufferSize - BufferFile.LONGSTORAGE);
						this.bufferFile.setBuffer(bufferNumber, buffer, 0,
								this.bufferSize);
						return;
					} else {
						// need to read next buffer
						bufferNumber = nextBufferNumber;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public HashSet getFileList(long bufferNumber) {
		HashSet ret = new HashSet();
		boolean needMoreRead = true;
		while (needMoreRead) {
			try {
				byte[] buffer = this.readFromCache(bufferNumber);
				int nFiles = BufferFile.RetrieveInt(buffer, 0);
				for (int i = 0; i < nFiles; i++) {
					long l = BufferFile.RetrieveLong(buffer,
							BufferFile.INTSTORAGE + i * BufferFile.LONGSTORAGE);
					ret.add(l);
				}
				if (nFiles < this.maxFilesPerBuffer) {
					needMoreRead = false;
					break;
				} else {
					bufferNumber = BufferFile.RetrieveLong(buffer,
							this.bufferSize - BufferFile.LONGSTORAGE);
					if (bufferNumber == -1) {
						needMoreRead = false;
						break;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		return ret;
	}

	private void writeToCache(long bufferNumber, byte[] buffer) {
		if (this.usedSpace < this.cacheSize) {
			// cache not full
			CachedObject cachedObject = new CachedObject(bufferNumber,
					usedSpace, buffer);
			usedSpace++;
			this.cache.put(bufferNumber, cachedObject);
		} else {
			// cache is full
			// discard one first
			for (Enumeration<CachedObject> e = this.cache.elements(); e
					.hasMoreElements();) {
				CachedObject o = e.nextElement();
				if (o.rank == 0) {
					this.cache.remove(o.bufferNumber);
				} else {
					o.rank--;
				}
				CachedObject cachedObject = new CachedObject(bufferNumber,
						usedSpace - 1, buffer);
				this.cache.put(bufferNumber, cachedObject);
			}
		}
	}

	// all buffer must be read from cache
	private byte[] readFromCache(long bufferNumber) {
		byte[] ret = null;
		CachedObject o = this.cache.get(bufferNumber);
		if (o == null) {
			// not cached
			// first read from file then cache it
			ret = new byte[this.bufferSize];
			try {
				this.bufferFile
						.getBuffer(bufferNumber, ret, 0, this.bufferSize);
				writeToCache(bufferNumber, ret);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		} else {
			// has been cached
			// adjust the rank
			int rank = o.rank;
			for (Enumeration<CachedObject> e = this.cache.elements(); e
					.hasMoreElements();) {
				CachedObject t = e.nextElement();
				if (t.rank == rank) {
					t.rank = usedSpace - 1;
				} else if (t.rank > rank) {
					t.rank--;
				}
			}
			ret = o.buffer;
		}

		return ret;
	}

	private void clearCache() {
		this.cache.clear();
	}

	public void close() {
		if (this.cache != null) {
			this.cache.clear();
		}
	}

	class CachedObject {
		long bufferNumber;
		int rank;
		byte[] buffer;

		CachedObject(long bufferNumber, int rank, byte[] buffer) {
			this.bufferNumber = bufferNumber;
			this.rank = rank;
			this.buffer = buffer;
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			RandomAccessFile raf = new RandomAccessFile("test", "rw");
			InvertedFile invertedFile = InvertedFile.initializeInvertedFile(
					raf, 10, 3);
			long bufferNumber = invertedFile.createNewInvertedList(1111);
			for (int i = 0; i < 10; i++) {
				invertedFile.addFileID(bufferNumber, -i);
			}
			HashSet set = invertedFile.getFileList(bufferNumber);
			Iterator it = set.iterator();
			while (it.hasNext()) {
				System.out.print(it.next());
			}
			invertedFile.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
