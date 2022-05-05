package org.eclipse.set.browser.lib;

import org.eclipse.swt.internal.C;

public abstract class CStruct {

	public long ptr;

	public abstract void allocate();

	public void free() {
		C.free(ptr);
		ptr = 0;
	}
}
