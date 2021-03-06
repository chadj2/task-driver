/*
 * TASK DRIVER - Command-line Task Framework
 *
 * Copyright 2016 by Chad Juliano
 *
 * Licensed under GNU Lesser General Public License v3.0 only. Some rights
 * reserved. See LICENSE.
 *
 * @license LGPL-3.0 <http://spdx.org/licenses/LGPL-3.0>
 */

package org.taskdriver;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.cli.ParseException;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.taskdriver.demo.TaskDriverDemo;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TaskDriverTest
{
    @Test(expected = ParseException.class)
    public void t001_testEmpty()
            throws Exception
    {
        ArrayList<String> _argList = new ArrayList<>();
        execTest(_argList);
    }

    @Test(expected = ParseException.class)
    public void t002_help()
            throws Exception
    {
        ArrayList<String> _argList = new ArrayList<>();
        _argList.add("--help");
        _argList.add("--verbose");
        execTest(_argList);
    }

    @Test()
    public void t010_taskNoParam()
            throws Exception
    {
        ArrayList<String> _argList = new ArrayList<>();
        _argList.addAll(Arrays.asList("no-param"));
        execTest(_argList);
    }

    @Test()
    public void t011_taskNoParamVerbose()
            throws Exception
    {
        ArrayList<String> _argList = new ArrayList<>();
        _argList.add("--verbose");
        _argList.addAll(Arrays.asList("no-param"));
        execTest(_argList);
    }

    @Test()
    public void t020_taskStrParam()
            throws Exception
    {
        ArrayList<String> _argList = new ArrayList<>();
        _argList.addAll(Arrays.asList("str-param", "Hello World!"));
        execTest(_argList);
    }

    @Test()
    public void t030_taskIntParam()
            throws Exception
    {
        ArrayList<String> _argList = new ArrayList<>();
        _argList.addAll(Arrays.asList("int-param", "9999"));
        execTest(_argList);
    }

    @Test(expected = ParseException.class)
    public void t031_taskIntParamError()
            throws Exception
    {
        ArrayList<String> _argList = new ArrayList<>();
        _argList.addAll(Arrays.asList("int-param", "9999x"));
        execTest(_argList);
    }

    @Test(expected = ParseException.class)
    public void t032_taskIntParamErrorDebug()
            throws Exception
    {
        ArrayList<String> _argList = new ArrayList<>();
        _argList.add("--debug");
        _argList.addAll(Arrays.asList("int-param", "9999x"));
        execTest(_argList);
    }

    @Test()
    public void t040_optionalOption()
            throws Exception
    {
        ArrayList<String> _argList = new ArrayList<>();
        _argList.addAll(0, Arrays.asList("--optional", "optional-opt-val"));
        _argList.addAll(Arrays.asList("no-param"));
        execTest(_argList);
    }

    private static void execTest(ArrayList<String> _argList)
            throws Exception
    {
        //_argList.add(0, "--verbose");
        _argList.addAll(0, Arrays.asList("--required", "reqired-opt-val"));

        TaskDriverDemo _testClass = new TaskDriverDemo();
        _testClass.run(_argList.toArray(new String[0]));
    }
}
