/*
 * Copyright 2004 Sun Microsystems, Inc.  All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa Clara,
 * CA 95054 USA or visit www.sun.com if you need additional information or
 * have any questions.
 */

package sun.management;

import java.lang.management.MemoryUsage;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.OpenDataException;

/**
 * A CompositeData for MemoryUsage for the local management support.
 * This class avoids the performance penalty paid to the 
 * construction of a CompositeData use in the local case.
 */
public class MemoryUsageCompositeData extends LazyCompositeData {
    private final MemoryUsage usage;

    private MemoryUsageCompositeData(MemoryUsage u) {
	this.usage = u;
    }

    public MemoryUsage getMemoryUsage() {
	return usage;
    }

    public static CompositeData toCompositeData(MemoryUsage u) {
        MemoryUsageCompositeData mucd = new MemoryUsageCompositeData(u);
        return mucd.getCompositeData();
    }

    protected CompositeData getCompositeData() {
	// CONTENTS OF THIS ARRAY MUST BE SYNCHRONIZED WITH
	// memoryUsageItemNames!
	final Object[] memoryUsageItemValues = {
	    new Long(usage.getInit()),
	    new Long(usage.getUsed()),
	    new Long(usage.getCommitted()),
	    new Long(usage.getMax()),
	};

	try {
	    return new CompositeDataSupport(memoryUsageCompositeType,
	  			    	    memoryUsageItemNames,
					    memoryUsageItemValues);
	} catch (OpenDataException e) {
            // Should never reach here
            throw Util.newInternalError(e);
	}
    }

    private static final CompositeType memoryUsageCompositeType;
    static {
	try {
	    memoryUsageCompositeType = (CompositeType)
		MappedMXBeanType.toOpenType(MemoryUsage.class);
	} catch (OpenDataException e) {
            // Should never reach here
            throw Util.newInternalError(e);
	}
    }

    static CompositeType getMemoryUsageCompositeType() {
        return memoryUsageCompositeType;
    }

    private static final String INIT      = "init";
    private static final String USED      = "used";
    private static final String COMMITTED = "committed";
    private static final String MAX       = "max";

    private static final String[] memoryUsageItemNames = {
	INIT,
	USED,
	COMMITTED,
	MAX,
    };

    public static long getInit(CompositeData cd) {
        return getLong(cd, INIT);
    }
    public static long getUsed(CompositeData cd) {
        return getLong(cd, USED);
    }
    public static long getCommitted(CompositeData cd) {
        return getLong(cd, COMMITTED);
    }
    public static long getMax(CompositeData cd) {
        return getLong(cd, MAX);
    }

    /** Validate if the input CompositeData has the expected
     * CompositeType (i.e. contain all attributes with expected
     * names and types).
     */ 
    public static void validateCompositeData(CompositeData cd) {
        if (cd == null) {
            throw new NullPointerException("Null CompositeData");
        }

        if (!isTypeMatched(memoryUsageCompositeType, cd.getCompositeType())) {
            throw new IllegalArgumentException(
                "Unexpected composite type for MemoryUsage");
        }
    }
}
