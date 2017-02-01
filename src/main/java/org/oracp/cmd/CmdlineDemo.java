/**
 * CMD ADAPTER - Command Line Task Driver
 *
 *  Copyright 2016 by Chad Juliano
 *
 *  Licensed under GNU Lesser General Public License v3.0 only.
 *  Some rights reserved. See LICENSE.
 *
 * @license LGPL-3.0 <http://spdx.org/licenses/LGPL-3.0>
 */

package org.oracp.cmd;

import java.io.PrintWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CmdlineDemo extends CmdlineAdapter
{
    private static final Logger LOG = LoggerFactory.getLogger(CmdlineDemo.class);
    private String _optionalOpt = null;
    private String _requiredOpt = null;

    enum DemoTaskEnum
    {
        NO_PARAM,
        STR_PARAM,
        INT_PARAM;
    };

    public CmdlineDemo()
    {
        addOption("v", "verbose", false, "Verbose mode. (show request/response message)");
        addOption("o", "optional", true, "Optional Option");
        addOption("r", "required", true, "Required Option");

        addTask(DemoTaskEnum.NO_PARAM, null, "Task with no params.");
        addTask(DemoTaskEnum.STR_PARAM, "PARAM-STR", "Task with string param.");
        addTask(DemoTaskEnum.INT_PARAM, "PARAM-INT", "Task with integer param.");
    }

    @Override
    protected void printHelpFooter(PrintWriter _pw)
    {
        _pw.println("This program is an example implimentation of CmdlineAdapter.");
        _pw.println("Contact Chad Juliano<chad.jualiano@oracle.com> for feedback or assistance.");
    }

    @Override
    protected void handleGetArgs(CmdlineArgs _cmdArgs)
            throws Exception
    {
        if(_cmdArgs.hasOption("v"))
        {
            setPackageDebug(CmdlineDemo.class.getPackage());
        }

        _optionalOpt = _cmdArgs.getOption("o", "default-val");
        _requiredOpt = _cmdArgs.getRequiredOption("r");
    }

    @Override
    protected void handleDoTask(Enum<?> _task, CmdlineTaskArgs _args)
            throws Exception
    {
        LOG.info("Required Option Value: <{}>", _requiredOpt);
        LOG.info("Optional Option Value: <{}>", _optionalOpt);

        switch((DemoTaskEnum)_task)
        {
            case NO_PARAM:
                LOG.info("Task {} was called.", _task);
                break;

            case STR_PARAM:
                String _paramStr = _args.takeArg("PARAM-STR");
                LOG.info("Task {} was called with: <{}>", _task, _paramStr);
                break;

            case INT_PARAM:
                int _paramInt = _args.takeArgInt("PARAM-INT");
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
            new CmdlineDemo().run(_args);
        }
        catch(Exception _ex)
        {
            System.exit(1);
        }
        System.exit(0);
    }
}
