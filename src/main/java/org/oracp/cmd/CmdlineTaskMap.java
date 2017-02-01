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

import java.util.HashMap;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;

/**
 * Contains the list of configured tasks.
 */
public class CmdlineTaskMap extends HashMap<String, CmdlineTaskMap.CmdlineTask>
{
    private static final long serialVersionUID = 1L;

    /**
     * @param cmdlineAdapter
     */
    protected CmdlineTaskMap()
    {}

    public CmdlineTask add(Enum<?> _enum, String _usage, String _description)
    {
        CmdlineTask _task = new CmdlineTask(_enum, _usage, _description);
        this.put(_task.toString(), _task);
        return _task;
    }

    public String getSummary()
    {
        String _tasks = values().stream()
                .map(CmdlineTask::toString)
                .sorted().collect(Collectors.joining("|", "[", "]"));
        return _tasks;
    }

    public String getDetails()
    {
        String _details = values().stream()
                .map(CmdlineTask::formatLine)
                .sorted().collect(Collectors.joining("\n"));
        return _details;
    }

    /**
     * Used in TaskMap
     */
    protected class CmdlineTask
    {
        private static final int TASK_LINE_WIDTH = 35;
        private final Enum<?> _enum;
        private final String  _paramName;
        private final String  _arg;
        private final String  _description;

        CmdlineTask(Enum<?> _enum, String _arg, String _description)
        {
            this._enum = _enum;
            this._arg = _arg;
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

        public String getUsage()
        {
            if(_arg == null)
            {
                return _paramName;
            }
            return String.format("%s [%s]", _paramName, _arg);
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
    }
}
