/*
 * Copyright 2001 Sun Microsystems, Inc.  All Rights Reserved.
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

package org.omg.PortableServer;


/**
* org/omg/PortableServer/CurrentHelper.java .
* Generated by the IDL-to-Java compiler (portable), version "3.1"
* from ../../../../src/share/classes/org/omg/PortableServer/poa.idl
* Tuesday, October 23, 2001 1:17:01 PM PDT
*/


/**
 * The PortableServer::Current interface, derived from
 * CORBA::Current, provides method implementations with
 * access to the identity of the object on which the
 * method was invoked. The Current interface is provided
 * to support servants that implement multiple objects,
 * but can be used within the context of POA-dispatched
 * method invocations on any servant. To provide location
 * transparency, ORBs are required to support use of
 * Current in the context of both locally and remotely
 * invoked operations. An instance of Current can be
 * obtained by the application by issuing the
 * CORBA::ORB::resolve_initial_references("POACurrent")
 * operation. Thereafter, it can be used within the
 * context of a method dispatched by the POA to obtain
 * the POA and ObjectId that identify the object on
 * which that operation was invoked.
 */
abstract public class CurrentHelper
{
    private static String  _id = "IDL:omg.org/PortableServer/Current:2.3";

    public static void insert (org.omg.CORBA.Any a, 
        org.omg.PortableServer.Current that)
    {
        org.omg.CORBA.portable.OutputStream out = a.create_output_stream ();
        a.type (type ());
        write (out, that);
        a.read_value (out.create_input_stream (), type ());
    }

    public static org.omg.PortableServer.Current extract (org.omg.CORBA.Any a)
    {
        return read (a.create_input_stream ());
    }

    private static org.omg.CORBA.TypeCode __typeCode = null;
    synchronized public static org.omg.CORBA.TypeCode type ()
    {
        if (__typeCode == null)
        {
            __typeCode = org.omg.CORBA.ORB.init ().create_interface_tc (
                org.omg.PortableServer.CurrentHelper.id (), "Current");
        }
        return __typeCode;
    }

    public static String id ()
    {
        return _id;
    }

    public static org.omg.PortableServer.Current read (
        org.omg.CORBA.portable.InputStream istream)
    {
        throw new org.omg.CORBA.MARSHAL ();
    }

    public static void write (org.omg.CORBA.portable.OutputStream ostream, 
        org.omg.PortableServer.Current value)
    {
        throw new org.omg.CORBA.MARSHAL ();
    }

    public static org.omg.PortableServer.Current narrow (
        org.omg.CORBA.Object obj)
    {
        if (obj == null)
            return null;
        else if (obj instanceof org.omg.PortableServer.Current)
            return (org.omg.PortableServer.Current)obj;
        else if (!obj._is_a (id ()))
            throw new org.omg.CORBA.BAD_PARAM ();
        return null;
    }

}
