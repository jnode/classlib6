/*
 * Copyright 2004-2005 Sun Microsystems, Inc.  All Rights Reserved.
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

// Locator2Impl.java - extended LocatorImpl
// http://www.saxproject.org
// Public Domain: no warranty.
// $Id: Locator2Impl.java,v 1.2 2004/11/03 22:49:08 jsuttor Exp $

package org.xml.sax.ext;

import org.xml.sax.Locator;
import org.xml.sax.helpers.LocatorImpl;


/**
 * SAX2 extension helper for holding additional Entity information,
 * implementing the {@link Locator2} interface.
 *
 * <blockquote>
 * <em>This module, both source code and documentation, is in the
 * Public Domain, and comes with <strong>NO WARRANTY</strong>.</em>
 * </blockquote>
 *
 * <p> This is not part of core-only SAX2 distributions.</p>
 *
 * @since SAX 2.0.2
 * @author David Brownell
 */
public class Locator2Impl extends LocatorImpl implements Locator2
{
    private String	encoding;
    private String	version;


    /**
     * Construct a new, empty Locator2Impl object.
     * This will not normally be useful, since the main purpose
     * of this class is to make a snapshot of an existing Locator.
     */
    public Locator2Impl () { }

    /**
     * Copy an existing Locator or Locator2 object.
     * If the object implements Locator2, values of the
     * <em>encoding</em> and <em>version</em>strings are copied,
     * otherwise they set to <em>null</em>. 
     *
     * @param locator The existing Locator object.
     */
    public Locator2Impl (Locator locator)
    {
	super (locator);
	if (locator instanceof Locator2) {
	    Locator2	l2 = (Locator2) locator;

	    version = l2.getXMLVersion ();
	    encoding = l2.getEncoding ();
	}
    }

    ////////////////////////////////////////////////////////////////////
    // Locator2 method implementations
    ////////////////////////////////////////////////////////////////////
    
    /**
     * Returns the current value of the version property.
     *
     * @see #setXMLVersion
     */
    public String getXMLVersion ()
	{ return version; }

    /**
     * Returns the current value of the encoding property.
     *
     * @see #setEncoding
     */
    public String getEncoding ()
	{ return encoding; }


    ////////////////////////////////////////////////////////////////////
    // Setters 
    ////////////////////////////////////////////////////////////////////
    
    /**
     * Assigns the current value of the version property.
     *
     * @param version the new "version" value
     * @see #getXMLVersion
     */
    public void setXMLVersion (String version)
	{ this.version = version; }

    /**
     * Assigns the current value of the encoding property.
     *
     * @param encoding the new "encoding" value
     * @see #getEncoding
     */
    public void setEncoding (String encoding)
	{ this.encoding = encoding; }
}