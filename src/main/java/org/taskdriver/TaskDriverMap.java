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

import java.util.AbstractMap;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.apache.commons.cli.MissingArgumentException;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Contains the list of configured tasks.
 */
public class TaskDriverMap extends HashMap<String, TaskDriverMap.TaskDefinition>
{
    private static final Logger LOG              = LoggerFactory.getLogger(TaskDriverMap.class);
    private static final long   serialVersionUID = 1L;

    /**
     * @param cmdlineAdapter
     */
    protected TaskDriverMap()
    {}

    public TaskDefinition add(Enum<?> _enum, String _description)
    {
        TaskDefinition _task = new TaskDefinition(_enum, _description);
        this.put(_task.toString(), _task);
        return _task;
    }

    public String getSummary()
    {
        String _tasks = values().stream()
                .map(TaskDefinition::toString)
                .sorted().collect(Collectors.joining("|", "[", "]"));
        return _tasks;
    }

    public String getDetails()
    {
        String _details = values().stream()
                .map(TaskDefinition::formatLine)
                .sorted().collect(Collectors.joining("\n"));
        return _details;
    }

    protected class TaskArgEntry extends AbstractMap.SimpleEntry<String, String>
    {
        private static final long serialVersionUID = 1L;

        public TaskArgEntry(String _name, String _value)
        {
            super(_name, _value);
        }
    }

    /**
     * Used in TaskMap
     */
    public class TaskDefinition
    {
        private static final int               TASK_LINE_WIDTH = 35;

        private final Enum<?>                  _enum;
        private final String                   _paramName;
        private final ArrayDeque<TaskArgEntry> _argDefs        = new ArrayDeque<>();
        private final String                   _description;

        TaskDefinition(Enum<?> _enum, String _description)
        {
            this._enum = _enum;
            this._description = _description;
            this._paramName = getParamName(_enum);
        }

        @Override
        public String toString()
        {
            return this._paramName;
        }

        public Enum<?> getEnum()
        {
            return _enum;
        }

        public TaskDefinition addArg(String _argName)
        {
            _argDefs.add(new TaskArgEntry(_argName, null));
            return this;
        }

        public String getUsage()
        {
            if(_argDefs.size() == 0)
            {
                return _paramName;
            }
            String _argDesc = _argDefs.stream().map(TaskArgEntry::getKey)
                    .collect(Collectors.joining("] [", "[", "]"));

            return String.format("%s %s", _paramName, _argDesc);
        }

        public String getDescription()
        {
            return this._description;
        }

        public String formatLine()
        {
            String _usageLine = StringUtils.rightPad(this.getUsage(), TASK_LINE_WIDTH, ".");
            return String.format("   %s %s", _usageLine, this.getDescription());
        }

        private String getParamName(Enum<?> _enum)
        {
            String _taskStr = _enum.name();
            _taskStr = _taskStr.replace('_', '-');
            _taskStr = _taskStr.toLowerCase();
            return _taskStr;
        }

        /**
         * Get the next parameter (not option) that was passed on the command
         * line.
         * @param _argName Paramter name used for logging and error
         *        descriptions.
         * @return
         * @throws MissingArgumentException
         */
        private TaskArgEntry takeArgEntry()
                throws MissingArgumentException
        {
            TaskArgEntry _argEntry;
            try
            {
                _argEntry = _argDefs.removeFirst();
            }
            catch(NoSuchElementException _ex)
            {
                throw new MissingArgumentException("No more parameter definitions.");
            }

            String _name = _argEntry.getKey();
            String _value = _argEntry.getValue();

            if(_value == null)
            {
                throw new MissingArgumentException("Missing parameter: " + _name);
            }
            LOG.debug("ARG: {} = <{}>", _name, _value);
            return _argEntry;
        }

        public String takeArg()
                throws MissingArgumentException
        {
            return takeArgEntry().getValue();
        }

        /**
         * Get the next parameter (not option) that was passed on the command
         * line as an integer.
         * @param _argName
         * @return
         * @throws Exception
         */
        public int takeArgInt()
                throws Exception
        {
            TaskArgEntry _argEntry = takeArgEntry();
            String _name = _argEntry.getKey();
            String _value = _argEntry.getValue();

            int _argInt;
            try
            {
                _argInt = Integer.parseUnsignedInt(_value);
            }
            catch(NumberFormatException _ex)
            {
                throw new ParseException(
                        String.format("Could not convert %s to integer: %s", _name, _value));
            }
            return _argInt;
        }

        /**
         * @param _taskArgs
         */
        public void setArgs(ArrayDeque<String> _taskArgs)
        {
            _argDefs.forEach(_item ->
            {
                String _argVal = _taskArgs.pollFirst();
                _item.setValue(_argVal);
            });
        }
    }
}
