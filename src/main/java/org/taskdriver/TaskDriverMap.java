/*
 * TASK DRIVER - Command-line Task Framework
 * Copyright 2016 by Chad Juliano
 *
 * Licensed under GNU Lesser General Public License v3.0 only. Some rights
 * reserved. See LICENSE.
 *
 * @license LGPL-3.0 <http://spdx.org/licenses/LGPL-3.0>
 */

package org.taskdriver;

import java.util.HashMap;
import java.util.stream.Collectors;

/**
 * Contains a mapping of task names to definitions.
 * @author Chad Juliano
 * @param <E> Enumeration of supported tasks.
 */
public class TaskDriverMap<E extends Enum<E>> extends HashMap<String, TaskDefinition<E>>
{
    private static final long serialVersionUID = 1L;

    /**
     * Constructor.
     * @param cmdlineAdapter
     */
    protected TaskDriverMap()
    {}

    /**
     * Add a new task definition.
     * @param _enum
     * @param _description
     * @return
     */
    public TaskDefinition<E> add(E _enum, String _description)
    {
        TaskDefinition<E> _task = new TaskDefinition<E>(_enum, _description);
        this.put(_task.toString(), _task);
        return _task;
    }

    /**
     * Get a one-line summary of the tasks for the help screen.
     * @return
     */
    public String getSummary()
    {
        String _tasks = values().stream()
                .map(TaskDefinition::toString)
                .sorted().collect(Collectors.joining("|", "[", "]"));
        return _tasks;
    }

    /**
     * Get a multi-line description of all the tasks for the help screen.
     * @return
     */
    public String getDetails()
    {
        String _details = values().stream()
                .map(TaskDefinition::formatLine)
                .sorted().collect(Collectors.joining("\n"));
        return _details;
    }
}
