/**   
 * License Agreement for OpenSearchServer
 *
 * Copyright (C) 2012 Emmanuel Keller / Jaeksoft
 * 
 * http://www.open-search-server.com
 * 
 * This file is part of OpenSearchServer.
 *
 * OpenSearchServer is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 * OpenSearchServer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with OpenSearchServer. 
 *  If not, see <http://www.gnu.org/licenses/>.
 **/

package com.opensearchserver.search.index.osse;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;

import com.jaeksoft.searchlib.SearchLibException;
import com.jaeksoft.searchlib.util.FunctionTimer;
import com.jaeksoft.searchlib.util.FunctionTimer.ExecutionToken;
import com.sun.jna.Pointer;
import com.sun.jna.WString;
import com.sun.jna.ptr.IntByReference;

public class OsseDocCursor implements Closeable {

	private Pointer docCursorPtr;

	public OsseDocCursor(OsseIndex index, OsseErrorHandler error)
			throws SearchLibException {
		ExecutionToken et = FunctionTimer
				.newExecutionToken("OSSCLib_DocTCursor_Create");
		docCursorPtr = null;// OsseLibrary.OSSCLib_DocTCursor_Create(
		// index.getPointer(), error.getPointer());
		et.end();
		if (docCursorPtr == null)
			error.throwError();
	}

	public List<String> getTerms(Pointer indexFieldPtr, long docId) {
		ExecutionToken et = FunctionTimer
				.newExecutionToken("OSSCLib_DocTCursor_FindFirstTerm");
		WString term = null; /*
							 * OsseLibrary.OSSCLib_DocTCursor_FindFirstTerm(
							 * docCursorPtr, indexFieldPtr, docId,
							 * error.getPointer());
							 */
		et.end();
		if (term == null)
			return null;
		List<String> list = new ArrayList<String>(1);
		list.add(term.toString());
		IntByReference bError = new IntByReference();
		for (;;) {
			et = FunctionTimer
					.newExecutionToken("OSSCLib_DocTCursor_FindNextTerm");
			term = null; // OsseLibrary.OSSCLib_DocTCursor_FindNextTerm(docCursorPtr,
			// bError, error.getPointer());
			et.end();
			if (term == null)
				break;
			if (bError.getValue() != 0)
				break;
			list.add(term.toString());
		}
		return list;
	}

	final public Pointer getPointer() {
		return docCursorPtr;
	}

	@Override
	final public void close() {
		if (docCursorPtr == null)
			return;
		ExecutionToken et = FunctionTimer
				.newExecutionToken("OSSCLib_DocTCursor_Delete");
		OsseJNALibrary.OSSCLib_MsDocTCursor_Delete(docCursorPtr);
		et.end();
		docCursorPtr = null;
	}

}
