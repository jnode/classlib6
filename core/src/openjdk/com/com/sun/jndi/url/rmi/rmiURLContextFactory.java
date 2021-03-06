/*
 * Copyright (c) 1999, 2004, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
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
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package com.sun.jndi.url.rmi;


import java.util.Hashtable;

import javax.naming.*;
import javax.naming.spi.ObjectFactory;


/**
 * An RMI URL context factory creates contexts that can resolve names
 * that are RMI URLs as defined by rmiURLContext.
 * In addition, if given a specific RMI URL (or an array of them), the
 * factory will resolve all the way to the named registry or object.
 *
 * @author Scott Seligman
 *
 * @see rmiURLContext
 */


public class rmiURLContextFactory implements ObjectFactory {

    public Object getObjectInstance(Object urlInfo, Name name,
				    Context nameCtx, Hashtable<?,?> env)
	    throws NamingException
    {
	if (urlInfo == null) {
	    return (new rmiURLContext(env));
	} else if (urlInfo instanceof String) {
	    return getUsingURL((String)urlInfo, env);
	} else if (urlInfo instanceof String[]) {
	    return getUsingURLs((String[])urlInfo, env);
	} else {
	    throw (new ConfigurationException(
		    "rmiURLContextFactory.getObjectInstance: " +
		    "argument must be an RMI URL String or an array of them"));
	}
    }

    private static Object getUsingURL(String url, Hashtable env)
	    throws NamingException
    {
	rmiURLContext urlCtx = new rmiURLContext(env);
	try {
	    return urlCtx.lookup(url);
	} finally {
	    urlCtx.close();
	}
    }

    /*
     * Try each URL until lookup() succeeds for one of them.
     * If all URLs fail, throw one of the exceptions arbitrarily.
     * Not pretty, but potentially more informative than returning null.
     */
    private static Object getUsingURLs(String[] urls, Hashtable env)
	    throws NamingException
    {
	if (urls.length == 0) {
	    throw (new ConfigurationException(
		    "rmiURLContextFactory: empty URL array"));
	}
	rmiURLContext urlCtx = new rmiURLContext(env);
	try {
	    NamingException ne = null;
	    for (int i = 0; i < urls.length; i++) {
		try {
		    return urlCtx.lookup(urls[i]);
		} catch (NamingException e) {
		    ne = e;
		}
	    }
	    throw ne;
	} finally {
	    urlCtx.close();
	}
    }
}
