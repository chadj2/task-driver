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

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.MissingArgumentException;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Contains the set of options not associated with tasks.
 * @author cjuliano
 *
 */
public class TaskDriverOptions
{
    private static final Logger LOG = LoggerFactory.getLogger(TaskDriverOptions.class);
    private final Options       _options;
    private final CommandLine   _cmd;

    /**
     * Constructor
     * @param _options Apache options descriptor.
     * @param _cmd Apache options values.
     */
    protected TaskDriverOptions(Options _options, CommandLine _cmd)
    {
        this._options = _options;
        this._cmd = _cmd;
    }

    /**
     * Get an option with a default if not specified.
     * @param _opt argument name.
     * @param _default Default if option sis not passed.
     * @return
     */
    public String getOptionOrDefault(String _opt, String _default)
    {
        String _value = _cmd.getOptionValue(_opt);
        Option _optObj = this._options.getOption(_opt);

        if(_value == null)
        {
            _value = _default;
        }

        LOG.debug("OPTION: {} = <{}>", _optObj.getLongOpt(), _value);
        return _value;
    }

    /**
     * Get a required option from the CommandLine and throw an exception if not
     * found.
     * @param _opt argument name
     * @return
     * @throws MissingArgumentException
     */
    public String getRequiredOption(String _opt)
            throws MissingArgumentException
    {
        String _value = _cmd.getOptionValue(_opt);
        Option _optObj = this._options.getOption(_opt);

        if(_value == null)
        {
            String _msg = String.format("Missing option: --%s <%s>", _optObj.getLongOpt(),
                    _optObj.getDescription());
            throw new MissingArgumentException(_msg);
        }

        LOG.debug("OPTION: {} = <{}>", _optObj.getLongOpt(), _value);
        return _value;
    }

    /**
     * Indicates if the option value was passed on the command line.
     * @param _opt argument name
     * @return
     */
    public boolean hasOption(String _opt)
    {
        return _cmd.hasOption(_opt);
    }
}
