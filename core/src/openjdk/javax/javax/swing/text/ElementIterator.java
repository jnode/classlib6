/*
 * Copyright 1998-2002 Sun Microsystems, Inc.  All Rights Reserved.
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

package javax.swing.text;

import java.util.Stack;
import java.util.Enumeration;

/**
 * <p>
 * ElementIterator, as the name suggests, iteratates over the Element
 * tree.  The constructor can be invoked with either Document or an Element
 * as an argument.  If the constructor is invoked with a Document as an
 * argument then the root of the iteration is the return value of
 * document.getDefaultRootElement().
 *
 * The iteration happens in a depth-first manner.  In terms of how
 * boundary conditions are handled:
 * a) if next() is called before first() or current(), the
 *    root will be returned.
 * b) next() returns null to indicate the end of the list.
 * c) previous() returns null when the current element is the root
 *    or next() has returned null.
 *
 * The ElementIterator does no locking of the Element tree. This means
 * that it does not track any changes.  It is the responsibility of the
 * user of this class, to ensure that no changes happen during element
 * iteration.
 *
 * Simple usage example:
 *
 *    public void iterate() {
 *        ElementIterator it = new ElementIterator(root);
 *	  Element elem;
 *	  while (true) {
 *           if ((elem = next()) != null) {
 *		 // process element
 *		 System.out.println("elem: " + elem.getName());
 *	     } else {
 *	         break;
 *	     }
 *	  }
 *    }
 *
 * @author Sunita Mani
 *
 */

public class ElementIterator implements Cloneable {


    private Element root;
    private Stack elementStack = null;

    /**
     * The StackItem class stores the element
     * as well as a child index.  If the
     * index is -1, then the element represented
     * on the stack is the element itself.
     * Otherwise, the index functions as as index
     * into the vector of children of the element.
     * In this case, the item on the stack
     * represents the "index"th child of the element
     *
     */
    private class StackItem implements Cloneable {
	Element item;
	int childIndex;

	private StackItem(Element elem) {
	    /**
	     * -1 index implies a self reference,
	     * as opposed to an index into its
	     * list of children.
	     */
	    this.item = elem;
	    this.childIndex = -1;
	}

	private void incrementIndex() {
	    childIndex++;
	}

	private Element getElement() {
	    return item;
	}

	private int getIndex() {
	    return childIndex;
	}

	protected Object clone() throws java.lang.CloneNotSupportedException {
	    return super.clone();
	}
    }

    /**
     * Creates a new ElementIterator. The
     * root element is taken to get the
     * default root element of the document.
     *
     * @param document a Document.
     */
    public ElementIterator(Document document) {
	root = document.getDefaultRootElement();
    }


    /**
     * Creates a new ElementIterator.
     *
     * @param root the root Element.
     */
    public ElementIterator(Element root) {
	this.root = root;
    }


    /**
     * Clones the ElementIterator.
     *
     * @return a cloned ElementIterator Object.
     */
    public synchronized Object clone() {

	try {
	    ElementIterator it = new ElementIterator(root);
	    if (elementStack != null) {
		it.elementStack = new Stack();
		for (int i = 0; i < elementStack.size(); i++) {
		    StackItem item = (StackItem)elementStack.elementAt(i);
		    StackItem clonee = (StackItem)item.clone();
		    it.elementStack.push(clonee);
		}
	    }
	    return it;
	} catch (CloneNotSupportedException e) {
	    throw new InternalError();
	}
    }


    /**
     * Fetches the first element.
     *
     * @return an Element.
     */
    public Element first() {
	// just in case...
	if (root == null) {
	    return null;
	}

	elementStack = new Stack();
	if (root.getElementCount() != 0) {
	    elementStack.push(new StackItem(root));
	}
	return root;
    }

    /**
     * Fetches the current depth of element tree.
     *
     * @return the depth.
     */
    public int depth() {
	if (elementStack == null) {
	    return 0;
	}
	return elementStack.size();
    }


    /**
     * Fetches the current Element.
     *
     * @return element on top of the stack or
     *          <code>null</code> if the root element is <code>null</code>
     */
    public Element current() {

	if (elementStack == null) {
	    return first();
	}

	/*
	  get a handle to the element on top of the stack.
	*/
	if (! elementStack.empty()) {
	    StackItem item = (StackItem)elementStack.peek();
	    Element elem = item.getElement();
	    int index = item.getIndex();
	    // self reference
	    if (index == -1) {
		return elem;
	    }
	    // return the child at location "index".
	    return elem.getElement(index);
	}
	return null;
    }


    /**
     * Fetches the next Element. The strategy
     * used to locate the next element is
     * a depth-first search.
     *
     * @return the next element or <code>null</code>
     *          at the end of the list.
     */
    public Element next() {

	/* if current() has not been invoked
	   and next is invoked, the very first
	   element will be returned. */
	if (elementStack == null) {
	    return first();
	}

	// no more elements
	if (elementStack.isEmpty()) {
	    return null;
	}

	// get a handle to the element on top of the stack

	StackItem item = (StackItem)elementStack.peek();
	Element elem = item.getElement();
	int index = item.getIndex();

	if (index+1 < elem.getElementCount()) {
	    Element child = elem.getElement(index+1);
	    if (child.isLeaf()) {
		/* In this case we merely want to increment
		   the child index of the item on top of the
		   stack.*/
		item.incrementIndex();
	    } else {
		/* In this case we need to push the child(branch)
		   on the stack so that we can iterate over its
		   children. */
		elementStack.push(new StackItem(child));
	    }
	    return child;
	} else {
	    /* No more children for the item on top of the
	       stack therefore pop the stack. */
	    elementStack.pop();
	    if (!elementStack.isEmpty()) {
		/* Increment the child index for the item that
		   is now on top of the stack. */
		StackItem top = (StackItem)elementStack.peek();
		top.incrementIndex();
		/* We now want to return its next child, therefore
		   call next() recursively. */
		return next();
	    }
	}
	return null;
    }


    /**
     * Fetches the previous Element. If howver the current
     * element is the last element, or the current element
     * is null, then null is returned.
     *
     * @return previous <code>Element</code> if available
     *
     */
    public Element previous() {

	int stackSize;
	if (elementStack == null || (stackSize = elementStack.size()) == 0) {
	    return null;
	}

	// get a handle to the element on top of the stack
	//
	StackItem item = (StackItem)elementStack.peek();
	Element elem = item.getElement();
	int index = item.getIndex();

	if (index > 0) {
	    /* return child at previous index. */
	    return getDeepestLeaf(elem.getElement(--index));
	} else if (index == 0) {
	    /* this implies that current is the element's
	       first child, therefore previous is the
	       element itself. */
	    return elem;
	} else if (index == -1) {
	    if (stackSize == 1) {
		// current is the root, nothing before it.
		return null;
	    }
	    /* We need to return either the item
	       below the top item or one of the
	       former's children. */
	    Object top = elementStack.pop();
	    item = (StackItem)elementStack.peek();

	    // restore the top item.
	    elementStack.push(top);
	    elem = item.getElement();
	    index = item.getIndex();
	    return ((index == -1) ? elem : getDeepestLeaf(elem.getElement
							  (index)));
	}
	// should never get here.
	return null;
    }

    /**
     * Returns the last child of <code>parent</code> that is a leaf. If the
     * last child is a not a leaf, this method is called with the last child.
     */
    private Element getDeepestLeaf(Element parent) {
	if (parent.isLeaf()) {
	    return parent;
	}
	int childCount = parent.getElementCount();
	if (childCount == 0) {
	    return parent;
	}
	return getDeepestLeaf(parent.getElement(childCount - 1));
    }

    /*
      Iterates through the element tree and prints
      out each element and its attributes.
    */
    private void dumpTree() {

	Element elem;
	while (true) {
	    if ((elem = next()) != null) {
		System.out.println("elem: " + elem.getName());
		AttributeSet attr = elem.getAttributes();
		String s = "";
		Enumeration names = attr.getAttributeNames();
		while (names.hasMoreElements()) {
		    Object key = names.nextElement();
		    Object value = attr.getAttribute(key);
		    if (value instanceof AttributeSet) {
			// don't go recursive
			s = s + key + "=**AttributeSet** ";
		    } else {
			s = s + key + "=" + value + " ";
		    }
		}
		System.out.println("attributes: " + s);
	    } else {
		break;
	    }
	}
    }
}
