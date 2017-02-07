package org.taskdriver;

import java.util.AbstractMap;
import java.util.ArrayDeque;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.apache.commons.cli.MissingArgumentException;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Used in TaskMap
 */
public class TaskDefinition<E extends Enum<E>>
{
    private static final Logger            LOG             = LoggerFactory.getLogger(TaskDefinition.class);
    private static final int               TASK_LINE_WIDTH = 35;

    private final E                        _enum;
    private final String                   _paramName;
    private final ArrayDeque<TaskArgEntry> _argDefs        = new ArrayDeque<>();
    private final String                   _description;

    private class TaskArgEntry extends AbstractMap.SimpleEntry<String, String>
    {
        private static final long serialVersionUID = 1L;

        public TaskArgEntry(String _name, String _value)
        {
            super(_name, _value);
        }
    }

    protected TaskDefinition(E _enum, String _description)
    {
        this._enum = _enum;
        this._description = _description;

        String _taskStr = _enum.name();
        _taskStr = _taskStr.replace('_', '-');
        _taskStr = _taskStr.toLowerCase();
        this._paramName = _taskStr;
    }

    public TaskDefinition<E> addArg(String _argName)
    {
        _argDefs.add(new TaskArgEntry(_argName, null));
        return this;
    }

    /**
     * @param _taskArgs
     */
    protected void setArgs(ArrayDeque<String> _taskArgs)
    {
        _argDefs.forEach(_item ->
        {
            String _argVal = _taskArgs.pollFirst();
            _item.setValue(_argVal);
        });
    }

    @Override
    public String toString()
    {
        return this._paramName;
    }

    public E getEnum()
    {
        return _enum;
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

    /**
     * Get the next task argument that was passed on the command line as a
     * string.
     * @return
     * @throws MissingArgumentException
     */
    public String takeArg()
            throws MissingArgumentException
    {
        return takeArgEntry().getValue();
    }

    /**
     * Get the next task argument that was passed on the command line as an
     * integer.
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
     * Get the next task argument that was passed on the command line.
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

}
