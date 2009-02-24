/*******************************************************************************
 * Copyright (c) 2009, Leonardo Pessoa and others. All rights reserved.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 
 * Leonardo Pessoa - Initial API and implementation
 *******************************************************************************/
package org.eclipse.cdt.objc.core.xcode;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Represents an item from a XCode project file.
 */
public abstract class XCodeProjectItem implements Comparable<XCodeProjectItem> {

    /**
     * Creates a new XCode project item for the object in contents associated
     * with the given ID.
     * 
     * @param parent
     *            The parent of the new project item
     * @param id
     *            The ID of the new project item
     * @param project
     *            The project file
     * @return A new project item from the contents associated with the given ID
     *         or <code>null</code> if no such ID exists in the contents.
     */
    @SuppressWarnings("unchecked")
    public static XCodeProjectItem build(XCodeProjectItem parent, String id, XCodeProject project) {
        Map<String, Object> values = (Map<String, Object>) project.contents.get(id);
        if (values == null) {
            return null;
        }
        String isa = values.get("isa").toString(); //$NON-NLS-1$
        try {
            Class<?> isaClass = Class.forName("br.com.aetheris.objc.internal.core.model.xcode." + isa); //$NON-NLS-1$
            Constructor<?> init = isaClass.getConstructor(XCodeProjectItem.class, String.class);
            XCodeProjectItem item = (XCodeProjectItem) init.newInstance(parent, id);
            item.load(values, project);
            return item;
        } catch (Exception e) {
            return null;
        }
    }

    private final boolean breakLines;
    private final Map<String, Object> data = new HashMap<String, Object>();
    private final String id;

    private final XCodeProjectItem parent;

    /**
     * Creates a new XCode project item with the given ID and kind.
     * 
     * @param parent
     *            The parent of this project item
     * @param id
     *            The ID of this project item
     * @param breakLines
     *            Whether the contents of this object should break lines between
     *            each property or be layed on a single line.
     */
    protected XCodeProjectItem(XCodeProjectItem parent, String id, boolean breakLines) {
        this.breakLines = breakLines;
        this.parent = parent;
        this.id = id;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(XCodeProjectItem anotherItem) {
        return id.compareTo(anotherItem.id);
    }

    /**
     * Returns the comment used to describe an item inside the XCode project
     * file.
     * 
     * @return The comment used to describe an item inside the XCode project
     *         file.
     */
    protected abstract String describe();

    /**
     * Evaluates the object as an array of String.
     * 
     * @param value
     *            The object to be evaluated
     * @return The string representation of the given object.
     */
    private String evaluateArray(Object value) {
        String[] items = (String[]) value;
        Arrays.sort(items);
        String valueStr = "("; //$NON-NLS-1$
        for (String item : items) {
            valueStr += "\n\t" + item; //$NON-NLS-1$
        }
        valueStr += "\n)"; //$NON-NLS-1$
        return valueStr;
    }

    /**
     * Evaluates the object as a boolean value.
     * 
     * @param value
     *            The object to be evaluated
     * @return The string representation of the given object.
     */
    private String evaluateBoolean(Object value) {
        if ((Boolean) value) {
            return "1"; //$NON-NLS-1$
        }
        return "0"; //$NON-NLS-1$
    }

    /**
     * Evaluates the object as List of XCodeProjectItem.
     * 
     * @param value
     *            The object to be evaluated
     * @return The string representation of the given object.
     */
    @SuppressWarnings("unchecked")
    private String evaluateList(Object value) {
        XCodeProjectItem[] items = ((List<XCodeProjectItem>) value).toArray(new XCodeProjectItem[0]);
        String valueStr = "("; //$NON-NLS-1$
        for (XCodeProjectItem item : items) {
            valueStr += "\n\t" + item.toString(); //$NON-NLS-1$
        }
        valueStr += "\n)"; //$NON-NLS-1$
        return valueStr;
    }

    /**
     * Evaluates the object as a Map<String, Object>.
     * 
     * @param value
     *            The object to be evaluated
     * @return The string representation of the given object.
     */
    @SuppressWarnings("unchecked")
    private String evaluateMap(Object value) {
        Map<String, Object> items = (Map<String, Object>) value;
        String valueStr = "{"; //$NON-NLS-1$
        String[] keys = items.keySet().toArray(new String[0]);
        Arrays.sort(keys);
        for (String key : keys) {
            String v2 = evaluateToString(items.get(key));
            valueStr += "\n\t" + key + " = " + XCodeProject.increaseIndent(v2, false); //$NON-NLS-1$ //$NON-NLS-2$
        }
        valueStr += "\n}"; //$NON-NLS-1$
        return valueStr;
    }

    /**
     * Returns the string representation for the given object in a XCode 3.x
     * project file.
     * 
     * @param value
     *            The value to be represented
     * @return The string representation for the given object.
     */
    private String evaluateToString(Object value) {
        if (value instanceof Boolean) {
            return evaluateBoolean(value);
        } else if (value instanceof String[]) {
            return evaluateArray(value);
        } else if (value instanceof List) {
            return evaluateList(value);
        } else if (value instanceof Map) {
            return evaluateMap(value);
        } else {
            String valueStr = value.toString();
            if (valueStr.indexOf('-') >= 0 || valueStr.indexOf(' ') >= 0 || valueStr.indexOf('<') >= 0
                    || valueStr.indexOf('>') >= 0 || valueStr.indexOf('/') >= 0 || valueStr.length() == 0) {
                valueStr = '"' + valueStr + '"';
            }
            return valueStr;
        }
    }

    /**
     * Returns the value which maps to the given key.
     * 
     * @param key
     *            The key whose associated value is to be returned
     * @return The value which maps to the given key or <code>null</code> if the
     *         item has no value for this key.
     */
    protected Object get(String key) {
        return data.get(key);
    }

    /**
     * Returns this item as stored in a XCode project file.
     * 
     * @return This item as stored in a XCode project file.
     */
    public String getContents() {
        StringBuilder sb = new StringBuilder();
        sb.append(toString());
        sb.append(" {"); //$NON-NLS-1$
        if (breakLines) {
            sb.append("\n\t"); //$NON-NLS-1$
        }
        sb.append("isa = "); //$NON-NLS-1$
        sb.append(getClass().getSimpleName());
        sb.append(";"); //$NON-NLS-1$
        sb.append(breakLines ? "\n\t" : " "); //$NON-NLS-1$ //$NON-NLS-2$
        // Use IXCode constants to set order
        for (String key : getXCodeConstants()) {
            if (get(key) != null) {
                sb.append(key);
                sb.append(" = "); //$NON-NLS-1$
                sb.append(XCodeProject.increaseIndent(evaluateToString(get(key)), false));
                sb.append(";"); //$NON-NLS-1$
                sb.append(breakLines ? "\n\t" : " "); //$NON-NLS-1$ //$NON-NLS-2$
            }
        }
        if (breakLines) {
            sb.setLength(sb.length() - 1);
        }
        sb.append("};"); //$NON-NLS-1$
        return sb.toString();
    }

    /**
     * Returns the ID of this project item.
     * 
     * @return The ID of this project item.
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the parent of this project item.
     * 
     * @return The parent of this project item.
     */
    public XCodeProjectItem getParent() {
        return parent;
    }

    /**
     * Returns the project where this item is contained.
     * 
     * @return The project where this item is contained.
     */
    public XCodeProject getProject() {
        return parent.getProject();
    }

    /**
     * Returns a list of known XCode project values for this kind of item in the
     * order specified by the current class.
     * 
     * @return A list of known XCode project values for this kind of item.
     */
    private String[] getXCodeConstants() {
        List<String> consts = new ArrayList<String>();
        for (Method method : getClass().getMethods()) {
            if (!isVariableMethod(method)) {
                continue;
            }
            String variable = method.getName().substring(3);
            variable = Character.toLowerCase(variable.charAt(0)) + variable.substring(1);
            consts.add(variable);
        }
        String[] constArray = consts.toArray(new String[0]);
        Arrays.sort(constArray);
        return constArray;
    }

    /**
     * Checks whether the given method is a valid <code>get()</code> method
     * representing a property of the XCode 3.x project file.
     * 
     * @param method
     *            The method to be verified
     * @return <code>true</code> if the method is a valid <code>get()</code> for
     *         a property; otherwise, returns <code>false</code>.
     */
    private boolean isVariableMethod(Method method) {
        return method.getName().startsWith("get") //$NON-NLS-1$
                && method.getParameterTypes().length == 0
                && !method.getDeclaringClass().isAssignableFrom(XCodeProjectItem.class);
    }

    /**
     * Returns a set view of the keys contained in this XCode project item.
     * 
     * @return A set view of the keys contained in this XCode project item.
     */
    protected Set<String> keySet() {
        return data.keySet();
    }

    /**
     * Loads the contents of the project item from the given value map. The
     * content map is given so the object can load it's children items.
     * 
     * @param values
     *            The values of this project item
     * @param project
     *            The project file
     */
    protected abstract void load(Map<String, Object> values, XCodeProject project);

    /**
     * Associates the specified value with the given key. If the map previously
     * contained the given key, the associated value replaces the old one. If
     * the value is <code>null</code> the key is removed from the project item.
     * 
     * @param key
     *            The key with which the value is to be associated
     * @param value
     *            The value to be associated with the given key
     */
    protected void put(String key, Object value) {
        if (key.equals("isa")) { //$NON-NLS-1$
            throw new IllegalArgumentException("%XCode.UnchangeableIsA"); //$NON-NLS-1$
        }
        if (value != null) {
            data.put(key, value);
        } else {
            data.remove(key);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(id);
        sb.append(" /* "); //$NON-NLS-1$
        sb.append(describe().trim());
        sb.append(" */"); //$NON-NLS-1$
        return sb.toString();
    }

}
