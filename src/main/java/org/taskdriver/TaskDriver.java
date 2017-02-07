/**
 * TASK DRIVER - Command-line Task Framework
 *
 *  Copyright 2016 by Chad Juliano
 *
 *  Licensed under GNU Lesser General Public License v3.0 only.
 *  Some rights reserved. See LICENSE.
 *
 * @license LGPL-3.0 <http://spdx.org/licenses/LGPL-3.0>
 */

package org.taskdriver;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.MissingArgumentException;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;

public abstract class TaskDriver<E extends Enum<E>>
{
    private static final Logger    LOG                 = LoggerFactory.getLogger(TaskDriver.class);

    private static final int       HELP_DESC_PAD_WIDTH = 5;
    private static final int       HELP_INDENT_WIDTH   = 3;
    private static final int       HELP_LINE_WIDTH     = 100;

    private final Options          _optionDefs         = new Options();
    private final TaskDriverMap<E> _taskDefs           = new TaskDriverMap<E>();
    private TaskDefinition<E>      _task               = null;

    private final String           _version;
    private final String           _specTitle;
    private final String           _implTitle;

    protected TaskDriver()
    {
        addOption("help", "print this message", "h", false);
        addOption("debug", "turn on debug messages", "d", false);

        // Descriptions are read from the JAR manifest. If there is not JAR then this will not work.
        Package _package = getClass().getPackage();
        this._version = coalesce(_package.getSpecificationVersion(), "<Specification-Version>");
        this._specTitle = coalesce(_package.getSpecificationTitle(), "<Specification-Title>");
        this._implTitle = coalesce(_package.getImplementationTitle(), "<Implementation-Title>");
    }

    protected void addOption(String _longOpt, String _desc, String _opt, boolean _hasArg)
    {
        _optionDefs.addOption(_opt, _longOpt, _hasArg, _desc);
    }

    protected TaskDefinition<E> addTask(E _enum, String _desc)
    {
        return _taskDefs.add(_enum, _desc);
    }

    /**
     * Get and check command line arguments.
     * @param _cmdArgs
     * @throws Exception
     */
    protected abstract void handleGetArgs(TaskDriverOptions _cmdArgs)
            throws Exception;

    /**
     * Execute one of the configured tasks.
     * @param _task The selected task
     * @param _args Task arguments
     * @throws Exception
     */
    protected abstract void handleDoTask(E _task, TaskDefinition<E> _taskDef)
            throws Exception;

    /**
     * Print additional information when help is specified.
     * @param _pw
     */
    protected abstract void printHelpFooter(PrintWriter _pw);

    /**
     * Start the process.
     * @param _args Command line parameters.
     * @throws Exception
     */
    public void run(String[] _args)
            throws Exception
    {
        try
        {
            parseArgs(_args);

            LOG.debug("* Starting task: <{}>", _task);
            handleDoTask(_task.getEnum(), _task);
        }
        catch(ParseException _ex)
        {
            if(LOG.isDebugEnabled())
            {
                LOG.error("Terminating: {}", _ex.getMessage(), _ex);
            }
            else
            {
                LOG.error("Terminating: {}", _ex.getMessage());
            }
            throw _ex;
        }
        catch(Exception _ex)
        {
            LOG.error("Process Failed: {}", _ex.getMessage(), _ex);
            throw _ex;
        }
        LOG.info("Task Complete: <{}>", _task);
    }

    /**
     * Set a specified loggers in package to DEBUG.
     * @param _package
     */
    public static void setPackageDebug(Package _package)
    {
        String _packageName = _package.getName();
        ch.qos.logback.classic.Logger _logger = (ch.qos.logback.classic.Logger)LoggerFactory.getLogger(_packageName);
        _logger.setLevel(Level.DEBUG);

        LOG.debug("Level {} enabled for: <{}>", Level.DEBUG, _packageName);
    }

    /**
     * Parse command line arguments into member variables.
     * @param _cmdArgs
     * @throws Exception
     */
    private void parseArgs(String[] _args)
            throws Exception
    {
        // hook for subclass
        CommandLine _cmd = parseOptions(_args);

        TaskDriverOptions _cmdArgs = new TaskDriverOptions(_optionDefs, _cmd);
        handleGetArgs(_cmdArgs);

        // copy args to the queue
        // some of these args will be processed by the task
        List<String> _argList = _cmd.getArgList();
        parseTask(_argList);
    }

    /**
     * Default option parse.
     * @param _args
     * @param _cmd
     * @return
     * @throws ParseException
     */
    private CommandLine parseOptions(String[] _args)
            throws ParseException
    {
        CommandLineParser _parser = new DefaultParser();
        CommandLine _cmd = _parser.parse(_optionDefs, _args);

        if(_cmd.hasOption("d"))
        {
            setPackageDebug(TaskDriver.class.getPackage());
        }

        String _argDesc = Arrays.asList(_args).stream()
                .collect(Collectors.joining(") (", "(", ")"));

        LOG.debug("ARGS: {}", _argDesc);

        if(_cmd.hasOption("h") || _cmd.getArgList().size() == 0)
        {
            StringWriter _sw = new StringWriter();
            printHelp(new PrintWriter(_sw));
            LOG.info(_sw.toString());
            throw new ParseException("Help option requested");
        }

        return _cmd;
    }

    private void parseTask(List<String> _args)
            throws MissingArgumentException
    {
        ArrayDeque<String> _taskArgs = new ArrayDeque<String>(_args);
        String _taskStr;
        try
        {
            _taskStr = _taskArgs.removeFirst();
        }
        catch(NoSuchElementException _ex)
        {
            throw new MissingArgumentException("Missing task argument: " + _taskDefs.getSummary());
        }

        this._task = _taskDefs.get(_taskStr);
        if(_task == null)
        {
            throw new MissingArgumentException(
                    String.format("<%s> must be one one of %s. ", _taskStr, _taskDefs.getSummary()));
        }

        _task.setArgs(_taskArgs);
        TaskDriver.LOG.debug("OPTION: task = <{}>", _task.getEnum());
    }

    private void printHelp(PrintWriter _pw)
    {
        final String _cmdSyntax = String.format("%s [OPTIONS] %s", this._implTitle, _taskDefs.getSummary());

        HelpFormatter _help = new HelpFormatter();
        _help.printHelp(_pw,
                HELP_LINE_WIDTH,
                _cmdSyntax, null,
                this._optionDefs,
                HELP_INDENT_WIDTH,
                HELP_DESC_PAD_WIDTH,
                null, false);

        _pw.println();
        _pw.println(String.format("%s (v%s)", this._specTitle, this._version));
        _pw.println();
        _pw.println("You must choose one of the following tasks:");
        _pw.println(_taskDefs.getDetails());
        _pw.println();
        printHelpFooter(_pw);
    }

    private static String coalesce(String _arg1, String _arg2)
    {
        if(_arg1 != null)
        {
            return _arg1;
        }
        return _arg2;
    }
}
