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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.StringTokenizer;

import org.eclipse.cdt.objc.core.model.ObjCModelException;

/**
 * An object that gathers project settings read from a XCode 3.x project file.
 */
public final class XCodeProject {

    private static final String NonXCode3Project = "File is not an XCode 3.x project"; //$NON-NLS-1$
    private static final String PBXPROJ_HEADER_1 = "// !$*UTF8*$! { archiveVersion ="; //$NON-NLS-1$
    private static final String PBXPROJ_HEADER_2 = "classes = { }; objectVersion ="; //$NON-NLS-1$
    private static final String PBXPROJ_HEADER_3 = "objects = {"; //$NON-NLS-1$

    /**
     * Checks whether the source contains a series of tokens.
     * 
     * @param source
     *            The source to be tested
     * @param tokens
     *            The tokens expected on the source
     * @return <code>true</code> if the source has the very same tokens;
     *         otherwise, returns <code>false</code>.
     */
    private static boolean assertTokens(StringTokenizer source, String tokens) {
        StringTokenizer tk = new StringTokenizer(tokens);
        while (tk.hasMoreTokens()) {
            String t1 = source.nextToken();
            String t2 = tk.nextToken();
            if (!t1.equals(t2)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Increase the indentation of the given source using one tab '\t'.
     * 
     * @param source
     *            The text to be indented
     * @param firstLine
     *            Whether the first line should be indented
     * @return The given text with increased indentation.
     */
    public static String increaseIndent(String source, boolean firstLine) {
        return increaseIndent("\t", source, firstLine); //$NON-NLS-1$
    }

    /**
     * Increase the indentation of the given source using the given indentation
     * text.
     * 
     * @param indentText
     *            The text to use for indentation
     * @param source
     *            The text to be indented
     * @param firstLine
     *            Whether the first line should be indented
     * @return The given text with increased indentation.
     */
    public static String increaseIndent(String indentText, String source, boolean firstLine) {
        String[] lines = source.split("\n"); //$NON-NLS-1$
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < lines.length; ++i) {
            if (i > 0) {
                result.append('\n');
            }
            if (firstLine || i > 0) {
                result.append('\t');
            }
            result.append(lines[i]);
        }
        return result.toString();
    }

    /**
     * Reads an array of strings (stripped of any C-like comments) from the
     * source.
     * 
     * @param source
     *            The source from where to read the array of strings
     * @return The array of string read from the source.
     */
    private static String[] readArray(StringTokenizer source) {
        List<String> values = new ArrayList<String>();
        String token;
        do {
            token = source.nextToken();
            if (token.equals(");")) { //$NON-NLS-1$
                continue;
            }
            if (token.endsWith(",")) { //$NON-NLS-1$
                values.add(token.substring(0, token.length() - 1));
            } else {
                values.add(token);
            }
            while (!token.endsWith(",")) { //$NON-NLS-1$
                token = source.nextToken();
            }
        } while (!token.equals(");")); //$NON-NLS-1$
        return values.toArray(new String[0]);
    }

    /**
     * Reads a group of key/value pairs from source, starting with the given
     * token. If the token is <code>null</code>, a new token is read first.
     * 
     * @param source
     *            The source from where to read the remaining of the group
     * @param token
     *            The token which is the first part of the group
     * @return A dictionary that represents the group contents.
     * @throws ObjCUIException
     */
    private static Map<String, Object> readGroup(StringTokenizer source, String previousToken)
            throws ObjCModelException {
        Map<String, Object> values = new HashMap<String, Object>();
        String token;
        do {
            if (previousToken != null && !previousToken.equals("")) { //$NON-NLS-1$
                token = previousToken;
                previousToken = null;
            } else {
                token = source.nextToken();
            }
            if (token.equals("};")) { //$NON-NLS-1$
                continue;
            } else if (token.equals("/*")) { //$NON-NLS-1$
                // Must read and skip comment
                do {
                    token = source.nextToken();
                } while (!token.endsWith("*/")); //$NON-NLS-1$
            } else {
                // Must read a full item
                String name = token;
                token = source.nextToken();
                if (token.equals("/*")) { //$NON-NLS-1$
                    // Must read and skip comment
                    do {
                        token = source.nextToken();
                    } while (!token.endsWith("*/")); //$NON-NLS-1$
                    token = source.nextToken();
                }
                // If not equals '=', the file is wrong
                if (!token.equals("=")) { //$NON-NLS-1$
                    throw new ObjCModelException(NonXCode3Project);
                }
                token = source.nextToken();
                Object value = null;
                if (token.startsWith("{")) { //$NON-NLS-1$
                    // This is a group, read all values again
                    value = readGroup(source, token.substring(1));
                } else if (token.equals("(")) { //$NON-NLS-1$
                    // This is an array, read all values
                    if (!token.endsWith(");")) { //$NON-NLS-1$
                        value = readArray(source);
                    }
                } else {
                    // This value is something else like int, bool or string
                    value = readString(source, token);
                }
                if (value != null) {
                    values.put(name, value);
                }
            }
        } while (!token.equals("};")); //$NON-NLS-1$
        return values;
    }

    /**
     * Reads a string (stripped of any C-like comments) from the source,
     * starting with the given token.
     * 
     * @param source
     *            The source from where to read the remaining of the string
     * @param token
     *            The token which is the first part of the string
     * @return The string read from the source.
     */
    private static String readString(StringTokenizer source, String token) {
        StringBuilder result = new StringBuilder(token);
        while (!result.toString().endsWith(";")) { //$NON-NLS-1$
            result.append(' ');
            result.append(source.nextToken());
        }
        result.deleteCharAt(result.length() - 1);
        if (result.toString().startsWith("\"") && result.toString().endsWith("\"")) { //$NON-NLS-1$ //$NON-NLS-2$ 
            result.deleteCharAt(result.length() - 1);
            result.deleteCharAt(0);
        }
        int commentStart = result.toString().indexOf("/*"); //$NON-NLS-1$
        if (commentStart >= 0) {
            int commentEnd = result.toString().indexOf("*/") + 2; //$NON-NLS-1$
            result.delete(commentStart, commentEnd);
        }
        return result.toString().trim();
    }

    private int archiveVersion; // expected '1'

    Map<String, Object> contents;

    private int objectVersion; // excpected '44'

    private final PBXProject rootObject;

    /**
     * Creates a brand new XCode 3.x project file.
     */
    public XCodeProject() {
        rootObject = new PBXProject(null, createNewId());
        rootObject.load(null, this);
        archiveVersion = 1;
        objectVersion = 44;
    }

    /**
     * Loads project configuration from a specified XCode 3.x project file.
     * 
     * @param fileName
     *            The path to the <i>.xcodeproj</i> bundle of the project
     * @throws ObjCModelException
     * @throws FileNotFoundException
     */
    public XCodeProject(String fileName) throws ObjCModelException, FileNotFoundException {
        // Loads the contents of the project file
        String lines = readToEnd(fileName + File.separatorChar + "project.pbxproj"); //$NON-NLS-1$
        // Prepares to parse the project file
        StringTokenizer source = new StringTokenizer(lines);
        validateFile(source);
        // Ignores comments and parses the rest
        contents = readGroup(source, null);
        if (!source.nextToken().equals("rootObject")) { //$NON-NLS-1$
            throw new ObjCModelException(NonXCode3Project);
        }
        source.nextToken();
        // Builds the real tree
        rootObject = (PBXProject) XCodeProjectItem.build(null, source.nextToken(), this);
        contents = null;
    }

    /**
     * Creates and returns a new ID, ensuring it doesn't exist on the project.
     * 
     * @return A new unexisting ID.
     */
    public String createNewId() {
        final String constants = "0123456789ABCDEF"; //$NON-NLS-1$
        Random rand = new Random();
        while (true) {
            String newId = ""; //$NON-NLS-1$
            for (int i = 0; i < 24; ++i) {
                newId += constants.charAt(rand.nextInt(constants.length()));
            }
            // Returns only if unused
            if (!exists(newId)) {
                return newId;
            }
        }
    }

    /**
     * Returns whether the given ID is not used by an item of this XCode
     * project.
     * 
     * @param id
     *            The ID to be checked
     * @return <code>true</code> if the project has an item with the given ID;
     *         otherwise, returns <code>false</code>.
     */
    public boolean exists(String id) {
        XCodeProjectItem[] items = find(XCodeProjectItem.class);
        for (XCodeProjectItem item : items) {
            if (item.getId().equals(id)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Finds all objects of the given class under the given object.
     * 
     * @param list
     *            The list of objects to be built
     * @param clazz
     *            The class for which to find all objects
     * @param object
     *            The object which is supposed to be a
     *            <code>XCodeProjectItem</code>.
     */
    private void fillItems(List<XCodeProjectItem> list, Class<?> clazz, Object object) {
        if (object instanceof XCodeProjectItem) {
            XCodeProjectItem[] subItems = find(clazz, (XCodeProjectItem) object);
            for (XCodeProjectItem item : subItems) {
                list.add(item);
            }
        }
    }

    /**
     * Finds all objects of the given class in this project.
     * 
     * @param clazz
     *            The class for which to find all objects
     * @return A list of all objects of the given class.
     */
    public XCodeProjectItem[] find(Class<?> clazz) {
        return find(clazz, rootObject);
    }

    /**
     * Finds all objects of the given class under the given object.
     * 
     * @param clazz
     *            The class for which to find all objects
     * @param node
     *            The root object from which to find all objects of the given
     *            class.
     * @return A list of all objects of the given class.
     */
    private XCodeProjectItem[] find(Class<?> clazz, XCodeProjectItem node) {
        List<XCodeProjectItem> list = new ArrayList<XCodeProjectItem>();
        // Test the root object
        if (clazz.isAssignableFrom(node.getClass())) {
            list.add(node);
        }
        // Test the child properties
        Set<String> keys = node.keySet();
        for (String key : keys) {
            Object object = node.get(key);
            if (object instanceof XCodeProjectItem) {
                // Process other XCodeProjectItems
                fillItems(list, clazz, object);
            } else if (object instanceof List) {
                // Process lists (unknown type)
                for (Object o : (List<?>) object) { // Unsolveable
                    fillItems(list, clazz, o);
                }
            } else if (object instanceof Map) {
                // Process maps (unknown types)
                for (Object o : ((Map<?, ?>) object).keySet()) { // Unsolveable
                    Object o2 = ((Map<?, ?>) object).get(o); // Unsolveable
                    fillItems(list, clazz, o2);
                }
            }
        }
        // Returns the resulting list
        return list.toArray(new XCodeProjectItem[0]);
    }

    /**
     * Returns the version of the archive.
     * 
     * @return The version of the archive.
     */
    public int getArchiveVersion() {
        return archiveVersion;
    }

    /**
     * Returns a list of all classes which represent valid XCode project
     * sections.
     * 
     * @return A list of all classes which represent valid XCode project
     *         sections.
     */
    private Class<?>[] getClassesFromPackage() {
        List<Class<?>> classes = new ArrayList<Class<?>>();
        classes.add(PBXBuildFile.class);
        classes.add(PBXCopyFilesBuildPhase.class);
        classes.add(PBXFileReference.class);
        classes.add(PBXFrameworksBuildPhase.class);
        classes.add(PBXGroup.class);
        classes.add(PBXNativeTarget.class);
        classes.add(PBXProject.class);
        classes.add(PBXSourcesBuildPhase.class);
        classes.add(XCBuildConfiguration.class);
        classes.add(XCConfigurationList.class);
        return classes.toArray(new Class<?>[0]);
    }

    /**
     * Returns the contents of the XCode project file represented by this
     * object.
     * 
     * @return The contents of the XCode project file represented by this
     *         object.
     */
    public String getContents() {
        StringBuilder result = new StringBuilder();
        // Header lines
        result.append("// !$*UTF8*$!\n{\n\tarchiveVersion = "); //$NON-NLS-1$
        result.append(String.valueOf(getArchiveVersion()));
        result.append(";\n\tclasses = {\n\t};\n\tobjectVersion = "); //$NON-NLS-1$
        result.append(String.valueOf(getObjectVersion()));
        result.append(";\n\tobjects = {\n"); //$NON-NLS-1$
        // Finds all non-abstract classes deriving from XCodeProjectItem
        Class<?>[] classes = getClassesFromPackage();
        for (Class<?> cls : classes) {
            XCodeProjectItem[] items = find(cls);
            if (items.length > 0) {
                // Begin section
                result.append("\n/* Begin "); //$NON-NLS-1$
                result.append(cls.getSimpleName());
                result.append(" section */\n"); //$NON-NLS-1$
                // Posts all items to contents
                for (XCodeProjectItem item : items) {
                    String content = item.getContents();
                    if (content != null) {
                        content = increaseIndent(content, true);
                        content = increaseIndent(content, true);
                        result.append(content);
                        result.append('\n');
                    }
                }
                // End section
                result.append("/* End "); //$NON-NLS-1$
                result.append(cls.getSimpleName());
                result.append(" section */\n"); //$NON-NLS-1$
            }
        }
        result.append("\t};\n\trootObject = "); //$NON-NLS-1$
        result.append(getRootObject().toString());
        result.append(";\n}"); //$NON-NLS-1$
        return result.toString();
    }

    /**
     * Returns the project item with the given ID.
     * 
     * @param id
     *            The ID of the project item to be found
     * @return The project item with the given ID.
     */
    public XCodeProjectItem getItemById(String id) {
        XCodeProjectItem[] items = find(XCodeProjectItem.class);
        for (XCodeProjectItem item : items) {
            if (item.getId().equals(id)) {
                return item;
            }
        }
        return null;
    }

    /**
     * Returns the version of the object list.
     * 
     * @return The version of the object list.
     */
    public int getObjectVersion() {
        return objectVersion;
    }

    /**
     * Returns the root object of the project.
     * 
     * @return The root object of the project.
     */
    public PBXProject getRootObject() {
        return rootObject;
    }

    private String readToEnd(String fileName) throws FileNotFoundException {
        BufferedReader file = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
        StringBuilder lines = new StringBuilder();
        String line = null;
        do {
            try {
                line = file.readLine();
            } catch (IOException e) {
                line = null;
            }
            if (line != null) {
                lines.append(line + '\n');
            }
        } while (line != null);
        return lines.toString();
    }

    /**
     * Saves the project information to the given project bundle folder.
     * 
     * @param fileName
     *            The path to the project bundle folder where to save the
     *            project.
     */
    public void save(String fileName) throws IOException {
        File file = new File(fileName + File.separatorChar + "project.pbxproj"); //$NON-NLS-1$
        FileOutputStream fo = new FileOutputStream(file);
        fo.getChannel().truncate(0);
        fo.write(getContents().getBytes());
        fo.flush();
        fo.close();
    }

    /**
     * Validates the project file by reading the beginning of the
     * <code>StringTokenizer</code>.
     * 
     * @param source
     *            The <code>StringTokenizer</code> which contains the contents
     *            of the XCode project.
     * @throws ObjCModelException
     */
    private void validateFile(StringTokenizer source) throws ObjCModelException {
        if (!assertTokens(source, PBXPROJ_HEADER_1)) {
            throw new ObjCModelException(NonXCode3Project);
        }
        // Fetch versions
        String token = source.nextToken();
        archiveVersion = Integer.parseInt(token.substring(0, token.length() - 1));
        if (!assertTokens(source, PBXPROJ_HEADER_2)) {
            throw new ObjCModelException(NonXCode3Project);
        }
        token = source.nextToken();
        objectVersion = Integer.parseInt(token.substring(0, token.length() - 1));
        if (!assertTokens(source, PBXPROJ_HEADER_3)) {
            throw new ObjCModelException(NonXCode3Project);
        }
    }

}
