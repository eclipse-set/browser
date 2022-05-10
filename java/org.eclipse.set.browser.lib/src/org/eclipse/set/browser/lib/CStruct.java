/**
 * Copyright (c) 2022 DB Netz AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 */
package org.eclipse.set.browser.lib;

import org.eclipse.swt.internal.C;

/**
 * Helper class to bind a Java class to a native object
 * 
 * @author Stuecker
 */
public abstract class CStruct {

	/**
	 * Raw pointer to the memory, may be null if not allocated
	 */
	public long ptr = 0;

	/**
	 * Allocate the raw memory any copy the fields of the Java class into it
	 * 
	 * Note: Make sure to call free(), otherwise memory will leak
	 */
	public abstract void allocate();

	/**
	 * Frees the memory
	 */
	public void free() {
		C.free(ptr);
		ptr = 0;
	}
}
