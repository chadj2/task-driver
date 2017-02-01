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

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.MissingArgumentException;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CmdlineArgs
{
    private static final Logger   LOG = LoggerFactory.getLogger(CmdlineArgs.class);
    private final Options _options;
    private final CommandLine     _cmd;

    protected CmdlineArgs(Options _options, CommandLine _cmd)
    {
        this._options = _options;
        this._cmd = _cmd;
    }

    /**
     * Get an option with a default if not specified.
     * @param _smallOpt
     * @param _default
     * @return
     */
    public String getOption(String _smallOpt, String _default)
    {
        String _value = _cmd.getOptionValue(_smallOpt);
        Option _optObj = this._options.getOption(_smallOpt);

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
     * @param _smallOpt
     * @return
     * @throws MissingArgumentException
     */
    public String getRequiredOption(String _smallOpt)
            throws MissingArgumentException
    {
        String _value = _cmd.getOptionValue(_smallOpt);
        Option _optObj = this._options.getOption(_smallOpt);

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
     * @param _smallOpt
     * @return
     */
    public boolean hasOption(String _smallOpt)
    {
        return _cmd.hasOption(_smallOpt);
    }
}
