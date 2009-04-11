/*
 * Copyright 1997-2006 Sun Microsystems, Inc.  All Rights Reserved.
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

package javax.swing.event;

import java.util.EventObject;
import javax.swing.table.*;

/**
 * <B>TableColumnModelEvent</B> is used to notify listeners that a table
 * column model has changed, such as a column was added, removed, or
 * moved.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases. The current serialization support is
 * appropriate for short term storage or RMI between applications running
 * the same version of Swing.  As of 1.4, support for long term storage
 * of all JavaBeans<sup><font size="-2">TM</font></sup>
 * has been added to the <code>java.beans</code> package.
 * Please see {@link java.beans.XMLEncoder}.
 *
 * @author Alan Chung
 * @see TableColumnModelListener
 */
public class TableColumnModelEvent extends java.util.EventObject
{
//
//  Instance Variables
//

    /** The index of the column from where it was moved or removed */
    protected int	fromIndex;

    /** The index of the column to where it was moved or added */
    protected int	toIndex;

//
// Constructors
//

    /**
     * Constructs a {@code TableColumnModelEvent} object.
     *
     * @param source  the {@code TableColumnModel} that originated the event
     * @param from    an int specifying the index from where the column was
     *                moved or removed
     * @param to      an int specifying the index to where the column was
     *                moved or added
     * @see #getFromIndex
     * @see #getToIndex
     */
    public TableColumnModelEvent(TableColumnModel source, int from, int to) {
	super(source);
	fromIndex = from;
	toIndex = to;
    }
    
//
// Querying Methods
//

    /** Returns the fromIndex.  Valid for removed or moved events */
    public int getFromIndex() { return fromIndex; };

    /** Returns the toIndex.  Valid for add and moved events */
    public int getToIndex() { return toIndex; };
}
