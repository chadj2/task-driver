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

package org.taskdriver.demo;

import java.io.PrintWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.taskdriver.TaskDefinition;
import org.taskdriver.TaskDriver;
import org.taskdriver.TaskDriverOptions;

/**
 * Demonstration implementation of the TaskDriver.
 * @author cjuliano
 *
 */
public class TaskDriverDemo extends TaskDriver<TaskDriverDemo.DemoTaskEnum>
{
    private static final Logger LOG          = LoggerFactory.getLogger(TaskDriverDemo.class);
    private String              _optionalOpt = null;
    private String              _requiredOpt = null;

    /**
     * Set of available tasks.
     */
    enum DemoTaskEnum
    {
        NO_PARAM,
        STR_PARAM,
        INT_PARAM;
    };

    /**
     * Constructor
     */
    public TaskDriverDemo()
    {
        addOption("verbose", "Verbose mode. (show request/response message)", "v", false);
        addOption("optional", "Optional Option", "o", true);
        addOption("required", "Required Option", "r", true);

        addTask(DemoTaskEnum.NO_PARAM, "Task with no params.");
        addTask(DemoTaskEnum.STR_PARAM, "Task with string param.")
                .addArg("PARAM-STR");
        addTask(DemoTaskEnum.INT_PARAM, "Task with integer param.")
                .addArg("PARAM-INT");
    }

    @Override
    protected void printHelpFooter(PrintWriter _pw)
    {
        _pw.println("This program is an example implimentation of TaskDriver.");
        _pw.println("Contact Chad Juliano<chad.jualiano@oracle.com> for feedback or assistance.");
    }

    @Override
    protected void handleGetArgs(TaskDriverOptions _cmdArgs)
            throws Exception
    {
        if(_cmdArgs.hasOption("d"))
        {
            // add additional loggers here
        }

        if(_cmdArgs.hasOption("v"))
        {
            setPackageDebug(TaskDriverDemo.class.getPackage());
            // add additional loggers here
        }

        _optionalOpt = _cmdArgs.getOptionOrDefault("o", "default-val");
        _requiredOpt = _cmdArgs.getRequiredOption("r");
    }

    @Override
    protected void handleDoTask(DemoTaskEnum _task, TaskDefinition<DemoTaskEnum> _taskDef)
            throws Exception
    {
        LOG.info("Required Option Value: <{}>", _requiredOpt);
        LOG.info("Optional Option Value: <{}>", _optionalOpt);

        // only visible in verbose mode
        LOG.debug("Verbose Logging is turned on.");

        switch(_task)
        {
            case NO_PARAM:
                LOG.info("Task {} was called.", _task);
                break;

            case STR_PARAM:
                String _paramStr = _taskDef.takeArg();
                LOG.info("Task {} was called with: <{}>", _task, _paramStr);
                break;

            case INT_PARAM:
                int _paramInt = _taskDef.takeArgInt();
                LOG.info("Task {} was called with: <{}>", _task, _paramInt);
                break;

            default:
                throw new Exception("Not a valid task: " + _task);
        }
    }

    /**
     * Program entry point.
     * @param args
     */
    public static void main(String _args[])
    {
        try
        {
            new TaskDriverDemo().run(_args);
        }
        catch(Exception _ex)
        {
            System.exit(1);
        }
        System.exit(0);
    }
}
