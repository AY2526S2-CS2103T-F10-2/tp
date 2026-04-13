package cms.logic.parser;

import static cms.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cms.logic.commands.ExportCommand;
import cms.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new {@code ExportCommand} object.
 */
public class ExportCommandParser implements Parser<ExportCommand> {

    public static final String MESSAGE_INVALID_FILE_PATH = "File path is invalid: %1$s\n"
        + "Format: " + ExportCommand.MESSAGE_USAGE;
    public static final String MESSAGE_EMPTY_FILE_PATH = "File path cannot be empty\n"
        + "Format: " + ExportCommand.MESSAGE_USAGE;
    public static final String MESSAGE_FILE_EXTENSION_REQUIRED = "File path must end with .json\n"
        + "Format: " + ExportCommand.MESSAGE_USAGE;

    private static final Pattern QUOTED_PATH_PATTERN = Pattern.compile("^\\s*(\"(?:[^\"\\\\]|\\\\.)*\")\\s*$");

    @Override
    public ExportCommand parse(String args) throws ParseException {
        Matcher matcher = QUOTED_PATH_PATTERN.matcher(args);
        if (!matcher.matches()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, ExportCommand.MESSAGE_USAGE));
        }

        String pathString = matcher.group(1).substring(1, matcher.group(1).length() - 1);

        if (pathString.isEmpty()) {
            throw new ParseException(MESSAGE_EMPTY_FILE_PATH);
        }

        final Path exportFilePath;
        try {
            exportFilePath = Path.of(pathString);
        } catch (InvalidPathException ipe) {
            throw new ParseException(String.format(MESSAGE_INVALID_FILE_PATH, ipe.getReason()));
        }

        if (!exportFilePath.toString().toLowerCase().endsWith(".json")) {
            throw new ParseException(MESSAGE_FILE_EXTENSION_REQUIRED);
        }

        return new ExportCommand(exportFilePath);
    }
}
